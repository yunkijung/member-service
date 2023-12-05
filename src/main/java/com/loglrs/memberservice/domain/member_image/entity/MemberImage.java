package com.loglrs.memberservice.domain.member_image.entity;

import com.loglrs.memberservice.domain.common.auditor.AuditorEntity;
import com.loglrs.memberservice.domain.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class MemberImage extends AuditorEntity {
    @Id
    @Column(name="member_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originFileName;

    private String uniqueFileName;

    @OneToOne(mappedBy = "memberImage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Member member;
}
