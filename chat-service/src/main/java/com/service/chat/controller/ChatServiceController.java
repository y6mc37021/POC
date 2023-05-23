package com.service.chat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.service.chat.model.ChatMessage;
import com.service.chat.model.ChatRoomInfo;
import com.service.chat.model.ConnectRequest;
import com.service.chat.model.JoinRoomRequest;
import com.service.chat.model.UserTypingRequest;
import com.service.chat.service.ChatService;

@Controller
public class ChatServiceController {
	
	private static final Logger log = LoggerFactory.getLogger(ChatServiceController.class);
	private ChatService chatService;
	
	public ChatServiceController(ChatService chatService) {
		this.chatService = chatService;
	}
	
	@MessageMapping("/connect")
    public void socketConnect(@Payload ConnectRequest connectRequest,SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", connectRequest.getUserName());
    	headerAccessor.getSessionAttributes().put("roomId", connectRequest.getRoomName());
    	this.chatService.updateSocket(connectRequest,headerAccessor);
    }
	
	@MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
		chatService.sendMessage(chatMessage);
		log.info(chatMessage.getContent());
    }
	
	@MessageMapping("/userTyping")
    public void userTyping(@Payload UserTypingRequest userTypingRequest) {
		//System.out.println("Typing....");
        this.chatService.userTyping(userTypingRequest);
    }
    
    @MessageMapping("/leaveRoom")
    public void userLeave(@Payload ChatRoomInfo room) {
        this.chatService.userLeave(room);
    }
    
    @MessageMapping("/joinRoom")
    public void userJoin(@Payload JoinRoomRequest user) {
        this.chatService.userJoin(user);
    }
    
    @MessageMapping("/away")
    public void userJoin() {
        //this.chatService.userJoin(user);
    }
    
}
