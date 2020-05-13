package com.citylib.citylibservices.repository;

import com.citylib.citylibservices.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByDefTrue();
}
