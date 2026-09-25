package com.neueda.leap.trading.identity;
import java.util.List;import io.swagger.v3.oas.annotations.Operation;import io.swagger.v3.oas.annotations.tags.Tag;import org.springframework.web.bind.annotation.*;
@Tag(name="Admin RBAC",description="Configurable administrative roles and capabilities")
@RestController @RequestMapping("/api/v1/admin/rbac")
public class AdminRbacController{
 private final IdentityService identity;public AdminRbacController(IdentityService identity){this.identity=identity;}
 @Operation(summary="List capabilities") @GetMapping("/capabilities") public List<String> capabilities(){return identity.allCapabilities();}
 @Operation(summary="List roles and capabilities") @GetMapping("/roles") public List<RoleResponse> roles(){return identity.roleDefinitions();}
 @Operation(summary="Create role") @PostMapping("/roles") public RoleResponse create(@RequestBody CreateRoleRequest r){return identity.createRole(r);}
 @Operation(summary="Update role capabilities") @PutMapping("/roles/{roleId}") public RoleResponse update(@PathVariable long roleId,@RequestBody CreateRoleRequest r){return identity.updateRole(roleId,r);}
}
