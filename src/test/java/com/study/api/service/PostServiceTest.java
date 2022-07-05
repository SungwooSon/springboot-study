package com.study.api.service;

import com.study.api.domain.Post;
import com.study.api.exception.PostNotFound;
import com.study.api.repository.PostRepository;
import com.study.api.request.PostCreate;
import com.study.api.request.PostEdit;
import com.study.api.request.PostSearch;
import com.study.api.response.PostResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @AfterEach
    void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1() {
        //given
        PostCreate post = PostCreate.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();

        //when
        postService.write(post);

        //then
        assertThat(postRepository.count()).isEqualTo(1);
        Post findPost = postRepository.findAll().get(0);
        assertThat(findPost.getTitle()).isEqualTo("제목입니다");
        assertThat(findPost.getContent()).isEqualTo("내용입니다");
    }

    @Test
    @DisplayName("글 1건 조회")
    void get3() {
        //given
        Post reqeust = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(reqeust);

        //when
        PostResponse findPost = postService.get(reqeust.getId());

        //then
        assertThat(findPost).isNotNull();
        assertThat(postRepository.count()).isEqualTo(1);
        assertThat(findPost.getTitle()).isEqualTo(reqeust.getTitle());
        assertThat(findPost.getContent()).isEqualTo(reqeust.getContent());
    }

    @Test
    @DisplayName("글 여러건 조회")
    void get4() {
        //given
        Post reqeust = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        //postRepository.save(reqeust);

        Post reqeust2 = Post.builder()
                .title("제목입니다2")
                .content("내용입니다2")
                .build();
        //postRepository.save(reqeust2);

        postRepository.saveAll(List.of(reqeust, reqeust2));

        //PageRequest pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .build();


        //when
        List<PostResponse> posts = postService.getList(postSearch);

        //then
        assertThat(posts.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("글 첫 페이지 조회")
    void get5() {
        //given
        List<Post> requestPosts = IntStream.range(0, 30)
                .mapToObj(i -> {
                    return Post.builder()
                            .title("제목 - " +i)
                            .content("내용 - " + i)
                            .build();
                })
                .collect(Collectors.toList());


        postRepository.saveAll(requestPosts);


        //PageRequest pageRequest = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
        PostSearch postSearch = PostSearch.builder()
                .page(1)
                .build();


        //when
        List<PostResponse> posts = postService.getList(postSearch);
        // 1 페이지를 조회하길 원하여 page: 1로 전달하겠지만
        // Pagable의 시작 인덱스는 0이다.
        // 이것을 그대로 FE로 넘기면 혼란스럽다.
        // application.yml 에서 spring.data.web.pageable.one-indexed-parameters: true 로 설정하면 1로 시작한다.
        //  -> 하지만 이것은 Pageable을 사용한 Web 요청으로 넘어 왔을떄만 적용됨. 수동으로 테스트 할 떈, 0으로 입력해주어야함.

        //then
        assertThat(posts.size()).isEqualTo(10);
        //assertThat(posts.get(0).getId()).isEqualTo(30);
        //assertThat(posts.get(4).getId()).isEqualTo(26);
    }


    @Test
    @DisplayName("재목 수정")
    void get6() {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .content("내용입니다")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post updatedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다." + post.getId()));

        assertThat(updatedPost.getTitle()).isEqualTo("제목");
        assertThat(updatedPost.getContent()).isEqualTo("내용입니다"); // 수정을 하지 않음.
    }

    @Test
    @DisplayName("재목 수정")
    void get7() {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post updatedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다." + post.getId()));

        assertThat(updatedPost.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("게시글 삭제")
    void get8() {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);


        //when
        postService.delete(post.getId());

        //then
        assertThat(postRepository.count()).isEqualTo(0);
    }


    @Test
    @DisplayName("글 1건 조회")
    void get9() {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        //expected
        assertThatThrownBy(()-> postService.get(post.getId()+1))
                .isInstanceOf(PostNotFound.class)
                .hasMessage("존재하지 않는 글입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제")
    void get10() {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        //expected
        assertThatThrownBy(()-> postService.delete(post.getId() + 1))
                .isInstanceOf(PostNotFound.class)
                .hasMessage("존재하지 않는 글입니다.");
    }

    @Test
    @DisplayName("재목 수정")
    void get11() {
        //given
        Post post = Post.builder()
                .title("제목입니다")
                .content("내용입니다")
                .build();
        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("제목")
                .build();

        //expected
        assertThatThrownBy(()-> postService.edit(post.getId()+1, postEdit))
                .isInstanceOf(PostNotFound.class)
                .hasMessage("존재하지 않는 글입니다.");
    }
}
