package com.easy.ntloginer.handler;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import com.easy.ntdb.dao.IUserDAO;
import com.easy.ntdb.entity.mongo.UserFriendMongo;
import com.easy.ntdb.entity.mongo.UserMongo;
import com.easy.ntloginer.NTLoginerMain;
import com.easy.ntprotocol.ServiceTypeEnum;
import com.easy.ntprotocol.json.LoginResp;
import com.easy.ntprotocol.protobuf.LoginBuf;
import com.easynetty4.bean.proto.MessageBuf.Message;
import com.easynetty4.bootstrap.IEasyNettyConnector;
import com.easynetty4.utils.JacksonUtil;
import com.easynetty4.utils.ProtocolUtil;
import com.easynetty4.utils.UUIDUtil;
import com.google.protobuf.InvalidProtocolBufferException;

public class HttpLoginHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static final Logger logger = Logger
			.getLogger(HttpLoginHandler.class.getName());

	ThreadPoolExecutor threadPoolExecutor;
	private static AtomicInteger blance=new AtomicInteger();
	private IEasyNettyConnector client;
	private IUserDAO userDAO;
	public HttpLoginHandler(IEasyNettyConnector client,IUserDAO userDAO,ThreadPoolExecutor threadPoolExecutor) {
		this.client = client;
		this.userDAO = userDAO;
		this.threadPoolExecutor = threadPoolExecutor;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

	}

	private String getParameter(QueryStringDecoder queryStringDecoder,
			String parameterName) {
		if (queryStringDecoder == null || parameterName == null) {
			return null;
		}
		List<String> values = (List<String>) queryStringDecoder.parameters()
				.get(parameterName);

		if ((values == null) || (values.isEmpty())) {
			return null;
		}
		return (String) values.get(0);
	}

	@Override
	protected void messageReceived(final ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		if (msg instanceof HttpRequest) {
			final HttpRequest request = (HttpRequest) msg;
			threadPoolExecutor.execute(new Runnable() {
				@Override
				public void run() {

					//String uri = request.getUri();
					//System.out.println("Uri:" + uri);
					if (request.getMethod() == HttpMethod.GET) { // 处理get请求
						QueryStringDecoder queryDecoder = new QueryStringDecoder(
								request.getUri());
						//System.out.println(queryDecoder.path());
						String userNo = getParameter(queryDecoder, "userNo");
						//System.out.println(userNo);
						String userPwd = getParameter(queryDecoder, "userPwd");
						//System.out.println(userPwd);
						LoginResp resp = new LoginResp();
						UserMongo userMongo = userDAO.getUser(userNo);
						if(userMongo!=null){
							if(userPwd.equals(userMongo.getUserPwd())){
								resp.setRet(1);
								String userKey = UUIDUtil.base58Uuid();

								LoginBuf.LoginSyncReq.Builder syncReq = LoginBuf.LoginSyncReq
										.newBuilder();
								syncReq.setUserKey(userKey);
								syncReq.setUsrNo(userNo);
								Message message = ProtocolUtil.builderNewReqRespMessage(
										ServiceTypeEnum.LOGIN_SYNC, syncReq.build()
												.toByteString());
								int port = NTLoginerMain.connectorPort1;
								/*if(blance.incrementAndGet()%2==1){
									port = NTLoginerMain.connectorPort2;
								}*/
								Message respMessage = (Message) client.writeSync(
										new InetSocketAddress("127.0.0.1", port), message,
										3, TimeUnit.SECONDS);
								if (respMessage == null) {
									resp.setRet(-2);
								} else {
									LoginBuf.LoginSyncResp syncResp=null;
									try {
										syncResp = LoginBuf.LoginSyncResp
												.parseFrom(respMessage.getBody());
									} catch (InvalidProtocolBufferException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (syncResp.getRet() != 1) {
										resp.setRet(-2);
									} else {
										resp.setUserKey(userKey);
										resp.setIp(NTLoginerMain.ip);
										resp.setPort(port);
										List<UserFriendMongo> list = userDAO.getFriend(userNo);
										List<String> friendList = new ArrayList<String>();
										for(UserFriendMongo f:list){
											friendList.add(f.getFriendUserNo());
										}
										resp.setFriends(friendList);
									}
								}
							}else {
								resp.setRet(-1);
							}
						}else {
							resp.setRet(-3);
						}
						boolean close = request.headers().contains(CONNECTION,
								HttpHeaders.Values.CLOSE, true)
								|| request.getProtocolVersion().equals(
										HttpVersion.HTTP_1_0)
								&& !request.headers().contains(CONNECTION,
										HttpHeaders.Values.KEEP_ALIVE, true);

						FullHttpResponse response=null;
						try {
							response = new DefaultFullHttpResponse(
									HTTP_1_1, OK, Unpooled.wrappedBuffer(JacksonUtil
											.getJsonStr(resp).getBytes("UTF-8")));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						response.headers().set(CONTENT_TYPE, "text/plain");
						response.headers().set(CONTENT_LENGTH,
								response.content().readableBytes());
						boolean keepAlive = HttpHeaders.isKeepAlive(request);
						//System.out.println(close + "=========close==" + keepAlive);
						if (close) {
							ctx.writeAndFlush(response).addListener(
									ChannelFutureListener.CLOSE);
						} else {
							response.headers().set(CONNECTION, Values.KEEP_ALIVE);
							ctx.writeAndFlush(response);
						}
						// ctx.write(response);
						// ctx.flush();
					
				}
			
				}
				});
		}
		}
		
}