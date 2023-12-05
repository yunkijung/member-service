package com.loglrs.memberservice.security.jwt.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MemberAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }


        Object principal = authentication.getPrincipal();

        if (principal instanceof LoginInfoDto) {
            return Optional.of(((LoginInfoDto) principal).getMemberId());
        } else {
            // Handle the case where principal is not a LoginInfoDto
            return Optional.empty(); // or throw an exception, depending on your needs
        }
    }
}
