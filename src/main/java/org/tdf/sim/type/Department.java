package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public class Department {
    @JsonProperty("department_name")
    public String departmentName;
    public List<Major> majors;
    @Builder
    public static class Major {
        @JsonProperty("major_name")
        public String majorName;
        public List<Grade> grades;
        @Builder
        public static class Grade {
            @JsonProperty("grade_name")
            public String gradeName;
            public List<Classes> classes;
            @Builder
            public static class Classes {
                @JsonProperty("class_name")
                public String className;
                @JsonProperty("class_id")
                public String classID;
            }
        }
    }
}

