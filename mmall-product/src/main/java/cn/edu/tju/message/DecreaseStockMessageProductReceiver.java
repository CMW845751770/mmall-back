package cn.edu.tju.message;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.service.ProductService;
import cn.edu.tju.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Component
@Slf4j
public class DecreaseStockMessageProductReceiver {

    @Resource
    private ProductService productServiceImpl ;
    @Resource
    private AmqpTemplate amqpTemplate ;

    @RabbitListener(queuesToDeclare = @Queue(Const.DECREASE_STOCK_MESSAGE_ROUTING_KEY))
    public void process(String message){
        log.info("Project项目的process方法从【{}】接收到消息：{}",Const.DECREASE_STOCK_MESSAGE_ROUTING_KEY,message);
        ServerResponse serverResponse = productServiceImpl.decreaseStock(message);
        String responseStr = JacksonUtil.bean2Json(serverResponse)  ;
        amqpTemplate.convertAndSend("productResponse",responseStr);
    }
}
