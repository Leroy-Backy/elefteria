package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    //@Modifying and @Transactional are required to delete or update native sql statements
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "delete from notification n where id in( select id from (select id from notification where user_owner_id = (select id from user where username = ?1) and `read` = true order by created_date desc limit 99999 offset 10) n_temp)")
    void deleteAllExceptFirstTenUnread(String username);

    List<Notification> findByOwnerUsernameOrderByCreatedDateAsc(String username);

    Integer countByOwnerUsernameAndRead(String username, boolean read);


    @Modifying
    @Transactional
    @Query(value = "update notification set `read` = true where user_owner_id in (select id from user where username = ?1)", nativeQuery = true)
    void makeNotificationsRead(String username);
}
