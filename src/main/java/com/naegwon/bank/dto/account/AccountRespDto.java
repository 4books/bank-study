package com.naegwon.bank.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.transaction.Transaction;
import com.naegwon.bank.domain.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountRespDto {

    //DTO는 똑같아도 재사용하지 않기(만약에 출금할 때 조금 DTO가 달라져야 하면 수정 범위가 너무 넓음 - 독립적으로 생성)
    @Getter
    @Setter
    public static class AccountWithdrawRespDto {
        private Long id; //계좌 id
        private Long number; //계좌 번호
        private Long balance; //잔액
        private TransactionDto transaction; //거래내역

        public AccountWithdrawRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.createdAt = transaction.getCreatedAt().toString();
            }
        }
    }


    @Getter
    @Setter
    public static class AccountDepositRespDto {
        private Long id; //계좌 id
        private Long number; //계좌 번호
        private TransactionDto transaction; //거래내역

        public AccountDepositRespDto(Account account, Transaction transaction) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.transaction = new TransactionDto(transaction);
        }

        @Getter
        @Setter
        public class TransactionDto {
            private Long id;
            private String gubun;
            private String sender;
            private String receiver;
            private Long amount;

            @JsonIgnore
            private Long depositAccountBalance; //클라이언트에게 전달X -> 서비스단에 테스트 용도
            private String tel;
            private String createdAt;

            public TransactionDto(Transaction transaction) {
                this.id = transaction.getId();
                this.gubun = transaction.getGubun().getValue();
                this.sender = transaction.getSender();
                this.receiver = transaction.getReceiver();
                this.amount = transaction.getAmount();
                this.depositAccountBalance = transaction.getDepositAccountBalance();
                this.tel = transaction.getTel();
                this.createdAt = transaction.getCreatedAt().toString();
            }
        }
    }

    @Getter
    @Setter
    public static class AccountSaveRespDto{
        private Long id;
        private Long number;
        private Long balance;

        public AccountSaveRespDto(Account account) {
            this.id = account.getId();
            this.number = account.getNumber();
            this.balance = account.getBalance();
        }
    }

    @Getter
    @Setter
    public static class AccountListRespDto {
        private String fullname;
        private List<AccountDto> accounts = new ArrayList<>();

        public AccountListRespDto(User user, List<Account> accounts) {
            this.fullname = user.getFullname();
            this.accounts = accounts.stream().map(AccountDto::new).collect(Collectors.toList());
        }

        @Getter
        @Setter
        public class AccountDto {
            private Long id;
            private Long number;
            private Long balance;

            public AccountDto(Account account) {
                this.id = account.getId();
                this.number = account.getNumber();
                this.balance = account.getBalance();
            }
        }
    }
}
