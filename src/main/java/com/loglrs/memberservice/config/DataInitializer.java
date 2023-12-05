package com.loglrs.memberservice.config;


import com.loglrs.memberservice.domain.role.entity.Role;
import com.loglrs.memberservice.domain.role.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository
    ) {
        return args -> {
            if (roleRepository.count() == 0) { // role 테이블에 데이터가 없을 경우
                Role userRole = new Role();
//                userRole.setId(1L);
                userRole.setName("ROLE_USER");

                Role adminRole = new Role();
//                adminRole.setRoleId(2L);
                adminRole.setName("ROLE_ADMIN");

                roleRepository.save(userRole);
                roleRepository.save(adminRole);
            }

        };
    }
}
