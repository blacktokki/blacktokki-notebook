package com.blacktokki.spreadocs.core.config;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;

import org.modelmapper.module.jsr310.FromTemporalConverter;
// import org.modelmapper.module.jsr310.Jsr310Module;
import org.modelmapper.module.jsr310.Jsr310ModuleConfig;
import org.modelmapper.module.jsr310.TemporalToTemporalConverter;
import org.modelmapper.module.jsr310.ToTemporalConverter;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

public class CustomJsr310Module implements Module{
    private final Jsr310ModuleConfig config = Jsr310ModuleConfig.builder().build();

    private final FromTemporalConverter fromTemporalConverter = new FromTemporalConverter(config){
        @Override
        public Object convert(MappingContext<Temporal, Object> mappingContext) {
            if (mappingContext.getSource() == null)
            return null;

            Class<?> sourceType = mappingContext.getSourceType();
            Class<?> destinationType = mappingContext.getDestinationType();
            if (LocalDateTime.class.equals(sourceType) && Timestamp.class.equals(destinationType))
                return Timestamp.valueOf((LocalDateTime)mappingContext.getSource());
            if (LocalDate.class.equals(sourceType) && Date.class.equals(destinationType))
                return Date.valueOf((LocalDate)mappingContext.getSource());
            if (LocalTime.class.equals(sourceType) && Time.class.equals(destinationType))
                return Time.valueOf((LocalTime)mappingContext.getSource());
            else
                return super.convert(mappingContext);
        }
    };

    private final ToTemporalConverter toTemporalConverter = new ToTemporalConverter(config);
    
    private final TemporalToTemporalConverter temporalToTemporalConverter = new TemporalToTemporalConverter();
    
    @Override
    public void setupModule(ModelMapper modelMapper) {
        modelMapper.getConfiguration().getConverters().add(0, fromTemporalConverter);
        modelMapper.getConfiguration().getConverters().add(0, toTemporalConverter);
        modelMapper.getConfiguration().getConverters().add(0, temporalToTemporalConverter);
    }
}
