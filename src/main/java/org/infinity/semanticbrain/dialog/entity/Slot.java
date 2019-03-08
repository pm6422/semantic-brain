package org.infinity.semanticbrain.dialog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("意图识别槽位")
public class Slot implements Serializable {
    private static final long serialVersionUID = 6996205094272648344L;

    // 槽位类型值
    public static final String TYPE_AFFIRMATIVE = "affirmative";// 肯定类型
    public static final String TYPE_NEGATIVE    = "negative";// 否定类型，如："不要"买北京到上海的机票

    @ApiModelProperty("槽位代码")
    private String  code;
    @ApiModelProperty("槽位名称")
    private String  name;
    @ApiModelProperty("槽位值")
    private String  value;
    @ApiModelProperty("槽位类型")// 取值范围见常量定义
    private String  type;
    @ApiModelProperty("反问槽位优先级") // 优先级高的优先反问
    private int     askPrecedence;
    @ApiModelProperty("是否为必要槽位")
    private boolean required;
}
