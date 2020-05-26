package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JpaRepository extension for user related operations.
 *
 * @author crosart
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
