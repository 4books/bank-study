package com.naegwon.bank.config.dummy;

import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
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
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository){
        return (args) -> {
            //서버 실행시 무조건 실행됨
            User user = userRepository.save(newUser("test", "테스트"));
            Account testAccount = accountRepository.save(newAccount(1111L, user));

            User user2 = userRepository.save(newUser("test2", "테스트2"));
            Account testAccount2 = accountRepository.save(newAccount(2222L, user2));
        };
    }
    
}
