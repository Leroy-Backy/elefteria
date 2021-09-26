package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPostId(Long id);

    @Query("select ui.avatar from UserInfo ui join User u where u.id = ?1")
    Optional<Image> getImageByUserId(Long id);
}
