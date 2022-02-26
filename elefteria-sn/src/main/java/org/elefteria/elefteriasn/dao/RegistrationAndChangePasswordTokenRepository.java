package org.elefteria.elefteriasn.dao;

import org.elefteria.elefteriasn.entity.RegistrationAndChangePasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationAndChangePasswordTokenRepository extends JpaRepository<RegistrationAndChangePasswordToken, Long> {

    Optional<RegistrationAndChangePasswordToken> findByTokenAndChangePassword(String token, boolean changePassword);
}
