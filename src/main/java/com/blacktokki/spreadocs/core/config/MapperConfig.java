package com.blacktokki.spreadocs.core.config;


import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    private final CustomJsr310Module customJsr310Module = new CustomJsr310Module();

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // .setCollectionsMergeEnabled(false)
        modelMapper.registerModule(customJsr310Module);
        return modelMapper;
    }

    @Bean
    public ModelMapper notNullModelMapper() {
        ModelMapper notNullModelMapper = new ModelMapper();
        notNullModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        // .setCollectionsMergeEnabled(false)
        notNullModelMapper.registerModule(customJsr310Module);
        return notNullModelMapper;
    }
}
