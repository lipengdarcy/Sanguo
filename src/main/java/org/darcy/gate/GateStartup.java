package org.darcy.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 1.网关启动入口
 */
@ServletComponentScan
@SpringBootApplication
public class GateStartup {

	public static void main(String[] args) {
		SpringApplication.run(GateStartup.class, args);
	}

}
