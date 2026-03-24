package mraffi.learn_sping_restful_api.exception;

import mraffi.learn_sping_restful_api.model.response.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<WebResponse<Object>> apiException(ApiException exception){

        Map<String, List<String>> errors = Map.of(
                exception.getField(),
                List.of(exception.getMessage())
        );

        return ResponseEntity.status(exception.getStatus())
                .body(WebResponse.builder()
                        .message("Business Error")
                        .errors(errors)
                        .code(exception.getCode())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Object>> validationException(MethodArgumentNotValidException exception){

        Map<String, List<String>> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));

        return ResponseEntity.badRequest().body(
                WebResponse.builder()
                        .message("Validation Failed")
                        .errors(errors)
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Object>> generalException(Exception exception){

        Map<String, List<String>> errors = Map.of(
                "global",
                List.of("Unexpected error occurred")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(WebResponse.builder()
                        .message("Internal Server Error")
                        .errors(errors)
                        .build());
    }
}
