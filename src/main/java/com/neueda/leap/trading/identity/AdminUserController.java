package com.neueda.leap.trading.identity;

import java.util.List;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
    private final IdentityService identity;
    public AdminUserController(IdentityService identity){this.identity=identity;}

    @GetMapping public List<UserResponse> users(){return identity.users();}
    @GetMapping("/{id}") public UserResponse user(@PathVariable UUID id){return identity.user(id);}
    @PostMapping public UserResponse create(@Valid @RequestBody CreateAdminUserRequest request){return identity.createAdmin(request);}
    @PatchMapping("/{id}/status") public UserResponse status(@PathVariable UUID id,@RequestParam boolean active){return identity.setActive(id,active);}
    @PutMapping("/{id}/roles") public UserResponse roles(@PathVariable UUID id,@RequestBody List<String> roles){return identity.replaceRoles(id,roles);}
}
