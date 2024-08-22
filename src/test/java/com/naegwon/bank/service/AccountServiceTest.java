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
import com.naegwon.bank.dto.account.AccountReqDto.AccountTransferReqDto;
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
    
    @Test
    public void depositAccount_test2() throws Exception{
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

        //stub2
        User user2 = newMockUser(2L, "test2", "테스트2");
        Account account2 = newMockAccount(2L, 1111L, 1000L, user2);
        Transaction transaction = newMockDepositTransaction(1L, account2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        //when
        AccountDepositRespDto accountDepositRespDto = accountService.depositAccount(accountDepositReqDto);

        //then
        String responseBody = om.writeValueAsString(accountDepositRespDto);
        System.out.println("테스트 = " + responseBody);

        assertEquals(account1.getBalance(), 1100L);
    }

    //기술적인 테크닉
    //서비르슷 테스트하고 싶으면 내가 지금 무엇을 여기서 테스트해야할지 명확히 구분(책임 분리)
    //DTO 를 만드는 책임 -> 서비스에 있지만 (서비스에서 DTO 검증 X -> Controller 테스트 해볼 것이니깐)
    //DB 관련 책임 -> 서비스 것이 아님. 필요 없음
    //DB 관련된 것을 조회했을 떄 그 값을 통해서 어떤 비지니스 로직이 흘러가는 것이 있으면 -> stub으로 정의해서 테스트해본다.
    
    //DB stub(가짜로 DB 만들어서 deposit 검증... 0원 검증...)
    @Test
    public void depositAccount_test3() throws Exception{
        //given
        Account account = newMockAccount(1L, 1111L, 1000L, null);
        Long amount = 100L;

        //when
        //0원 체크
        if(amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        account.deposit(100L);

        //then
        assertEquals(account.getBalance(), 1100L);
    }

    //계좌 출금 테스트
    @Test
    public void withdrawAccount_test() throws Exception {
        //given
        Long amount = 100L;
        Long password = 1234L;
        Long userId = 1L;

        User user = newMockUser(1L, "test", "테스트");
        Account userAccount = newMockAccount(1L, 1111L, 1000L, user);

        //when
        if(amount <= 0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }
        userAccount.checkOwner(userId);
        userAccount.checkSamePassword(password);
        userAccount.checkBalance(amount);
        userAccount.withdraw(amount);

        //then
        assertEquals(userAccount.getBalance(), 900L);
    }
    
    //계좌 이체 테스트
    @Test
    public void transferAccount_test() throws Exception{
        //given
        Long userId = 1L;
        AccountTransferReqDto accountTransferReqDto = new AccountTransferReqDto();
        accountTransferReqDto.setWithdrawNumber(1111L);
        accountTransferReqDto.setDepositNumber(2222L);
        accountTransferReqDto.setWithdrawPassword(1234L);
        accountTransferReqDto.setAmount(100L);
        accountTransferReqDto.setGubun("TRANSFER");

        User user1 = newMockUser(1L, "user1", "유저1");
        User user2 = newMockUser(2L, "user2", "유저2");
        Account withdrawAccount = newMockAccount(1L, 1111L, 1000L, user1);
        Account depositAccount = newMockAccount(2L, 2222L, 1000L, user2);

        //when
        //출금계좌와 입금계좌가 동일하면 안됨
        if (accountTransferReqDto.getWithdrawNumber().longValue() == accountTransferReqDto.getDepositNumber().longValue()) {
            throw new CustomApiException("입금 계좌와 출금계좌는 동일할 수 없습니다.");
        }
        //0원 체크
        if(accountTransferReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }

        //출금 소유자 확인(로그인한 사람과 동일한지)
        withdrawAccount.checkOwner(userId);

        //출금 계좌 비밀번호 확인
        withdrawAccount.checkSamePassword(accountTransferReqDto.getWithdrawPassword());

        //출금 계좌 출금 확인
        withdrawAccount.checkBalance(accountTransferReqDto.getAmount());

        //이체하기
        withdrawAccount.withdraw(accountTransferReqDto.getAmount());
        depositAccount.deposit(accountTransferReqDto.getAmount());

        //then
        assertEquals(withdrawAccount.getBalance(), 900L);
        assertEquals(depositAccount.getBalance(), 1100L);
        
    }
    
    
    //계좌 목록 보기 테스트

    //계좌 상세보기 테스트
    

}