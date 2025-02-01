package com.blacktokki.spreadocs.core.config;


import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    private final CustomJsr310Module customJsr310Module = new CustomJsr310Module();
    private final RecordModule recordModule = new RecordModule();

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // .setCollectionsMergeEnabled(false)
        modelMapper.registerModule(customJsr310Module);
        modelMapper.registerModule(recordModule);
        return modelMapper;
    }

    @Bean
    public ModelMapper notNullModelMapper() {
        ModelMapper notNullModelMapper = new ModelMapper();
        notNullModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        // .setCollectionsMergeEnabled(false)
        notNullModelMapper.registerModule(customJsr310Module);
        notNullModelMapper.registerModule(recordModule);
        return notNullModelMapper;
    }
}
