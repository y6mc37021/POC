package com.service.chat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.gson.Gson;
import com.service.chat.model.ChatMessage;

@SpringBootTest
class RecieveNotificationIfLeft {
	
	static final String WEBSOCKET_URI = "http://localhost:8080";
	static final String WEBSOCKET_TOPIC = "/topic/public";
	static final String WEBSOCKET_CHAT_LEAVE = "/app/leaveRoom";
	static final String WEBSOCKET_WS = "/ws";
	static final  String message = " is typing...";

	BlockingQueue<ChatMessage> blockingQueue;
	WebSocketStompClient stompClient;
	ChatMessage chatmsg=null;
	
	@BeforeEach
	public void setup() {

		blockingQueue = new LinkedBlockingDeque<>();

		WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
		List<Transport> transports = new ArrayList<>(2);
		transports.add(new WebSocketTransport(simpleWebSocketClient));
		SockJsClient sockJsClient = new SockJsClient(transports);
		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
	}
	
	@Test
	public void recieveNotificationIfSomeoneLeaves() throws Exception {
		
		StompSession session = stompClient.connectAsync(WEBSOCKET_URI+WEBSOCKET_WS, new StompSessionHandlerAdapter() {
		}).get(1, TimeUnit.SECONDS);
		session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());
		
		ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setSender("John");
        session.send(WEBSOCKET_CHAT_LEAVE, chatMessage);
        
        ChatMessage recivedMsg = blockingQueue.poll(5, TimeUnit.SECONDS);
		Assertions.assertEquals(ChatMessage.MessageType.LEAVE, recivedMsg.getType());
	}
	
	class DefaultStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return byte[].class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			String json = new String((byte[]) o);
			blockingQueue.offer(new Gson().fromJson(json, ChatMessage.class));
		}
	}
	

}
