package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(nativeQuery = true, value = "select p.* from post p join user u on u.id = p.user_id join following f on f.followed_user_id = u.id where f.follower_user_id = ?1 order by p.created_date desc",
    countQuery = "select count(p.id) from post p join user u on u.id = p.user_id join following f on f.followed_user_id = u.id where f.follower_user_id = ?1")
    Page<Post> findPostsForFeedByUserId(Long id, Pageable pageable);

    Page<Post> findByUserIdOrderByCreatedDateDesc(Long id, Pageable pageable);

    @Query("select p from Post p order by p.amountOfLikes desc")
    Page<Post> getPostsOrderByAmountOfLikesDesc(Pageable pageable);

    @Query(nativeQuery = true, value = "select p.* from post p left join comment c on c.post_id = p.id where datediff(now(), p.created_date) < 7 group by p.id order by (select count(c.id) + p.amount_of_likes) desc",
    countQuery = "select count(id) from post where datediff(now(), created_date) < 7")
    Page<Post> getPostFromLast7DaysOrderByLikesPlusComments(Pageable pageable);

    Optional<Post> findTop1ByUserUsernameOrderByCreatedDateDesc(String username);
}
