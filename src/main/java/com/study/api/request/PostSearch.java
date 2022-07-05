package com.study.api.request;

import lombok.Builder;
import lombok.Data;

import static java.lang.Math.*;

@Data
@Builder
public class PostSearch {

    private static final int MAX_SIZE = 20;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    /*@Builder
    public PostSearch(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }*/

    public long getOffSet() {
        return (long) (max(page,1)-1) * min(size, MAX_SIZE);
    }
}
