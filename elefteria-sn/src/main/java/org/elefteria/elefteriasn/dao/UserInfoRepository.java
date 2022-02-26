package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
}
