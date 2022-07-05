package com.study.api.controller;


import com.study.api.request.PostCreate;
import com.study.api.request.PostEdit;
import com.study.api.request.PostSearch;
import com.study.api.response.PostResponse;
import com.study.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/hodol")
public class PostController {

    private final PostService service;

    @PostMapping("/posts")
    //public String get(@RequestParam String title, @RequestParam String content) {
    //public String get(@RequestParam Map<String, String> params) {
    //public String get(@ModelAttribute PostCreate params) { //@ModelAttribute 생략가능
    //public Map<String, String> get(@RequestBody @Valid PostCreate params, BindingResult result) { //@Valid : PostCreate 필드에 걸린 검증을 해증.
    public void create(@RequestBody @Valid PostCreate request) { //@ControllerAdvice 사용
        //log.info("title={}, content={}", title, content);
        log.info("request={}", request);


        request.validate();

        /* ControllerAdvice 사용
        if (result.hasErrors()) {
            List<FieldError> fieldErrors = result.getFieldErrors();
            FieldError firstFieldError = fieldErrors.get(0);

            String fieldName = firstFieldError.getField();
            String errorMessage = firstFieldError.getDefaultMessage();

            Map<String, String> error = new HashMap<>();
            error.put(fieldName, errorMessage);
            return error;
        }*/

        //저장 성공시 HttpStatusCode  : 200, 201
        //보통 저장한 후 아무것도 응답하지 않지만,
        //FE 에서 저장하기 위한 데이터를 context에서 관리 하지 못하고, 다시 응답에 담아서 보내주기를 요청하는 경우도 있다.
        //그럴땐, wirte 한후 응답객체를 return 받아서 그것을 다시 리턴한다...
        service.write(request);
        // Case 1. 저장한 데이터 Entity -> response로 응답하기
        // Case 2. 저장한 데이터의 primary_id -> response로 응답하기. return Map.of("postId", postId);
        //      -> Client에서는 수신한 id를 글 조회 API를 통해서 데이터를 수신받음.
        // Case 3. 응답 필요 없음.(best case) -> 클라이언트에서 모든 Post(글) 데이터 context를 잘 관리함.
        // bad Case. 서버에서 반드시 이렇게 할겁니다 fix
        //  -> 서버에서 차라리 유연하게 대응하는게 좋다. -> 코드를 잘 짜야함..
        //  -> 한번에 일관적으로 잘 처리되는 케이스가 없다. -> 잘 관리하는 형태가 중요하다.
    }

    /**
     * /posts -> 글 전체 조회(검색 + 페이징)
     * /posts/{postId} -> 글 하나 조
     */
    @GetMapping("/posts/{postId}")
    //public void get(@PathVariable(name = "postId") Long id) {
    public PostResponse get(@PathVariable Long postId) {
        return service.get(postId);
    }

    @GetMapping("/posts")
    //public List<PostResponse> getList(@PageableDefault(size = 5, direction = Sort.Direction.DESC, sort = {"id"}) Pageable pageable) {
    //public List<PostResponse> getList(Pageable pageable) {
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) { // querydsl paging 적용
        //return service.getList(pageable);
        log.info(">>>>>>> getList");
        return service.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit) {
        service.edit(postId, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        service.delete(postId);
    }


}
