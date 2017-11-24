package io.hahahahaha.petiterpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.hahahahaha.petiterpc.client.ClientFuture;
import io.hahahahaha.petiterpc.common.Request;
import io.hahahahaha.petiterpc.loadbalancer.LoadBalancer;
import io.hahahahaha.petiterpc.serialization.Serializer;
import io.hahahahaha.petiterpc.transport.AddressChannelList;
import io.hahahahaha.petiterpc.transport.ChannelManager;
import io.hahahahaha.petiterpc.transport.TransportChannel;

/**
 * 生成接口的代理类(JDK Proxy)
 * 
 * @author shibinfei
 */
public class JDKProxy implements InvocationHandler {
	
	private Class<?> interfaceClass;
	
	private LoadBalancer loadBalancer;
	
	private Serializer serializer;
	
	public JDKProxy(LoadBalancer loadBalancer, Serializer serializer) {
	    this.loadBalancer = loadBalancer;
	    this.serializer = serializer;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
	    ChannelManager channelManager = ChannelManager.getInstance();
	    
	    List<AddressChannelList> addressChannelLists = channelManager.lookup(interfaceClass);
	    
	    AddressChannelList addressChannelList = loadBalancer.select(addressChannelLists);
	    TransportChannel channel = addressChannelList.next();

	    Request request = new Request();
		request.setInterfaceName(interfaceClass.getName());
		request.setMethodName(method.getName());
		request.setArgs(args);
		
		byte[] requestBytes = serializer.serialize(request);
		channel.write(requestBytes);
		
		ClientFuture<?> future = new ClientFuture<>(method, args, request.getRequestId());
		return future.get(3, TimeUnit.SECONDS); // fixed timeout
	}

}
