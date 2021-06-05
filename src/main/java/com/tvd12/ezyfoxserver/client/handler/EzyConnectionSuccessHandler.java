package com.tvd12.ezyfoxserver.client.handler;

import com.tvd12.ezyfox.sercurity.EzyKeysGenerator;
import com.tvd12.ezyfoxserver.client.constant.EzyConnectionStatus;
import com.tvd12.ezyfoxserver.client.event.EzyEvent;
import com.tvd12.ezyfoxserver.client.request.EzyHandshakeRequest;
import com.tvd12.ezyfoxserver.client.request.EzyRequest;

import java.security.KeyPair;
import java.util.UUID;

/**
 * Created by tavandung12 on 10/1/18.
 */

@SuppressWarnings("rawtypes")
public class EzyConnectionSuccessHandler extends EzyAbstractEventHandler {
	
    @Override
    public final void handle(EzyEvent event) {
    	client.setStatus(EzyConnectionStatus.CONNECTED);
        sendHandshakeRequest();
        postHandle();
    }

    protected void postHandle() {}

    protected void sendHandshakeRequest() {
        EzyRequest request = newHandshakeRequest();
        client.send(request);
    }

    protected final EzyRequest newHandshakeRequest() {
        EzyHandshakeRequest request = new EzyHandshakeRequest(
                getClientId(),
                generateClientKey(),
                "JAVA",
                "1.1.1",
                client.isEnableSSL(),
                getStoredToken()
        );
        return request;
    }

    protected String getClientId() {
        String id = UUID.randomUUID().toString();
        return id;
    }

    protected byte[] generateClientKey() {
    	if(!client.isEnableSSL()) {
    		return null;
    	}
    	KeyPair keyPair = EzyKeysGenerator.builder()
    		.build()
    		.generate();
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privatekey = keyPair.getPrivate().getEncoded();
        client.setPublicKey(publicKey);
        client.setPrivateKey(privatekey);
        return publicKey;
    }

    protected  String getStoredToken() {
        return "";
    }
}
