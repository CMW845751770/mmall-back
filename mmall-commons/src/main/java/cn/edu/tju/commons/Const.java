package cn.edu.tju.commons;

/**
 *
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL="email" ;
    public static final String USERNAME = "username" ;

    public static final String TOKEN_PREFIX = "token_" ;
    public static final int TOKEN_EXPIRES_TIME = 30 ; //����

    public static final int DEFAULT_REDIS_SAVE_TIME = 10 ;//����
    public static final String REDIS_CATEGORY_KEY_PREFIX = "category_key_" ;

    public static final Integer USER_NOT_ONLINE = -1 ;

    public static final String DECREASE_STOCK_MESSAGE_ROUTING_KEY = "decrease_stock_message_key" ;
    public static final int RATE_LIMIT_TOKENS = 20;

    public interface ROLE{
        int ROLE_CUSTOMER = 0 ;
        int ROLE_ADMIN = 1 ;
    }
    public interface Cart{
        int CHECKED = 1;//�����ﳵѡ��״̬
        int UN_CHECKED = 0;//���ﳵ��δѡ��״̬

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"����");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusEnum{
        CANCELED(0,"��ȡ��"),
        WAITING(5,"�ŶӵȺ���"),
        NO_PAY(10,"δ֧��"),
        PAID(20,"�Ѹ���"),
        SHIPPED(40,"�ѷ���"),
        ORDER_SUCCESS(50,"�������"),
        ORDER_CLOSE(60,"�����ر�");


        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("ô���ҵ���Ӧ��ö��");
        }
    }
    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }



    public enum PayPlatformEnum{
        ALIPAY(1,"֧����");

        PayPlatformEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"����֧��");

        PaymentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }


        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("û���ҵ���Ӧ��ö��");
        }

    }


}
