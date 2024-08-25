package com.naegwon.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.transaction.TransactionRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TransactionControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        dataSetting();
        em.clear();
    }

    @Test
    @WithUserDetails(value = "naegwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void findTransactionList_test() throws Exception{
        //given
        Long number = 1111L;
        String gubun = "ALL";
        String page = "0";

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/account/" + number + "/transaction")
                .param("gubun", gubun)
                .param("page", page));
        String responseBody = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        resultActions.andExpect(jsonPath("$.data.transactions[0].balance").value(900L));
        resultActions.andExpect(jsonPath("$.data.transactions[1].balance").value(800L));
        resultActions.andExpect(jsonPath("$.data.transactions[2].balance").value(700L));
        resultActions.andExpect(jsonPath("$.data.transactions[3].balance").value(800L));
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

}