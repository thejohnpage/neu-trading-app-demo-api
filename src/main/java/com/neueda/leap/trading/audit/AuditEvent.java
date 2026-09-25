package com.neueda.leap.trading.audit;
import java.time.Instant;import java.util.UUID;
/** Immutable audit event returned to administrative clients. */
public class AuditEvent {
 private UUID auditEventId,actorId;private Instant occurredAt;private String actorType,actorEmail,action,resourceType,resourceId,outcome,details;
 public AuditEvent(){}
 public UUID getAuditEventId(){return auditEventId;}public void setAuditEventId(UUID v){auditEventId=v;}public Instant getOccurredAt(){return occurredAt;}public void setOccurredAt(Instant v){occurredAt=v;}public String getActorType(){return actorType;}public void setActorType(String v){actorType=v;}public UUID getActorId(){return actorId;}public void setActorId(UUID v){actorId=v;}public String getActorEmail(){return actorEmail;}public void setActorEmail(String v){actorEmail=v;}public String getAction(){return action;}public void setAction(String v){action=v;}public String getResourceType(){return resourceType;}public void setResourceType(String v){resourceType=v;}public String getResourceId(){return resourceId;}public void setResourceId(String v){resourceId=v;}public String getOutcome(){return outcome;}public void setOutcome(String v){outcome=v;}public String getDetails(){return details;}public void setDetails(String v){details=v;}
}
