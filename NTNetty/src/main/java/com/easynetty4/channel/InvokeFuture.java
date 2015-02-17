package com.easynetty4.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.easynetty4.exception.EasyRuntimeException;
import com.easynetty4.exception.TimeoutRuntimeException;
import com.easynetty4.utils.StackTraceUtil;

public class InvokeFuture {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private AtomicBoolean done = new AtomicBoolean(false);
	private AtomicBoolean success = new AtomicBoolean(false);
	private Semaphore semaphore = new Semaphore(0);
	private Object result;
	protected Throwable cause;
	private List<InvokeFutureListener> listernerList = new ArrayList<InvokeFutureListener>();
	private IEasyChannel channel;

	public void addListener(InvokeFutureListener listener) {
		listernerList.add(listener);
		notifyListener(listener);

	}

	private void notifyListener(InvokeFutureListener listener) {
		if (!isDone()) {
			try {
				listener.operationComplete(this);
			} catch (Exception e) {
				logger.error("notifyListener error,ex:{}",
						StackTraceUtil.getStackTrace(e));
			}
		}
	}

	public boolean isDone() {
		return done.get();
	}

	public void setDone(boolean isDone) {
		done.set(isDone);
	}

	public boolean isSuccess() {
		return success.get();
	}

	public void setSuccess(boolean isSuccess) {
		success.set(isSuccess);
	}

	public Object getResult() throws EasyRuntimeException {
		if (!isDone()) {
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				cause = e;
				throw new EasyRuntimeException(e);
			}
		}

		return this.result;
	}

	public void setResult(Object result) {
		this.result = result;
		done.set(true);
		success.set(true);
		semaphore.release(Integer.MAX_VALUE - semaphore.availablePermits());
		for (InvokeFutureListener listener : listernerList) {
			notifyListener(listener);
		}
	}

	public Object getResult(long timeout, TimeUnit unit) {
		if (!isDone()) {
			try {
				if (!semaphore.tryAcquire(timeout, unit)) {
					setCause(new TimeoutRuntimeException("timeout"));
				}
			} catch (InterruptedException e) {
				logger.error("getResult interrupted,ex:{}",
						StackTraceUtil.getStackTrace(e));
				setCause(new RuntimeException(e));
			}
		}
		return result;

	}

	public void setCause(Throwable t) {
		setDone(true);
		setSuccess(false);
		cause = t;
	}

	public IEasyChannel getChannel() {
		return channel;
	}

	public void setChannel(IEasyChannel channel) {
		this.channel = channel;
	}
}
