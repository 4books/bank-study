package com.naegwon.bank.service;

import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.account.AccountRepository;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.transaction.TransactionRepository;
import com.naegwon.bank.dto.transaction.TransactionRespDto;
import com.naegwon.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;
import com.naegwon.bank.handler.ex.CustomApiException;
import com.naegwon.bank.util.CustomDateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionListRespDto findTransactionList(Long userId, Long accountNumber, String gubun, int page){
        Account accountPersist = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("해당 계좌를 찾을 수 없습니다."));

        accountPersist.checkOwner(userId);

        List<Transaction> transactionListPersist = transactionRepository.findTransactionList(accountPersist.getId(), gubun, page);
        return new TransactionListRespDto(transactionListPersist, accountPersist);
    }
}
