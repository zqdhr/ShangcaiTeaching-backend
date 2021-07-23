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
@Table(name = "course_experiment_category", indexes = {@Index(columnList = "link_id", name = "index_course_category_link_id")})
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
public class CourseExperimentCategoryEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "category_id", nullable = false)
    private String categoryID;

    @Column(name = "link_id", nullable = false)
    private String linkID;

    /**
     * 1.目录 2.课程 3.实验
     */
    @Column(name = "type", nullable = false)
    private int type;


    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
