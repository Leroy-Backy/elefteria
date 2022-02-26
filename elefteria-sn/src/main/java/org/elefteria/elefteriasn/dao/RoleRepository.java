package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
