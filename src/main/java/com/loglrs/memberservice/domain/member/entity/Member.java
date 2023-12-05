package com.loglrs.memberservice.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loglrs.memberservice.domain.common.auditor.AuditorEntity;
import com.loglrs.memberservice.domain.member_image.entity.MemberImage;
import com.loglrs.memberservice.domain.role.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity // Database Table과 맵핑하는 객체.
@Table(name="member") // Database 테이블 이름 user3 와 User라는 객체가 맵핑.
@NoArgsConstructor // 기본생성자가 필요하다.
@Getter
public class Member extends AuditorEntity {
    @Id // 이 필드가 Table의 PK.
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // memberId는 자동으로 생성되도록 한다. 1,2,3,4
    private Long id;

    @Column(length = 255, unique = true)
    private String email;

    @JsonIgnore
    @Column(length = 500)
    private String password;

    @Column(length = 50)
    private String name;

    @Column
    private Boolean isLocked;


    @OneToOne
    @JoinColumn(name = "member_image_id")
    private MemberImage memberImage;

    @ManyToMany
    @JoinTable(name = "member_role",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();




    public Member(String email, String password, String name, Boolean isLocked) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.isLocked = isLocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "memberId=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void addRole(Role role) {
        roles.add(role);
    }


}

// User -----> Role