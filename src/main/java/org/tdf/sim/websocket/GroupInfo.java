package org.tdf.sim.websocket;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInfo {
    //房间id
    @JsonProperty("id")
    public String id;

    //用户Id以0填充为机器人
    @JsonProperty("userList")
    public List<User> userList;

    //币种名称
    @JsonProperty("coin_name")
    public String coinName;

    //班级名称
    @JsonProperty("class_id")
    public String classID;

    @JsonProperty("code")
    @Builder.Default
    private static Integer maxKeys = 20;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class User{
        //用户的userId
        @JsonProperty("user_id")
        public String userId;
        //用户的名称
        @JsonProperty("user_name")
        public String userName;
        //用户的余额
        @JsonProperty("balance")
        public long balance;
        //用户类型：0.普通用户，1.房主，2机器人
        @JsonProperty("type")
        public int type;
    }

    public static void main(String[] args) throws JsonProcessingException {
        User user = new User("User_id","User_name",100L,1);
        User user2 = new User("User_id_2","User_name_2",100L,2);
        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user2);
        GroupInfo groupInfo = new GroupInfo("ID",list,"CoinName","1111");
        System.out.println(new ObjectMapper().writeValueAsString(groupInfo));
    }
}
