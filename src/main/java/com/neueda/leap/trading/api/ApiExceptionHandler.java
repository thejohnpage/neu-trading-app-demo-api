package com.neueda.leap.trading.api;

import java.time.Instant;
import java.util.Map;
import com.neueda.leap.trading.order.OrderRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<Map<String,Object>> handleNotFound(ResourceNotFoundException e){
        return error(HttpStatus.NOT_FOUND,"Not Found",e.getMessage());
    }
    @ExceptionHandler(OrderRejectedException.class)
    ResponseEntity<Map<String,Object>> handleRejected(OrderRejectedException e){
        return error(HttpStatus.UNPROCESSABLE_ENTITY,"Order Rejected",e.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException e){
        String message=e.getBindingResult().getFieldErrors().stream().findFirst()
                .map(f -> f.getField()+": "+f.getDefaultMessage()).orElse("Invalid request");
        return error(HttpStatus.BAD_REQUEST,"Bad Request",message);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,Object>> handleBadRequest(IllegalArgumentException e){
        return error(HttpStatus.BAD_REQUEST,"Bad Request",e.getMessage());
    }
    private ResponseEntity<Map<String,Object>> error(HttpStatus status,String error,String message){
        return ResponseEntity.status(status).body(Map.of("timestamp",Instant.now().toString(),
                "status",status.value(),"error",error,"message",message));
    }
}
