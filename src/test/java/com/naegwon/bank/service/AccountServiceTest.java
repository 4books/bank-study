package com.naegwon.bank.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naegwon.bank.config.dummy.DummyObject;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
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



}