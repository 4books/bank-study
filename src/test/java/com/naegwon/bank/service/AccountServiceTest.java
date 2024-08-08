package com.naegwon.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.transaction.TransactionRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.account.AccountReqDto;
import com.naegwon.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import com.naegwon.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import com.naegwon.bank.handler.ex.CustomApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.naegwon.bank.dto.account.AccountRespDto.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest extends DummyObject {

    @InjectMocks //모든 mock 들이 injectMocks 로 주입됨
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Spy //진짜 객체를 InjectMocks에 주입한다.
    private ObjectMapper om;

    @Test
    public void saveAccount_test() throws Exception{
        //given
        Long userId = 1L;

        AccountSaveReqDto accountSaveReqDto = new AccountSaveReqDto();
        accountSaveReqDto.setNumber(1111L);
        accountSaveReqDto.setPassword(1234L);

        //stub
        User user = newMockUser(userId, "test", "테스트");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        when(accountRepository.findByNumber(any())).thenReturn(Optional.empty());

        Account testAccount = newMockAccount(1L, 1111L, 1000L, user);
        when(accountRepository.save(any())).thenReturn(testAccount);

        //when
        AccountSaveRespDto accountSaveRespDto = accountService.saveAccount(accountSaveReqDto, userId);
        String responseBody = om.writeValueAsString(accountSaveRespDto);
        System.out.println("테스트 = " + responseBody);

        //then
        assertEquals(accountSaveRespDto.getNumber(), 1111L);
    }

    @Test
    public void deleteAccount_test() throws Exception{
        //given
        Long number= 1111L;
        Long userId = 2L;

        //stub
        User testUser = newMockUser(1L, "test", "테스트");
        Account testAccount = newMockAccount(1L, 1111L, 1000L, testUser);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(testAccount));

        //when

        //then
        assertThrows(CustomApiException.class, () -> accountService.deleteAccount(number, userId));
    }

    //Account -> balance 변경됐는지
    //Transaction -> balance 잘 기록됐는지
    @Test
    public void depositAccount_test() throws Exception{
        //given
        AccountDepositReqDto accountDepositReqDto = new AccountDepositReqDto();
        accountDepositReqDto.setNumber(1111L);
        accountDepositReqDto.setAmount(100L);
        accountDepositReqDto.setGubun("DEPOSIT");
        accountDepositReqDto.setTel("01012345678");

        //stub
        User user1 = newMockUser(1L, "test", "테스트"); 
        Account account1 = newMockAccount(1L, 1111L, 1000L, user1);
        when(accountRepository.findByNumber(any())).thenReturn(Optional.of(account1));

        //stub이 진행될 때 마다 연관된 객체는 새로 만들어서 주입하기 - 타이밍 때문에 꼬인다
        Account account2 = newMockAccount(2L, 1111L, 1000L, user1);
        Transaction transaction = newMockDepositTransaction(1L, account2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        //when
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);
        System.out.println("테스트 트랜잭션 입금계좌 잔액= " + accountDepositRespDto.getTransaction().getDepositAccountBalance());
        System.out.println("테스트 계좌쪽 잔액= " + account1.getBalance());

        //then
        assertEquals(account1.getBalance(), 1100L);
        assertEquals(accountDepositRespDto.getTransaction().getDepositAccountBalance(), 1100L);
        assertEquals(account2.getBalance(), 1100L);
    }



}