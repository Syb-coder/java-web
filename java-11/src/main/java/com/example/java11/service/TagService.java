package com.example.java11.service;  // 服务层包，存放业务逻辑

import com.example.java11.dto.TagResponse;
import com.example.java11.model.PostTag;
import com.example.java11.model.Tag;
import com.example.java11.repository.PostTagRepository;
import com.example.java11.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签服务
 * <p>
 * 负责标签的创建、查询、热门标签推荐及帖子标签关联管理。
 * 标签不存在时自动创建，通过 usageCount 统计热度。
 * </p>
 */
@Service
public class TagService {

    /** 标签数据访问层 */
    private final TagRepository tagRepository;

    /** 帖子-标签关联数据访问层 */
    private final PostTagRepository postTagRepository;

    /**
     * 构造器注入依赖
     *
     * @param tagRepository     标签数据访问层
     * @param postTagRepository 帖子-标签关联数据访问层
     */
    public TagService(TagRepository tagRepository, PostTagRepository postTagRepository) {
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
    }

    /**
     * 获取所有标签
     *
     * @return 标签响应列表
     */
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门标签（按使用次数排序，前 10）
     *
     * @return 热门标签列表
     */
    public List<TagResponse> getHotTags() {
        return tagRepository.findTop10ByOrderByUsageCountDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取帖子的标签名列表
     *
     * @param postId 帖子 ID
     * @return 标签名列表，无标签时返回空列表
     */
    public List<String> getTagsByPost(Long postId) {
        List<PostTag> postTags = postTagRepository.findByPostId(postId);
        List<String> tagNames = new ArrayList<>();
        for (PostTag postTag : postTags) {
            Optional<Tag> tag = tagRepository.findById(postTag.getTagId());
            tag.ifPresent(t -> tagNames.add(t.getName()));
        }
        return tagNames;
    }

    /**
     * 保存帖子标签关联
     * <p>
     * 标签不存在时自动创建，已存在则更新使用次数。
     * 先清除帖子原有标签关联，再重新建立。
     * </p>
     *
     * @param postId   帖子 ID
     * @param tagNames 标签名列表，为 null 或空时仅清除原有关联
     */
    @Transactional
    public void savePostTags(Long postId, List<String> tagNames) {
        // 先删除帖子原有标签关联
        postTagRepository.deleteByPostId(postId);

        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        for (String tagName : tagNames) {
            String trimmedName = tagName.trim();
            if (trimmedName.isEmpty()) {
                continue;
            }
            // 标签不存在则创建
            Tag tag = tagRepository.findByName(trimmedName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(trimmedName);
                        return tagRepository.save(newTag);
                    });
            // 更新使用次数
            tag.setUsageCount(tag.getUsageCount() + 1);
            tagRepository.save(tag);
            // 建立关联
            PostTag postTag = new PostTag(postId, tag.getId());
            postTagRepository.save(postTag);
        }
    }

    /**
     * 创建标签
     *
     * @param name 标签名
     */
    @Transactional
    public void createTag(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("标签名不能为空");
        }
        if (tagRepository.findByName(name.trim()).isPresent()) {
            throw new RuntimeException("标签已存在");
        }
        Tag tag = new Tag(name.trim());
        tagRepository.save(tag);
    }

    /**
     * 删除标签
     *
     * @param id 标签 ID
     */
    @Transactional
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("标签不存在");
        }
        tagRepository.deleteById(id);
    }

    /**
     * 实体转 DTO
     *
     * @param tag 标签实体
     * @return 标签响应 DTO，实体为 null 时返回 null
     */
    private TagResponse toResponse(Tag tag) {
        if (tag == null) {
            return null;
        }
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setUsageCount(tag.getUsageCount());
        response.setCreatedAt(tag.getCreatedAt());
        return response;
    }
}
