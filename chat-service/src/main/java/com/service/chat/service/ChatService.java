package com.service.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.service.chat.model.ChatMessage;
import com.service.chat.model.ChatMessage.MessageType;
import com.service.chat.model.ChatRoomInfo;
import com.service.chat.model.ConnectRequest;
import com.service.chat.model.JoinRoomRequest;
import com.service.chat.model.UserTypingRequest;
import com.service.chat.util.ChatUtil;

@Service
public class ChatService {
		
	private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);
	private static final String CHAT_TOPIC = "/topic/public";
	private HashSet<String> roomIds = new HashSet<String>();
	private List<String> users = new ArrayList<>();
	private Map<HashSet<String>, List<String>> session = new ConcurrentHashMap<HashSet<String>, List<String>>();
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final String SPACE= " ";
	
	
    public ChatService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    
    
    public void sendMessage(ChatMessage request) {
    	if(request!=null && request.getContent()!=null) {
    		request.setContent(request.getContent().concat(SPACE).concat(getTime()));
    	}
        this.simpMessagingTemplate.convertAndSend(CHAT_TOPIC,  request);
        LOG.info(request.getContent());
    }
    
    
    public void userTyping(UserTypingRequest request) {
    	String message = " is typing...";
    	UserTypingRequest userTypingRequest = new UserTypingRequest();
    	userTypingRequest.setUserName(request.getUserName()+message);
    	this.simpMessagingTemplate.convertAndSend(CHAT_TOPIC, userTypingRequest);
    }
    
    
    public void userLeave(ChatRoomInfo chatRoomInfo) {
        String user = chatRoomInfo.getUserName();
        session.values()
                .stream()
                .flatMap(list -> list.stream())
                .collect(Collectors.toList()).remove(user);
        if(users.isEmpty()) {
        	session =  null;
        }
        
    }
    
    public void updateSocket(ConnectRequest connectRequest, SimpMessageHeaderAccessor headerAccessor) {
    	ChatMessage chat = new ChatMessage();
    	chat.setType(MessageType.JOIN);
    	chat.setSender(connectRequest.getUserName());
    	roomIds.add(ChatUtil.usingRandomUUID());
    	users.add(connectRequest.getUserName());
    	session.put(roomIds,users);
    	LOG.info(connectRequest.getUserName());
    	this.simpMessagingTemplate.convertAndSend(CHAT_TOPIC,  chat);
    }
    
    public void userJoin(JoinRoomRequest request) {
    	
    }
    public String getTime() {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    	Supplier<String> s=()->LocalDateTime.now().format(formatter);
    	return s.get();
    }
}
