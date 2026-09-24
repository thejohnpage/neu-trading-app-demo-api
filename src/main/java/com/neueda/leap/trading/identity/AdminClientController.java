package com.neueda.leap.trading.identity;import java.util.*;import io.swagger.v3.oas.annotations.Operation;import io.swagger.v3.oas.annotations.tags.Tag;import org.springframework.web.bind.annotation.*;
@Tag(name="Admin Clients",description="Administrative client profile and classification management")
@RestController @RequestMapping("/api/v1/admin/clients")
public class AdminClientController{
 private final IdentityService identity;public AdminClientController(IdentityService identity){this.identity=identity;}
 @Operation(summary="List clients") @GetMapping public List<ClientAdminResponse> clients(){return identity.clients();}
 @Operation(summary="Get client") @GetMapping("/{id}") public ClientAdminResponse client(@PathVariable UUID id){return identity.client(id);}
 @Operation(summary="Update client") @PutMapping("/{id}") public ClientAdminResponse update(@PathVariable UUID id,@RequestBody UpdateClientRequest r){return identity.updateClient(id,r);}
 @Operation(summary="List client segments") @GetMapping("/segments") public List<ClientSegmentResponse> segments(){return identity.clientSegments();}
}
