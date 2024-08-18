package com.naegwon.bank.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.transaction.TransactionEnum;
import com.naegwon.bank.domain.transaction.TransactionRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.account.AccountReqDto;
import com.naegwon.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import com.naegwon.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import com.naegwon.bank.dto.account.AccountRespDto;
import com.naegwon.bank.dto.account.AccountRespDto.AccountDepositRespDto;
import com.naegwon.bank.dto.account.AccountRespDto.AccountListRespDto;
import com.naegwon.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import com.naegwon.bank.dto.account.AccountRespDto.AccountWithdrawRespDto;
import com.naegwon.bank.handler.ex.CustomApiException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public AccountListRespDto getUserAccountList(Long userId){
        User userPersist = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        );

        //유저의 모든 계좌 목록
        List<Account> accountListPersist = accountRepository.findByUser_id(userId);

        return new AccountListRespDto(userPersist, accountListPersist);
    }

    @Transactional
    public AccountSaveRespDto saveAccount(AccountSaveReqDto accountSaveReqDto, Long userId) {
        //User DB에 있는지 검증 겸 유저 엔티티를 가져옴
        User userPersist = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다")
        );

        //해당 계좌가 DB에 있는지 중복여부 체크
        Optional<Account> accountOptional = accountRepository.findByNumber(accountSaveReqDto.getNumber());
        if (accountOptional.isPresent()) {
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }

        //계좌 등록
        Account accountPersist = accountRepository.save(accountSaveReqDto.toEntity(userPersist));

        //DTO 응답
        return new AccountSaveRespDto(accountPersist);
    }

    @Transactional
    public void deleteAccount(Long number, Long userId) {
        //1. 계좌 확인
        Account accountPersist = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        );

        //2. 계좌 소유자 확인
        accountPersist.checkOwner(userId);

        //3. 계좌 삭제
        accountRepository.deleteById(accountPersist.getId());
    }

    //인증이 필요 없음
    @Transactional
    public AccountDepositRespDto depositAccount(AccountDepositReqDto accountDepositReqDto){ //ATM -> 누군가의 계좌
        //0원 체크
        if(accountDepositReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다");
        }
        
        //입금계좌 확인
        Account depositAccountPersist = accountRepository.findByNumber(accountDepositReqDto.getNumber()).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        );
        
        //입금 (해당 계좌 balance 조정 - update문 - 더티체킹)
        depositAccountPersist.deposit(accountDepositReqDto.getAmount());
        
        //거래내역 남기기
        Transaction transaction = Transaction.builder()
                .depositAccount(depositAccountPersist)
                .withdrawAccount(null)
                .depositAccountBalance(depositAccountPersist.getBalance())
                .withdrawAccountBalance(null)
                .amount(accountDepositReqDto.getAmount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(depositAccountPersist.getNumber() + "")
                .tel(accountDepositReqDto.getTel())
                .build();

        Transaction transactionPersist = transactionRepository.save(transaction);
        return new AccountDepositRespDto(depositAccountPersist, transactionPersist);
    }

    @Transactional
    public AccountWithdrawRespDto withdrawAccount(AccountWithDrawReqDto accountWithDrawReqDto, Long userId){
        //0원 체크
        if(accountWithDrawReqDto.getAmount() <= 0L){
            throw new CustomApiException("0원 이하의 금액을 출금할 수 없습니다");
        }

        //출금계좌 확인
        Account withdrawAccountPersist = accountRepository.findByNumber(accountWithDrawReqDto.getNumber()).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다")
        );

        //출금 소유자 확인(로그인한 사람과 동일한지)
        withdrawAccountPersist.checkOwner(userId);

        //출금 계좌 비밀번호 확인
        withdrawAccountPersist.checkSamePassword(accountWithDrawReqDto.getPassword());

        //출금 계좌 출금 확인
        withdrawAccountPersist.checkBalance(accountWithDrawReqDto.getAmount());

        //출금하기
        withdrawAccountPersist.withdraw(accountWithDrawReqDto.getAmount());

        //거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPersist)
                .depositAccount(null)
                .withdrawAccountBalance(withdrawAccountPersist.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithDrawReqDto.getAmount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithDrawReqDto.getNumber() + "")
                .receiver("ATM")
                .build();

        Transaction transactionPersist = transactionRepository.save(transaction);

        //DTO 응답
        return new AccountWithdrawRespDto(withdrawAccountPersist, transactionPersist);
    }

    @Getter
    @Setter
    public static class AccountWithDrawReqDto {

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        @NotNull
        private Long amount;

        @NotNull
        @Pattern(regexp = "WITHDRAW")
        private String gubun;
    }




}
