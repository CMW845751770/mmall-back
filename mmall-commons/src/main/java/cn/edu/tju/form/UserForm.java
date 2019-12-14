package cn.edu.tju.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: CMW天下第一
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserForm implements Serializable {

    private static final long serialVersionUID = -8574209714158944906L;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message="邮箱不能为空")
    @Email(message = "不支持的邮箱格式")
    private String email;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ,message = "不支持的手机号格式")
    private String phone;

    @NotBlank(message = "密码提示问题不能为空")
    private String question;

    @NotBlank(message = "密码提示答案不能为空")
    private String answer;
}
