// 声明当前类所在的包路径，归类为 service 业务服务层
package com.example.java1.service;

import com.example.java1.dto.ReaderRequest; // 引入读者请求 DTO，封装新增/修改参数
import com.example.java1.dto.ReaderResponse; // 引入读者响应 DTO，用于对外返回读者信息
import com.example.java1.model.Reader; // 引入读者实体类，对应数据库 reader 表
import com.example.java1.repository.ReaderRepository; // 引入读者 JPA 仓储
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入 BCrypt 编码器，用于密码加密与比对
import org.springframework.stereotype.Service; // 引入 @Service 注解，标记服务层组件
import org.springframework.transaction.annotation.Transactional; // 引入 @Transactional 注解，用于声明式事务管理

import java.time.LocalDateTime; // 引入时间类，用于记录创建与登录时间
import java.util.List; // 引入 List 集合接口
import java.util.stream.Collectors; // 引入 Stream 收集器工具

/**
 * 读者业务服务
 * <p>
 * 负责读者的增删改查与密码管理。密码使用 BCrypt 加密。
 * </p>
 */
@Service // 声明为 Spring 服务组件，由 IoC 容器管理为单例 Bean
public class ReaderService {

    /** 读者仓储 */
    private final ReaderRepository repository; // 读者仓储，final 保证不可变
    /** BCrypt 编码器（线程安全） */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // BCrypt 编码器实例化，无状态可共享

    /**
     * 构造方法注入
     */
    public ReaderService(ReaderRepository repository) { // 构造方法注入仓储依赖
        this.repository = repository; // 赋值读者仓储
    }

    /**
     * 编码明文密码（用于初始化读者）
     *
     * @param rawPassword 明文密码
     * @return BCrypt 加密后的密码
     */
    public String encodePassword(String rawPassword) { // 密码编码方法，供初始化数据调用
        return passwordEncoder.encode(rawPassword); // 调用 BCrypt 编码，每次生成随机盐值
    }

    /**
     * 查询全部读者
     *
     * @return 读者响应列表
     */
    public List<ReaderResponse> getAllReaders() { // 查询全部读者方法（只读）
        return repository.findAll().stream() // 调用仓储加载全部读者实体
                .map(ReaderResponse::from) // 实体转 DTO，避免直接暴露实体
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 按 ID 查询读者
     *
     * @param id 读者 ID
     * @return 读者响应（可能为空）
     */
    public ReaderResponse getReaderById(Long id) { // 按 ID 查询读者方法
        return repository.findById(id) // 调用仓储按主键查询
                .map(ReaderResponse::from) // 存在则转换为 DTO
                .orElse(null); // 不存在返回 null，由上层判空处理
    }

    /**
     * 按姓名检索读者
     *
     * @param name 姓名关键字
     * @return 匹配的读者列表
     */
    public List<ReaderResponse> searchReaders(String name) { // 按姓名模糊检索读者
        if (name == null || name.isBlank()) { // 关键字为空时降级返回全部读者
            return getAllReaders(); // 返回全部读者列表
        }
        return repository.findByNameContainingIgnoreCase(name.trim()).stream() // 调用仓储忽略大小写模糊匹配，去除关键字首尾空白
                .map(ReaderResponse::from) // 实体转 DTO
                .collect(Collectors.toList()); // 收集为 List 返回
    }

    /**
     * 获取读者实体（供 BorrowService 调用）
     *
     * @param id 读者 ID
     * @return 读者实体
     * @throws IllegalArgumentException 读者不存在
     */
    public Reader getReaderEntity(Long id) { // 按读者 ID 加载实体方法（供内部服务调用）
        return repository.findById(id) // 调用仓储按主键查询
                .orElseThrow(() -> new IllegalArgumentException("读者不存在: " + id)); // 不存在则抛出参数异常，触发 400 响应
    }

    /**
     * 根据学号/工号获取读者（登录校验用）
     *
     * @param readerNo 学号/工号
     * @return 读者实体（可能为空）
     */
    public Reader getByReaderNo(String readerNo) { // 按学号/工号查询读者实体方法
        return repository.findByReaderNo(readerNo).orElse(null); // 不存在时返回 null，由调用方判空处理
    }

    /**
     * 新增读者
     * <p>密码必填，使用 BCrypt 加密存储。</p>
     *
     * @param req 读者请求
     * @return 新建的读者响应
     * @throws IllegalArgumentException 学号/工号已存在
     */
    @Transactional // 开启事务，保证新增操作的原子性
    public ReaderResponse createReader(ReaderRequest req) { // 新增读者业务入口方法
        if (repository.existsByReaderNo(req.readerNo())) { // 校验学号/工号唯一性，防止重复注册
            throw new IllegalArgumentException("学号/工号已存在: " + req.readerNo()); // 抛出参数异常，触发 400 响应
        }
        if (req.password() == null || req.password().length() < 6) { // 校验密码长度，防止弱密码
            throw new IllegalArgumentException("密码至少 6 位"); // 抛出参数异常，触发 400 响应
        }
        Reader reader = new Reader( // 创建新的读者实体
                req.readerNo(), // 设置学号/工号
                passwordEncoder.encode(req.password()), // 设置 BCrypt 加密后的密码
                req.name(), // 设置姓名
                req.type(), // 设置读者类型（学生/教师）
                req.department(), // 设置院系
                req.phone() // 设置联系电话
        );
        reader.setCreateTime(LocalDateTime.now()); // 记录创建时间，用于审计
        Reader saved = repository.save(reader); // 持久化读者到数据库
        return ReaderResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 修改读者信息
     * <p>password 字段为空时保持原密码不变。</p>
     *
     * @param id 读者 ID
     * @param req 读者请求
     * @return 更新后的读者响应
     */
    @Transactional // 开启事务，保证修改操作的原子性
    public ReaderResponse updateReader(Long id, ReaderRequest req) { // 修改读者业务入口方法
        Reader reader = getReaderEntity(id); // 加载现有读者实体（不存在则抛异常）
        reader.setReaderNo(req.readerNo()); // 更新学号/工号
        reader.setName(req.name()); // 更新姓名
        reader.setType(req.type()); // 更新读者类型
        reader.setDepartment(req.department()); // 更新院系
        reader.setPhone(req.phone()); // 更新联系电话
        // 密码非空时才更新（允许仅修改基本信息而不重置密码）
        if (req.password() != null && !req.password().isBlank()) { // 密码非空时才更新，支持仅修改基本信息场景
            if (req.password().length() < 6) { // 校验密码长度，防止弱密码
                throw new IllegalArgumentException("密码至少 6 位"); // 抛出参数异常，触发 400 响应
            }
            reader.setPassword(passwordEncoder.encode(req.password())); // 设置 BCrypt 加密后的新密码
        }
        Reader saved = repository.save(reader); // 持久化更新
        return ReaderResponse.from(saved); // 转换为响应 DTO 返回
    }

    /**
     * 删除读者
     *
     * @param id 读者 ID
     */
    @Transactional // 开启事务，保证删除操作的原子性
    public void deleteReader(Long id) { // 删除读者业务入口方法
        if (!repository.existsById(id)) { // 校验读者是否存在，避免删除不存在记录
            throw new IllegalArgumentException("读者不存在: " + id); // 抛出参数异常，触发 400 响应
        }
        repository.deleteById(id); // 按主键删除读者
    }

    /**
     * 读者登录校验
     *
     * @param readerNo 学号/工号
     * @param password 明文密码
     * @return 读者实体（成功）或 null（失败）
     */
    public Reader login(String readerNo, String password) { // 读者登录校验方法
        Reader reader = repository.findByReaderNo(readerNo).orElse(null); // 按学号/工号查询读者，不存在返回 null
        if (reader == null || !passwordEncoder.matches(password, reader.getPassword())) { // 读者不存在或密码比对失败，统一返回 null 避免泄露读者是否存在
            return null; // 返回 null 表示登录失败，由上层转换为 401 响应
        }
        reader.setLastLoginAt(LocalDateTime.now()); // 记录本次登录时间，用于安全审计
        repository.save(reader); // 持久化登录时间更新
        return reader; // 返回读者实体，供上层生成登录态
    }

    /**
     * 增加读者当前借阅数（借书时调用）
     *
     * @param reader 读者实体
     */
    @Transactional // 开启事务，保证借阅计数更新的原子性
    public void incrementBorrowCount(Reader reader) { // 增加读者当前借阅计数方法（借书时调用）
        reader.setCurrentBorrowCount(reader.getCurrentBorrowCount() + 1); // 当前借阅数加 1
        repository.save(reader); // 持久化借阅计数更新
    }

    /**
     * 减少读者当前借阅数（还书时调用）
     *
     * @param reader 读者实体
     */
    @Transactional // 开启事务，保证借阅计数更新的原子性
    public void decrementBorrowCount(Reader reader) { // 减少读者当前借阅计数方法（还书时调用）
        reader.setCurrentBorrowCount(Math.max(0, reader.getCurrentBorrowCount() - 1)); // 当前借阅数减 1，下限为 0 防止负数
        repository.save(reader); // 持久化借阅计数更新
    }

    /**
     * 读者总数
     *
     * @return 读者总数
     */
    public long getTotalReaderCount() { // 读者总数聚合方法（只读）
        return repository.count(); // 调用仓储 count 聚合查询
    }
}
