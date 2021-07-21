package org.tdf.sim.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tdf.sim.dao.UserDao;
import org.tdf.sim.entity.UserEntity;
import org.tdf.sim.service.SecurityService;
import org.tdf.sim.type.Response;
import org.tdf.sim.websocket.message.*;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ServerEndpoint("/webSocket/{sid}")
@Component
public class WebSocketServer {

    public static WebSocketServer webSocketServer;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ClassPool classPool;

    @Autowired
    private UserDao userDao;


    @PostConstruct
    public void init() {
        webSocketServer = this;
        webSocketServer.securityService = this.securityService;
        webSocketServer.classPool = this.classPool;
        webSocketServer.userDao = this.userDao;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static AtomicInteger onlineNum = new AtomicInteger();

    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    public void sendMessage(Session session, String message) throws IOException {
        if (session != null) {
            synchronized (session) {
//                System.out.println("发送数据：" + message);
                session.getBasicRemote().sendText(message);
            }
        }
    }

    //给指定用户发送信息
    public void sendInfo(String userID, String message) {
        Session session = sessionPools.get(userID);
        try {
            sendMessage(session, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String userID) {
//        String userID = securityService.getUserId(jwt);
        sessionPools.put(userID, session);
        addOnlineCount();
        try {
            sendMessage(session, "欢迎" + userID + "加入房间！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "sid") String userID) {
        sessionPools.remove(userID);
        subOnlineCount();
        System.out.println(userID + "断开webSocket连接！当前人数为" + onlineNum);
    }

    //收到客户端信息
    @OnMessage
    public void onMessage(String message) throws IOException {
        Response response = new Response();
        int i = 0;
        String classID = "";
        String roomID = "";
        try {
            JSONObject obj = JSON.parseObject(message);
            if (obj == null || obj.get("type") == null || obj.get("data") == null) {
                response.setCode(5000);
                response.setMessage("消息格式错误");
            } else {
                switch (obj.getString("type")) {
                    case "0":
                        //加入消息
                        JoinMessage joinMessage = objectMapper.readValue(obj.getString("data").getBytes(), JoinMessage.class);
                        response = webSocketServer.classPool.add(webSocketServer.userDao.findById(joinMessage.getUserID()).get());
                        messTexting(joinMessage.getUserID(),0,objectMapper.writeValueAsString(response),"");
                        break;
                    case "1":
//                        i = 1;
                        //加入机器人消息
                        AddRobotMessage addRobotMessage = objectMapper.readValue(obj.getString("data").getBytes(), AddRobotMessage.class);
                        response = webSocketServer.classPool.addRobot(addRobotMessage.getClassID(), addRobotMessage.getRoomID());
                        classID = addRobotMessage.getClassID();
                        roomID = addRobotMessage.getRoomID();
                        messTexting(roomID,1,objectMapper.writeValueAsString(response),classID);
                        break;
                    case "2":
//                        i = 2;
                        //发币消息
                        IssueCurrencyMessage issueCurrencyMessage = objectMapper.readValue(obj.getString("data").getBytes(), IssueCurrencyMessage.class);
                        response = webSocketServer.classPool.issueCurrency(issueCurrencyMessage.getClassID(), issueCurrencyMessage.getRoomID(), issueCurrencyMessage.getName(), issueCurrencyMessage.getAmount());
                        classID = issueCurrencyMessage.getClassID();
                        roomID = issueCurrencyMessage.getRoomID();
                        messTexting(roomID,2,objectMapper.writeValueAsString(response),classID);
                        break;
                    case "3":
                        i = 3;
                        //转账消息
                        TransferMessage transferMessage = objectMapper.readValue(obj.getString("data").getBytes(), TransferMessage.class);
//                        classID = webSocketServer.userDao.findById(transferMessage.getFrom()).get().getClassID();
                        response = webSocketServer.classPool.transaction(transferMessage.getClassID(), transferMessage.getRoomID(), transferMessage.getFrom(), transferMessage.getTo(), transferMessage.getAmount());
                        roomID = transferMessage.getRoomID();
                        messTexting(roomID,3,objectMapper.writeValueAsString(response),transferMessage.getClassID());
                        break;
                    case "4":
                        i = 4;
                        //退出消息
                        QuitMessage quitMessage = objectMapper.readValue(obj.getString("data").getBytes(), QuitMessage.class);
                        UserEntity userEntity = webSocketServer.userDao.findById(quitMessage.getUserID()).get();
                        response = webSocketServer.classPool.remove(userEntity);
                        roomID = quitMessage.getRoomID();
                        messTexting(roomID,4,objectMapper.writeValueAsString(response),userEntity.getClassID());
                        if (response.getCode() == 202){
                            sessionPools.remove(userEntity.getId());
                        }
                        break;
                    case "5":
                        i = 5;
                        //心跳
                        Heartbeat heartbeat = objectMapper.readValue(obj.getString("data").getBytes(), Heartbeat.class);
                        Session session = sessionPools.get(heartbeat.getUserID());
                        try {
                            Response response1 = new Response();
                            response1.setCode(200);
                            sendMessage(session, objectMapper.writeValueAsString(response));
                        } catch (Exception e) {
                            log.error("无连接信息");
                        }
                        break;
                    default:
                        response.setCode(5000);
                        response.setMessage("消息格式错误");
                        log.error("错误的消息：" + message);
                        return;
                }
                log.info("客户端：" + message + ",已收到");
            }
//            for (Session session : sessionPools.values()) {
//                try {
//                    sendMessage(session, objectMapper.writeValueAsString(response));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    continue;
//                }
//            }
//            if (i > 0 && i < 5) {
//                String finalClassID = classID;
//                String finalRoomID = roomID;
//                webSocketServer.classPool.getUserList(classID, roomID).stream().forEach(user -> {
//                    try {
//                        sendInfo(user.getUserId(), objectMapper.writeValueAsString(webSocketServer.classPool.getGroupInfo(finalClassID, finalRoomID)));
//                    } catch (JsonProcessingException e) {
//                        log.error("广播消息json格式错误", e);
//                    }
//                });
//            }
        } catch (Exception e) {
            log.error("处理消息时", e);
            for (Session session : sessionPools.values()) {
                sendMessage(session, objectMapper.writeValueAsString(Response.error("处理消息时发生错误")));
            }
        }
    }

    //错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误");
        throwable.printStackTrace();
    }

    public static void addOnlineCount() {
        onlineNum.incrementAndGet();
    }

    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }

    public void messTexting(String id, int type, String message,String classId) {
        webSocketServer.classPool.massTexting(id, type,classId).stream().forEach(user -> {
            if (user.getType() != 2) {
                sendInfo(user.getUserId(), message);
            }
        });
    }

//    @Scheduled(fixedRate=1000)
//    private void configureTasks() {
//        ConcurrentHashMap<String,GroupInfo> userID_info= webSocketServer.classPool.getUserID_info();
//        if (!userID_info.isEmpty()){
//            for(String key : userID_info.keySet()) {
//                try {
//                    sendInfo(key, objectMapper.writeValueAsString(Response.success(objectMapper.writeValueAsString(userID_info.get(key)))));
//                } catch (JsonProcessingException e) {
//                    log.error("错误的消息：" + e);
//                }
//            }
//        }
//    }

    public static void main(String[] args) {
//        JSONObject obj = JSON.parseObject("{\n" +
//                "    \"Name\":\"李念\"}");
//        System.out.println(obj);
//        int t = Integer.parseInt(obj.getString("test"));
//        System.out.println(0);


        ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    }

}
