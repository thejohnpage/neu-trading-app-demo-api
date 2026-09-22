package com.neueda.leap.trading.identity;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;

@Tag(name="Client Registration",description="Demo client registration and initial trading-account creation")
@RestController @RequestMapping("/api/v1/registration")
public class ClientRegistrationController {
 private final IdentityService identity; public ClientRegistrationController(IdentityService identity){this.identity=identity;}
 @Operation(summary="Register a client",description="Creates a client identity and the associated initial trading account.")
 @PostMapping("/client") @ResponseStatus(HttpStatus.CREATED)
 public ClientRegistrationResponse register(@Valid @RequestBody ClientRegistrationRequest request){return identity.registerClient(request);}
}
