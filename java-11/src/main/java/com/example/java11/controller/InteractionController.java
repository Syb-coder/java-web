package com.example.java11.controller;

import com.example.java11.dto.ApiResponse;
import com.example.java11.model.Favorite;
import com.example.java11.model.Follow;
import com.example.java11.model.User;
import com.example.java11.service.AuthService;
import com.example.java11.service.InteractionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 互动控制器
 * <p>
 * 统一处理点赞、收藏、关注三类互动行为。
 * 所有接口均需登录，从 session 获取当前用户 ID。
 * </p>
 */
@RestController
@RequestMapping("/api/interaction")
public class InteractionController {

    /** 互动服务 */
    private final InteractionService interactionService;

    /** 认证服务，用于获取当前登录用户 */
    private final AuthService authService;

    /**
     * 构造器注入依赖
     *
     * @param interactionService 互动服务
     * @param authService        认证服务
     */
    public InteractionController(InteractionService interactionService, AuthService authService) {
        this.interactionService = interactionService;
        this.authService = authService;
    }

    // ==================== 点赞 ====================

    /**
     * 切换点赞状态
     *
     * @param targetType 目标类型（POST 或 COMMENT）
     * @param targetId   目标 ID
     * @param session    HTTP 会话
     * @return data 为 true 表示已点赞，false 表示已取消；未登录返回 error
     */
    @PostMapping("/like")
    public ApiResponse toggleLike(@RequestParam String targetType,
                                  @RequestParam Long targetId,
                                  HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        boolean liked = interactionService.toggleLike(currentUser.getId(), targetType, targetId);
        return ApiResponse.success(liked ? "已点赞" : "已取消点赞", liked);
    }

    /**
     * 检查是否已点赞
     *
     * @param targetType 目标类型
     * @param targetId   目标 ID
     * @param session    HTTP 会话
     * @return data 为 true 已点赞，false 未点赞；未登录返回 error
     */
    @GetMapping("/liked")
    public ApiResponse isLiked(@RequestParam String targetType,
                               @RequestParam Long targetId,
                               HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        boolean liked = interactionService.isLiked(currentUser.getId(), targetType, targetId);
        return ApiResponse.success(liked);
    }

    // ==================== 收藏 ====================

    /**
     * 切换收藏状态
     *
     * @param postId  帖子 ID
     * @param session HTTP 会话
     * @return data 为 true 表示已收藏，false 表示已取消；未登录返回 error
     */
    @PostMapping("/favorite")
    public ApiResponse toggleFavorite(@RequestParam Long postId, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        boolean favorited = interactionService.toggleFavorite(currentUser.getId(), postId);
        return ApiResponse.success(favorited ? "已收藏" : "已取消收藏", favorited);
    }

    /**
     * 获取当前用户收藏列表
     *
     * @param session HTTP 会话
     * @return 收藏记录列表，未登录返回 error
     */
    @GetMapping("/favorites")
    public ApiResponse getFavorites(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<Favorite> list = interactionService.getFavorites(currentUser.getId());
        return ApiResponse.success(list);
    }

    /**
     * 获取当前用户收藏分组列表
     *
     * @param session HTTP 会话
     * @return 分组名列表，未登录返回 error
     */
    @GetMapping("/favorites/groups")
    public ApiResponse getFavoriteGroups(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<String> groups = interactionService.getFavoriteGroups(currentUser.getId());
        return ApiResponse.success(groups);
    }

    /**
     * 按分组获取收藏列表
     *
     * @param groupName 分组名
     * @param session   HTTP 会话
     * @return 收藏记录列表，未登录返回 error
     */
    @GetMapping("/favorites/group/{groupName}")
    public ApiResponse getFavoritesByGroup(@PathVariable String groupName, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<Favorite> list = interactionService.getFavoritesByGroup(currentUser.getId(), groupName);
        return ApiResponse.success(list);
    }

    // ==================== 关注 ====================

    /**
     * 切换关注状态
     *
     * @param userId  被关注用户 ID
     * @param session HTTP 会话
     * @return data 为 true 表示已关注，false 表示已取消；未登录返回 error
     */
    @PostMapping("/follow")
    public ApiResponse toggleFollow(@RequestParam Long userId, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        boolean following = interactionService.toggleFollow(currentUser.getId(), userId);
        return ApiResponse.success(following ? "已关注" : "已取消关注", following);
    }

    /**
     * 获取当前用户关注列表
     *
     * @param session HTTP 会话
     * @return 关注记录列表，未登录返回 error
     */
    @GetMapping("/following")
    public ApiResponse getFollowing(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<Follow> list = interactionService.getFollowingList(currentUser.getId());
        return ApiResponse.success(list);
    }

    /**
     * 获取当前用户粉丝列表
     *
     * @param session HTTP 会话
     * @return 粉丝记录列表，未登录返回 error
     */
    @GetMapping("/followers")
    public ApiResponse getFollowers(HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        if (currentUser == null) {
            return ApiResponse.error("未登录");
        }
        List<Follow> list = interactionService.getFollowerList(currentUser.getId());
        return ApiResponse.success(list);
    }
}
