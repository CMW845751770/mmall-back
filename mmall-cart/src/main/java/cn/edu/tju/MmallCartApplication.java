package cn.edu.tju;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.math.BigDecimal;

@EnableEurekaClient
@EnableDiscoveryClient
@MapperScan("cn.edu.tju.mapper")
@SpringBootApplication
public class MmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmallCartApplication.class, args);
    }

}
