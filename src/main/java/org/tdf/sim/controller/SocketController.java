package org.tdf.sim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.tdf.sim.websocket.WebSocketServer;

@Controller
public class SocketController {

    @Autowired
    private WebSocketServer webSocketServer;

    @RequestMapping("/webSocket")
    public String socket() {
//        mav.addObject("userId", userId);
        return "webSocket";
    }
}
