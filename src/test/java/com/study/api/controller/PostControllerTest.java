package com.study.api.controller;

import com.study.api.domain.Post;
import com.study.api.repository.PostRepository;
import com.study.api.request.PostCreate;
import com.study.api.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository repository;

    Logger log = LoggerFactory.getLogger(PostControllerTest.class);


    @AfterEach
    void clean () {
        repository.deleteAll();
    }

    @Test
    @DisplayName("/hodol/posts 요청시 hello 를 출력한다.")
    public void test() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("제목")
                .content("내용 입니다")
                .build();

        //ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(request);
        log.info("josn={}", json);

        // expected
        mockMvc.perform(post("/hodol/posts")
                /*
                   .contentType(MediaType.APPLICATION_FORM_URLENCODED) // application/x-www-form-urlencoded 는 key=value&key=value 형태로 요청
                    .param("title", "글 제목")
                    .param("content", "글 내용입니다.")
                    application/x-www-form-urlencoded 방식은 단순히 key=value 형태의 요청만 처리할수 있다.
                    */
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print()); //console에 http 요청에 대한 summary를 보여줌.
    }

    @Test
    @DisplayName("/hodol/posts 요청시 title 값은 필수다")
    public void get2() throws Exception {

        //given
        PostCreate request = PostCreate.builder()
                //.title("제목")
                .content("내용 입니다")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(
                post("/hodol/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("/hodol/posts 요청시 DB에 값이 저장된다.")
    public void test3() throws Exception {

        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("글 내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);


        // expected
        mockMvc.perform(
                post("/hodol/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertThat(repository.count()).isEqualTo(1);

        Post post = repository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("제목입니다.");
        assertThat(post.getContent()).isEqualTo("글 내용입니다.");

    }


    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        Post save = repository.save(post);

        //expected(when + then)
        mockMvc.perform(
                get("/hodol/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("foo"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        Post save = repository.save(post);

        Post post2 = Post.builder()
                .title("foo2")
                .content("bar2")
                .build();
        Post save2 = repository.save(post2);

        //expected(when + then)
        mockMvc.perform(
                        get("/hodol/posts?page=1&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                /**
                 * [{"id":1, "title":"foo", "content":"bar"}, {.....}]
                 */
                .andExpect(jsonPath("$.length()", Matchers.is(2)))
                .andExpect(jsonPath("$[1].id").value(post.getId()))
                .andExpect(jsonPath("$[1].title").value("foo"))
                .andExpect(jsonPath("$[1].content").value("bar"))
                .andExpect(jsonPath("$[0].id").value(post2.getId()))
                .andExpect(jsonPath("$[0].title").value("foo2"))
                .andExpect(jsonPath("$[0].content").value("bar2"))
                .andDo(print());

    }

    @Test
    @DisplayName("첫 페이지 조회")
    void test6() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("제목 - " +i)
                            .content("내용 - " + i)
                            .build();
                })
                .collect(Collectors.toList());
        repository.saveAll(requestPosts);

        //expected(when + then)
        mockMvc.perform(
                //get("/hodol/posts?page=1&sort=id,desc")
                get("/hodol/posts?page=2&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                //.andExpect(jsonPath("$[0].id").value(20))
                .andExpect(jsonPath("$[0].title").value("제목 - 19"))
                .andExpect(jsonPath("$[0].content").value("내용 - 19"))
                .andDo(print());
    }


    @Test
    @DisplayName("첫 페이지 를 0으로 입력시 첫 페이지로 조회")
    void test7() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("제목 - " +i)
                            .content("내용 - " + i)
                            .build();
                })
                .collect(Collectors.toList());
        repository.saveAll(requestPosts);

        //expected(when + then)
        mockMvc.perform(
                //get("/hodol/posts?page=1&sort=id,desc")
                get("/hodol/posts?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(10)))
                //.andExpect(jsonPath("$[0].id").value(30))
                .andExpect(jsonPath("$[0].title").value("제목 - 29"))
                .andExpect(jsonPath("$[0].content").value("내용 - 29"))
                .andDo(print());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test8() throws Exception {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        Post save = repository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("foo2")
                .content("bar")
                .build();

        //expected(when + then)
        mockMvc.perform(
                patch("/hodol/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 삭제")
    void test9() throws Exception {
        //given
        Post post = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        Post save = repository.save(post);

        //expected(when + then)
        mockMvc.perform(
                delete("/hodol/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test10() throws Exception {

        //expected(when + then)
        mockMvc.perform(
                get("/hodol/posts/{postId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void test11() throws Exception {
        PostEdit postEdit = PostEdit.builder()
                .title("foo2")
                .content("bar")
                .build();

        //expected(when + then)
        mockMvc.perform(
                patch("/hodol/posts/{postId}", 1l)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
        )
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 작성시 '바보'는 포함될수 없다")
    void test12() throws Exception {
        PostCreate postCreate = PostCreate.builder()
                .title("나는 바보")
                .content("bar")
                .build();

        //expected(when + then)
        mockMvc.perform(
                post("/hodol/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreate))
        )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}

// API 문서 생성
// 클라이언트를 위한 문서
// Spring RestDocs
// 운영코드에 영향이 없다
// TEst 코드 실행 -> 문서 생성 해주어, 최신화,신뢰도가 높다.
