package com.neueda.leap.trading.identity;
import java.time.Instant; import java.util.*; import org.springframework.dao.DuplicateKeyException; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service public class IdentityService {
 private static final Set<String> ADMIN_ROLES=Set.of("SUPER_ADMIN","ADMIN_OPERATIONS","ADMIN_REPORTING","ADMIN","TRADING_OPERATIONS","RISK","COMPLIANCE","FINANCE","ANALYST");
 private final IdentityMapper mapper; private final PasswordEncoder passwords;
 public IdentityService(IdentityMapper mapper,PasswordEncoder passwords){this.mapper=mapper;this.passwords=passwords;}
 public List<UserResponse> users(){return mapper.users().stream().map(this::response).toList();}
 public UserResponse user(UUID id){return response(mapper.user(id).orElseThrow(()->new IllegalArgumentException("User not found")));}
 @Transactional public UserResponse createAdmin(CreateAdminUserRequest r){validateRoles(r.roles());UUID id=UUID.randomUUID();Instant now=Instant.now();mapper.insertUser(id,r.email().toLowerCase(),passwords.encode(r.password()),r.firstName(),r.lastName(),now);replaceRoles(id,r.roles());return user(id);}
 @Transactional public UserResponse setActive(UUID id,boolean active){requireUser(id);mapper.setActive(id,active);return user(id);}
 @Transactional public UserResponse replaceRoles(UUID id,List<String> roles){requireUser(id);validateRoles(roles);mapper.deleteRoles(id);for(String role:roles)if(mapper.insertRole(id,role)!=1)throw new IllegalArgumentException("Role not found: "+role);return user(id);}
 @Transactional public ClientRegistrationResponse registerClient(ClientRegistrationRequest r){UUID clientId=UUID.randomUUID(),accountId=UUID.randomUUID();String email=r.email().toLowerCase(),number="ACC-"+accountId.toString().substring(0,8).toUpperCase();try{mapper.insertClient(clientId,email,passwords.encode(r.password()),r.firstName(),r.lastName(),r.clientSegment());mapper.insertAccount(accountId,clientId,number);mapper.insertInitialCash(accountId);}catch(DuplicateKeyException e){throw new IllegalArgumentException("Email is already registered");}return new ClientRegistrationResponse(clientId,accountId,number,email);}
 private UserResponse response(IdentityUserRow u){return new UserResponse(u.getUserId(),u.getEmail(),u.getFirstName(),u.getLastName(),u.isActive(),mapper.roles(u.getUserId()),u.getCreatedAt(),u.getUpdatedAt());}
 private void validateRoles(List<String> roles){if(roles==null||roles.isEmpty())throw new IllegalArgumentException("Administrative user requires at least one role");if(roles.stream().anyMatch(r->!ADMIN_ROLES.contains(r)))throw new IllegalArgumentException("Unknown administrative role");}
 private void requireUser(UUID id){if(mapper.userCount(id)==0)throw new IllegalArgumentException("User not found");}
}