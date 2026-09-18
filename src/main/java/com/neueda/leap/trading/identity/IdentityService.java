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
            "SUPER_ADMIN", "ADMIN_OPERATIONS", "ADMIN_REPORTING",
            "ADMIN", "TRADING_OPERATIONS", "RISK", "COMPLIANCE", "FINANCE", "ANALYST");

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwords;

    public IdentityService(JdbcTemplate jdbc, PasswordEncoder passwords) {
        this.jdbc = jdbc;
        this.passwords = passwords;
    }

    public List<UserResponse> users() {
        return jdbc.query("""
            SELECT u.user_id,u.email,u.first_name,u.last_name,u.active,u.client_id,u.created_at,u.updated_at
            FROM identity.users u ORDER BY u.created_at,u.email
            """, (rs,n) -> user(rs.getObject("user_id",UUID.class), rs.getString("email"),
                    rs.getString("first_name"), rs.getString("last_name"), rs.getBoolean("active"),
                    rs.getObject("client_id",UUID.class), rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("updated_at").toInstant()));
    }

    public UserResponse user(UUID id) {
        return jdbc.query("""
            SELECT user_id,email,first_name,last_name,active,client_id,created_at,updated_at
            FROM identity.users WHERE user_id=?
            """, rs -> {
                if (!rs.next()) throw new IllegalArgumentException("User not found");
                return user(id,rs.getString("email"),rs.getString("first_name"),rs.getString("last_name"),
                        rs.getBoolean("active"),rs.getObject("client_id",UUID.class),
                        rs.getTimestamp("created_at").toInstant(),rs.getTimestamp("updated_at").toInstant());
            }, id);
    }

    @Transactional
    public UserResponse createAdmin(CreateAdminUserRequest request) {
        UUID id=UUID.randomUUID(); Instant now=Instant.now();
        jdbc.update("""
            INSERT INTO identity.users(user_id,email,password_hash,first_name,last_name,active,created_at,updated_at)
            VALUES (?,?,?,?,?,true,?,?)
            """,id,request.email().toLowerCase(),passwords.encode(request.password()),request.firstName(),
                request.lastName(),Timestamp.from(now),Timestamp.from(now));
        replaceRoles(id, request.roles()==null ? List.of() : request.roles());
        return user(id);
    }

    @Transactional
    public UserResponse setActive(UUID id, boolean active) {
        requireUser(id);
        jdbc.update("UPDATE identity.users SET active=?,updated_at=CURRENT_TIMESTAMP WHERE user_id=?",active,id);
        return user(id);
    }

    @Transactional
    public UserResponse replaceRoles(UUID id, List<String> roles) {
        requireUser(id);
        if (roles.stream().anyMatch(r -> !ADMIN_ROLES.contains(r)))
            throw new IllegalArgumentException("Unknown administrative role");
        jdbc.update("DELETE FROM identity.user_roles WHERE user_id=?",id);
        for(String role:roles) {
            int count=jdbc.update("""
                INSERT INTO identity.user_roles(user_id,role_id)
                SELECT ?,role_id FROM identity.roles WHERE role_name=?
                """,id,role);
            if(count!=1) throw new IllegalArgumentException("Role not found: "+role);
        }
        return user(id);
    }

    @Transactional
    public UserResponse associateClient(UUID id, UUID clientId) {
        requireUser(id);
        Integer exists=jdbc.queryForObject("SELECT COUNT(*) FROM identity.clients WHERE client_id=?",Integer.class,clientId);
        if(exists==null || exists==0) throw new IllegalArgumentException("Client not found");
        jdbc.update("UPDATE identity.users SET client_id=?,updated_at=CURRENT_TIMESTAMP WHERE user_id=?",clientId,id);
        jdbc.update("DELETE FROM identity.user_roles WHERE user_id=?",id);
        return user(id);
    }

    @Transactional
    public UserResponse clearClient(UUID id) {
        requireUser(id);
        jdbc.update("UPDATE identity.users SET client_id=NULL,updated_at=CURRENT_TIMESTAMP WHERE user_id=?",id);
        return user(id);
    }

    @Transactional
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        UUID clientId=UUID.randomUUID(), userId=UUID.randomUUID(), accountId=UUID.randomUUID();
        String email=request.email().toLowerCase();
        String accountNumber="ACC-"+accountId.toString().substring(0,8).toUpperCase();
        try {
            jdbc.update("""
                INSERT INTO identity.clients(client_id,email,first_name,last_name,client_segment,active)
                VALUES (?,?,?,?,?,true)
                """,clientId,email,request.firstName(),request.lastName(),request.clientSegment());
            jdbc.update("""
                INSERT INTO identity.users(user_id,email,password_hash,first_name,last_name,active,client_id)
                VALUES (?,?,?,?,?,true,?)
                """,userId,email,passwords.encode(request.password()),request.firstName(),request.lastName(),clientId);
            jdbc.update("""
                INSERT INTO trading.accounts(account_id,client_id,account_number,base_currency,status)
                VALUES (?,?,?,'USD','ACTIVE')
                """,accountId,clientId,accountNumber);
            jdbc.update("""
                INSERT INTO trading.cash_balances(account_id,currency,balance)
                VALUES (?,'USD',0)
                """,accountId);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("Email is already registered");
        }
        return new ClientRegistrationResponse(clientId,userId,accountId,accountNumber,email);
    }

    private UserResponse user(UUID id,String email,String first,String last,boolean active,UUID clientId,Instant created,Instant updated) {
        List<String> roles=jdbc.queryForList("""
            SELECT r.role_name FROM identity.user_roles ur JOIN identity.roles r ON r.role_id=ur.role_id
            WHERE ur.user_id=? ORDER BY r.role_name
            """,String.class,id);
        return new UserResponse(id,email,first,last,active,clientId,roles,created,updated);
    }

    private void requireUser(UUID id) {
        Integer count=jdbc.queryForObject("SELECT COUNT(*) FROM identity.users WHERE user_id=?",Integer.class,id);
        if(count==null || count==0) throw new IllegalArgumentException("User not found");
    }
}
