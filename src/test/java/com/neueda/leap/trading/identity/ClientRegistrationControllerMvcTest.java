package com.neueda.leap.trading.identity;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
class ClientRegistrationControllerMvcTest {
 private IdentityService identity;
 private MockMvc mvc;
 @BeforeEach void setup(){
  identity=mock(IdentityService.class);
  mvc=MockMvcBuilders.standaloneSetup(new ClientRegistrationController(identity)).build();
 }
 @Test void validRegistrationReturnsCreated() throws Exception {
  UUID clientId=UUID.randomUUID(),accountId=UUID.randomUUID();
  when(identity.registerClient(any())).thenReturn(
   new ClientRegistrationResponse(clientId,accountId,"ACC-12345678","test@example.com"));
  mvc.perform(post("/api/v1/registration/client").contentType("application/json")
   .content("{\"email\":\"test@example.com\",\"firstName\":\"Test\",\"lastName\":\"User\",\"clientSegment\":\"RETAIL\",\"password\":\"longsecret123\"}"))
   .andExpect(status().isCreated()).andExpect(jsonPath("$.email").value("test@example.com"));
  verify(identity).registerClient(any());
 }
 @Test void invalidJsonDoesNotInvokeRegistration() throws Exception {
  mvc.perform(post("/api/v1/registration/client").contentType("application/json").content("{"))
   .andExpect(status().isBadRequest());
  verifyNoInteractions(identity);
 }
}
