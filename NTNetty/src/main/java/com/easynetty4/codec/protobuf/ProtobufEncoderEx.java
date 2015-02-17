package com.easynetty4.codec.protobuf;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import com.easynetty4.bean.Header;
import com.easynetty4.bean.Message;

@Sharable
public class ProtobufEncoderEx extends MessageToMessageEncoder<Message> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Message message,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		Header header = message.getHeader();
		ByteBuf buf = Unpooled.buffer();

		buf.writeByte(header.getMessageType().getValue());
		buf.writeLong(header.getSessionId());
		buf.writeInt(header.getServiceType());
		if (message.getBody() != null) {
			buf.writeInt(message.getBody().length);
			buf.writeBytes(message.getBody());
		} else {
			buf.writeInt(0);
		}
		out.add(buf);
	}
}
