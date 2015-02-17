package com.easynetty4.codec.protobuf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import com.easynetty4.Enum.MessageType;
import com.easynetty4.bean.Header;
import com.easynetty4.bean.Message;

/**
 * protobuf解码器，根据类型查找协议类
 * 
 * @author tankma
 *
 */
@Sharable
public class ProtobufDecoderEx extends MessageToMessageDecoder<ByteBuf> {


	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf frame,
			List<Object> out) throws Exception {
		//frame.skipBytes(4);
		Message message = new Message();
		Header header = new Header();
		header.setMessageType(MessageType.getType(frame.readByte()));
		
		header.setSessionId(frame.readLong());
		header.setServiceType(frame.readInt());
		//System.out.println(header.getServiceType()+"====st="+header.getMessageType()+"==="+header.getSessionId());
		if (frame.readableBytes() > 4) {
			int bodyLen=frame.readInt();
				byte[] decoded = new byte[bodyLen];
				frame.readBytes(decoded);
				message.setBody(decoded);
		}
		message.setHeader(header);
		out.add(message);
		
	}
}
