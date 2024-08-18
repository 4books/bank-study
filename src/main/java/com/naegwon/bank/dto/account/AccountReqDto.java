package com.naegwon.bank.dto.account;

import com.naegwon.bank.domain.account.Account;
import com.naegwon.bank.domain.user.User;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class AccountReqDto {

    @Getter
    @Setter
    public static class AccountDepositReqDto {
        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        private Long amount;

        @NotEmpty
        @Pattern(regexp = "DEPOSIT")
        private String gubun; //DEPOSIT

        @NotEmpty
        @Pattern(regexp = "^[0-9]{11}")
        private String tel;
    }

    @Getter
    @Setter
    public static class AccountSaveReqDto {

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long number;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long password;

        public Account toEntity(User user){
            return Account.builder()
                    .number(number)
                    .password(password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
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

    @Getter
    @Setter
    public static class AccountTransferReqDto {

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long withdrawNumber;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long depositNumber;

        @NotNull
        @Digits(integer = 4, fraction = 4)
        private Long withdrawPassword;

        @NotNull
        private Long amount;

        @NotNull
        @Pattern(regexp = "TRANSFER")
        private String gubun;
    }
}
