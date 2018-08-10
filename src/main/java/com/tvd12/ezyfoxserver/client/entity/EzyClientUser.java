package com.tvd12.ezyfoxserver.client.entity;

import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyfox.util.EzyProperties;
import com.tvd12.ezyfoxserver.entity.EzyDeliver;

public interface EzyClientUser extends EzyDeliver, EzyProperties, EzyDestroyable {

	/**
	 * Get user id
	 * 
	 * @return the user id
	 */
	long getId();
	
	/**
	 * Get user name
	 * 
	 * @eturn the user name
	 */
	String getName();
	
	/**
	 * Get zone id
	 * 
	 * @return the zone id
	 */
	int getZoneId();
	
	/**
	 * Get current session
	 * 
	 * @return the current session
	 */
	EzyClientSession getSession();
	
	/**
	 * Add new session
	 * 
	 * @param session the session to add
	 */
	void setSession(EzyClientSession session);
	
}
