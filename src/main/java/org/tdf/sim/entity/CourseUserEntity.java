package org.tdf.sim.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "course_user")
public class CourseUserEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;


    @Column(name = "course_id", nullable = false)
    private String courseID;

    @Column(name = "user_id", nullable = false)
    private int userID;

    @Column(name = "group_number", nullable = false)
    private int groupNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
