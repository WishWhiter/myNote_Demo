package com.example.demo.entity;

import jakarta.persistence.*;

import java.util.Objects;

/*  Note 实体类
包含了:

主键@Column:
id,content,title,

外键@JoinColumn:
User
 */


// 同样的,@Entity  注解标记这个类是一个 JPA 实体, 对应数据库中的一张表
// @Table 注解可以指定实体对应的数据库表名, 这里指定为 'notes'
@Entity
@Table
public class Note {

    // @Id 注解标记这个属性是表的主键
    @Id
    // @GeneratedValue 注解表示主键值由数据库自动生成
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column 注解配置属性对应的数据库列
    // nullable = false 表示内容不能为空
    // columnDefinition = "TEXT" 指定数据库列类型为 TEXT，可以存储较长的 Markdown 内容
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 标题，不能为空
    @Column(nullable = false)
    private String title;

    // @ManyToOne 注解表示多个笔记 (Note) 属于一个用户 (User)
    // fetch = FetchType.LAZY 表示懒加载，只有在真正访问 user 对象时才去数据库查询
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn 注解指定外键列名 (user_id) 和不能为空
    @JoinColumn(nullable = false, name = "user_id" )
    private User user; // 关联到 User 实体

    // --- Constructors ---
    public Note() {
        // JPA 需要一个无参构造函数
    }

    // 通常笔记创建时需要关联用户和内容
    public Note(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // --- 添加 equals 和 hashCode 方法 ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    // --- 添加结束 ---

}
