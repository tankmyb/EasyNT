package com.easynetty4.bootstrap;

import com.easynetty4.bootstrap.IEasyBootstrap;
import com.easynetty4.config.optionkey.OptionKey;


public interface IEasyNettyAcceptor  extends IEasyBootstrap{

	void bind(int port);
}
