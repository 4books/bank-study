package com.naegwon.bank.config.dummy;

import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.transaction.TransactionRepository;
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
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
        return (args) -> {
            //서버 실행시 무조건 실행됨
            User naegwon = userRepository.save(newUser("naegwon", "황내권"));
            User test = userRepository.save(newUser("test", "테스트"));
            User someone = userRepository.save(newUser("someone", "아무개"));
            User admin = userRepository.save(newUser("admin", "관리자"));

            Account naegwonAccount1 = accountRepository.save(newAccount(1111L, naegwon));
            Account testAccount = accountRepository.save(newAccount(2222L, test));
            Account someoneAccount = accountRepository.save(newAccount(3333L, someone));
            Account naegwonAccount2 = accountRepository.save(newAccount(4444L, naegwon));

            Transaction withdrawTransaction1 = transactionRepository
                    .save(newWithdrawTransaction(naegwonAccount1, accountRepository));
            Transaction depositTransaction1 = transactionRepository
                    .save(newDepositTransaction(testAccount, accountRepository));
            Transaction transferTransaction1 = transactionRepository
                    .save(newTransferTransaction(naegwonAccount1, testAccount, accountRepository));
            Transaction transferTransaction2 = transactionRepository
                    .save(newTransferTransaction(naegwonAccount1, someoneAccount, accountRepository));
            Transaction transferTransaction3 = transactionRepository
                    .save(newTransferTransaction(testAccount, naegwonAccount1, accountRepository));
        };
    }
    
}
