package com.example.demo.service;

import java.util.List;

public interface NoteService {

    /**
     * 列出所有笔记文件名
     * @return 文件名列表
     */
    List<String> listNoteFilenames();

    /**
     * 读取笔记内容
     * @param filename 文件名
     * @return 文件内容，如果不存在则返回 null
     */
    String readNoteContent(String filename);

    /**
     * 保存笔记内容
     * @param filename 文件名
     * @param content 文件内容
     * @return true 如果保存成功，false 否则
     */
    boolean saveNoteContent(String filename, String content);

    /**
     * 删除笔记文件
     * @param filename 文件名
     * @return true 如果删除成功，false 否则
     */
    boolean deleteNote(String filename);
}