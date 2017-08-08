package com.stephen.bangbang.mvcconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

@Configuration
@EnableWebMvc
public class WebMVCConfig {
    @Bean
    ValidatorFactory validatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }
}
