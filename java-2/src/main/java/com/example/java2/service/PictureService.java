package com.example.java2.service;

import com.example.java2.dto.PictureResponse;
import com.example.java2.model.Picture;
import com.example.java2.repository.PictureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 图片业务层
 *
 * 采用本地生成器模拟"每日一图"外部源，使用 picsum.photos 作为图片提供方，
 * 内置文案库保证离线也可演示完整链路。
 */
@Service
@Transactional
public class PictureService {

    /** 文案标题库 */
    private static final String[] TITLES = {
            "晨曦微光", "云端漫步", "城市夜色", "山林秘境", "海岸线的低语",
            "星空下的誓言", "秋日私语", "冬日暖阳", "雨后初晴", "花开无声",
            "湖光山色", "沙漠绿洲", "霓虹之梦", "乡村清晨", "峡谷回响"
    };

    /** 文案描述库 */
    private static final String[] EXPLANATIONS = {
            "光影交错间，时间仿佛静止。这一刻，世界温柔得像一首诗。",
            "行走于云端，俯瞰大地的褶皱，方知天地之广袤。",
            "万家灯火渐次亮起，每盏灯下都有一个属于夜晚的故事。",
            "深入山林的腹地，听溪水与鸟鸣合奏，找回久违的宁静。",
            "海浪一遍遍抚摸沙滩，像是在诉说一个永不厌倦的秘密。",
            "仰望浩瀚星河，渺小如尘，却也因此拥有整个宇宙。",
            "金黄的落叶铺满小径，踩上去沙沙作响，是秋天写给大地的信。",
            "冬日的阳光斜斜地洒下，把寒冷也染上了一层暖意。",
            "雨后的天空格外澄澈，连空气都带着泥土的清香。",
            "花开时不喧哗，凋零时不悲伤，它只是认真地活过每一刻。"
    };

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    /**
     * 获取今日一图（若当天已生成则复用，否则新生成）
     */
    public PictureResponse today() {
        LocalDate today = LocalDate.now();
        return pictureRepository.findByPictureDate(today)
                .map(PictureResponse::from)
                .orElseGet(() -> PictureResponse.from(generateForDate(today)));
    }

    /**
     * 随机获取一张新图（刷新按钮）
     */
    public PictureResponse random() {
        Picture p = generateRandom();
        return PictureResponse.from(pictureRepository.save(p));
    }

    /**
     * 收藏夹列表
     */
    public List<PictureResponse> favorites() {
        return pictureRepository.findByFavoritedTrueOrderByCreatedAtDesc()
                .stream().map(PictureResponse::from).toList();
    }

    /**
     * 全部图片列表（按日期倒序）
     */
    public List<PictureResponse> all() {
        return pictureRepository.findAllByOrderByPictureDateDesc()
                .stream().map(PictureResponse::from).toList();
    }

    /**
     * 切换收藏状态
     */
    public PictureResponse toggleFavorite(Long id) {
        Picture p = pictureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在: " + id));
        p.setFavorited(!p.getFavorited());
        return PictureResponse.from(pictureRepository.save(p));
    }

    /**
     * 点赞 +1
     */
    public PictureResponse like(Long id) {
        Picture p = pictureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("图片不存在: " + id));
        p.setLikes(p.getLikes() + 1);
        return PictureResponse.from(pictureRepository.save(p));
    }

    /**
     * 删除
     */
    public void delete(Long id) {
        if (!pictureRepository.existsById(id)) {
            throw new IllegalArgumentException("图片不存在: " + id);
        }
        pictureRepository.deleteById(id);
    }

    private Picture generateForDate(LocalDate date) {
        // 用日期作为种子保证同一天生成同样的图
        long seed = date.toEpochDay();
        java.util.Random rnd = new java.util.Random(seed);
        String externalId = "pic-" + date.toEpochDay();
        // 检查是否已存在
        return pictureRepository.findByExternalId(externalId)
                .orElseGet(() -> pictureRepository.save(buildPicture(externalId, date, rnd)));
    }

    private Picture generateRandom() {
        long seed = System.currentTimeMillis();
        java.util.Random rnd = new java.util.Random(seed);
        String externalId = "pic-" + seed;
        return buildPicture(externalId, LocalDate.now(), rnd);
    }

    private Picture buildPicture(String externalId, LocalDate date, java.util.Random rnd) {
        int imgId = 10 + rnd.nextInt(1000);
        String imageUrl = "https://picsum.photos/seed/" + imgId + "/800/500";
        String title = TITLES[rnd.nextInt(TITLES.length)];
        String explanation = EXPLANATIONS[rnd.nextInt(EXPLANATIONS.length)];
        return new Picture(externalId, imageUrl, title, explanation, date);
    }
}
