package cn.edu.tju.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: CMW天下第一
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class UserVO implements Serializable {

    private static final long serialVersionUID = 5041640819123135528L;

    private Integer id;

    private String username;

    private String email;

    private String phone;

    private String question;

    private String answer;

}