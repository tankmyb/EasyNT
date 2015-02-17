package com.easynetty4.dispatcher;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.easynetty4.bean.proto.HeaderBuf.Header;
import com.easynetty4.bean.proto.MessageBuf;
import com.easynetty4.channel.IEasyChannel;
import com.easynetty4.exception.EasyRuntimeException;
import com.easynetty4.listener.IChannelActiveListener;
import com.easynetty4.service.IService;
import com.easynetty4.service.ServiceManager;

public class NettyDispatcher implements INettyDispatcher {
	private ThreadPoolExecutor executor;

	public NettyDispatcher(ThreadPoolExecutor executor) {
		this.executor = executor;
	}

	@Override
	public void handleChannelActive(final IEasyChannel easyChannel) {
		final List<IChannelActiveListener> channelActiveListeners = easyChannel
				.channelActiveListeners();
		final List<IChannelActiveListener> bootstrapActiveListeners = easyChannel
				.bootstrap().channelActiveListeners();

		if ((null != channelActiveListeners && channelActiveListeners.size() > 0)
				|| (null != bootstrapActiveListeners && bootstrapActiveListeners
						.size() > 0)) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					if (null != bootstrapActiveListeners
							&& bootstrapActiveListeners.size() > 0) {
						for (IChannelActiveListener listener : bootstrapActiveListeners) {
							listener.channelActive(easyChannel);
						}
					}
					if (null != channelActiveListeners
							&& channelActiveListeners.size() > 0) {
						for (IChannelActiveListener listener : channelActiveListeners) {
							listener.channelActive(easyChannel);
						}
					}

				}
			});
		}
	}

	@Override
	public void dispatchMessageReceived(MessageBuf.Message message,
			IEasyChannel channel) {
		executor.execute(new EventHandler(message, channel));
	}

	class EventHandler implements Runnable {

		private MessageBuf.Message message;
		private IEasyChannel channel;

		public EventHandler(MessageBuf.Message message, IEasyChannel channel) {
			this.message = message;
			this.channel = channel;
		}

		@Override
		public void run() {
			Header header = message.getHeader();
			// if (header.getMtype() == MessageType.SERVICE_REQ) {
			// System.out.println(header.getStype()+"====1");
			IService service = ServiceManager.get(header.getStype());
			// System.out.println(message.getChannel()+"=================");
			try {
				service.handle(message, channel);
			} catch (Exception e) {
				throw new EasyRuntimeException(e);
			}

			// }

		}

	}

}
