package org.tdf.sim.type;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

public class UpdateUserPasPost {
    @JsonProperty("user_id_list")
    @NotBlank(message = "id不能为空")
    @Pattern(regexp = "[a-zA-Z0-9_]{1,20}", message = "用户名只能包含字母数字或下划线，且长度最大为20")
    public List<String> userIDs;

    @JsonProperty("password")
    public String password;
}
