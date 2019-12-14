package cn.edu.tju.exception;

/**
 * @Author: CMW天下第一
 */
public class InadequateStockException extends RuntimeException {

    public InadequateStockException(){
        super("库存不足异常") ;
    }
}
