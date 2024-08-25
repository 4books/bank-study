package com.naegwon.bank.web;

import com.naegwon.bank.config.auth.LoginUser;
import com.naegwon.bank.dto.ResponseDto;
import com.naegwon.bank.dto.transaction.TransactionRespDto;
import com.naegwon.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.naegwon.bank.dto.transaction.TransactionRespDto.*;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<?> findTransactionList(@PathVariable("number") Long number,
                                                 @RequestParam(value = "gubun", defaultValue = "ALL") String gubun,
                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @AuthenticationPrincipal LoginUser loginUser) {
        TransactionListRespDto transactionListRespDto = transactionService.findTransactionList(loginUser.getUser().getId(), number,
                gubun, page);
//        return new ResponseEntity<>(new ResponseDto<>(1, "입출금목록 보기 성공", transactionListRespDto), HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto<>(1, "입출금목록보기 성공", transactionListRespDto));
    }
}
