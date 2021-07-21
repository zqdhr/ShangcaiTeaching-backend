package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Classes {

    @JsonProperty("class_id")
    public String classesID;

    @JsonProperty("class_name")
    public String className;

    @JsonProperty("grade_name")
    public String gradeName;

    @JsonProperty("department")
    public String department;

    @JsonProperty("major")
    public String major;

    public int numbers;

}

