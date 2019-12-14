package cn.edu.tju.service.impl;

import cn.edu.tju.commons.ResponseCode;
import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.ShippingForm;
import cn.edu.tju.mapper.ShippingMapper;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.service.ShippingService;
import cn.edu.tju.utils.JacksonUtil;
import cn.edu.tju.utils.Pojo2VOUtil;
import cn.edu.tju.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ShippingServiceImpl implements ShippingService {

    @Resource
    private ShippingMapper shippingMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate ;

    //获取redis中的用户信息
    private UserVO getUserVOInSession(String token){
        if(StringUtils.isBlank(token)){
            return null ;
        }
        String userJson = stringRedisTemplate.opsForValue().get(token) ;
        if(StringUtils.isBlank(userJson)){
            return null ;
        }
        UserVO userVO = JacksonUtil.json2Bean(userJson , UserVO.class) ;
        return userVO ;
    }

    @Override
    public ServerResponse add(String userKey , ShippingForm shippingForm) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        Shipping shipping = Pojo2VOUtil.shippingForm2Shipping(shippingForm) ;
        shipping.setUserId(user.getId());
        int rows = shippingMapper.insert(shipping);
        if (rows > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse del(String userKey, Integer shippingId) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        int rows = shippingMapper.deletByUserIdPrimaryKey(user.getId(), shippingId);
        if (rows > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(String userKey , ShippingForm shippingForm) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        Shipping shipping = Pojo2VOUtil.shippingForm2Shipping(shippingForm) ;
        shipping.setUserId(user.getId());
        int rows = shippingMapper.updateByPrimaryKeySelective(shipping);
        if (rows > 0) {
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse select(String userKey, Integer shippingId) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        Shipping shipping = shippingMapper.selectByUserIdPrimaryKey(user.getId(), shippingId);
        if (shipping != null) {
            return ServerResponse.createBySuccess("查询地址成功", shipping);
        }
        return ServerResponse.createByErrorMessage("无法查询到该地址");
    }

    @Override
    public ServerResponse list(String userKey, Integer pageNum, Integer pageSize) {
        UserVO user = getUserVOInSession(userKey);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    ResponseCode.NEED_LOGIN.getDesc()) ;
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> list = shippingMapper.selectListByUserId(user.getId());
        PageInfo pi = new PageInfo(list);
        return ServerResponse.createBySuccess(pi);
    }
}
