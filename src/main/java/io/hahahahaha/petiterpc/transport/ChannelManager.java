package io.hahahahaha.petiterpc.transport;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.hahahahaha.petiterpc.common.Address;

/**
 * @author shibinfei
 *
 */
public enum ChannelManager {
	
	INSTANCE;
	
	public static ChannelManager getInstance() {
		return INSTANCE;
	}
	
	/**
	 * 每个地址对应一组Channel. (即一组Netty channel)
	 */
	ConcurrentMap<Address, AddressChannelList> addressChannelListMapping = Maps.newConcurrentMap();
	
	/**
	 * 服务对应的Channel地址
	 */
	ConcurrentMap<Class<?>, CopyOnWriteArrayList<AddressChannelList>> serviceChannelListMapping = Maps.newConcurrentMap();
	
	/**
	 * 根据服务查找对应的所有的Channel组. 
	 * @param serviceClass
	 * @return
	 */
	public CopyOnWriteArrayList<AddressChannelList> lookup(Class<?> serviceClass) {
		
		CopyOnWriteArrayList<AddressChannelList> list = serviceChannelListMapping.get(serviceClass);
		
		if (list != null) {
			return list;
		}
		
		CopyOnWriteArrayList<AddressChannelList> newList = Lists.newCopyOnWriteArrayList();
		list = serviceChannelListMapping.putIfAbsent(serviceClass, newList);
		
		if (list == null) {
			return newList;
		}
		
		return list;
	}
	
	
	/**
	 * 建立服务和Channel组的映射
	 * @param serviceClass
	 * @param addressChannelList
	 * @return
	 */
	public boolean manage(Class<?> serviceClass, AddressChannelList addressChannelList) {
		CopyOnWriteArrayList<AddressChannelList> list = lookup(serviceClass);
		return list.addIfAbsent(addressChannelList);
	}
	
}