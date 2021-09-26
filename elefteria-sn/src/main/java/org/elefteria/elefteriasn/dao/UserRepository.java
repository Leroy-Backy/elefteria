package org.elefteria.elefteriasn.dao;


import org.elefteria.elefteriasn.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Page<User> findByFollows_id(Long id, Pageable pageable);

    Page<User> findByFollowers_id(Long id, Pageable pageable);

    @Query(nativeQuery = true, value = "select if(count(u.id) > 0, 'true', 'false') from user u join following f on f.follower_user_id = u.id where u.username = ?1 and f.followed_user_id = ?2")
    boolean isSubscribed(String followerUsername, Long followedId);

    @Query("select u from User u join UserInfo ui on u.userInfo.id = ui.id where u.username like %?1% or ui.firstName like %?1% or ui.lastName like %?1%")
    Page<User> searchByUsernameOrFirstOrLastName(String keyword, Pageable pageable);

    @Query("select u from User u join UserInfo ui on u.userInfo.id = ui.id order by ui.amountOfFollowers desc")
    Page<User> findUsersOrderByFollowers(Pageable pageable);
}
