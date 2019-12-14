package cn.edu.tju.hystrix;

import cn.edu.tju.commons.ServerResponse;
import cn.edu.tju.form.UserUpdateForm;
import cn.edu.tju.pojo.User;
import cn.edu.tju.service.UserService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {
    @Override
    public UserService create(Throwable cause) {
        return new UserService() {

            @Override
            public ServerResponse checkValid(String type, String str) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse getUserInfo(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse forgetGetQuestion(String username) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse forgetCheckAnswer(String username, String question, String answer) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse forgetRestPassword(String username, String passwordNew, String forgetToken) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse resetPassword(String passwordOld, String passwordNew , String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse update_information(UserUpdateForm user, String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }

            @Override
            public ServerResponse<User> get_information(String userKey) {
                return ServerResponse.createByErrorMessage("降级服务提示：该服务暂时关闭");
            }
        };
    }
}
