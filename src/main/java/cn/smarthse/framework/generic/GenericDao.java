package cn.smarthse.framework.generic;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 所有自定义Dao的顶级接口, 封装常用的增删查改操作,
 * 可以通过Mybatis Generator Maven 插件自动生成Dao,
 * 也可以手动编码,然后继承GenericDao 即可.
 * <p/>
 * Model : 代表数据库中的表 映射的Java对象类型
 * PK :代表对象的主键类型
 */
public interface GenericDao<T> {

    /**
     * 插入对象
     *
     * @param model 对象
     */
    int insertSelective(T model);

    /**
     * 更新对象
     *
     * @param model 对象
     */
    int updateByPrimaryKeySelective(T model);

    /**
     * 通过主键, 删除对象
     *
     * @param id 主键
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 通过主键, 查询对象
     *
     * @param id 主键
     * @return
     */
    T selectByPrimaryKey(Integer id);

    int countByExample(Object example);
    
    int deleteByExample(Object example);
    
    List<T> selectByExample(Object example);
    
    /**
	 * 批量更新
	 *
	 * @return 更新条目
	 */
	int updateByExampleSelective(@Param("record") T record,
			@Param("example") Object example);

	/**
	 * 批量更新
	 *
	 * @return 更新条目
	 */
	int updateByExample(@Param("record") T record,
			@Param("example") Object example);

}
