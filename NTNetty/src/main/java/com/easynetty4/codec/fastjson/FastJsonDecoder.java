package com.easynetty4.codec.fastjson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.alibaba.fastjson.JSON;

public class FastJsonDecoder extends ByteToMessageDecoder {

	private Class<Object> cls;
	public FastJsonDecoder(Class cls){
		this.cls= cls;
	}
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;// (1)
		}
		int dataLength = in.getInt(in.readerIndex());
		if (in.readableBytes() < dataLength + 4) {
			return;// (2)
		}
		in.skipBytes(4);// (3)
		byte[] decoded = new byte[dataLength];
		in.readBytes(decoded);
		String msg = new String(decoded);// (4)
		out.add(JSON.parseObject(msg, cls));

	}
}
