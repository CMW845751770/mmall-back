package cn.edu.tju.message.producer;

import cn.edu.tju.message.MessageConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: CMW天下第一
 */
@Component
@Slf4j
public class DecreaseStockProducer {

    @Resource
    private RabbitTemplate rabbitTemplate ;


    public void sendMessage(String message){
        CorrelationData correlationData = new CorrelationData(message) ;
        Message<String> msg = MessageBuilder.withPayload(message).build();
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.convertAndSend(MessageConst.DECREASE_STOCK_MESSAGE_EXCHANGE, MessageConst.DECREASE_STOCK_MESSAGE_ROUTINGKEY
                ,msg,correlationData);
    }

    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {

        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            log.info("扣库存 confirm 方法回调,{}",correlationData);
            if(!ack){
                log.error("扣库存 消息{}发送到broker失败,{},建议人工补偿",correlationData,cause);
            }
        }
    } ;

}
