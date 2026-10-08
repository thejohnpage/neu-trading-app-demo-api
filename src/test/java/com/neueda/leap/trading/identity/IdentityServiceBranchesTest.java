package com.neueda.leap.trading.identity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
class IdentityServiceBranchesTest {
 private IdentityMapper mapper;
 private IdentityService service;
 private UUID id;
 @BeforeEach void setup() {
  mapper=mock(IdentityMapper.class);
  service=new IdentityService(mapper,mock(PasswordEncoder.class));
  id=UUID.randomUUID();
 }
 @Test void cannotDisableLastActiveSuperAdmin() {
  when(mapper.userCount(id)).thenReturn(1);
  when(mapper.isSuperAdmin(id)).thenReturn(1);
  when(mapper.activeSuperAdminCount()).thenReturn(1);
  assertThrows(IllegalArgumentException.class,()->service.setActive(id,false));
  verify(mapper,never()).setActive(any(),anyBoolean());
 }
 @Test void cannotAssignUnknownRole() {
  when(mapper.userCount(id)).thenReturn(1);
  assertThrows(IllegalArgumentException.class,()->service.replaceRoles(id,List.of("NOT_A_ROLE")));
  verify(mapper,never()).deleteRoles(any());
 }
 @Test void invalidRoleInsertionThrows() {
  when(mapper.userCount(id)).thenReturn(1);
  when(mapper.roleNameCount("ADMIN")).thenReturn(1);
  assertThrows(IllegalArgumentException.class,()->service.replaceRoles(id,List.of("ADMIN")));
 }
 @Test void duplicateClientEmailIsRejected() {
  doThrow(new DuplicateKeyException("duplicate")).when(mapper)
   .insertClient(any(),any(),any(),any(),any(),any());
  assertEquals("Email is already registered",assertThrows(IllegalArgumentException.class,
   ()->service.registerClient(new ClientRegistrationRequest(
    "test@example.com","Test","User","RETAIL","longsecret123"))).getMessage());
  verify(mapper,never()).insertAccount(any(),any(),any());
 }
 @Test void duplicateRoleNameRejected() {
  when(mapper.roleNameCount("ADMIN")).thenReturn(1);
  assertThrows(IllegalArgumentException.class,
   ()->service.createRole(new CreateRoleRequest("ADMIN","description",List.of())));
  verify(mapper,never()).insertRoleRow(any());
 }
 @Test void unknownUserCannotReadCapabilities() {
  assertThrows(IllegalArgumentException.class,()->service.capabilities(id));
  verify(mapper,never()).capabilities(any());
 }
 @Test void missingProfileReturnsEmptyProfileProjection() {
  when(mapper.userCount(id)).thenReturn(1);
  when(mapper.profile(id)).thenReturn(Optional.empty());
  assertEquals(id,service.profile(id).userId());
 }
}
