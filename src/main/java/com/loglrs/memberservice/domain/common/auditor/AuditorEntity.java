package com.loglrs.memberservice.domain.common.auditor;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditorEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    private Long createdBy;


    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    private Long updatedBy;

    @PrePersist
    public void PrePersist(){ //영속성컨텍스트 일어나기 전에 시행
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        lastModifiedDate = now;
    }

    @PreUpdate
    public void PreUpdate(){ // 업데이트 일어나기 전에 실행
        lastModifiedDate = LocalDateTime.now();
    }

}
