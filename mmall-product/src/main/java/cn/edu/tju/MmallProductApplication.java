package cn.edu.tju;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@MapperScan("cn.edu.tju.mapper")
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
public class MmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmallProductApplication.class, args);
    }

}
