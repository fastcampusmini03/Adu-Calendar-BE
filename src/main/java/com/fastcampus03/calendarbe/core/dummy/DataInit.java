package com.fastcampus03.calendarbe.core.dummy;

import com.fastcampus03.calendarbe.model.user.User;
import com.fastcampus03.calendarbe.model.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Component
public class DataInit extends DummyEntity{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository){
        return args -> {
            User ssar = newUser("ssar", "ADMIN");
            User cos = newUser("cos", "USER");
            userRepository.save(ssar);
            userRepository.save(cos);
        };
    }
}
