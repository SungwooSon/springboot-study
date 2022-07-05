package com.study.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * {
 *     "code" : "400",
 *     "message" : "잘못된 요청입니다.",
 *     "validation" : { // 사용자에게 피드백을 주기 위한 데이터
 *         "title" : "값을 입력해주세요"
 *     }
 * }
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {

    private final String code;
    private final String message;

    //private final Map<String, String> validation = new HashMap<>(); // 해시맵을 안쓰는 방향으로 리팩터링 필요.
    private final Map<String, String> validation; // 해시맵을 안쓰는 방향으로 리팩터링 필요.

    @Builder
    //public ErrorResponse(String code, String message) {
    public ErrorResponse(String code, String message, Map<String, String> validation) {
        this.code = code;
        this.message = message;
        this.validation = validation != null ? validation : new HashMap<>();
    }

    public void addValidation(String field, String defaultMessage) {
        this.validation.put(field, defaultMessage);
    }

}
