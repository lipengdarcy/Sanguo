package cn.smarthse.business.model.system;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection="Syslog")
public class ASyslog implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8962032018164441850L;

	/**
     */
    private Integer id;

    /**
     *   企业ID
     */
    private Integer cid;

    /**
     *   用户ID
     */
    private Integer uid;
    
    /**
     *   操作IP
     */
    private String operateip;


	/**
     *   日志类型， 0：登录日志; 1:文件上传错误日志; 2：消息推送日志;
     */
    private Byte type;

    /**
     *   操作表名
     */
    private String tablename;

    /**
     *   记录ID，操作表的主键
     */
    private Integer recordid;

    /**
     *   日志内容简单说明
     */
    private String description;

    /**
     *   日志内容
     */
    private String content;
    
    
    /**
     *   创建时间
     */
    private Date createtime;

    /**
     *   创建人
     */
    private Integer creator;

    /**
     *   修改时间
     */
    private Date updatetime;

    /**
     *   修改人
     */
    private Integer updator;
}