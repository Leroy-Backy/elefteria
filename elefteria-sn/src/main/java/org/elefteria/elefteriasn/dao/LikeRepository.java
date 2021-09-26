package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
//    @Query("select l from Like l where l.post.id = ?1 and l.username = ?2")
    Optional<Like> findByPostIdAndUsername(Long postId, String username);

    @Query("select l.username from Like l where l.post.id = ?1")
    Set<String> getLikesUsernameByPostId(Long postId);
}
