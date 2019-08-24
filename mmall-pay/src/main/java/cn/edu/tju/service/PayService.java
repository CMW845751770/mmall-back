package cn.edu.tju.service;

import cn.edu.tju.commons.ServerResponse;

import java.text.ParseException;
import java.util.Map;

public interface PayService {
    ServerResponse pay(Long orderNo, Integer userId, String path);
    ServerResponse aliCallback(Map<String, String> params) throws ParseException;
    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
