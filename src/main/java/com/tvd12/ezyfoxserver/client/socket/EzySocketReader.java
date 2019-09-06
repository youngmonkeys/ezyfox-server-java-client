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

	protected ByteBuffer buffer;
	protected EzyQueue<EzyArray> dataQueue;
	protected EzySocketDataDecoder decoder;
	protected final int readBufferSize;
	protected final EzyCallback<EzyMessage> decodeBytesCallback;

	public EzySocketReader() {
		super();
		this.readBufferSize = EzySocketConstants.MAX_READ_BUFFER_SIZE;
		this.decodeBytesCallback = new EzyCallback<EzyMessage>() {
			@Override
			public void call(EzyMessage message) {
				onMesssageReceived(message);
			}
		};
	}

	@Override
	protected void loop() {
		this.dataQueue = new EzySynchronizedQueue<>();
		this.buffer = ByteBuffer.allocateDirect(readBufferSize);
		super.loop();
	}

	@Override
	protected void update() {
		while (true) {
			try {
				Thread.sleep(1);
				if(!active)
					return;
				this.buffer.clear();
				long bytesToRead = readSocketData();
				if(bytesToRead <= 0)
					return;
				buffer.flip();
				byte[] binary = new byte[buffer.limit()];
				buffer.get(binary);
				decoder.decode(binary, decodeBytesCallback);
			}
			catch (InterruptedException e) {
				logger.warn("socket reader interrupted", e);
				return;
			}
			catch (Exception e) {
				logger.warn("I/O error at socket-reader", e);
			}
		}
	}

	protected abstract long readSocketData();

	public void popMessages(List<EzyArray> buffer) {
		dataQueue.pollAll(buffer);
	}

	private void onMesssageReceived(EzyMessage message) {
		try {
			Object data = decoder.decode(message);
			dataQueue.add((EzyArray) data);
		}
		catch (Exception e) {
			logger.warn("decode error at socket-reader", e);
		}
	}

	public void setDecoder(EzySocketDataDecoder decoder) {
		this.decoder = decoder;
	}

	@Override
	protected String getThreadName() {
		return "ezyfox-socket-reader";
	}
}
