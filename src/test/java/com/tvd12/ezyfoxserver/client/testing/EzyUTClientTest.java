package com.tvd12.ezyfoxserver.client.testing;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.entity.EzyArray;
import com.tvd12.ezyfox.factory.EzyEntityFactory;
import com.tvd12.ezyfox.util.EzyEntityArrays;
import com.tvd12.ezyfoxserver.client.EzyUTClient;
import com.tvd12.ezyfoxserver.client.config.EzyClientConfig;
import com.tvd12.ezyfoxserver.client.constant.EzyCommand;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;
import com.tvd12.ezyfoxserver.client.socket.EzyTcpSocketClient;
import com.tvd12.ezyfoxserver.client.socket.EzyUTSocketClient;
import static org.mockito.Mockito.*;

import java.util.Random;

@SuppressWarnings("resource")
public class EzyUTClientTest {
	
	@Test
	public void creation() {
		// given
		EzyClientConfig config = EzyClientConfig.builder()
				.build();
		
		// when
		// then
		new EzyUTClient(config);
	}
	
	@Test
	public void udpConnectHostPort() {
		// given
		String host = "host";
		int port = new Random().nextInt();
		EzyClientConfig config = EzyClientConfig.builder()
				.build();
		EzyUTSocketClient mockSocketClient = mock(EzyUTSocketClient.class);
		EzyUTClient sut = new EzyUTClient(config) {
			protected EzyTcpSocketClient newTcpSocketClient() {
				return mockSocketClient;
			}
		};
		
		// when
		sut.udpConnect(host, port);
		
		// then
		verify(mockSocketClient, times(1)).udpConnect(host, port);
	}
	
	@Test
	public void udpConnectPort() {
		// given
		int port = new Random().nextInt();
		EzyClientConfig config = EzyClientConfig.builder()
				.build();
		EzyUTSocketClient mockSocketClient = mock(EzyUTSocketClient.class);
		EzyUTClient sut = new EzyUTClient(config) {
			protected EzyTcpSocketClient newTcpSocketClient() {
				return mockSocketClient;
			}
		};
		
		// when
		sut.udpConnect(port);

		// then
		verify(mockSocketClient, times(1)).udpConnect(port);
	}
	
	@Test
	public void updSendRequest() {
		// given
		EzyClientConfig config = EzyClientConfig.builder()
				.build();
		EzyUTSocketClient mockSocketClient = mock(EzyUTSocketClient.class);
		EzyUTClient sut = new EzyUTClient(config) {
			protected EzyTcpSocketClient newTcpSocketClient() {
				return mockSocketClient;
			}
		};
		EzyCommand cmd = EzyCommand.APP_REQUEST;
		EzyArray data = EzyEntityArrays.newArray("test");
		EzyRequest request = mock(EzyRequest.class);
		when(request.getCommand()).thenReturn(cmd);
		when(request.serialize()).thenReturn(data);
		EzyArray message = EzyEntityFactory.newArrayBuilder()
                .append(cmd.getId())
                .append(data)
                .build();
		
		// when
		sut.udpSend(request);
		
		// then
		verify(mockSocketClient).udpSendMessage(message);
	}
	
}
