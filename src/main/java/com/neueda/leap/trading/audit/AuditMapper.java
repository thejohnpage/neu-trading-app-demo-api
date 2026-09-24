package com.neueda.leap.trading.audit;
import java.time.Instant;import java.util.*;import org.apache.ibatis.annotations.*;
/** MyBatis mapper for the append-only administrative audit trail. */
@Mapper public interface AuditMapper {
 @Insert("INSERT INTO audit.audit_events(audit_event_id,occurred_at,actor_type,actor_id,actor_email,action,resource_type,resource_id,outcome,details) VALUES(#{auditEventId},#{occurredAt},#{actorType},#{actorId,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler},#{actorEmail},#{action},#{resourceType},#{resourceId},#{outcome},CAST(#{details} AS jsonb))") int insert(AuditEvent e);
 @Select("""<script>
 SELECT audit_event_id,occurred_at,actor_type,actor_id,actor_email,action,resource_type,resource_id,outcome,details::text details
 FROM audit.audit_events
 <where>
  <if test='action != null and action != ""'>AND action=#{action}</if>
  <if test='actorType != null and actorType != ""'>AND actor_type=#{actorType}</if>
  <if test='resourceType != null and resourceType != ""'>AND resource_type=#{resourceType}</if>
  <if test='from != null'>AND occurred_at &gt;= #{from}</if>
  <if test='to != null'>AND occurred_at &lt;= #{to}</if>
 </where>
 ORDER BY occurred_at DESC LIMIT #{limit}
 </script>""")
 List<AuditEvent> search(@Param("action")String action,@Param("actorType")String actorType,@Param("resourceType")String resourceType,@Param("from")Instant from,@Param("to")Instant to,@Param("limit")int limit);
 @Select("SELECT audit_event_id,occurred_at,actor_type,actor_id,actor_email,action,resource_type,resource_id,outcome,details::text details FROM audit.audit_events WHERE audit_event_id=#{id,typeHandler=com.neueda.leap.trading.config.PostgresUuidTypeHandler}") Optional<AuditEvent> find(UUID id);
}
