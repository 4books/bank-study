package com.naegwon.bank.service;

import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.domain.user.UserRepository;
import com.naegwon.bank.dto.account.AccountRespDto;
import com.naegwon.bank.dto.account.AccountRespDto.AccountListRespDto;
import com.naegwon.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import com.naegwon.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import com.naegwon.bank.handler.ex.CustomApiException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

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
}
