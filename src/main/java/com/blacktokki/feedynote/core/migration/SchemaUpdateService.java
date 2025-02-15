package com.blacktokki.feedynote.core.migration;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.hibernate.boot.Metadata;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.tool.schema.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.tool.schema.internal.ExceptionHandlerCollectingImpl;
import org.hibernate.tool.schema.spi.ContributableMatcher;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.hibernate.tool.schema.spi.SchemaMigrator;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToWriter;

@Service
public class SchemaUpdateService implements InitializingBean {


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private static final Logger log = LoggerFactory.getLogger(SchemaUpdateService.class);

    private static final String userDir = System.getProperty("user.dir") + "/";

    private static final String fileNamePrefix = "V";

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private DataSource dataSource;

    @Value("${spring.jpa.properties.application-name}")
    private String applicationName;

    @Override
    public void afterPropertiesSet() throws Exception {
        SessionFactoryServiceRegistry serviceRegistry = HibernateInfoHolder.getServiceRegistry();
        Metadata metadata = HibernateInfoHolder.getMetadata();
        SchemaManagementTool schemaManagementTool = serviceRegistry.getService(SchemaManagementTool.class);

        Map<String, Object> config = new HashMap<>();
        config.put(AvailableSettings.HBM2DDL_DELIMITER, ";");
        config.put(AvailableSettings.FORMAT_SQL, true);
        StringWriter writer = new StringWriter();
        TargetDescriptor target = new TargetDescriptor() {
            @Override
            public EnumSet<TargetType> getTargetTypes() {
                return EnumSet.of(TargetType.SCRIPT, TargetType.STDOUT);
            }

            @Override
            public ScriptTargetOutput getScriptTargetOutput() {
                return new ScriptTargetOutputToWriter(writer) {
                    @Override
                    public void accept(String command) {
                        super.accept(command);
                    }
                };
            }
        };

        ExceptionHandlerCollectingImpl exceptionHandler = new ExceptionHandlerCollectingImpl();
        ExecutionOptions executionOptions = SchemaManagementToolCoordinator.buildExecutionOptions(
                    config,
                    exceptionHandler);

        SchemaMigrator migrator = schemaManagementTool.getSchemaMigrator(config);

        log.warn("Starting SCHEMA MIGRATION lookup------------------------------------------------------------------");
        // log.warn("please add the following SQL code (if any) to a flyway migration");
        String schemaName = "db1_" + applicationName;
        log.warn("Working on schema: " +  schemaName);
        migrator.doMigration(metadata, executionOptions, ContributableMatcher.ALL, target);
        String path = userDir + "src/main/resources/db/migration/";
        File[] fileList = new File(path).listFiles();
        Flyway flyway = Flyway.configure().dataSource(dataSource).schemas(schemaName).load();
        int sqlFileCount = fileList.length;
        int migrationCount = flyway.info().applied().length;
        log.warn("Number of migrations: " + sqlFileCount + " files / " + migrationCount+ " applied");
        String fileName = null;
        if (sqlFileCount <= migrationCount){
            fileName = path + fileNamePrefix + formatter.format(LocalDateTime.now()) + "__auto.sql";
        }
        else{
            Arrays.sort(fileList);
            fileName = fileList[migrationCount].getAbsolutePath();
        }
        File file = new File(fileName);
        String sql = writer.toString();
        if (sql.length() != 0) {  // migrations present.
            log.warn("Migrations also written to: " + fileName);
            Files.writeString( file.toPath(), sql, StandardOpenOption.CREATE);
        } else {  // delete empty files
            log.warn("No migrations");
            if(file.exists()){
                Files.writeString(file.toPath(), sql, StandardOpenOption.CREATE);
            }
        }
        log.warn("END OF SCHEMA MIGRATION lookup--------------------------------------------------------------------");
    }
}