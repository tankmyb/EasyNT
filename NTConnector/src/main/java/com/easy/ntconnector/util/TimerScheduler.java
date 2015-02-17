package com.easy.ntconnector.util;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerScheduler {
	private static final Logger logger = LoggerFactory
			.getLogger(TimerScheduler.class);
	private final static ConcurrentHashMap<String, Timeout> scheduledFutures = new ConcurrentHashMap<String, Timeout>();
	private static ReadWriteLock lock = new ReentrantReadWriteLock();
	private static Timer hashedWheelTimer = new HashedWheelTimer();
	public static void cancel(String key) {
		Lock readLock = lock.readLock();
		readLock.lock();
		try {
			Timeout timeout = scheduledFutures.remove(key);
			if (timeout != null) {
				//logger.error(key + "==can=");
				timeout.cancel();
			}
		} finally {
			readLock.unlock();
		}
	}
    public static void remove(String key){
    	scheduledFutures.remove(key);
    }
	public static void schedule(final String key, final TimerTask timerTask,
			final long delay, final TimeUnit unit) {
		Lock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			Timeout timeout = hashedWheelTimer.newTimeout(timerTask, delay, unit);
			scheduledFutures.putIfAbsent(key, timeout);
		} finally {
			writeLock.unlock();
		}

	}
}
