package com.pvt.blog.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.pvt.blog.enums.ResultEnum;
import com.pvt.blog.pojo.Post;
import com.pvt.blog.pojo.dto.PostDTO;
import com.pvt.blog.repository.PostRepository;
import com.pvt.blog.service.IPostService;
import com.pvt.blog.util.ResultResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author eucotopia
 */
@Service
public class PostServiceImpl implements IPostService {
    @Resource
    private PostRepository postRepository;

    @Override
    public ResultResponse<List<Post>> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> all = postRepository.findAll(pageable);
        System.out.println("TotalPages" + all.getTotalPages());
        List<Post> content = all.getContent();
        for (Post post : content) {
            System.out.println(post.getId());
        }
        System.out.println("content:" + all.getContent());
        return ResultResponse.success(ResultEnum.SUCCESS, all.getContent());
    }

    @Override
    public ResultResponse<String> addPost(PostDTO postDTO) {
        Post post = BeanUtil.copyProperties(postDTO, Post.class);
        Post save = postRepository.save(post);
        return ResultResponse.success(ResultEnum.SUCCESS, "成功");
    }

    @Override
    public ResultResponse<Post> getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("没有该文章"));
        return ResultResponse.success(ResultEnum.SUCCESS, post);
    }

    @Override
    public ResultResponse<Long> getPostCount() {
        return ResultResponse.success(ResultEnum.SUCCESS, postRepository.count());
    }

    @Override
    public ResultResponse<String> likeBlog(Long id) {
        Optional<Post> byId = postRepository.findById(id);
        if (byId.isPresent()) {
            Post post = byId.get();
            post.setLikes(post.getLikes() + 1);
            postRepository.save(post);
            return ResultResponse.success(ResultEnum.SUCCESS, "点赞成功");
        } else {
            return ResultResponse.fail(ResultEnum.FAIL);
        }
    }
}
