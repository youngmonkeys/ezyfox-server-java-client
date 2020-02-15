package com.tvd12.ezyfoxserver.client;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.entity.EzyData;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;

public class EzyUTClient extends EzyTcpClient {

	public EzyUTClient(EzyClientConfig config) {
		super(config);
	}
	
	@Override
	protected EzyTcpSocketClient newTcpSocketClient() {
		return new EzyUTSocketClient();
	}
	
	@Override
	public void udpConnect(String host, int port) {
		((EzyUTSocketClient)socketClient).udpConnect(host, port);
	}
	
	@Override
	public void udpSend(EzyRequest request) {
        Object cmd = request.getCommand();
        EzyData data = request.serialize();
        send((EzyCommand) cmd, (EzyArray) data);
    }

	@Override
    public void udpSend(EzyCommand cmd, EzyArray data) {
        EzyArray array = requestSerializer.serialize(cmd, data);
        if (socketClient != null) {
        	((EzyUTSocketClient)socketClient).udpSendMessage(array);
            printSentData(cmd, data);
        }
    }
	
	
}
