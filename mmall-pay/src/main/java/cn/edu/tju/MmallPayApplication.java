package cn.edu.tju;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("cn.edu.tju.mapper")
public class MmallPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmallPayApplication.class, args);
    }

}
