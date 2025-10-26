package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // 标记这是一个 Controller 类
public class AuthController {
    // 不在同一个 软件包下， 但是可以在同一个com.example.demo下互相使用
// 为什么用 private 修饰?
    @Autowired // 自动注入 UserRepository
    private UserRepository userRepository;

    @Autowired // 自动注入 PasswordEncoder
    private PasswordEncoder passwordEncoder;

    /*
        显示用户注册请求
     */
    @GetMapping("/register")
    // 处理 GET /register 请求
    public String showRegistrationForm() {
        return "register";
    }
    // 为什么能跨文件夹知道"register.html"在哪里???

    /*
        处理用户注册请求
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password) {
        // 1. 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            // 如果用户名已存在，可以重定向回注册页并带一个错误参数
            // 例如 return "redirect:/register?error=userexists";
            // 这里为了简单，我们直接重定向到登录页，实际应用中应提示错误
            return "redirect:/register?error=userexists";
        }
            //  2. else 创建新用户对象
            User user = new User();
            user.setUsername(username);
            //  3. 使用 PasswordEncoder 加密密码，然后存储
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            //  4. 保存用户到数据库
            userRepository.save(user);
            // 这里是如何保存的,原理是什么?

        return "redirect:/login?registered=true"; // 可以带一个参数提示注册成功

    }

    /*
        显示登录界面
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
        // 如果有自定义的 login.html 模板，返回它
        // 否则，Spring Security 会提供一个默认的登录页面
        // 这里我们假设你稍后会创建一个 login.html
    }


}
