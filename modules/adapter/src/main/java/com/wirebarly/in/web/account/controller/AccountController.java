package com.wirebarly.in.web.account.controller;

import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.in.web.WebAdapter;
import com.wirebarly.in.web.account.request.AccountCreateRequest;
import com.wirebarly.in.web.account.request.AccountDepositRequest;
import com.wirebarly.in.web.account.request.AccountWithdrawRequest;
import com.wirebarly.in.web.account.response.AccountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@WebAdapter
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountUseCase accountUseCase;

    @PostMapping("/new")
    public ResponseEntity<AccountResponse> registerAccount(@Valid @RequestBody AccountCreateRequest request) {
        return ResponseEntity.ok(AccountResponse.from(
                accountUseCase.register(request.toCommand())
        ));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("accountId") Long accountId) {
        accountUseCase.remove(accountId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable("accountId") Long accountId,
                                        @Valid @RequestBody AccountDepositRequest request) {
        accountUseCase.deposit(accountId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable("accountId") Long accountId,
                                        @Valid @RequestBody AccountWithdrawRequest request) {
        accountUseCase.withdraw(accountId, request.toCommand());
        return ResponseEntity.ok().build();
    }
}
