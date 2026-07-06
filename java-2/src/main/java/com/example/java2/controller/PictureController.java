package com.example.java2.controller;

import com.example.java2.dto.PictureResponse;
import com.example.java2.service.PictureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 图片 REST 接口
 */
@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    private final PictureService pictureService;

    public PictureController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    /** 今日一图 */
    @GetMapping("/today")
    public PictureResponse today() {
        return pictureService.today();
    }

    /** 随机刷新一张新图 */
    @GetMapping("/random")
    public PictureResponse random() {
        return pictureService.random();
    }

    /** 收藏夹 */
    @GetMapping("/favorites")
    public List<PictureResponse> favorites() {
        return pictureService.favorites();
    }

    /** 全部图片 */
    @GetMapping
    public List<PictureResponse> all() {
        return pictureService.all();
    }

    /** 切换收藏 */
    @PatchMapping("/{id}/favorite")
    public PictureResponse toggleFavorite(@PathVariable Long id) {
        return pictureService.toggleFavorite(id);
    }

    /** 点赞 */
    @PatchMapping("/{id}/like")
    public PictureResponse like(@PathVariable Long id) {
        return pictureService.like(id);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pictureService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handle(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
