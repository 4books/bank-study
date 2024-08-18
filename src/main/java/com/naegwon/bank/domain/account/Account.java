package com.naegwon.bank.domain.account;

import com.naegwon.bank.domain.user.User;
import com.naegwon.bank.handler.ex.CustomApiException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 4)
    private Long number; //계좌번호

    @Column(nullable = false, length = 4)
    private Long password; //계좌 비밀번호

    @Column(nullable = false)
    private Long balance; //잔액(기본값 1000원)

    //항상 ORM 에서 FK의 주인은 Many Entity 쪽
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    @CreatedDate //Insert
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate //Insert, Update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Account(Long id, Long number, Long password, Long balance, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.password = password;
        this.balance = balance;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void checkOwner(Long userId){
//        String username = user.getUsername();
//        System.out.println("테스트 = " + username);
        
        //Lazy 로딩이어도 id를 조회할 때는 select 쿼리가 날라가지 않는다
        //왜냐하면 Account table에 user_id는 가지고 있기 때문이다.
        if (!Objects.equals(user.getId(), userId)) {
            throw new CustomApiException("계좌 소유자가 아닙니다.");
        }
    }

    public void deposit(Long amount) {
        balance += amount;
    }

    public void checkSamePassword(Long password) {
        if (this.password.longValue() != password.longValue()) {
            throw new CustomApiException("계좌 비밀번호 검증에 실패했습니다.");
        }
    }

    public void checkBalance(Long amount) {
        if (this.balance < amount) {
            throw new CustomApiException("계좌 잔액이 부족합니다");
        }
    }

    public void withdraw(Long amount) {
        checkBalance(amount);
        balance -= amount;
    }
}
