package com.loglrs.memberservice.domain.component_service.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberInfoOptionsDto {
    private Long genderId;
    private Long occupationId;
    private Long religionId;
    private Long raceId;
    private Long nationalityId;
}
