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
    public interface ROLE{
        int ROLE_CUSTOMER = 0 ;
        int ROLE_ADMIN = 1 ;
    }
}
