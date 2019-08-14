package cn.edu.tju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MmallEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmallEurekaApplication.class, args);
    }

}
