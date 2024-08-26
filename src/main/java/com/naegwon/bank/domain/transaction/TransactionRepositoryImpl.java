package com.naegwon.bank.domain.transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("gubun") String gubun,
                                          @Param("page") Integer page);

}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao{

    private final EntityManager em;

    @Override
    public List<Transaction> findTransactionList(Long accountId, String gubun, Integer page) {
        //JPQL
        String sql = "";
        sql += "select t from Transaction t ";

        if (gubun.equals("WITHDRAW")) {
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        } else if (gubun.equals("DEPOSIT")) {
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId";
        } else { // gubun = ALL
            sql += "left join fetch t.withdrawAccount wa ";
            sql += "left join fetch t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId ";
        }
        sql += "ORDER BY t.createdAt DESC";

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if (gubun.equals("WITHDRAW")) {
            query = query.setParameter("withdrawAccountId", accountId);
        } else if (gubun.equals("DEPOSIT")) {
            query = query.setParameter("depositAccountId", accountId);
        } else {
            query = query.setParameter("withdrawAccountId", accountId)
                    .setParameter("depositAccountId", accountId);
        }

        query.setFirstResult(page * 5); // 5, 10, 15
        query.setMaxResults(5);

        return query.getResultList();
    }
}
