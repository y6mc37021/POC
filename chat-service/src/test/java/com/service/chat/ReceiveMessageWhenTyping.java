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
import com.service.chat.model.UserTypingRequest;

@SpringBootTest
class ReceiveMessageWhenTyping {

	static final String WEBSOCKET_URI = "http://localhost:8080";
	static final String WEBSOCKET_TOPIC = "/topic/public";
	static final String WEBSOCKET_CHAT_TYPEING = "/app/userTyping";
	static final String WEBSOCKET_WS = "/ws";
	private final long END_TIME = System.currentTimeMillis() + 5000;
	static final  String message = " is typing...";

	BlockingQueue<UserTypingRequest> blockingQueue;
	WebSocketStompClient stompClient;
	
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
	public void shouldReceiveAMessageWhenTypingFromTheServer() throws Exception {
		StompSession session = stompClient.connectAsync(WEBSOCKET_URI+WEBSOCKET_WS, new StompSessionHandlerAdapter() {
		}).get(1, TimeUnit.SECONDS);
		session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());
		UserTypingRequest type = new UserTypingRequest();
		type.setUserName("John");
		while (System.currentTimeMillis() < END_TIME) {
			session.send(WEBSOCKET_CHAT_TYPEING, type);
		}
		UserTypingRequest type1 = blockingQueue.poll(2, TimeUnit.SECONDS);
		Assertions.assertEquals("John".concat(message), type1.getUserName());
		//session.send(WEBSOCKET_CHAT_TYPEING, type);
		//UserTypingRequest type1 = blockingQueue.poll(5, TimeUnit.SECONDS);
		//Assertions.assertEquals("John".concat(message), type1.getUserName());
		
		
		
		//UserTypingRequest recivedMsg = blockingQueue.poll(5, TimeUnit.SECONDS);
		//Assertions.assertEquals("John".concat(message), recivedMsg.getUserName());
		
		
	}
	
	class DefaultStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return byte[].class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			//blockingQueue.offer(new String((byte[]) o));
			String json = new String((byte[]) o);
			blockingQueue.offer(new Gson().fromJson(json, UserTypingRequest.class));
		}
	}
	

}
