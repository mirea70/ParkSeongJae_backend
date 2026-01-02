package com.wirebarley.in.web.account.controller;

import com.wirebarley.in.account.usecase.AccountUseCase;
import com.wirebarley.in.transfer.usecase.TransferUseCase;
import com.wirebarley.in.web.WebAdapter;
import com.wirebarley.in.web.account.request.AccountCreateRequest;
import com.wirebarley.in.web.account.request.AccountDepositRequest;
import com.wirebarley.in.web.account.request.AccountWithdrawRequest;
import com.wirebarley.in.web.account.request.TransferCreateRequest;
import com.wirebarley.in.web.account.response.AccountResponse;
import com.wirebarley.in.web.account.response.TransferResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@WebAdapter
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountUseCase accountUseCase;
    private final TransferUseCase transferUseCase;

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

    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<Void> transfer(@PathVariable("accountId") Long accountId,
                                         @Valid @RequestBody TransferCreateRequest request) {
        transferUseCase.transfer(request.toCommand(accountId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/transfers")
    public ResponseEntity<List<TransferResponse>> getTransfers(@PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(
                TransferResponse.from(
                        transferUseCase.getTransfers(accountId)
                )
        );
    }
}
