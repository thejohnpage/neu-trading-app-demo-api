package com.neueda.leap.trading.identity;
import java.time.Instant; import java.util.*; import org.apache.ibatis.annotations.*;
/** MyBatis mapper for identities, roles, registration, and bootstrap. */
@Mapper public interface IdentityMapper {
 @Select("SELECT user_id userId,email,first_name firstName,last_name lastName,active,created_at createdAt,updated_at updatedAt FROM identity.users ORDER BY created_at,email") List<IdentityUserRow> users();
 @Select("SELECT user_id userId,email,first_name firstName,last_name lastName,active,created_at createdAt,updated_at updatedAt FROM identity.users WHERE user_id=#{id}") Optional<IdentityUserRow> user(UUID id);
 @Select("SELECT r.role_name FROM identity.user_roles ur JOIN identity.roles r ON r.role_id=ur.role_id WHERE ur.user_id=#{id} ORDER BY r.role_name") List<String> roles(UUID id);
 @Select("SELECT COUNT(*) FROM identity.users WHERE user_id=#{id}") int userCount(UUID id);
 @Select("SELECT COUNT(*) FROM identity.users WHERE email=#{email}") int emailCount(String email);
 @Insert("INSERT INTO identity.users(user_id,email,password_hash,first_name,last_name,active,created_at,updated_at) VALUES(#{id},#{email},#{hash},#{first},#{last},true,#{now},#{now})") int insertUser(@Param("id")UUID id,@Param("email")String email,@Param("hash")String hash,@Param("first")String first,@Param("last")String last,@Param("now")Instant now);
 @Update("UPDATE identity.users SET active=#{active},updated_at=CURRENT_TIMESTAMP WHERE user_id=#{id}") int setActive(@Param("id")UUID id,@Param("active")boolean active);
 @Delete("DELETE FROM identity.user_roles WHERE user_id=#{id}") int deleteRoles(UUID id);
 @Insert("INSERT INTO identity.user_roles(user_id,role_id) SELECT #{id},role_id FROM identity.roles WHERE role_name=#{role}") int insertRole(@Param("id")UUID id,@Param("role")String role);
 @Insert("INSERT INTO identity.clients(client_id,email,password_hash,first_name,last_name,client_segment,active) VALUES(#{id},#{email},#{hash},#{first},#{last},#{segment},true)") int insertClient(@Param("id")UUID id,@Param("email")String email,@Param("hash")String hash,@Param("first")String first,@Param("last")String last,@Param("segment")String segment);
 @Insert("INSERT INTO trading.accounts(account_id,client_id,account_number,base_currency,status) VALUES(#{accountId},#{clientId},#{number},'USD','ACTIVE')") int insertAccount(@Param("accountId")UUID accountId,@Param("clientId")UUID clientId,@Param("number")String number);
 @Insert("INSERT INTO trading.cash_balances(account_id,currency,balance) VALUES(#{accountId},'USD',0)") int insertInitialCash(UUID accountId);
 @Insert("INSERT INTO identity.users(user_id,email,password_hash,first_name,last_name,active) VALUES(#{id},#{email},#{hash},'Super','Admin',true)") int insertSuperAdmin(@Param("id")UUID id,@Param("email")String email,@Param("hash")String hash);
}
