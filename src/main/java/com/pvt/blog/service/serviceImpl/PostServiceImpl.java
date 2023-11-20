package com.pvt.blog.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.pvt.blog.common.RedisConstant;
import com.pvt.blog.common.ResultConstant;
import com.pvt.blog.enums.ResultEnum;
import com.pvt.blog.pojo.Post;
import com.pvt.blog.pojo.dto.PostDTO;
import com.pvt.blog.repository.PostRepository;
import com.pvt.blog.repository.UserRepository;
import com.pvt.blog.service.IPostService;
import com.pvt.blog.util.RedisUtil;
import com.pvt.blog.util.ResultResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author eucotopia
 */
@Service
@Slf4j
public class PostServiceImpl implements IPostService {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserRepository userRepository;
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
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        com.pvt.blog.pojo.User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("没有该用户"));
        String key = RedisConstant.BLOG_KEY + id + ":" + user.getId();
        // 判断是否点过赞
        if (redisUtil.hasKey(key)) {
            // 如果点过赞，就取消点赞
            redisUtil.deleteObject(key);
            postRepository.dislikeBlog(id);
        } else {
            // 如果没有点过赞，就点赞
            redisUtil.setCacheObject(key, id);
            postRepository.likeBlog(id);
        }
        return ResultResponse.success(ResultEnum.SUCCESS, "成功");
    }

    /**
     * 判断是否点过赞
     *
     * @param id id
     * @return ResultResponse<Boolean>
     */
    @Override
    public ResultResponse<Boolean> isLiked(Long id) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        com.pvt.blog.pojo.User user = userRepository.findByUsername(principal.getUsername()).orElseThrow(() -> new RuntimeException("没有该用户"));
        String key = RedisConstant.BLOG_KEY + id + ":" + user.getId();
        if (redisUtil.hasKey(key)) {
            return ResultResponse.success(ResultEnum.SUCCESS, true);
        } else {
            return ResultResponse.success(ResultEnum.SUCCESS, false);
        }
    }
}
