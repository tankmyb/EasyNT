package com.easynetty4.utils;

import com.easy.ntprotocol.ServiceTypeEnum;
import com.easynetty4.Enum.MessageType;
import com.easynetty4.bean.proto.HeaderBuf.Header;
import com.easynetty4.bean.proto.MessageBuf;
import com.google.protobuf.ByteString;

public class ProtocolUtil {

	public static MessageBuf.Message builderNewReqRespMessage(
			ServiceTypeEnum stype, ByteString data) {
		MessageBuf.Message.Builder message = MessageBuf.Message.newBuilder();
		message.setHeader(builderNewHeader(MessageType.SERVICE_REQ_RESP, stype));
		message.setBody(data);
		return message.build();
	}

	public static MessageBuf.Message builderHandleMessage(Header header,
			ServiceTypeEnum stype, ByteString data) {
		MessageBuf.Message.Builder message = MessageBuf.Message.newBuilder();
		message.setHeader(copyNewHeader(header, MessageType.SERVICE_HANDLE,
				stype));
		message.setBody(data);
		return message.build();
	}

	public static Header builderNewHeader(MessageType messageType,
			ServiceTypeEnum stype) {
		Header.Builder header = Header.newBuilder();
		header.setSid(SeqUtil.incrementSeq());
		header.setStype(stype.value());
		header.setMtype(messageType.getValue());
		return header.build();
	}

	public static Header copyNewHeader(Header header, MessageType messageType,
			ServiceTypeEnum serviceTypeEnum) {
		Header.Builder newHeader = Header.newBuilder();
		newHeader.setSid(header.getSid());
		if (serviceTypeEnum != null) {
			newHeader.setStype(serviceTypeEnum.value());
		} else {
			newHeader.setStype(header.getStype());
		}
		if (messageType != null) {
			newHeader.setMtype(messageType.getValue());
		} else {
			newHeader.setMtype(header.getMtype());
		}

		return newHeader.build();
	}

	public static MessageBuf.Message builderMessage(Header header,
			ByteString data) {
		MessageBuf.Message.Builder message = MessageBuf.Message.newBuilder();
		message.setHeader(copyNewHeader(header, null, null));
		message.setBody(data);
		return message.build();
	}
}
