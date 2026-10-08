package com.neueda.leap.trading.audit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class AuditTrailFilterTest {
    private AuditMapper mapper;
    private AuditTrailFilter filter;
    private FilterChain chain;
    private UUID actorId;

    @BeforeEach void setUp() {
        mapper=mock(AuditMapper.class);
        filter=new AuditTrailFilter(mapper);
        chain=mock(FilterChain.class);
        actorId=UUID.randomUUID();
    }

    private MockHttpServletRequest request(String method,String path) {
        MockHttpServletRequest req=new MockHttpServletRequest(method,path);
        req.setAttribute("authSubjectId",actorId.toString());
        req.setAttribute("authSubjectType","CLIENT");
        req.setAttribute("authSubjectEmail","client@example.com");
        return req;
    }

    @Test void unauthenticatedRequestDoesNotCreateAuditEvent() throws Exception {
        filter.doFilter(new MockHttpServletRequest("GET","/api/v1/orders"),
                new MockHttpServletResponse(),chain);
        verify(chain).doFilter(any(),any());
        verifyNoInteractions(mapper);
    }

    @Test void auditReadDoesNotRecursivelyAuditItself() throws Exception {
        filter.doFilter(request("GET","/api/v1/admin/audit/events"),
                new MockHttpServletResponse(),chain);
        verifyNoInteractions(mapper);
    }

    @Test void orderSubmissionCreatesSuccessAuditEvent() throws Exception {
        MockHttpServletRequest req=request("POST","/api/v1/orders");
        filter.doFilter(req,new MockHttpServletResponse(),chain);
        ArgumentCaptor<AuditEvent> captor=ArgumentCaptor.forClass(AuditEvent.class);
        verify(mapper).insert(captor.capture());
        AuditEvent event=captor.getValue();
        assertEquals(actorId,event.getActorId());
        assertEquals("CLIENT",event.getActorType());
        assertEquals("client@example.com",event.getActorEmail());
        assertEquals("ORDER_SUBMITTED",event.getAction());
        assertEquals("ORDER",event.getResourceType());
        assertEquals("SUCCESS",event.getOutcome());
        assertNotNull(event.getOccurredAt());
    }

    @Test void unsuccessfulCashWithdrawalIsAuditedAsFailure() throws Exception {
        MockHttpServletRequest req=request("POST","/api/v1/me/cash/withdrawals");
        MockHttpServletResponse res=new MockHttpServletResponse();
        res.setStatus(422);
        filter.doFilter(req,res,chain);
        ArgumentCaptor<AuditEvent> captor=ArgumentCaptor.forClass(AuditEvent.class);
        verify(mapper).insert(captor.capture());
        assertEquals("CASH_WITHDRAWAL",captor.getValue().getAction());
        assertEquals("CASH",captor.getValue().getResourceType());
        assertEquals("FAILURE",captor.getValue().getOutcome());
    }

    @Test void invalidActorIdDoesNotBreakRequest() throws Exception {
        MockHttpServletRequest req=request("GET","/api/v1/orders");
        req.setAttribute("authSubjectId","not-a-uuid");
        assertDoesNotThrow(()->filter.doFilter(req,new MockHttpServletResponse(),chain));
        verifyNoInteractions(mapper);
    }

    @Test void auditDatabaseFailureDoesNotBreakRequest() throws Exception {
        doThrow(new IllegalStateException("database unavailable")).when(mapper).insert(any());
        assertDoesNotThrow(()->filter.doFilter(request("GET","/api/v1/orders"),
                new MockHttpServletResponse(),chain));
        verify(chain).doFilter(any(),any());
    }
}
