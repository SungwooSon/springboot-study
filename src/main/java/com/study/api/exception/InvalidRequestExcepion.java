package com.study.api.exception;


import lombok.Getter;

/**
 * 400
 */
@Getter
public class InvalidRequestExcepion extends HodolLogException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    //private String fieldName;
    //private String message;

    public InvalidRequestExcepion() {
        super(MESSAGE);
    }

    public InvalidRequestExcepion(String fieldName, String message) {
        super(MESSAGE);
       // this.fieldName = fieldName;
        //this.message = message;

        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
