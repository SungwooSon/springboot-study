package com.study.api.repository;

import com.study.api.domain.Post;
import com.study.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
