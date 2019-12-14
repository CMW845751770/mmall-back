package cn.edu.tju.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Author: CMW天下第一
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ShippingForm implements Serializable {

    private static final long serialVersionUID = 6431509935146773239L;

    @NotBlank(message = "收货姓名不能为空")
    private String receiverName;

    @NotBlank(message = "固定电话不能为空")
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ,message = "不支持的固定电话格式")
    private String receiverPhone;

    @NotBlank(message = "移动电话不能为空")
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ,message = "不支持的移动电话格式")
    private String receiverMobile;

    @NotBlank(message = "省份不能为空")
    private String receiverProvince;

    @NotBlank(message = "城市不能为空")
    private String receiverCity;

    @NotBlank(message = "区/县不能为空")
    private String receiverDistrict;

    @NotBlank(message = "详细地址不能为空")
    private String receiverAddress;

    @NotBlank(message = "邮编不能为空")
    private String receiverZip;
}
