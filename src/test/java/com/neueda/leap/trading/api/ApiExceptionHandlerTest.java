package com.neueda.leap.trading.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import com.neueda.leap.trading.order.OrderRejectedException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.List;

class ApiExceptionHandlerTest {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test void missingResourceReturns404() {
        var result=handler.handleNotFound(new ResourceNotFoundException("Missing instrument"));
        assertEquals(HttpStatus.NOT_FOUND,result.getStatusCode());
        assertEquals("Missing instrument",result.getBody().get("message"));
        assertEquals(404,result.getBody().get("status"));
        assertNotNull(result.getBody().get("timestamp"));
    }

    @Test void rejectedOrderReturns422() {
        var result=handler.handleRejected(new OrderRejectedException("Insufficient cash"));
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,result.getStatusCode());
        assertEquals("Order Rejected",result.getBody().get("error"));
    }

    @Test void invalidArgumentReturns400() {
        var result=handler.handleBadRequest(new IllegalArgumentException("Invalid currency"));
        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        assertEquals("Invalid currency",result.getBody().get("message"));
    }

    @Test void validationUsesFirstFieldError() {
        BindingResult binding=mock(BindingResult.class);
        MethodArgumentNotValidException exception=mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(binding);
        when(binding.getFieldErrors()).thenReturn(List.of(new FieldError("request","amount","must be positive")));
        var result=handler.handleValidation(exception);
        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
        assertEquals("amount: must be positive",result.getBody().get("message"));
    }

    @Test void validationWithoutFieldErrorsUsesFallback() {
        BindingResult binding=mock(BindingResult.class);
        MethodArgumentNotValidException exception=mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(binding);
        when(binding.getFieldErrors()).thenReturn(List.of());
        var result=handler.handleValidation(exception);
        assertEquals("Invalid request",result.getBody().get("message"));
    }
}
