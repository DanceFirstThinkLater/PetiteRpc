package io.hahahahaha.petiterpc.transport.netty.client;

import io.hahahahaha.petiterpc.transport.Connection;

/**
 * @author shibinfei
 *
 */
public interface Connector {

	/**
	 * 连接远程节点
	 * @param ip
	 * @param port
	 * @return
	 */
	Connection connect(String host, int port);
	

	boolean isServiceAvaiable(Class<?> interfaceName);
	
	
	void shutdownGracefully();
}
