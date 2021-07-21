package org.tdf.sim.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.tdf.sim.entity.UserEntity;
import org.tdf.sim.type.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Component
@EnableScheduling
public class ClassPool {

    public static final int HOMEOWNER = 1;
    public static final int USERTYPE = 0;
    public static final String ROBOTNAME = "ROBOT";

    private ConcurrentHashMap<String, List<GroupInfo>> classPool = new ConcurrentHashMap<>();

    private List<String> studentList = Collections.synchronizedList(new ArrayList<String>());

    private ConcurrentHashMap<String, GroupInfo> userID_info = new ConcurrentHashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * @param user
     * @return
     */
    public  Response add(UserEntity user) {
        try {
            if (studentList.contains(user.getId())) {
                return Response.error(Response.Code.LOGIN_REPEAT_ERROR,user.getName()+"已添加",objectMapper.writeValueAsString(userID_info.get(user.getId())));
            }
            if (classPool == null) {
                classPool = new ConcurrentHashMap<>();
                userID_info = new ConcurrentHashMap<>();
                List<GroupInfo.User> userList = new ArrayList<>();
                List<GroupInfo> groupInfoList = new ArrayList<>();
                userList.add(new GroupInfo.User(user.getId(), user.getName(), 0L, HOMEOWNER));
                GroupInfo groupInfo = new GroupInfo(generateID(), userList, "", user.getClassID());
                groupInfoList.add(groupInfo);
                classPool.put(user.getClassID(), groupInfoList);
                studentList.add(user.getId());
                userID_info.put(user.getId(), groupInfo);
            } else {
                if (classPool.containsKey(user.getClassID())) {
                    List<GroupInfo> list = classPool.get(user.getClassID());
                    if (list.size() == 0) {
                        List<GroupInfo.User> userList = new ArrayList<>();
                        userList.add(new GroupInfo.User(user.getId(), user.getName(), 0L, HOMEOWNER));
                        GroupInfo groupInfo = new GroupInfo(generateID(), userList, "", user.getClassID());
                        list.add(groupInfo);
                        classPool.put(user.getClassID(), list);
                        studentList.add(user.getId());
                        userID_info.put(user.getId(), groupInfo);
                    } else {
                        GroupInfo groupInfo = list.get(list.size() - 1);
                        List<GroupInfo.User> userEntityList = groupInfo.getUserList();
                        if (userEntityList.size() < 4) {
                            userEntityList.add(new GroupInfo.User(user.getId(), user.getName(), 0L, USERTYPE));
                            groupInfo.setUserList(userEntityList);
                            list.set(list.size() - 1, groupInfo);
                            classPool.put(user.getClassID(), list);
                            studentList.add(user.getId());
                            userID_info.put(user.getId(), groupInfo);
                        } else {
                            List<GroupInfo.User> userList = new ArrayList<>();
                            userList.add(new GroupInfo.User(user.getId(), user.getName(), 0L, HOMEOWNER));
                            GroupInfo groupInfo_new = new GroupInfo(generateID(), userList, "", user.getClassID());
                            list.add(groupInfo_new);
                            classPool.put(user.getClassID(), list);
                            studentList.add(user.getId());
                            userID_info.put(user.getId(), groupInfo_new);
                        }
                    }
                } else {
                    List<GroupInfo.User> userList = new ArrayList<>();
                    List<GroupInfo> groupInfoList = new ArrayList<>();
                    userList.add(new GroupInfo.User(user.getId(), user.getName(), 0L, HOMEOWNER));
                    GroupInfo groupInfo = new GroupInfo(generateID(), userList, "", user.getClassID());
                    groupInfoList.add(groupInfo);
                    classPool.put(user.getClassID(), groupInfoList);
                    studentList.add(user.getId());
                    userID_info.put(user.getId(), groupInfo);
                }
            }
            return Response.success(Response.Code.SUCCESS_WEBSOCKET_LOGIN, objectMapper.writeValueAsString(userID_info.get(user.getId())), user.getName());
        } catch (Exception e) {
            log.error("多人模式添加用户时发生错误,用户ID：" + user.getId(), e);
            return Response.error("多人模式添加用户时发生错误");
        }
    }

    /**
     * @param user
     * @return
     */
    public  Response remove(UserEntity user) {
        int h = 0;
        List<GroupInfo> list_test = new ArrayList<>();
        try {
            List<GroupInfo> list = classPool.get(user.getClassID());
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.get(i).getUserList().size(); j++) {
                    if (list.get(i).getUserList().get(j).getUserId().equals(user.getId())) {
                        if (list.get(i).getUserList().get(j).getType() == 1){
                            list.get(i).setCoinName("");
                        }
                        list.get(i).getUserList().remove(list.get(i).getUserList().get(j));
                        long counts = list.get(i).getUserList().stream().filter(users -> users.type == 2).count();
                        if (counts == list.get(i).getUserList().size() || list.get(i).getUserList().size() == 0) {
                            list.remove(list.get(i));
                        }else {
                            GroupInfo groupInfoList = list.get(i);
                            boolean haveHomeowner = false;
                            for (int l = 0;l<groupInfoList.getUserList().size();l++){
                                if (groupInfoList.getUserList().get(l).getType() == 1){
                                    haveHomeowner = true;
                                }
                            }
                            if (!haveHomeowner){
                                for (int l = 0;l<groupInfoList.getUserList().size();l++){
                                    if (groupInfoList.getUserList().get(l).getType() == 0){
                                        groupInfoList.getUserList().get(l).setType(1);
                                        break;
                                    }
                                }
                            }

                        }
                        if (list.isEmpty()){
                            classPool.remove(user.getClassID());
                        }
                        classPool.put(user.getClassID(), list);
                        studentList.remove(user.getId());
                        userID_info.remove(user.getId());
                        h = i;
                        list_test = list;
                        if(list.isEmpty()){
                            return Response.success(Response.Code.SUCCESS_WEBSOCKET_LOGOUT, objectMapper.writeValueAsString(new GroupInfo()), user.getName());
                        }
                        return Response.success(Response.Code.SUCCESS_WEBSOCKET_LOGOUT, objectMapper.writeValueAsString(list.get(i)), user.getName());
                    }
                }
            }

        } catch (Exception e) {
            log.error("退出多人模式时发生错误,用户ID：" + user.getId(), e);
            log.info("i值为："+h);
            log.info("list长度："+list_test.size());
            return Response.error("退出多人模式时发生错误");
        }
        return Response.error("退出多人模式时未删除");
    }

    /**
     * @param classID
     * @param roomID
     * @param coinName
     * @param number
     * @return
     */
    public  Response issueCurrency(String classID, String roomID, String coinName, Long number) {
        AtomicInteger i = new AtomicInteger();
        AtomicReference<GroupInfo> groupInfo = new AtomicReference<>(new GroupInfo());
        try {
            classPool.get(classID).stream().forEach(c -> {
                if (c.getId().equals(roomID)) {
                    if (c.getUserList().size() < 2) {
                        i.set(501);
                        return;
                    }
                    c.setCoinName(coinName);
                    c.getUserList().stream().forEach(user -> {
                        if (user.getType() == 1){
                            c.getUserList().get(0).setBalance(number);
                        }
                    });
                    i.set(200);
                    groupInfo.set(c);
                    return;
                }
            });
        } catch (Exception e) {
            log.error("发币时发生错误", e);
            i.set(500);
        }
        if (i.get() == 200) {
            try {
                return Response.success(Response.Code.SUCCESS_WEBSOCKET_ISSUECERRENCY, objectMapper.writeValueAsString(groupInfo.get()), coinName);
            } catch (JsonProcessingException e) {
                log.error("发币时获取房间详情发生错误", e);
                return Response.error("发币时获取房间详情发生错误");
            }
        } else if (i.get() == 501) {
            return Response.error("房间人数不足");
        }
        return Response.error("发币时发生错误");
    }

    /**
     * @param classID
     * @param roomID
     * @param from_ID
     * @param to_ID
     * @param number
     * @return 200.正确 500.错误 501.余额不足
     */
    public  Response transaction(String classID, String roomID, String from_ID, String to_ID, long number) {
        AtomicInteger i = new AtomicInteger();
        AtomicReference<GroupInfo> groupInfo = new AtomicReference<>(new GroupInfo());
        AtomicReference<String> from_name = new AtomicReference<>("");
        AtomicReference<String> to_name = new AtomicReference<>("");
        try {
            classPool.get(classID).stream().forEach(c -> {
                if (c.getId().equals(roomID)) {
                    c.getUserList().stream().forEach(user -> {
                        if (user.getUserId().equals(from_ID)) {
                            from_name.set(user.userName);
                            if (user.balance >= number) {
                                user.balance = user.balance - number;
                            } else {
                                i.set(501);
                                return;
                            }
                        }
                        if (user.getUserId().equals(to_ID)) {
                            to_name.set(user.userName);
                            user.balance = user.balance + number;
                        }
                        groupInfo.set(c);
                        i.set(200);
                    });
                }
            });
        } catch (Exception e) {
            log.error("转账时发生错误", e);
            return Response.error("转账时发生错误");
        }
        if (i.get() == 200) {
            try {
                return Response.success(Response.Code.SUCCESS_WEBSOCKET_TRANSFER, objectMapper.writeValueAsString(groupInfo.get()), from_name + "向" + to_name + "发起转账，");
            } catch (JsonProcessingException e) {
                log.error("转账获取房间详情时发生错误", e);
                return Response.error("转账时发生错误");
            }
        }
        return Response.error("余额不足");
    }

    /**
     * @param classID
     * @param roomID
     * @return 200.正确 500.错误 501.房间已满 502.房间无人
     */
    public  Response addRobot(String classID, String roomID) {
        AtomicInteger i = new AtomicInteger();
        AtomicReference<GroupInfo> groupInfo = new AtomicReference<>(new GroupInfo());
        try {
            classPool.get(classID).stream().forEach(c -> {
                if (c.getId().equals(roomID)) {
                    if (c.getUserList().size() > 3) {
                        i.set(501);
                        return;
                    }
                    if (c.getUserList().size() == 0) {
                        i.set(502);
                        return;
                    }
                    int count = 1;
                    for (int j = 0;j<c.getUserList().size();j++){
                        if (c.getUserList().get(j).getType() == 2){
                            count++;
                        }
                    }
                    c.getUserList().add(new GroupInfo.User(generateID(), ROBOTNAME+String.valueOf(count), 0L, 2));
                    groupInfo.set(c);
                    i.set(200);
                }
            });
        } catch (Exception e) {
            log.error("添加机器人发生错误", e);
            return Response.error("添加机器人发生错误");
        }
        if (i.get() == 200) {
            try {
                return Response.success(Response.Code.SUCCESS_WEBSOCKET_ROBOT, objectMapper.writeValueAsString(groupInfo.get()), "");
            } catch (JsonProcessingException e) {
                log.error("添加机器人获取房间详情时发生错误", e);
                return Response.error("添加机器人获取房间详情时发生错误");
            }
        } else if (i.get() == 501) {
            return Response.error("房间已满");
        } else if (i.get() == 502) {
            return Response.error("房间无人");
        }
        return Response.error("添加机器人失败");
    }

    public  List<GroupInfo.User> getUserList(String classID, String roomID) {
        AtomicReference<List<GroupInfo.User>> list = new AtomicReference<>(new ArrayList<>());
        classPool.get(classID).stream().forEach(c -> {
            if (c.getId().equals(roomID)) {
                list.set(c.getUserList());
                return;
            }
        });
        return list.get();
    }

    public  GroupInfo getGroupInfo(String classID, String roomID) {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setUserList(new ArrayList<>());
        AtomicReference<GroupInfo> list = new AtomicReference<>(groupInfo);
        classPool.get(classID).stream().forEach(c -> {
            if (c.getId().equals(roomID)) {
                list.set(c);
                return;
            }
        });
        return list.get();
    }

    public ConcurrentHashMap<String, GroupInfo> getUserID_info() {
        return userID_info;
    }

    public List<GroupInfo.User> massTexting(String id, int type, String classId) {
        AtomicReference<List<GroupInfo.User>> list = new AtomicReference<>(new ArrayList<>());
        if (userID_info.isEmpty()){
            return list.get();
        }
        if (type == 0) {
            return userID_info.get(id).getUserList();
        } else {
            classPool.get(classId).stream().forEach(groupInfo -> {
                if (groupInfo.getId().equals(id)) {
                    list.set(groupInfo.getUserList());
                    return;
                }
            });

        }
        return list.get();
    }

    public String generateID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        return str.replace("-", "");
    }


    public static void main(String[] args) {
        ConcurrentHashMap<String, List<String>> classPool = new ConcurrentHashMap<>();
        List<String> list = new ArrayList<>();
        list.add("test");
        classPool.put("123", list);
        System.out.println(list);

    }
}
