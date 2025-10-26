package com.example.demo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理器，用于捕获整个应用中未处理的异常
 */
@ControllerAdvice // 这个注解告诉 Spring Boot 这是一个全局异常处理器
public class GlobalExceptionHandler {

    /**
     * 捕获所有未被其他处理器捕获的 Exception 类型异常
     * @param ex 抛出的异常对象
     * @param model 用于向视图传递数据的 Model 对象
     * @return 错误页面的视图名称
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        // 1. 记录错误日志（使用 System.err 或者日志框架如 Logback/Log4j）
        System.err.println("An unexpected error occurred in the application: " + ex.getMessage());
        ex.printStackTrace(); // 打印完整的堆栈跟踪，方便调试

        // 2. 准备错误信息，传递给错误页面
        model.addAttribute("error", "An unexpected error occurred: " + ex.getMessage());

        // 3. 返回错误页面的视图名称
        return "error"; // 这将对应 templates/error.html
    }
}