package com.service.chat.model;


public class ChatRoomInfo{

	private String roomName;
	private String userName;
	private Long roomId;
    private Long userId;
    
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	@Override
	public String toString() {
		return "ChatRoomInfo [roomName=" + roomName + ", userName=" + userName + ", roomId=" + roomId + ", userId="
				+ userId + "]";
	}
    
    
    
    
    
    
}
