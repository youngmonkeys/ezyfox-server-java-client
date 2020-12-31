package com.tvd12.ezyfoxserver.client.testing.entity;

import java.util.Random;

import org.testng.annotations.Test;

import com.tvd12.ezyfoxserver.client.entity.EzySimpleUser;

public class EzySimpleUserTest {

	@Test
	public void propertiesTest() {
		// given
		long userId = new Random().nextInt();
		String username = "testUserName";
		EzySimpleUser user = new EzySimpleUser(userId, username);
		
		// when
		long actualUserId = user.getId();
		String actualUsername = user.getName();
		
		// then
		assert actualUserId == userId;
		assert actualUsername.equals(username);
		assert user.hashCode() == Long.hashCode(userId);
		System.out.println(user);
	}
	
}
