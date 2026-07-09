// 声明包路径，存放业务服务层
package com.example.java4.service;

// 导入 DTO 与实体类
import com.example.java4.dto.ReaderResponse;
import com.example.java4.dto.UserProfileRequest;
import com.example.java4.model.Reader;
import com.example.java4.model.ReaderType;

// 导入 Repository
import com.example.java4.repository.ReaderRepository;

// 导入 Spring 工具
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 读者业务服务
 * <p>
 * 提供读者个人信息修改（用户端）与读者列表查询（管理端）能力。
 * </p>
 */
@Service // 声明为 Spring 服务组件
public class ReaderService {

    /** 读者仓储 */
    private final ReaderRepository readerRepository;

    /**
     * 构造方法注入依赖
     */
    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    /**
     * 读者修改个人信息（用户端）
     * <p>
     * 仅允许修改姓名、院系、电话，不允许修改学号/工号与读者类型。
     * </p>
     *
     * @param readerId 读者 ID
     * @param req      个人信息修改请求
     * @return 修改后的读者响应
     * @throws IllegalArgumentException 读者不存在
     */
    public ReaderResponse updateProfile(Long readerId, UserProfileRequest req) {
        Reader reader = readerRepository.findById(readerId)
                .orElseThrow(() -> new IllegalArgumentException("读者不存在"));
        // 仅更新允许修改的字段
        if (req.getName() != null && !req.getName().trim().isEmpty()) {
            reader.setName(req.getName());
        }
        reader.setDepartment(req.getDepartment());
        reader.setPhone(req.getPhone());
        readerRepository.save(reader);
        return toResponse(reader);
    }

    /**
     * 获取读者个人信息（用户端）
     *
     * @param readerId 读者 ID
     * @return 读者响应
     */
    public ReaderResponse getProfile(Long readerId) {
        Reader reader = readerRepository.findById(readerId).orElse(null);
        return reader != null ? toResponse(reader) : null;
    }

    /**
     * 分页查询读者列表（管理端）
     *
     * @param keyword  搜索关键字（姓名或学号，可为空）
     * @param pageable 分页参数
     * @return 读者分页结果
     */
    public Page<ReaderResponse> searchReaders(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return readerRepository.findAll(pageable).map(this::toResponse);
        }
        return readerRepository.findByNameContainingOrReaderNoContaining(keyword, keyword, pageable)
                .map(this::toResponse);
    }

    /**
     * 根据 ID 获取读者详情（管理端）
     *
     * @param id 读者 ID
     * @return 读者响应
     */
    public ReaderResponse getReaderById(Long id) {
        Reader reader = readerRepository.findById(id).orElse(null);
        return reader != null ? toResponse(reader) : null;
    }

    /**
     * 实体转响应 DTO
     *
     * @param reader 读者实体
     * @return 读者响应 DTO
     */
    public ReaderResponse toResponse(Reader reader) {
        ReaderResponse resp = new ReaderResponse();
        resp.setId(reader.getId());
        resp.setReaderNo(reader.getReaderNo());
        resp.setName(reader.getName());
        resp.setReaderType(reader.getType().name());
        resp.setDepartment(reader.getDepartment());
        resp.setPhone(reader.getPhone());
        resp.setCurrentBorrowCount(reader.getCurrentBorrowCount());
        // 借阅上限由读者类型决定
        resp.setMaxBorrowCount(reader.getType().getMaxBorrowCount());
        resp.setLastLoginAt(reader.getLastLoginAt());
        resp.setCreateTime(reader.getCreateTime());
        resp.setUpdateTime(reader.getUpdateTime());
        return resp;
    }
}
