package io.hahahahaha.petiterpc.consumer;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import io.hahahahaha.petiterpc.loadbalancer.LoadBalancer;
import io.hahahahaha.petiterpc.loadbalancer.RoundRobinLoadBalancer;
import io.hahahahaha.petiterpc.proxy.JDKProxy;
import io.hahahahaha.petiterpc.registry.Registry;
import io.hahahahaha.petiterpc.transport.Connector;
import io.hahahahaha.petiterpc.transport.netty.consumer.NettyConnector;

/**
 * @author shibinfei
 *
 */
public class ConsumerContext {

	private Registry registry;
	
	private List<Class<?>> interfaces;
	
	private Connector connector = new NettyConnector();
	
	public void start() {
		registry.connect();
		
		for (Class<?> clazz : interfaces) {
			ConnectionWatcher connectionWatcher = new ConnectionWatcher(clazz, registry);
			connectionWatcher.setConnector(connector);
			connectionWatcher.start();
		}
	}
	
	
	public <T> T getService(Class<T> clazz) {
	    LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
	    JDKProxy proxy = new JDKProxy(loadBalancer, clazz);
	    Object object = Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, proxy);
        return clazz.cast(object);
	}


    public Registry getRegistry() {
        return registry;
    }


    public void setRegistry(Registry registry) {
        this.registry = registry;
    }


    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Class<?>> list) {
        this.interfaces = list;
    }
    
    public void addInterface(Class<?> clazz) {
        this.interfaces = new ArrayList<>();
        this.interfaces.add(clazz);
    }
	
}
