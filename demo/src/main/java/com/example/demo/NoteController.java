package com.example.demo; // ⚠️ 请根据你的实际包名修改！例如，如果你的 pom.xml 中是 <groupId>com.yourname</groupId>，这里就是 package com.yourname;

// 移除旧的 imports
// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.Arrays;
// import java.util.stream.Collectors;

// 添加新的 imports
import com.example.demo.entity.Note;
import com.example.demo.entity.User;
import com.example.demo.repository.NoteRepository;
// import com.example.demo.service.NoteService; // 移除对文件服务的依赖
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.ext.gfm.tables.TablesExtension;
// import jakarta.annotation.PostConstruct; // 不再需要
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication; // 导入 Authentication
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional; // 用于处理可能为空的查询结果

@Controller
public class NoteController {

    // 注入数据库 Repository
    @Autowired
    private NoteRepository noteRepository;

    // 移除对 NoteService 的注入，因为我们现在使用数据库
    // @Autowired
    // private NoteService noteService;

    // 首页：列出当前用户的笔记
    @GetMapping("/")
    public String listNotes(Model model, Authentication authentication) {
        System.out.println("DEBUG: listNotes method called.");
        if (authentication != null && authentication.isAuthenticated()) {
            User currentUser = (User) authentication.getPrincipal();
            System.out.println("DEBUG: Current User ID: " + currentUser.getId() + ", Username: " + currentUser.getUsername());
            List<Note> userNotes = noteRepository.findByUser(currentUser);
            System.out.println("DEBUG: Found " + userNotes.size() + " notes for user " + currentUser.getUsername());
            // 尝试访问笔记的属性，看是否引发异常 (仅用于调试)
            for (Note note : userNotes) {
                System.out.println("DEBUG: Note ID: " + note.getId() + ", Title: " + note.getTitle());
                // 如果 list.html 中用到了 note.user，可以尝试打印
                // System.out.println("DEBUG: Note User ID: " + note.getUser().getId()); // 注意：如果 user 是 LAZY 加载，这行可能触发查询
            }
            model.addAttribute("notes", userNotes);
        } else {
            System.out.println("DEBUG: User not authenticated, adding empty list");
            model.addAttribute("notes", List.of());
        }
        System.out.println("DEBUG: listNotes method completed, returning 'list' view.");
        return "list";
    }
//    @GetMapping("/")
//    public String listNotes(Model model, Authentication authentication) { // 接收 Authentication 对象
//        if (authentication != null && authentication.isAuthenticated()) {
//            User currentUser = (User) authentication.getPrincipal(); // 获取当前登录用户
//            System.out.println("DEBUG: Current User ID: " + currentUser.getId() + ", Username: " + currentUser.getUsername()); // 添加日志
//            List<Note> userNotes = noteRepository.findByUser(currentUser); // 查询该用户的所有笔记
//            System.out.println("DEBUG: Found " + userNotes.size() + " notes for user " + currentUser.getUsername()); // 添加日志
//            model.addAttribute("notes", userNotes); // 传递给前端模板
//        } else {
//            System.out.println("DEBUG: User not authenticated, adding empty list"); // 添加日志
//            model.addAttribute("notes", List.of()); // 未登录用户看到空列表
//        }
//        return "list"; // 对应 templates/list.html
//    }

    // 编辑页面：新建或编辑笔记
    @GetMapping("/edit")
    public String editNote(@RequestParam(required = false) Long id, Model model, Authentication authentication) { // 接收笔记 ID 和 Authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // 未登录用户重定向到登录页
        }
        User currentUser = (User) authentication.getPrincipal();

        String title = "";
        String content = "";

        if (id != null) { // 编辑现有笔记
            Optional<Note> noteOpt = noteRepository.findById(id); // 根据 ID 查询笔记
            if (noteOpt.isPresent()) {
                Note note = noteOpt.get();
                // 验证笔记是否属于当前用户
                if (!note.getUser().getId().equals(currentUser.getId())) {
                    // 如果笔记不属于当前用户，可以返回错误或重定向
                    return "redirect:/?error=unauthorized";
                }
                title = note.getTitle();
                content = note.getContent();
            } else {
                // 笔记不存在
                return "redirect:/?error=notfound";
            }
        } // 如果 id 为 null，则为新建笔记

        model.addAttribute("initContent", content); // 传递内容给编辑器
        model.addAttribute("initTitle", title);     // 传递标题
        model.addAttribute("noteId", id);           // 传递笔记 ID (编辑时有值，新建时为 null)
        return "edit"; // 对应 templates/edit.html
    }

    // 保存笔记
    @PostMapping("/save")
    public String saveNote(@RequestParam(required = false) Long id, // 笔记 ID，用于区分新建/更新
                           @RequestParam String title,
                           @RequestParam String content,
                           Authentication authentication) { // 接收 Authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = (User) authentication.getPrincipal();

        Note note;
        if (id != null) { // 更新现有笔记
            Optional<Note> existingNoteOpt = noteRepository.findById(id);
            if (existingNoteOpt.isPresent()) {
                note = existingNoteOpt.get();
                // 验证笔记是否属于当前用户
                if (!note.getUser().getId().equals(currentUser.getId())) {
                    return "redirect:/?error=unauthorized";
                }
            } else {
                // 笔记不存在，可能需要新建（或者返回错误）
                return "redirect:/?error=notfound";
            }
        } else { // 新建笔记
            note = new Note();
            note.setUser(currentUser); // 关联当前用户
        }

        note.setTitle(title.trim().isEmpty() ? "Untitled" : title.trim()); // 设置标题
        note.setContent(content); // 设置内容

        noteRepository.save(note); // 保存到数据库
        return "redirect:/view?id=" + note.getId(); // 保存后重定向到预览页面 (使用数据库中的 ID)
    }

    // 预览页面：显示渲染后的 HTML
    @GetMapping("/view")
    public String viewNote(@RequestParam Long id, Model model, Authentication authentication) { // 接收笔记 ID 和 Authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = (User) authentication.getPrincipal();

        Optional<Note> noteOpt = noteRepository.findById(id); // 根据 ID 查询笔记
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            // 验证笔记是否属于当前用户
            if (!note.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/?error=unauthorized";
            }

            String markdownContent = note.getContent();
            String htmlContent = markdownToHtml(markdownContent); // 你的 Markdown 转 HTML 方法

            model.addAttribute("htmlContent", htmlContent); // 传递渲染后的 HTML
            model.addAttribute("title", note.getTitle());   // 传递标题
            model.addAttribute("noteId", note.getId());     // 传递笔记 ID (可能用于编辑/删除按钮)
            return "view"; // 对应 templates/view.html
        } else {
            // 笔记不存在
            return "redirect:/?error=notfound";
        }
    }

    // 删除笔记 (可选)
    @PostMapping("/delete")
    public String deleteNote(@RequestParam Long id, Authentication authentication) { // 接收笔记 ID 和 Authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        User currentUser = (User) authentication.getPrincipal();

        Optional<Note> noteOpt = noteRepository.findById(id); // 根据 ID 查询笔记
        if (noteOpt.isPresent()) {
            Note note = noteOpt.get();
            // 验证笔记是否属于当前用户
            if (!note.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/?error=unauthorized";
            }
            noteRepository.delete(note); // 从数据库删除
        }
        // 无论成功与否，都重定向到首页
        return "redirect:/";
    }

    // 工具方法：将 Markdown 字符串转换为 HTML
    private String markdownToHtml(String markdown) {
        // 配置 Parser 以支持 GitHub 风格的表格 (GFM Tables)
        Parser parser = Parser.builder()
                .extensions(List.of(TablesExtension.create()))
                .build();
        // 解析 Markdown 文本为节点树
        Node document = parser.parse(markdown);

        // 配置 HtmlRenderer 以支持 GitHub 风格的表格
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(List.of(TablesExtension.create()))
                .build();
        // 将节点树渲染为 HTML 字符串
        return renderer.render(document);
    }
}