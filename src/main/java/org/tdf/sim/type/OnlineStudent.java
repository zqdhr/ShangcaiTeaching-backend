package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class OnlineStudent {

    @JsonProperty("user_id")
    public String userID;

    public String name;

    @JsonProperty("class_name")
    public String className;

    @JsonProperty("grade_name")
    public String gradeName;

    public int type;  // 0 学生 1 老师

}
