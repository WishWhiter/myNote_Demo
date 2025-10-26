package com.example.demo.config;


import com.example.demo.repository.UserRepository; // 导入创建的 Repository
import com.example.demo.entity.User; // 导入创建的 User 实体
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 导入密码加密器
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 标记这是一个配置类
@EnableWebSecurity // 启用 Spring Security Web 安全功能



public class SecurityConfig {

    // 通过 构造函数注入 UserRepository
    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
      定义安全过滤器链，配置哪些请求需要认证，哪些不需要，以及登录/登出行为。
     */
    @Bean // 将这个方法的返回值 注册为 Spring Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // 允许访问静态资源 (如果有的话)
                        .requestMatchers("/", "/register", "/login").permitAll()
                        // 允许所有人访问首页、注册页、登录页
                        .anyRequest().authenticated() // 其他所有请求都需要认证
                )
                .formLogin(form -> form
                        .loginPage("/login") // 指定自定义登录页面的路径
                        .defaultSuccessUrl("/", true) // <--- 添加这一行，指定登录成功后默认跳转到首页
                        .permitAll() // 登录页允许所有人访问
                )
                .logout(logout -> logout.permitAll()); // 登出页允许所有人访问

        return http.build(); // 构建并返回 SecurityFilterChain 对象

    }

    /*
       定义如何从数据库加载用户信息。
       Spring Security 会使用这个 Bean 来查找用户。
     */
    @Bean // 将这个方法的返回值 注册为 Spring Bean
    public UserDetailsService userDetailsService() {
        // 返回一个 Lambda 表达式, 实现了 UserDetailsService 接口的
        // loadUserByUsername 方法
        return username -> {
            // 从数据库 查询用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return user;
        };
    }

    /*
        定义密码加密器 Bean
        用于注册时加密密码，以及登录时对比密码
     */
    @Bean // 将这个方法的返回值注册为 Spring Bean
    public PasswordEncoder passwordEncoder() {
        // 使用 BCrypt 算法加密密码， 这是推荐的安全方式
        return new BCryptPasswordEncoder();//是一个安全且广泛使用的密码哈希函数。
    }

}
