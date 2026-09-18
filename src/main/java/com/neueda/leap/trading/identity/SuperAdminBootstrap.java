package com.neueda.leap.trading.identity;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SuperAdminBootstrap implements ApplicationRunner {
    private final JdbcTemplate jdbc; private final PasswordEncoder passwords;
    private final String email; private final String password;

    public SuperAdminBootstrap(JdbcTemplate jdbc, PasswordEncoder passwords,
            @Value("${app.bootstrap.super-admin.email:superadmin@trading.demo}") String email,
            @Value("${app.bootstrap.super-admin.password:}") String password) {
        this.jdbc=jdbc; this.passwords=passwords; this.email=email.toLowerCase(); this.password=password;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        Integer count=jdbc.queryForObject("SELECT COUNT(*) FROM identity.users WHERE email=?",Integer.class,email);
        if(count!=null && count>0) return;
        if(password==null || password.isBlank())
            throw new IllegalStateException("SUPER_ADMIN_PASSWORD (app.bootstrap.super-admin.password) must be set for first startup");
        UUID id=UUID.randomUUID();
        jdbc.update("""
            INSERT INTO identity.users(user_id,email,password_hash,first_name,last_name,active)
            VALUES (?,?,?,'Super','Admin',true)
            """,id,email,passwords.encode(password));
        jdbc.update("""
            INSERT INTO identity.user_roles(user_id,role_id)
            SELECT ?,role_id FROM identity.roles WHERE role_name='SUPER_ADMIN'
            """,id);
    }
}
