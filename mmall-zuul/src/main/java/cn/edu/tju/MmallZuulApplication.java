package cn.edu.tju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringCloudApplication
public class MmallZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmallZuulApplication.class, args);
    }

}
