package cn.smarthse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class AppStart {

	public static void main(String[] args) {
		SpringApplication.run(AppStart.class, args);
	}

}
