package com.zx.sms.connect.manager;

import java.util.List;

import com.zx.sms.BaseMessage;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Promise;

/**
 * @author Lihuanghe(18852780@qq.com)
 * 端口管理
 * 负责端口的打开，关闭，最大连接数据控制。，端口总速率设置，负载算法. 一个端口可以有多个tcp连接
 */
 public interface EndpointConnector<T extends EndpointEntity> {

	/**
	 *获取端口配置
	 */
	 T getEndpointEntity();
	
	/**
	 *打开一个端口 
	 */
	 ChannelFuture open() throws Exception;
	
	/**
	 * 关闭一个连接
	 */
	 void close(Channel channel) throws Exception;
	
	/**
	 *关闭端口的所有连接 
	 */
	 void close()throws Exception;
	
	/**
	 *根据负载均衡算法获取一个连接
	 */
	Channel fetch();
	/**
	 *获取端口当前连接数
	 */
	int getConnectionNum();

	/**
	 *连接创建成功后，将channel加入连接器，并发送用户事件 
	 */
	 boolean addChannel(Channel ch);
	 void removeChannel(Channel ch);
	 Channel[] getallChannel();
	
	/**
	 *异步发送消息，消息发送至网卡（写入tcp协议栈即表示完成）,发送前不检查连接是否可写
	 *@return 返回future对象，可获取消息写入网卡的成功状态，success表示写入网卡成功；这个方法无法获得对端回复的response 对象
	 */
	ChannelFuture asynwrite(Object msg);
	
	
	/**
	 *异步发送消息，消息发送至网卡（写入tcp协议栈即表示完成）,发送前不检查连接是否可写
	 *@return 返回future对象，可获取消息写入网卡的成功状态，success表示写入网卡成功。这个方法无法获得对端回复的response 对象
	 */
	ChannelFuture asynwriteUncheck(Object msg);
	
	/**
	 *同步发送消息，消息收到回复表示完成, 发送前检查连接是否可写
	 *@return 返回future对象，可获得对端回复的response 对象
	 */
	<K extends BaseMessage> Promise<K> synwrite(K msg);
	
	/**
	 *通过同一个连接同步发送一组消息, 发送前检查连接是否可写
	 *@return 返回future对象，可获得对端回复的response 对象
	 */
	<K extends BaseMessage> List<Promise<K>> synwrite(List<K> msgs);
	
	
	/**
	 *同步发送消息，消息收到回复表示完成,不检查连接是否可写
	 *@return 返回future对象，可获得对端回复的response 对象
	 */
	<K extends BaseMessage> Promise<K> synwriteUncheck(K msg);
	
	/**
	 *通过同一个连接同步发送一组消息,不检查连接是否可写
	 *@return 返回future对象，可获得对端回复的response 对象
	 */
	<K extends BaseMessage> List<Promise<K>> synwriteUncheck(List<K> msgs);
	
	
}
