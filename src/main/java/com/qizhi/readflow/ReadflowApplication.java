package com.qizhi.readflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.qizhi.readflow.mapper")
public class ReadflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadflowApplication.class, args);
	}

}
