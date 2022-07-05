package com.study.api.controller;

import com.study.api.repository.PostRepository;
import com.study.api.request.PostCreate;
import com.study.api.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.hodolman.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostService postService;

    @Autowired
    private MockMvc mockMvc;

    //AutoConfigureRestDocs 안먹음. beforeEach에서 하는 일이 MockMvc 주입받는 일이니.  @AutoConfigureMockMvc 로 대체
    /*
    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
    */

    @Test
    @DisplayName("글 단건 조회 테스트 -> 문서")
    public void test1() throws Exception {

        PostCreate requset = PostCreate.builder()
                .title("bar")
                .content("foo")
                .build();
        postService.write(requset);


        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/hodol/posts/{postId}", 1l).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-inquiry",
                        RequestDocumentation.pathParameters(
                                RequestDocumentation.parameterWithName("postId").description("게시글 ID")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("id").description("게시글 ID"),
                                PayloadDocumentation.fieldWithPath("title").description("제목"),
                                PayloadDocumentation.fieldWithPath("content").description("내용")
                        )
                ));
    }

    @Test
    @DisplayName("글 등록")
    public void test2() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("글 내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //expected
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/hodol/posts/")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json)
                        )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-create",
                       PayloadDocumentation.requestFields(
                            PayloadDocumentation.fieldWithPath("title").description("제목")
                               .attributes(key("constraint").value("'바보'가 포함될수 없음.")),
                            PayloadDocumentation.fieldWithPath("content").description("내용").optional()
                        )
                ));
    }
}
