package com.example.java2.repository;

import com.example.java2.model.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 图片数据访问层
 */
@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

    Optional<Picture> findByExternalId(String externalId);

    Optional<Picture> findByPictureDate(LocalDate pictureDate);

    List<Picture> findByFavoritedTrueOrderByCreatedAtDesc();

    List<Picture> findAllByOrderByPictureDateDesc();
}
