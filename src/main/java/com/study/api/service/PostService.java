package com.study.api.service;

import com.study.api.domain.Post;
import com.study.api.domain.PostEditor;
import com.study.api.exception.PostNotFound;
import com.study.api.repository.PostRepository;
import com.study.api.request.PostCreate;
import com.study.api.request.PostEdit;
import com.study.api.request.PostSearch;
import com.study.api.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository repository;

    public void write(PostCreate postCreate) {

        log.info("hello world3");

        //postCreate -> Entity
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();
        repository.save(post);
    }


    public PostResponse get(Long postId) {
        Post post = repository.findById(postId)
                //.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 글입니다."));
                //.orElseThrow(() -> new PostNotFound());
                .orElseThrow(PostNotFound::new);

        //응답 클래스 분리(서비스 정책에 맞도록)(도메인, 엔티티에 절대!! 비지니스 로직, 정책이 들어가서는 안됨)
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }


    //글이 많으면 비용이 너무 많이 든다.
    //글이 10000000 모두 조회하는 경우 DB가 뻗을수가 있다.
    //DB -> 어플리케이션 서버로 전달하는 시간, 트래픽 비용 등이 많이 발생할 수 있다.
    //public List<PostResponse> getList(Pageable pageable) {
    public List<PostResponse> getList(PostSearch postSearch) {

        //Pageable pageable = PageRequest.of(page, 5, Sort.by("id").descending());

        /* querydsl로 대체
        return repository.findAll(pageable).stream()
                //.map(post -> new PostResponse(post))
                .map(PostResponse::new)
                .collect(Collectors.toList());
        */
        log.info("postSearch={}", postSearch);
        log.info("size={}", postSearch.getSize());
        log.info("page={}", postSearch.getPage());
        return repository.getList(postSearch).stream()
                //.map(post -> new PostResponse(post))
                .map(PostResponse::new)
                .collect(Collectors.toList());

    }

    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        Post post = repository.findById(id)
                .orElseThrow(PostNotFound::new);

        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();
        PostEditor postEditor = editorBuilder
                                    .title(postEdit.getTitle())
                                    .content(postEdit.getContent()) // front에서 데이터를 넘기지 않으면 null, 수정하지 않는 데이터도 원본값으로 보내달라고 요청
                                    .build();

        /* // 프론트에서 원본 데이터를 보내지 않는다면...
        if(postEdit.getTitle() != null) {
            editorBuilder.title(postEdit.getTitle())
        }
        if(postEdit.getContent() != null) {
            editorBuilder.content(postEdit.getContent())
        }
        post.edit(editorBuilder.build());

        또는

        postEditor 생성자 안에서 체크하는 방법도 있다.
         */

        post.edit(postEditor);

        repository.save(post);
    }

    public void delete(Long id) {
        Post post = repository.findById(id)
                .orElseThrow(PostNotFound::new);
        repository.delete(post);
    }
}
