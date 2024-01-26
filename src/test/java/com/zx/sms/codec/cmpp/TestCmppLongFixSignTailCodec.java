package com.zx.sms.codec.cmpp;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.chinamobile.cmos.sms.SmsDcs;
import com.chinamobile.cmos.sms.SmsTextMessage;
import com.zx.sms.codec.AbstractTestMessageCodec;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.common.util.MsgId;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.SignatureType;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TestCmppLongFixSignTailCodec extends AbstractTestMessageCodec<CmppSubmitRequestMessage> {
	@Override
	protected int getVersion(){
		return 0x20;
	}

	//构造一个超出最大分片长度的签名
	private static String signTxt = "【温馨提示-超长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长长的签名】";
	protected EndpointEntity buildEndpointEntity() {
		EndpointEntity e = new CMPPClientEndpointEntity();
		e.setId(EndPointID);
		e.setSignatureType(new SignatureType(true,signTxt));
		return e;
	}
	
	@Test
	public void testCodecLong()
	{
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		
		msg.setDestterminalId(new String[]{"13800138000"});
		msg.setLinkID("0000");
		String content = UUID.randomUUID().toString();
		msg.setMsgContent(new SmsTextMessage("移娃没理解您的问题2【温馨提示】移娃没理解您的问题3【温馨提示】移娃没理解您的问题4【温馨提示】移娃没理解您的问题5【温馨提示】移娃没理解您的问题6"+signTxt,new SmsDcs((byte)8)));
		
		msg.setMsgid(new MsgId());
		msg.setServiceId("10000");
		msg.setSrcId("10000");
		

		System.out.println(msg);
		
		CmppSubmitRequestMessage result = testlongCodec(msg);
		System.out.println(result);
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());
		Assert.assertArrayEquals(msg.getDestterminalId(), result.getDestterminalId());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent()+signTxt);
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
	}
	

	@Test
	public void testCodec()
	{
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		
		msg.setDestterminalId(new String[]{"13800138000"});
		msg.setLinkID("0000");
		String content = UUID.randomUUID().toString();
		msg.setMsgContent(content);
		msg.setMsgContent(new SmsTextMessage("你好，我是闪信！"+signTxt,new SmsDcs((byte)15)));
		
		msg.setMsgid(new MsgId());
		msg.setServiceId("10000");
		msg.setSrcId("10000");
		
		
		System.out.println(msg);
		
		CmppSubmitRequestMessage result = testlongCodec(msg);
		
		System.out.println(result);
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());
		Assert.assertArrayEquals(msg.getDestterminalId(), result.getDestterminalId());
		Assert.assertEquals(msg.getMsgContent(), result.getMsgContent()+signTxt);
		Assert.assertEquals(msg.getServiceId(), result.getServiceId());
	}
	private CmppSubmitRequestMessage testlongCodec(CmppSubmitRequestMessage msg)
	{

		channel().writeOutbound(msg);
		ByteBuf buf =(ByteBuf)channel().readOutbound();
		ByteBuf copybuf = Unpooled.buffer();
	    while(buf!=null){
			
			
	    	ByteBuf copy = buf.copy();
	    	copybuf.writeBytes(copy);
	    	copy.release();
			int length = buf.readableBytes();
			
			Assert.assertEquals(length, buf.readInt());
			Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readInt());

			buf =(ByteBuf)channel().readOutbound();
	    }

		CmppSubmitRequestMessage result = decode(copybuf);
		return result;
	}
}
