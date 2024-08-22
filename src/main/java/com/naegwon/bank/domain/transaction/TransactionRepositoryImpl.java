package com.naegwon.bank.domain.transaction;

import org.springframework.data.repository.query.Param;

import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun,
                                          @Param("page") Integer page);

}

public class TransactionRepositoryImpl implements Dao{

    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        //동적쿼리(gubun 값을 가지고 동적쿼리 = DEPOSIT, WITHDRAW, ALL)
        return null;
    }
}
