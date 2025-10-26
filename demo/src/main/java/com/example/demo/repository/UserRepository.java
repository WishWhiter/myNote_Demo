package com.example.demo.repository;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPA 提供的接口
import java.util.Optional; // Java 8+ 的 Optional 类，用于处理可能为空的值
//创建 的是 接口

// JpaRepository<User, Long> 是 Spring Data JPA 提供的通用接口
// 它提供了对 User 实体进行 CRUD (创建、读取、更新、删除) 的基本方法
// 第一个泛型参数是实体类 (User) 第二个泛型参数是主键类型 (Long)
public interface  UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA 会根据方法名自动生成查询逻辑
    // findByUsername 会自动生成 "SELECT * FROM users WHERE username = ?"的查询
    Optional<User> findByUsername(String username);
    //---根据用户名查找用户,返回 Optional 包装的对象

}
