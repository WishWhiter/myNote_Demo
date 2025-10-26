package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value; // 确保导入了这个
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 列出查看, 读取内容, 保存 , 删除 笔记

@Service // 标记为 Spring 服务，以便注入到 Controller
public class FileNoteService implements NoteService {

    // 使用 @Value 注解从配置文件 (application.yml 或 application.properties) 中注入值
    // ${app.notes-dir} 指的是配置文件中 app.notes-dir 对应的值
    // 如果环境变量 NOTES_DIR 存在，则使用其值；否则使用 'notes' 作为默认值
    @Value("${app.notes-dir}")
    private String notesDir;

    @PostConstruct // 应用启动时创建目录
    public void init() {
        // 使用注入的配置值
        Path dirPath = Paths.get(notesDir);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
                System.out.println("Created notes directory: " + dirPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to create notes directory: " + e.getMessage());
                // 可以考虑抛出异常或记录日志
            }
        }
    }

    // 列出笔记文件名字
    @Override
    public List<String> listNoteFilenames() {
        // 使用注入的配置值
        Path dirPath = Paths.get(notesDir);
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.toLowerCase().endsWith(".md")) // 只列出 .md 文件
                    .sorted() // 排序
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error listing notes: " + e.getMessage());
            return List.of(); // 返回空列表
        }
    }

    // 读取笔记内容
    @Override
    public String readNoteContent(String filename) {
        // 使用重命名后的方法，逻辑更直观
        if (isInvalidFilename(filename)) {
            System.err.println("Invalid filename for reading: " + filename);
            return null;
        }
        Path filePath = Paths.get(notesDir).resolve(filename).normalize();
        // 安全检查：确保路径在配置的 notesDir 目录下
        if (!filePath.startsWith(Paths.get(notesDir))) {
            System.err.println("Path traversal attempt detected: " + filename);
            return null;
        }

        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            System.err.println("Error reading note " + filename + ": " + e.getMessage());
            return null; // 文件不存在或读取失败
        }
    }

    // 保存文件内容
    @Override
    public boolean saveNoteContent(String filename, String content) {
        // 使用重命名后的方法，逻辑更直观
        if (isInvalidFilename(filename)) {
            System.err.println("Invalid filename for saving: " + filename);
            return false;
        }
        Path filePath = Paths.get(notesDir).resolve(filename).normalize();
        // 安全检查：确保路径在配置的 notesDir 目录下
        if (!filePath.startsWith(Paths.get(notesDir))) {
            System.err.println("Path traversal attempt detected: " + filename);
            return false;
        }

        try {
            Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving note " + filename + ": " + e.getMessage());
            return false;
        }
    }

    // 删除笔记
    @Override
    public boolean deleteNote(String filename) {
        // 使用重命名后的方法，逻辑更直观
        if (isInvalidFilename(filename)) {
            System.err.println("Invalid filename for deletion: " + filename);
            return false;
        }
        Path filePath = Paths.get(notesDir).resolve(filename).normalize();
        // 安全检查：确保路径在配置的 notesDir 目录下
        if (!filePath.startsWith(Paths.get(notesDir))) {
            System.err.println("Path traversal attempt detected: " + filename);
            return false;
        }

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error deleting note " + filename + ": " + e.getMessage());
            return false;
        }
    }

    // 校验文件名是否**不安全** (即是否无效)
    private boolean isInvalidFilename(String filename) {
        // 如果 filename 为 null，或者不符合正则表达式，或者包含 ".."，则认为是无效的
        return filename == null ||
                !filename.matches("^[a-zA-Z0-9._-]+\\.md$") || // 注意：这里 ! 取反了原条件
                filename.contains(".."); // 防止路径穿越
    }
}