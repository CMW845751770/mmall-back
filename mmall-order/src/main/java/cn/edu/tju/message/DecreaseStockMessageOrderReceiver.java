package cn.edu.tju.message;

import cn.edu.tju.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class DecreaseStockMessageOrderReceiver {

    @Resource
    public OrderService orderService ;

    @RabbitListener(queuesToDeclare = @Queue("productResponse"))
    public void process(String message){
        log.info("Order项目的process方法从【{}】接收到消息：{}","productResponse");
        orderService.updateOrderStatus(message);
    }

}
