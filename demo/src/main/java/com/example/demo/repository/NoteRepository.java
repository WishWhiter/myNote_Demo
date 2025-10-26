package com.example.demo.repository;


import com.example.demo.entity.Note;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository<Note, Long> 提供对 Note 实体的基本 CRUD 方法
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Spring Data JPA 根据方法名自动生成查询逻辑
    // findByUser 会自动生成 "SELECT * FROM notes WHERE user_id = ?" 的查询
    List<Note> findByUser(User user);
    //--- 根据用户查找该用户的所有笔记
}
