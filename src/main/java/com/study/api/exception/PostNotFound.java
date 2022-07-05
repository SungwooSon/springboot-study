package com.study.api.exception;

/**
 * 404
 */
public class PostNotFound extends HodolLogException {

    private static final String Message = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(Message);
    }

    public PostNotFound(Throwable cause) {
        super(Message, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
