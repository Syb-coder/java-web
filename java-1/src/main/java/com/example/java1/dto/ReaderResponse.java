// 声明包路径，归类为 dto 层，存放数据传输对象（DTO）
package com.example.java1.dto;

// 导入读者实体类，用于 from 转换方法
import com.example.java1.model.Reader;
// 导入读者类型枚举（STUDENT / TEACHER），响应中透传给前端展示类型标签
import com.example.java1.model.ReaderType;

/**
 * 读者响应 DTO
 */
// 响应 DTO 不含密码字段，避免敏感信息通过 API 泄露
public record ReaderResponse(
        Long id, // 读者主键 ID，前端用于借阅/修改请求定位
        String readerNo, // 学号/工号，读者列表展示
        String name, // 姓名，借阅记录与读者列表展示
        ReaderType type, // 读者类型，前端映射为类型标签（学生/教师）
        String department, // 院系，读者详情展示
        String phone, // 电话，读者详情展示
        Integer currentBorrowCount // 当前借阅数，前端用于展示借阅进度（已借/限额）
) {
    /**
     * 从实体构造响应
     */
    public static ReaderResponse from(Reader reader) {
        return new ReaderResponse(
                reader.getId(), // 透传主键
                reader.getReaderNo(), // 透传学号/工号
                reader.getName(), // 透传姓名
                reader.getType(), // 透传读者类型
                reader.getDepartment(), // 透传院系
                reader.getPhone(), // 透传电话
                reader.getCurrentBorrowCount() // 透传当前借阅数
        );
    }
}
