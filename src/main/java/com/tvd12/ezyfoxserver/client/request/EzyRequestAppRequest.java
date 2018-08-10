package com.tvd12.ezyfoxserver.client.request;

import com.tvd12.ezyfox.constant.EzyConstant;
import com.tvd12.ezyfoxserver.client.constants.EzyClientCommand;

public class EzyRequestAppRequest extends EzyFixedCmdAppRequest {

	protected EzyRequestAppRequest(Builder builder) {
		super(builder);
	}
	
	@Override
	public EzyConstant getCommand() {
		return EzyClientCommand.APP_REQUEST;
	}
	
	@Override
	public Object getData() {
		return newArrayBuilder()
				.append(getAppId())
				.append(data)
				.build();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends EzyFixedCmdAppRequest.Builder<Builder> {
		
		@Override
		public EzyRequest build() {
			return new EzyRequestAppRequest(this);
		}
	}

}
