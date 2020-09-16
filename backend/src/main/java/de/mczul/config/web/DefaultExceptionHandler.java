package de.mczul.config.web;

import de.mczul.config.model.ValidationErrorResponse;
import de.mczul.config.model.ValidationErrorResponse.ValidationErrorResponseBuilder;
import de.mczul.config.model.Violation;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class DefaultExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseBody
//    ResponseEntity<String> handleInvalidArguments(MethodArgumentNotValidException ex) {
//        return ResponseEntity
//                .badRequest()
//                .contentType(MediaType.TEXT_PLAIN)
//                .body(ex.getLocalizedMessage());
//    }


    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e) {
        ValidationErrorResponseBuilder builder = ValidationErrorResponse.builder();
        e.getConstraintViolations().stream().map(violation -> Violation.builder()
                .fieldName(violation.getPropertyPath().toString())
                .message(violation.getMessage())
                .build()).forEach(builder::violation);
        return builder.build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        ValidationErrorResponseBuilder builder = ValidationErrorResponse.builder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            builder.violation(
                    Violation.builder()
                            .fieldName(fieldError.getField())
                            .message(fieldError.getDefaultMessage())
                            .build()
            );
        }
        return builder.build();
    }
}
