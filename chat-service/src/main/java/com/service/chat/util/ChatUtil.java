package com.service.chat.util;

import java.util.UUID;

public class ChatUtil {
	
	public static String usingRandomUUID() {
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString().replaceAll("-", "");
	}
}
