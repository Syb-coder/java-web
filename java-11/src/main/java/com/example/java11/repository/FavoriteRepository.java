package com.example.java11.repository;

import com.example.java11.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏记录数据访问层
 * <p>
 * 提供对 favorites 表的 CRUD 操作及自定义查询。
 * </p>
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 查询用户收藏列表，按创建时间倒序
     *
     * @param userId 用户 ID
     * @return 收藏列表
     */
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 按分组获取用户收藏
     *
     * @param userId    用户 ID
     * @param groupName 分组名
     * @return 收藏列表
     */
    List<Favorite> findByUserIdAndGroupName(Long userId, String groupName);

    /**
     * 检查用户是否已收藏指定帖子
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 收藏记录，未收藏时返回 empty
     */
    Optional<Favorite> findByUserIdAndPostId(Long userId, Long postId);

    /**
     * 取消收藏（删除收藏记录）
     *
     * @param userId 用户 ID
     * @param postId 帖子 ID
     */
    void deleteByUserIdAndPostId(Long userId, Long postId);

    /**
     * 获取用户所有收藏分组名（去重）
     *
     * @param userId 用户 ID
     * @return 分组名列表
     */
    @Query("SELECT DISTINCT f.groupName FROM Favorite f WHERE f.userId = :userId")
    List<String> findDistinctGroupNameByUserId(@Param("userId") Long userId);
}
