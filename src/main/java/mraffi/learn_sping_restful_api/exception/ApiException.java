package mraffi.learn_sping_restful_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final String field;

    public ApiException(String code, HttpStatus status, String field, String message){
        super(message);
        this.code = code;
        this.status = status;
        this.field = field;
    }

}
