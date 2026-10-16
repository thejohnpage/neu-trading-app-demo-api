package com.neueda.leap.trading.account;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.neueda.leap.trading.api.ApiExceptionHandler;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountManagementControllerMvcTest {
    private AccountManagementService service;
    private MockMvc mvc;

    @BeforeEach void setup() {
        service = mock(AccountManagementService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AccountManagementController(service))
            .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test void createsAccountWithSelectedCurrency() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any())).thenReturn(new AccountResponse(id, "ACC-12345678", "GBP", "ACTIVE", "UK Trading", "INDIVIDUAL"));
        mvc.perform(post("/api/v1/me/accounts")
                .contentType("application/json")
                .content("{\"accountName\":\"UK Trading\",\"accountType\":\"INDIVIDUAL\",\"baseCurrency\":\"GBP\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.baseCurrency").value("GBP"))
            .andExpect(jsonPath("$.accountName").value("UK Trading"));
    }

    @Test void closesAccount() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.close(id)).thenReturn(new AccountResponse(id, "ACC-12345678", "USD", "CLOSED", "Primary", "INDIVIDUAL"));
        mvc.perform(delete("/api/v1/me/accounts/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CLOSED"));
    }
}
