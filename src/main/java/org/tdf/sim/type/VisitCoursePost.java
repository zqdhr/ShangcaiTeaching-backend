package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class VisitCoursePost {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "[a-zA-Z0-9_]{1,20}", message = "用户名只能包含字母数字或下划线，且长度最大为20")
    @JsonProperty("user_id")
    private String userID;

    @NotBlank(message = "category_id不能为空")
    @JsonProperty("category_id")
    private String categoryID;

}
