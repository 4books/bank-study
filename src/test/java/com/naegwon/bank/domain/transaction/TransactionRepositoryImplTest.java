package com.naegwon.bank.domain.transaction;

import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest //DB 관련된 테스트
public class TransactionRepositoryImplTest extends DummyObject {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        autoIncrementReset();
        dataSetting();
        em.clear();//Persistence context 초기화
    }

    @Test
    public void dataJpa_test1(){
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach(transaction -> {
            System.out.println("테스트 = " + transaction.getId());
            System.out.println("입금 = " + transaction.getSender());
            System.out.println("출금 = " + transaction.getReceiver());
            System.out.println("구분 = " + transaction.getGubun());
            System.out.println("==================================");
        });
    }

    @Test
    public void dataJpa_test2(){
        List<Transaction> transactionList = transactionRepository.findAll();
        transactionList.forEach(transaction -> {
            System.out.println("테스트 = " + transaction.getId());
            System.out.println("입금 = " + transaction.getSender());
            System.out.println("출금 = " + transaction.getReceiver());
            System.out.println("구분 = " + transaction.getGubun());
            System.out.println("==================================");
        });
    }

    @Test
    public void findTransactionList_all_test() throws Exception{
        //given
        Long accountId = 1L;

        //when
        List<Transaction> transactionListPersist = transactionRepository.findTransactionList(accountId, "ALL", 0);
        transactionListPersist.forEach((transaction) -> {
            System.out.println("id = " + transaction.getId());
            System.out.println("amount = " + transaction.getAmount());
            System.out.println("sender = " + transaction.getSender());
            System.out.println("receiver = " + transaction.getReceiver());
            System.out.println("withdrawAccountBalance = " + transaction.getWithdrawAccountBalance());
            System.out.println("depositAccountBalance = " + transaction.getDepositAccountBalance());
            System.out.println("fullname = " + transaction.getWithdrawAccount().getUser().getFullname());
            System.out.println("==============================================");
        });

        //then
        assertEquals(transactionListPersist.get(3).getDepositAccountBalance(), 800L);
    }


    private void dataSetting() {
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
    }

    private void autoIncrementReset() {
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE account_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE transaction_tb ALTER COLUMN id RESTART WITH 1").executeUpdate();
    }
}
