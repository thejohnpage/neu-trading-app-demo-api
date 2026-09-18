package com.neueda.leap.trading.identity;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/registration")
public class ClientRegistrationController {
    private final IdentityService identity;
    public ClientRegistrationController(IdentityService identity){this.identity=identity;}

    @PostMapping("/client")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientRegistrationResponse register(@Valid @RequestBody ClientRegistrationRequest request){
        return identity.registerClient(request);
    }
}
