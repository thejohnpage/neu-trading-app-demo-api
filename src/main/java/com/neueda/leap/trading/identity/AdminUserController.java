package com.neueda.leap.trading.identity;

import java.util.List; import java.util.UUID; import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name="Admin Users",description="Internal-user administration, status and role assignment")
/** Exposes internal-user administration, status management, and role assignment. */
@RestController @RequestMapping("/api/v1/admin/users")
public class AdminUserController {
 private final IdentityService identity; public AdminUserController(IdentityService identity){this.identity=identity;}
 /** List admin users. */
 @Operation(summary="List admin users",description="Returns internal platform users.") @GetMapping public List<UserResponse> users(){return identity.users();}
 /** Get admin user. */
 @Operation(summary="Get admin user",description="Returns one internal platform user.") @GetMapping("/{id}") public UserResponse user(@PathVariable UUID id){return identity.user(id);}
 /** Create admin user. */
 @Operation(summary="Create admin user",description="Creates an internal platform user.") @PostMapping public UserResponse create(@Valid @RequestBody CreateAdminUserRequest request){return identity.createAdmin(request);}
 /** Change user status. */
 @Operation(summary="Change user status",description="Activates or deactivates an internal platform user.") @PatchMapping("/{id}/status") public UserResponse status(@PathVariable UUID id,@RequestParam boolean active){return identity.setActive(id,active);}
 /** Replace user roles. */
 @Operation(summary="Replace user roles",description="Replaces the complete role set assigned to an internal platform user.") @PutMapping("/{id}/roles") public UserResponse roles(@PathVariable UUID id,@RequestBody List<String> roles){return identity.replaceRoles(id,roles);}
}
