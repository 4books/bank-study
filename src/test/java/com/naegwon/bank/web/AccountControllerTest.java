package com.naegwon.bank.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.transaction.TransactionRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.account.AccountReqDto;
import com.naegwon.bank.handler.ex.CustomApiException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.naegwon.bank.dto.account.AccountReqDto.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@Transactional
@Sql("classpath:db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AccountControllerTest extends DummyObject {

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
    public void setUp() throws Exception {
        dataSetting();
        em.clear(); //Persist context에 있는 데이터 삭제
    }

    //Jwt token -> 인증 필터 -> 시큐리티 세션 생성
    //setupBefore=TEST_METHOD (setUP method 실행 전에 수행. 그래서 유저를 못 찾음)
    //setupBefore=TEST_EXECUTION (saveAccount_test method 실행 전에 수행됨)

    @WithUserDetails(value = "test", setupBefore = TestExecutionEvent.TEST_EXECUTION) //test 라는 이름의 유저가 로그인이 됨
    @Test
    public void saveAccount_test() throws Exception {
        //given
        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(9999L);
        accountSaveReqDto.setPassword(1234L);
        String requestBody = om.writeValueAsString(accountSaveReqDto);
        System.out.println("테스트 = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/s/account").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    /**
     * 테스트시에는 insert한 것들이 전부 Persist context에 올라감(영속화)
     * 영속화된 것들을 초기화 해주는 것이 개발 모드와 동일한 환경으로 테스트를 할 수 있게 해준다.
     * 최초 select는 쿼리가 발생하지만, PC에 있으면 1차 캐시를 함
     * Lazy 로딩은 쿼리도 발생하지 않음 - PC에 있다면
     * Lazy 로딩할 때 PC에 없다면 query 발생
     */
    @WithUserDetails(value = "naegwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void deleteAccount_test() throws Exception {
        //given
        Long number = 1111L;

        //when
        ResultActions resultActions = mvc.perform(delete("/api/s/account/{number}", number));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        //Junit 테스트에서 delete 쿼리는 DB관련(DML)으로 가장 마지막에 실행 되면 발동 안함
        assertThrows(CustomApiException.class, () -> accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        ));
    }

    @Test
    public void depositAccount_test() throws Exception {
        //given
        AccountReqDto.AccountDepositReqDto accountDepositReqDto = new AccountReqDto.AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01012345678");

        String requestBody = om.writeValueAsString(accountDepositReqDto);
        System.out.println("테스트 = " + requestBody);

        //when
        ResultActions resultActions = mvc.perform(post("/api/account/deposit")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(value = "naegwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void withdrawAccount_test() throws Exception {
        //given
        AccountWithDrawReqDto accountWithDrawReqDto = new AccountWithDrawReqDto();
        accountWithDrawReqDto.setNumber(1111L);
        accountWithDrawReqDto.setPassword(1234L);
        accountWithDrawReqDto.setAmount(100L);
        accountWithDrawReqDto.setGubun("WITHDRAW");

        //when
        String requestBody = om.writeValueAsString(accountWithDrawReqDto);
        System.out.println("테스트 = " + requestBody);

        ResultActions resultActions = mvc.perform(post("/api/s/account/withdraw")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(value = "naegwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void transferAccount_test() throws Exception {
        //given
        Long userId = 1L;
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");

        //when
        String requestBody = om.writeValueAsString(accountTransferReqDto);
        System.out.println("테스트 = " + requestBody);

        ResultActions resultActions = mvc.perform(post("/api/s/account/transfer")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        );

        String responseBody = resultActions.andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("테스트 = " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails(value = "naegwon", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void findDetailAccount_test() throws Exception {
        //given
        Long number = 1111L;
        String page = "0";

        //when
        ResultActions resultActions = mvc.perform(get("/api/s/account/" + number)
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