package com.service.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.service.chat.model.BaseSession;
import com.service.chat.model.ConnectRequest;
import com.service.chat.service.SessionService;

@Controller
public class SessionController {

    private static final Logger log = LoggerFactory.getLogger(SessionController.class);
    private final com.service.chat.service.SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @ResponseBody
    @RequestMapping(path="/startSession",method=RequestMethod.GET)
    public BaseSession startSession() {
        var session = this.sessionService.startSession();
        log.info("Session started: {}", session);
        return session;
    }

    @MessageMapping("/socketConnect")
    public void socketConnect(@Payload ConnectRequest connectRequest, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Starting new session for user: {}", connectRequest.getSessionId());
        this.sessionService.updateSocketSessionId(connectRequest.getSessionId());
    }

}
