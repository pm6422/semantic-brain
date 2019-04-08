package org.infinity.semanticbrain.domain.base;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * 必选字端Model
 * 注意事项: 该类必须实现Serializable，否则dubbo使用kyro反序列化结果的BaseModel中的字段全部为null
 */
public abstract class BaseModel implements Serializable {

    private static final long   serialVersionUID    = -1724006418603950L;
    public static final  String FIELD_ID            = "id";
    public static final  String FIELD_CREATED_TIME  = "createdTime";
    public static final  String FIELD_CREATED_BY    = "createdBy";
    public static final  String FIELD_MODIFIED_TIME = "modifiedTime";
    public static final  String FIELD_MODIFIED_BY   = "modifiedBy";
    public static final  String FIELD_ENABLED       = "enabled";

    /**
     * ID,主键
     */
    @Id
    protected String  id;
    /**
     * 创建时间
     */
    @Field(FIELD_CREATED_TIME)
    @CreatedDate
    protected Date    createdTime;
    /**
     * 创建者
     */
    @Field(FIELD_CREATED_BY)
    @CreatedBy
    protected String  createdBy;
    /**
     * 修改时间
     */
    @Field(FIELD_MODIFIED_TIME)
    @LastModifiedDate
    protected Date    modifiedTime;
    /**
     * 修改者
     */
    @Field(FIELD_MODIFIED_BY)
    @LastModifiedBy
    protected String  modifiedBy;
    /**
     * 是否可用
     */
    @Field(FIELD_ENABLED)
    protected Boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
