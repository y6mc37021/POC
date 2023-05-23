package com.service.chat.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.service.chat.model.BaseSession;
import com.service.chat.util.ChatUtil;

@Service
public class SessionService{

    private static final Logger log = LoggerFactory.getLogger(SessionService.class);
    private final Map<String, BaseSession> sessionMap;

    
    public SessionService() {
        this.sessionMap = new ConcurrentHashMap<>();
    }
    
    public BaseSession startSession() {
        BaseSession baseSession = new BaseSession();
        baseSession.setSessionId(ChatUtil.usingRandomUUID());
        this.sessionMap.put(baseSession.getSessionId(), baseSession);
        log.info("Starting session: {}", baseSession);
        return baseSession;
    }
    
    public void updateSocketSessionId(String sessionId) {
    	BaseSession baseSession = this.sessionMap.get(sessionId);
        if(baseSession == null) {
            return;
        }
        if(baseSession.getSessionId() != null) {
            this.sessionMap.remove(baseSession.getSessionId());
        }
        baseSession.setSessionId(sessionId);
        this.sessionMap.put(sessionId, baseSession);
    }
    
}
