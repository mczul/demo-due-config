package de.mczul.config.web;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    ResponseEntity<String> handleInvalidArguments(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .badRequest()
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getLocalizedMessage());
    }

}
