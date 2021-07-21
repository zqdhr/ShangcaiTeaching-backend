package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Personnel {

    @JsonProperty("user_id")
    public String id;

    public String name;

    @JsonProperty("identification_number")
    public String identificationNumber;

    public int gender; // 0 男 1 女

    public String phone;

    @Override
    public String toString() {
        return "Personnel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", identificationNumber='" + identificationNumber + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", department='" + department + '\'' +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", classes='" + classes + '\'' +
                ", type=" + type +
                '}';
    }

    public String department; // 院系

    public String major; // 专业

    public String grade; // 年级

    public String classes; // 班级

    public int type; // 0 学生 1教师

}
