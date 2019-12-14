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
 * @Author: CMW���µ�һ
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserForm implements Serializable {

    private static final long serialVersionUID = -8574209714158944906L;

    @NotBlank(message = "�û�������Ϊ��")
    private String username;

    @NotBlank(message = "���벻��Ϊ��")
    private String password;

    @NotBlank(message="���䲻��Ϊ��")
    @Email(message = "��֧�ֵ������ʽ")
    private String email;

    @NotBlank(message = "�ֻ��Ų���Ϊ��")
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ,message = "��֧�ֵ��ֻ��Ÿ�ʽ")
    private String phone;

    @NotBlank(message = "������ʾ���ⲻ��Ϊ��")
    private String question;

    @NotBlank(message = "������ʾ�𰸲���Ϊ��")
    private String answer;
}
