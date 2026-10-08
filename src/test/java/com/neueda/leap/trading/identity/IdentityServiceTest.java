package com.neueda.leap.trading.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class IdentityServiceTest {
    private IdentityMapper mapper;
    private PasswordEncoder passwords;
    private IdentityService service;

    @BeforeEach void setUp() {
        mapper=mock(IdentityMapper.class);
        passwords=mock(PasswordEncoder.class);
        service=new IdentityService(mapper,passwords);
    }

    @Test void registrationLowercasesEmailAndEncodesPassword() {
        when(passwords.encode("longsecret123")).thenReturn("encoded");
        ClientRegistrationResponse result=service.registerClient(
                new ClientRegistrationRequest("TEST@EXAMPLE.COM","Test","Client","RETAIL","longsecret123"));
        assertEquals("test@example.com",result.email());
        verify(passwords).encode("longsecret123");
        verify(mapper).insertInitialCash(result.accountId());
    }

    @Test void missingUserCannotBeActivated() {
        UUID id=UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,()->service.setActive(id,true));
        verify(mapper,never()).setActive(any(),anyBoolean());
    }

    @Test void missingUserCannotBeAssignedRoles() {
        UUID id=UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,()->service.replaceRoles(id,List.of("ADMIN")));
        verify(mapper,never()).deleteRoles(any());
    }

    @Test void unknownUserLookupThrows() {
        assertThrows(IllegalArgumentException.class,()->service.user(UUID.randomUUID()));
    }

    @Test void cannotCreateAdminWithoutRoles() {
        assertThrows(IllegalArgumentException.class,
                ()->service.createAdmin(new CreateAdminUserRequest("admin@example.com","Admin","User","longsecret123",List.of())));
        verify(mapper,never()).insertUser(any(),any(),any(),any(),any(),any());
    }

    @Test void missingClientLookupThrows() {
        assertThrows(IllegalArgumentException.class,()->service.client(UUID.randomUUID()));
    }
}
