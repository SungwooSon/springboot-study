package com.study.api.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEditor {
    //수정을 할수 있는 필드만 선언
    //이것만 보고 수정가능한 필드를 알수 있음.
    private final String title;
    private final String content;

    @Builder
    public PostEditor(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
