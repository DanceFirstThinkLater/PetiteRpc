package io.hahahahaha.petiterpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import io.hahahahaha.petiterpc.common.ReadWriteList;
import io.hahahahaha.petiterpc.common.Request;
import io.hahahahaha.petiterpc.consumer.ConsumerFuture;
import io.hahahahaha.petiterpc.loadbalancer.LoadBalancer;
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
	
	public JDKProxy(LoadBalancer loadBalancer, Class<?> interfaceClass) {
	    this.loadBalancer = loadBalancer;
	    this.interfaceClass = interfaceClass;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
	    ChannelManager channelManager = ChannelManager.getInstance();
	    
	    ReadWriteList<AddressChannelList> addressChannelLists = channelManager.lookup(interfaceClass);
	    
	    AddressChannelList addressChannelList = loadBalancer.select(addressChannelLists);
	    TransportChannel channel = addressChannelList.next();

	    Request request = new Request();
		request.setInterfaceClass(interfaceClass);
		request.setMethodName(method.getName());
		request.setArgs(args);
		request.setArgTypes(method.getParameterTypes());
		
		channel.write(request);
		
		ConsumerFuture<?> future = new ConsumerFuture<>(method, args, request.getRequestId());
		return future.get(3, TimeUnit.SECONDS); // fixed timeout
	}

}
