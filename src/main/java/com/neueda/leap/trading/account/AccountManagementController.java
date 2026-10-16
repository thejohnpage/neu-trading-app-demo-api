package com.neueda.leap.trading.account;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/me/accounts")
@Tag(name="Client Account Management")
public class AccountManagementController {
    private final AccountManagementService service;
    public AccountManagementController(AccountManagementService service) { this.service=service; }

    @GetMapping("/{id}")
    @Operation(summary="Get one owned account")
    public AccountResponse get(@PathVariable UUID id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary="Open an active brokerage account")
    public AccountResponse create(@RequestBody CreateAccountRequest request) { return service.create(request); }

    @PatchMapping("/{id}")
    @Operation(summary="Rename an owned account")
    public AccountResponse update(@PathVariable UUID id, @RequestBody UpdateAccountRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary="Close an empty account while retaining its history")
    public AccountResponse close(@PathVariable UUID id) { return service.close(id); }
}
