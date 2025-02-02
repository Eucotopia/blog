package com.pvt.blog.repository;

import com.pvt.blog.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author LW
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsUserByEmail(String email);

    /**
     * 根据用户名查询用户
     *
     * @param username username
     * @return User
     */
    Optional<User> findByUsername(String username);


    /**
     * 根据邮箱查询用户
     *
     * @param email email
     * @return User
     */
    Optional<User> findByEmail(String email);


    Boolean existsByUsername(String username);

    Boolean deleteUserById(Long id);
}
