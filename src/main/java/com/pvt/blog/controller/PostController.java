package com.pvt.blog.controller;

import com.pvt.blog.pojo.Post;
import com.pvt.blog.pojo.dto.PostDTO;
import com.pvt.blog.service.IPostService;
import com.pvt.blog.util.ResultResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author LIWEI
 */
@RequestMapping("/post")
@RestController
@Slf4j
public class PostController {
    @Resource
    private IPostService postService;

    /**
     * 分页查询所有文章
     * @return ResultResponse<List<Post>>
     */
    @GetMapping("/{page}/{size}")
    public ResultResponse<List<Post>>  getAllPost(@PathVariable("page") Integer page,@PathVariable("size") Integer size){
        return postService.findALl(page,size);
    }

    /**
     * 添加博客
     * @param postDTO postDTO
     * @return ResultResponse<String>
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResultResponse<String> addPost(@RequestBody PostDTO postDTO){
        return postService.addPost(postDTO);
    }

    /**
     * 根据id查询博客
     */
    @GetMapping("/{id}")
    public ResultResponse<Post> getPostById(@PathVariable("id") Long id){
        log.info("123");
        return postService.getPostById(id);
    }
}
