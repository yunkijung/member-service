package com.loglrs.memberservice.domain.role.repository;

import com.loglrs.memberservice.domain.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author : yun
 * @mailto : yun@gmail.com
 * @created : 2023/12/04, Wed
 **/
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
