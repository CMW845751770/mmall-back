package cn.edu.tju.service.impl;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.mapper.ShippingMapper;
import cn.edu.tju.pojo.Shipping;
import cn.edu.tju.service.ShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rows = shippingMapper.insert(shipping);
        if (rows > 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess(map);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    @Override
    public ServerResponse del(Integer shippingId, Integer userId) {
        int rows = shippingMapper.deletByUserIdPrimaryKey(userId, shippingId);
        if (rows > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rows = shippingMapper.updateByExample(shipping);
        if (rows > 0) {
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdPrimaryKey(userId, shippingId);
        if (shipping != null) {
            return ServerResponse.createBySuccess("查询地址成功", shipping);
        }
        return ServerResponse.createByErrorMessage("无法查询到该地址");
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> list = shippingMapper.selectListByUserId(userId);
        PageInfo pi = new PageInfo(list);
        return ServerResponse.createBySuccess(pi);
    }
}
