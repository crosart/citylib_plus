package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository extension for role related operations.
 *
 * @author crosart
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByDefTrue();
}
