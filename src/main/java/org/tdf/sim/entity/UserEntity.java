package org.tdf.sim.entity;

import lombok.*;
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
@Builder
@Table(name = "sim_user", indexes = {@Index(columnList = "class_id", name = "index_sim_user_class_id")})
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role_id", nullable = false)
    private ROLE_ID roleID;

    @Column(name = "class_id")
    private String classID;

    @Column(name = "gender", nullable = false)
    private GENDER gender;

    @Column(name = "identification_number", nullable = false)
    private String identificationNumber;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum ROLE_ID {
        NONE("0"), MANAGER("1"), TEACHER("2"), STUDENT("3");

        private String id;

        ROLE_ID(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public enum GENDER {
        MAN("0"), WOMAN("1");

        private String id;

        GENDER(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

}
