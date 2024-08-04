package com.naegwon.bank.config.dummy;

import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DevInit extends DummyObject{

    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository){
        return (args) -> {
            //서버 실행시 무조건 실행됨
            User user = userRepository.save(newUser("test", "테스트"));
        };
    }
    
}
