package org.darcy.gate;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.darcy.gate.net.ClientSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;

/**
 * 1.网关启动入口
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class GateStartup implements CommandLineRunner {

	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	ServerBootstrap gate;

	@Autowired
	InetSocketAddress address;

	public static void main(String[] args) {
		SpringApplication.run(GateStartup.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ChannelFuture future = gate.bind(address.getPort()).sync();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (future.channel() != null) {
					future.channel().close();
				}
			}
		});
		ClientSessionManager manager = new ClientSessionManager();
		Thread t = new Thread(manager, "ClientSessionManager");
		t.setDaemon(true);
		t.start();
		future.channel().closeFuture().syncUninterruptibly();
		log.info("网关启动成功！监听端口：" + address.getPort());
	}

}
