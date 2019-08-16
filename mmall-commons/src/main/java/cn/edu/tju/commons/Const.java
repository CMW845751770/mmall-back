package cn.edu.tju.commons;

/**
 * Created by geely
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL="email" ;
    public static final String USERNAME = "username" ;

    public static final String TOKEN_PREFIX = "token_" ;
    public static final int TOKEN_EXPIRES_TIME = 30 ; //分钟

    public static final int DEFAULT_REDIS_SAVE_TIME = 10 ;//分钟
    public static final String REDIS_CATEGORY_KEY_PREFIX = "category_key_" ;
    public interface ROLE{
        int ROLE_CUSTOMER = 0 ;
        int ROLE_ADMIN = 1 ;
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
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
}
