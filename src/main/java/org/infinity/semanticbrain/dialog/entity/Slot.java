package org.infinity.semanticbrain.dialog.entity;

import java.io.Serializable;

/**
 * Intent slot
 * Note: Serialization friendly class
 */
public class Slot implements Serializable {
    private static final long serialVersionUID = 6996205094272648344L;

    public static final String TYPE_AFFIRMATIVE = "affirmative";// 肯定类型
    public static final String TYPE_NEGATIVE    = "negative";// 否定类型，如："不要"买北京到上海的机票

    private String  code;
    private String  name;
    private String  value;
    // 槽位类型，取值范围见常量定义
    private String  type;
    // 反问槽位优先级，优先级高的优先反问
    private int     askPrecedence;
    private boolean required;
}
