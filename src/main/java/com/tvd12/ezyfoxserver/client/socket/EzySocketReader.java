package com.tvd12.ezyfoxserver.client.socket;

import java.nio.ByteBuffer;
import java.util.List;

import com.tvd12.ezyfox.callback.EzyCallback;
import com.tvd12.ezyfox.codec.EzyMessage;
import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfoxserver.client.concurrent.EzySynchronizedQueue;
import com.tvd12.ezyfoxserver.client.constant.EzySocketConstants;
import com.tvd12.ezyfoxserver.client.util.EzyQueue;

public abstract class EzySocketReader extends EzySocketAdapter {

	protected byte[] sessionKey;
	protected final ByteBuffer buffer;
	protected final EzyQueue<EzyArray> dataQueue;
	protected final int readBufferSize;
	protected EzySocketDataDecoder decoder;
	protected final EzyCallback<EzyMessage> decodeBytesCallback;

	public EzySocketReader() {
		super();
		this.readBufferSize = EzySocketConstants.MAX_READ_BUFFER_SIZE;
		this.dataQueue = new EzySynchronizedQueue<>();
		this.buffer = ByteBuffer.allocateDirect(readBufferSize);
		this.decodeBytesCallback = new EzyCallback<EzyMessage>() {
			@Override
			public void call(EzyMessage message) {
				onMesssageReceived(message);
			}
		};
	}
	
	@Override
	protected void loop() {
		super.loop();
	}

	@Override
	protected void update() {
		while (true) {
			try {
				if(!active)
					return;
				this.buffer.clear();
				int bytesToRead = readSocketData();
				if(bytesToRead <= 0)
					return;
				buffer.flip();
				byte[] binary = new byte[buffer.limit()];
				buffer.get(binary);
				decoder.decode(binary, decodeBytesCallback);
			}
			catch (InterruptedException e) {
				logger.debug("socket reader interrupted", e);
				return;
			}
			catch (Exception e) {
				logger.warn("I/O error at socket-reader", e);
			}
		}
	}

	protected abstract int readSocketData();
	
	@Override
	protected void clear() {
		if(dataQueue != null)
			dataQueue.clear();
	}

	public void popMessages(List<EzyArray> buffer) {
		dataQueue.pollAll(buffer);
	}

	private void onMesssageReceived(EzyMessage message) {
		try {
			Object data = decoder.decode(message, sessionKey);
			dataQueue.add((EzyArray) data);
		}
		catch (Exception e) {
			logger.warn("decode error at socket-reader", e);
		}
	}
	
	public void setSessionKey(byte[] sessionKey) {
		this.sessionKey = sessionKey;
	}

	public void setDecoder(EzySocketDataDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	protected String getThreadName() {
		return "ezyfox-socket-reader";
	}
}
