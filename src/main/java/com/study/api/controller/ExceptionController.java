package com.study.api.controller;


import com.study.api.exception.HodolLogException;
import com.study.api.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
//@ControllerAdvice
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse authenticationException(AuthenticationException e) {
        log.info(e.getMessage());
        return ErrorResponse.builder()
                .code("401")
                .message(e.getMessage())
                .build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //@ResponseBody //rest로 결과 전송. 안쓰면 viewResolver 찾음 -> ResControllerAdvice 사용
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {
        //log.info("exceptionHandler error", e);

        /* ErrorResponse 응답 객체 사용하는 것으로 대체
        FieldError fieldError = e.getFieldError();
        String field = fieldError.getField();
        String defaultMessage = fieldError.getDefaultMessage();

         Map<String, String> error = new HashMap<>();
         error.put(field, defaultMessage);
         return error;
         */

        //ErrorResponse errorResponse = new ErrorResponse("400", "잘못된 요청입니다.");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("400")
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorResponse;
    }

    //@ResponseStatus(HttpStatus.NOT_FOUND) 어노테이션으로는 StatusCode를 동적으로 바꿀수 없다. //이거 빼면 기본적으로 200이 날아감.
    @ExceptionHandler(HodolLogException.class)
    //public ErrorResponse hodolLogException(HodolLogException e) {
    public ResponseEntity<ErrorResponse> hodolLogException(HodolLogException e) {

        int statusCode = e.getStatusCode();

        ErrorResponse body = ErrorResponse.builder()
                .code(String.valueOf(statusCode)) //shift + f6  : 다른 클래스 메소드 이름도 다 바꾼다.
                //String 으로 넘기는게 향후에 이점이 있다.
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();

        //응답 json validation 에 어떤 오류인지 추가해주어야함
        /*if (e instanceof InvalidRequestExcepion) {
            InvalidRequestExcepion invalidRequestExcepion = (InvalidRequestExcepion) e;
            String fieldName = invalidRequestExcepion.getFieldName();
            String message = invalidRequestExcepion.getMessage();

            body.addValidation(fieldName, message);
        }*/

        ResponseEntity<ErrorResponse> response = ResponseEntity.status(statusCode)
                .body(body);

        return response;
    }

}
