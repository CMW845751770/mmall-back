package cn.edu.tju.message.consumer;

import cn.edu.tju.commons.Const;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.message.MessageConst;
import cn.edu.tju.service.ProductService;
import cn.edu.tju.utils.JacksonUtil;
import cn.edu.tju.vo.OrderItemVo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: CMW天下第一
 */
@Component
@Slf4j
public class DecreaseStockMessageConsumer {

    @Resource
    private ProductService productServiceImpl ;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MessageConst.DECREASE_STOCK_MESSAGE_QUEUE,durable = "true")
            ,exchange = @Exchange(value = MessageConst.DECREASE_STOCK_MESSAGE_EXCHANGE,type = "topic")
            ,key = MessageConst.DECREASE_STOCK_MESSAGE_ROUTINGKEY))
    @RabbitHandler
    public void process(Message msg , Channel channel){
        boolean isSuccess = true ;
        try {
            String message = (String) msg.getPayload();
            log.info("DecreaseStockMessageConsumer的process方法中从【{}】接收到消息：【{}】", MessageConst.DECREASE_STOCK_MESSAGE_ROUTINGKEY,message);
            List<OrderItemVo> orderItemVoList = JacksonUtil.json2BeanT(message, new TypeReference<List<OrderItemVo>>() {}  );
            productServiceImpl.decreaseStock(orderItemVoList);
        }catch (Exception e){
            log.error("消费端异步扣库存发生异常 {}",e.getMessage());
            isSuccess = false ;
        }
        Long deliveryTag  = (Long) msg.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        try {
            if(!isSuccess){
                boolean isRedelivered = (boolean) msg.getHeaders().get(AmqpHeaders.REDELIVERED);
                if(isRedelivered){
                    //已经投递过了 记录日志
                    channel.basicNack(deliveryTag,false,false);
                    log.error("消费端异步扣库存处理异常，请人工补偿，消息 {}",msg.getPayload());
                }else{
                    log.info("消费端异步扣库存处理异常,重新入队,消息 {}",msg.getPayload());
                    channel.basicNack(deliveryTag,false,true);
                }
            }else{
                //业务处理成功则ack
                log.info("消费端异步扣库存成功",msg.getPayload());
                channel.basicAck(deliveryTag,false);
            }
        }catch (Exception e){
            log.error("消费端异步扣库存ACK时发生异常 {}",e.getMessage());
        }

    }
}
