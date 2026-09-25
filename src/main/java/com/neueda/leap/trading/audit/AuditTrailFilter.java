package com.neueda.leap.trading.audit;
import java.io.IOException;import java.time.Instant;import java.util.UUID;import jakarta.servlet.*;import jakarta.servlet.http.*;import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
/** Records authenticated API activity after request processing. */
@Component public class AuditTrailFilter extends OncePerRequestFilter {
 private final AuditMapper audit;public AuditTrailFilter(AuditMapper audit){this.audit=audit;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  try{chain.doFilter(req,res);}finally{record(req,res);}
 }
 private void record(HttpServletRequest req,HttpServletResponse res){
  Object id=req.getAttribute("authSubjectId"),type=req.getAttribute("authSubjectType");if(id==null||type==null)return;
  String path=req.getRequestURI();if(path.startsWith("/api/v1/admin/audit"))return;
  try{AuditEvent e=new AuditEvent();e.setAuditEventId(UUID.randomUUID());e.setOccurredAt(Instant.now());e.setActorId(UUID.fromString(id.toString()));e.setActorType(type.toString());Object email=req.getAttribute("authSubjectEmail");e.setActorEmail(email==null?null:email.toString());e.setAction(action(req));e.setResourceType(resource(path));e.setResourceId(resourceId(path));e.setOutcome(res.getStatus()<400?"SUCCESS":"FAILURE");e.setDetails("{\"method\":\""+escape(req.getMethod())+"\",\"path\":\""+escape(path)+"\",\"status\":"+res.getStatus()+"}");audit.insert(e);}catch(Exception ignored){}
 }
 private String action(HttpServletRequest r){String m=r.getMethod(),p=r.getRequestURI();if(p.contains("/orders")&&"POST".equals(m))return "ORDER_SUBMITTED";if(p.contains("/cash/deposits"))return "CASH_DEPOSIT";if(p.contains("/cash/withdrawals"))return "CASH_WITHDRAWAL";if(p.contains("/cash/conversions"))return "FX_CONVERSION";if(p.contains("/admin/users")&&"POST".equals(m))return "ADMIN_USER_CREATED";if(p.contains("/admin/users")&&"PATCH".equals(m))return "ADMIN_USER_STATUS_CHANGED";if(p.contains("/admin/users")&&"PUT".equals(m))return "ADMIN_USER_ROLES_CHANGED";return m+" "+p;}
 private String resource(String p){if(p.contains("/orders"))return "ORDER";if(p.contains("/cash"))return "CASH";if(p.contains("/admin/users"))return "USER";if(p.contains("/positions"))return "POSITION";if(p.contains("/instruments"))return "INSTRUMENT";if(p.contains("/reports"))return "REPORT";return "API";}
 private String resourceId(String p){String[] s=p.split("/");return s.length>0?s[s.length-1]:null;}private String escape(String s){return s.replace("\\","\\\\").replace("\"","\\\"");}
}
