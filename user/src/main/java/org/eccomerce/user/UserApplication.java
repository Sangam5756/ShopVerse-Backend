package org.eccomerce.user;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper(); // default
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull()) // only not null properties allow
                .setMatchingStrategy(MatchingStrategies.STRICT);

        return mapper;
    }



}
