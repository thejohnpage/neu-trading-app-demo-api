package com.neueda.leap.trading.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.ObjectMapper;

class AuthValidationFilterTest {
    private final AuthValidationFilter filter=new AuthValidationFilter(
            new ObjectMapper(),"http://127.0.0.1:1/validate");

    @AfterEach void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test void absentBearerHeaderPassesThroughWithoutAuthentication() throws Exception {
        MockHttpServletRequest req=new MockHttpServletRequest("GET","/api/v1/instruments");
        MockHttpServletResponse res=new MockHttpServletResponse();
        FilterChain chain=mock(FilterChain.class);
        filter.doFilter(req,res,chain);
        verify(chain).doFilter(any(),any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test void unsupportedAuthorizationSchemePassesThrough() throws Exception {
        MockHttpServletRequest req=new MockHttpServletRequest("GET","/api/v1/instruments");
        req.addHeader("Authorization","Basic abc");
        FilterChain chain=mock(FilterChain.class);
        filter.doFilter(req,new MockHttpServletResponse(),chain);
        verify(chain).doFilter(any(),any());
    }

    @Test void unavailableValidationServiceFailsClosed() throws Exception {
        MockHttpServletRequest req=new MockHttpServletRequest("GET","/api/v1/orders");
        req.addHeader("Authorization","Bearer sample-token");
        MockHttpServletResponse res=new MockHttpServletResponse();
        FilterChain chain=mock(FilterChain.class);
        filter.doFilter(req,res,chain);
        assertEquals(503,res.getStatus());
        verifyNoInteractions(chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
