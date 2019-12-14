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
 * @Author: CMW���µ�һ
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ShippingForm implements Serializable {

    private static final long serialVersionUID = 6431509935146773239L;

    @NotBlank(message = "�ջ���������Ϊ��")
    private String receiverName;

    @NotBlank(message = "�̶��绰����Ϊ��")
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ,message = "��֧�ֵĹ̶��绰��ʽ")
    private String receiverPhone;

    @NotBlank(message = "�ƶ��绰����Ϊ��")
    @Pattern(regexp = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ,message = "��֧�ֵ��ƶ��绰��ʽ")
    private String receiverMobile;

    @NotBlank(message = "ʡ�ݲ���Ϊ��")
    private String receiverProvince;

    @NotBlank(message = "���в���Ϊ��")
    private String receiverCity;

    @NotBlank(message = "��/�ز���Ϊ��")
    private String receiverDistrict;

    @NotBlank(message = "��ϸ��ַ����Ϊ��")
    private String receiverAddress;

    @NotBlank(message = "�ʱ಻��Ϊ��")
    private String receiverZip;
}
