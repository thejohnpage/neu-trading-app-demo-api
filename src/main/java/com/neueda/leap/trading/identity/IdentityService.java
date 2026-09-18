package com.neueda.leap.trading.identity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IdentityService {
    private static final Set<String> ADMIN_ROLES = Set.of(
            "SUPER_ADMIN","ADMIN_OPERATIONS","ADMIN_REPORTING","ADMIN",
            "TRADING_OPERATIONS","RISK","COMPLIANCE","FINANCE","ANALYST");
    private final JdbcTemplate jdbc; private final PasswordEncoder passwords;
    public IdentityService(JdbcTemplate jdbc,PasswordEncoder passwords){this.jdbc=jdbc;this.passwords=passwords;}

    public List<UserResponse> users(){return jdbc.query("""
        SELECT user_id,email,first_name,last_name,active,created_at,updated_at
        FROM identity.users ORDER BY created_at,email
        """,(rs,n)->user(rs.getObject("user_id",UUID.class),rs.getString("email"),rs.getString("first_name"),
        rs.getString("last_name"),rs.getBoolean("active"),rs.getTimestamp("created_at").toInstant(),
        rs.getTimestamp("updated_at").toInstant()));}

    public UserResponse user(UUID id){return jdbc.query("""
        SELECT user_id,email,first_name,last_name,active,created_at,updated_at
        FROM identity.users WHERE user_id=?
        """,rs->{if(!rs.next())throw new IllegalArgumentException("User not found");
        return user(id,rs.getString("email"),rs.getString("first_name"),rs.getString("last_name"),
        rs.getBoolean("active"),rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant());},id);}

    @Transactional public UserResponse createAdmin(CreateAdminUserRequest r){
        validateRoles(r.roles()); UUID id=UUID.randomUUID(); Instant now=Instant.now();
        jdbc.update("""INSERT INTO identity.users(user_id,email,password_hash,first_name,last_name,active,created_at,updated_at)
        VALUES (?,?,?,?,?,true,?,?)""",id,r.email().toLowerCase(),passwords.encode(r.password()),r.firstName(),r.lastName(),Timestamp.from(now),Timestamp.from(now));
        replaceRoles(id,r.roles()); return user(id);
    }

    @Transactional public UserResponse setActive(UUID id,boolean active){requireUser(id);jdbc.update("UPDATE identity.users SET active=?,updated_at=CURRENT_TIMESTAMP WHERE user_id=?",active,id);return user(id);}

    @Transactional public UserResponse replaceRoles(UUID id,List<String> roles){
        requireUser(id); validateRoles(roles);
        jdbc.update("DELETE FROM identity.user_roles WHERE user_id=?",id);
        for(String role:roles){int n=jdbc.update("""INSERT INTO identity.user_roles(user_id,role_id)
        SELECT ?,role_id FROM identity.roles WHERE role_name=?""",id,role);if(n!=1)throw new IllegalArgumentException("Role not found: "+role);}
        return user(id);
    }

    @Transactional public ClientRegistrationResponse registerClient(ClientRegistrationRequest r){
        UUID clientId=UUID.randomUUID(),accountId=UUID.randomUUID(); String email=r.email().toLowerCase();
        String accountNumber="ACC-"+accountId.toString().substring(0,8).toUpperCase();
        try{
            jdbc.update("""INSERT INTO identity.clients(client_id,email,password_hash,first_name,last_name,client_segment,active)
            VALUES (?,?,?,?,?,?,true)""",clientId,email,passwords.encode(r.password()),r.firstName(),r.lastName(),r.clientSegment());
            jdbc.update("""INSERT INTO trading.accounts(account_id,client_id,account_number,base_currency,status)
            VALUES (?,?,?,'USD','ACTIVE')""",accountId,clientId,accountNumber);
            jdbc.update("INSERT INTO trading.cash_balances(account_id,currency,balance) VALUES (?,'USD',0)",accountId);
        }catch(DuplicateKeyException e){throw new IllegalArgumentException("Email is already registered");}
        return new ClientRegistrationResponse(clientId,accountId,accountNumber,email);
    }

    private UserResponse user(UUID id,String email,String first,String last,boolean active,Instant created,Instant updated){
        List<String> roles=jdbc.queryForList("""SELECT r.role_name FROM identity.user_roles ur
        JOIN identity.roles r ON r.role_id=ur.role_id WHERE ur.user_id=? ORDER BY r.role_name""",String.class,id);
        return new UserResponse(id,email,first,last,active,roles,created,updated);
    }
    private void validateRoles(List<String> roles){if(roles==null||roles.isEmpty())throw new IllegalArgumentException("Administrative user requires at least one role");if(roles.stream().anyMatch(r->!ADMIN_ROLES.contains(r)))throw new IllegalArgumentException("Unknown administrative role");}
    private void requireUser(UUID id){Integer n=jdbc.queryForObject("SELECT COUNT(*) FROM identity.users WHERE user_id=?",Integer.class,id);if(n==null||n==0)throw new IllegalArgumentException("User not found");}
}
