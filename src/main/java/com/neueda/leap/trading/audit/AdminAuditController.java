package com.neueda.leap.trading.audit;
import java.time.Instant;import java.util.*;import io.swagger.v3.oas.annotations.Operation;import io.swagger.v3.oas.annotations.tags.Tag;import org.springframework.web.bind.annotation.*;import com.neueda.leap.trading.api.ResourceNotFoundException;
/** Admin-only access to the append-only audit trail. */
@Tag(name="Admin Audit",description="Administrative security and business activity audit trail")
@RestController @RequestMapping("/api/v1/admin/audit")
public class AdminAuditController {
 private final AuditMapper audit;public AdminAuditController(AuditMapper audit){this.audit=audit;}
 @Operation(summary="Search audit events",description="Returns newest audit events with optional action, actor type, resource type and time filters.")
 @GetMapping public List<AuditEvent> search(@RequestParam(required=false)String action,@RequestParam(required=false)String actorType,@RequestParam(required=false)String resourceType,@RequestParam(required=false)Instant from,@RequestParam(required=false)Instant to,@RequestParam(defaultValue="100")int limit){return audit.search(action,actorType,resourceType,from,to,Math.min(Math.max(limit,1),500));}
 @Operation(summary="Get audit event") @GetMapping("/{id}") public AuditEvent find(@PathVariable UUID id){return audit.find(id).orElseThrow(()->new ResourceNotFoundException("Audit event not found"));}
}
