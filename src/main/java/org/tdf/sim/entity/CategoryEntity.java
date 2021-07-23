package org.tdf.sim.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "category")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class CategoryEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "category_name_chs", nullable = false)
    private String categoryNameChs;

    @Column(name = "category_name_en", nullable = false)
    private String categoryNameEn;

    @Column(name = "parent_id")
    private String parentID;

    @Column(name = "status", columnDefinition = "smallint default 1")
    private int status;

    @Column(name = "priority")
    private int priority;

    @Column(name = "annex")
    private String annex;

    /**
     * 1.目录 2.课程 3.实验
     */
    @Column(name = "type", nullable = false)
    private int type;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

