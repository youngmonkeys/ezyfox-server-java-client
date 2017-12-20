/**
 * 
 */
package com.tvd12.ezyfoxserver.client;

import static com.tvd12.ezyfoxserver.util.EzyProcessor.processWithException;
import static com.tvd12.ezyfoxserver.util.EzyProcessor.processWithLogException;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

import com.tvd12.ezyfoxserver.builder.EzyBuilder;
import com.tvd12.ezyfoxserver.client.constants.EzyClientCommand;
import com.tvd12.ezyfoxserver.client.constants.EzyConnectionError;
import com.tvd12.ezyfoxserver.client.context.EzyClientContext;
import com.tvd12.ezyfoxserver.client.controller.EzyConnectFailureController;
import com.tvd12.ezyfoxserver.client.socket.EzyClientChannelInitializer;
import com.tvd12.ezyfoxserver.codec.EzyCodecCreator;
import com.tvd12.ezyfoxserver.concurrent.EzyExecutors;
import com.tvd12.ezyfoxserver.constant.EzyConstant;
import com.tvd12.ezyfoxserver.util.EzyDestroyable;
import com.tvd12.ezyfoxserver.util.EzyLoggable;
import com.tvd12.ezyfoxserver.util.EzyShutdownable;
import com.tvd12.ezyfoxserver.util.EzyStartable;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

/**
 * @author tavandung12
 *
 */
public class EzyClientBoostrap 
		extends EzyLoggable 
		implements EzyStartable, EzyShutdownable, EzyDestroyable {

	protected int port;
    protected String host;
    protected EzyCodecCreator codecCreator;
    protected EzyClientContext clientContext;
    
    protected ChannelFuture closeFuture;
    protected EventLoopGroup eventLoopGroup;
    protected ChannelFuture connectionFuture;
    protected Future<?> eventLoopGroupFuture;
    
    protected ExecutorService startExecutorService;
    
    protected EzyClientBoostrap(Builder builder) {
    		this.host = builder.host;
    		this.port = builder.port;
        this.codecCreator = builder.codecCreator;
        this.clientContext = builder.clientContext;
        this.startExecutorService = builder.startExecutorService;
    }
    
    @Override
    public void start() throws Exception {
    		if(startExecutorService == null)
    			start0();
    		else
    			startExecutorService.execute(() -> processWithException(this::start0));
    }
    
    protected void start0() throws Exception {
        try {
        		eventLoopGroup = newLoopGroup();
        		// new client bootstrap
            Bootstrap b = newBootstrap(eventLoopGroup);
            
            getLogger().info("connecting to server ...");
            
            // connect to server
            connectionFuture = b.connect();
            
            // wait and listen connection
            connectionFuture.syncUninterruptibly();
            
            // process connection successful
            if(connectionFuture.isSuccess())
            		processConnectSuccess(connectionFuture);
        }
        catch(ConnectException e) {
        		getLogger().error("connect to server error", e);
        		processConnectFailure(EzyConnectionError.CONNECTION_REFUSED);
        }
        catch(NoRouteToHostException e) {
        		getLogger().error("connect to server error", e);
        		processConnectFailure(EzyConnectionError.NO_ROUTE_TO_HOST);
        }
        catch(SocketException e) {
        		getLogger().error("connect to server error", e);
        		processConnectFailure(EzyConnectionError.NETWORK_UNREACHABLE);
        }
        catch(Exception e) {
        		getLogger().error("connect to server error", e);
        		throw e;
        }

        finally {
        		eventLoopGroupFuture = eventLoopGroup
        				.shutdownGracefully()
        				.addListener(future -> 
        					getLogger().info("event loop group shutdown")
        				)
        				.sync();
        }
    }
    
    protected void processConnectFailure(EzyConstant error) {
    		// notify to controller
        notifyConnectionFailure(error);
        
        // destroy this boostrap
        destroy();
    }
    
    protected void notifyConnectionFailure(EzyConstant error) {
        EzyConnectFailureController ctrl = 
        		getClient().getController(EzyClientCommand.CONNECT_FAILURE);
        ctrl.handle(error);
    }
    
    protected void processConnectSuccess(ChannelFuture connectFuture) throws Exception {
    		getLogger().info("connect to server successfully");
    	
    		// get close future channel
        closeFuture = connectFuture.channel().closeFuture();
        
        // wait and listen close future state
        closeFuture.addListener(future ->
            getLogger().info("channel future close")
        )
        .sync();
    }
    
    protected Bootstrap newBootstrap(EventLoopGroup group) {
    		return new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(newChannelInitializer())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300 * 1000);
    }
    
    protected EventLoopGroup newLoopGroup() {
    		return new NioEventLoopGroup(1, EzyExecutors.newThreadFactory("clienteventloopgroup"));
    }
    
    protected ChannelInitializer<Channel> newChannelInitializer() {
    		return EzyClientChannelInitializer.builder()
    				.codecCreator(codecCreator)
    				.context(clientContext)
    				.build();
    }
    
    protected EzyClient getClient() {
    		return clientContext.getClient();
    }
    
    @Override
    public void destroy() {
    }
    
    @Override
    public void shutdown() {
    		if(closeFuture != null)
    			processWithLogException(closeFuture::getNow);
    		if(connectionFuture != null)
    			processWithLogException(connectionFuture::getNow);
    		if(eventLoopGroupFuture != null)
    			processWithLogException(eventLoopGroupFuture::getNow);
    		if(startExecutorService != null)
    			processWithLogException(startExecutorService::shutdown);
    }
    
    public static Builder builder() {
    		return new Builder();
    }
    
    public static class Builder implements EzyBuilder<EzyClientBoostrap> {
    	
    		protected int port;
        protected String host;
        protected EzyCodecCreator codecCreator;
        protected EzyClientContext clientContext;
        protected ExecutorService startExecutorService;
        
        public Builder port(int port) {
	        	this.port = port;
	        	return this;
        }
        
        public Builder host(String host) {
        		this.host = host;
            return this;
        }
        
        public Builder codecCreator(EzyCodecCreator codecCreator) {
        		this.codecCreator = codecCreator;
            return this;
        }
        
        public Builder clientContext(EzyClientContext clientContext) {
        		this.clientContext = clientContext;
            return this;
        }
        
        public Builder startExecutorService(ExecutorService startExecutorService) {
    			this.startExecutorService = startExecutorService;
    			return this;
        }
        
        @Override
        public EzyClientBoostrap build() {
        		return new EzyClientBoostrap(this);
        }
    }
    
}
