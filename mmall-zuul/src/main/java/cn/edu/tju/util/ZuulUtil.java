package cn.edu.tju.util;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: CMW天下第一
 */
@Slf4j
public class ZuulUtil {

    public static void responseHandler(HttpServletResponse response, String msg) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(JacksonUtil.bean2Json(ServerResponse.createByErrorMessage(msg)).getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
