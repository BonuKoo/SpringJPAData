package study.datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass //속성 공유
public class JpaBaseEntity {

    @Column(updatable = false)//이러면 값이 수정되지 않는다.
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist(){ //우선 미리 등록일자와 수정일자를 맞춰놓기.
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate
    public void preUpdate(){ //이건 수정용
        updatedDate = LocalDateTime.now();
    }
}
