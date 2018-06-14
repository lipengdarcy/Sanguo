package cn.smarthse.framework.generic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 * GenericService的实现类, 其他的自定义 ServiceImpl, 继承自它,可以获得常用的增删查改操作, 未实现的方法有 子类各自实现
 */
public abstract class GenericServiceImpl<T> implements GenericService<T> {

	/**
	 * 定义成抽象方法,由子类实现,完成dao的注入
	 *
	 * @return GenericDao实现类
	 */
	public abstract GenericDao<T> getDao();

	/**
	 * 更新实体的创建时间、创建者
	 * 
	 * @param entity
	 *            实体类
	 * @param loginStaffId
	 *            当前登录员工ID
	 * @param cid
	 *            企业ID
	 */
	private void setCreateInfo(T entity, Integer loginStaffId, Integer cid) {
		Class<? extends Object> cls = entity.getClass();
		Method setCreatetime;
		Method setCreator;
		Method setCid;

		try {
			setCreatetime = cls.getDeclaredMethod("setCreatetime", Date.class);
			if (setCreatetime != null) {
				setCreatetime.invoke(entity, new Date());
			}

			if (loginStaffId != null) {
				setCreator = cls.getDeclaredMethod("setCreator", Integer.class);
				if (setCreator != null) {
					setCreator.invoke(entity, loginStaffId);
				}
			}

			if (cid != null) {
				setCid = cls.getDeclaredMethod("setCid", Integer.class);
				if (setCid != null) {
					setCid.invoke(entity, cid);
				}
			}

		} catch (NoSuchMethodException e) {
			// begin 字段名不同
			try {
				setCreatetime = cls.getDeclaredMethod("setCreateTime", Date.class);
				if (setCreatetime != null) {
					setCreatetime.invoke(entity, new Date());
				}
				if (loginStaffId != null) {
					setCreator = cls.getDeclaredMethod("setCreateStaff", Integer.class);
					if (setCreator != null) {
						setCreator.invoke(entity, loginStaffId);
					}
				}
			} catch (NoSuchMethodException e1) {
				// e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e.printStackTrace();
			} catch (InvocationTargetException e1) {
				e.printStackTrace();
			}
			// end 字段名不同
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入对象
	 *
	 * @param T
	 *            对象
	 */
	public int insert(T record) {
		return getDao().insertSelective(record);
	}

	/**
	 * 设置更新相关的信息
	 * 
	 * @param entity
	 *            需要更新的实体类
	 * @param loginStaffId
	 *            当前登录员工ID
	 */
	private void setUpdateInfo(T entity, Integer loginStaffId) {
		Class<? extends Object> cls = entity.getClass();
		Method setUpdatetime;
		Method setUpdator;

		try {
			setUpdatetime = cls.getDeclaredMethod("setUpdatetime", Date.class);
			if (setUpdatetime != null) {
				setUpdatetime.invoke(entity, new Date());
			}
			if (loginStaffId != null) {
				setUpdator = cls.getDeclaredMethod("setUpdator", Integer.class);
				if (setUpdator != null) {
					setUpdator.invoke(entity, loginStaffId);
				}
			}
		} catch (NoSuchMethodException e) {
			// begin 字段名不同
			try {
				setUpdatetime = cls.getDeclaredMethod("setUpdateTime", Date.class);
				if (setUpdatetime != null) {
					setUpdatetime.invoke(entity, new Date());
				}
				if (loginStaffId != null) {
					setUpdator = cls.getDeclaredMethod("setUpdateStaff", Integer.class);
					if (setUpdator != null) {
						setUpdator.invoke(entity, loginStaffId);
					}
				}
			} catch (NoSuchMethodException e1) {
				// e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e.printStackTrace();
			} catch (InvocationTargetException e1) {
				e.printStackTrace();
			}
			// end 字段名不同
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新对象
	 *
	 * @param record
	 *            对象
	 * @param loginStaffId
	 *            当前登陆者staff的id
	 */
	public int update(T record) {
		return getDao().updateByPrimaryKeySelective(record);
	}

	/**
	 * 通过主键, 删除对象
	 *
	 * @param id
	 *            主键
	 */
	public int delete(Integer id) {
		return getDao().deleteByPrimaryKey(id);
	}

	/**
	 * 通过主键, 查询对象
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	public T selectById(Integer id) {
		return getDao().selectByPrimaryKey(id);
	}

	@Override
	public List<T> selectList() {
		return getDao().selectByExample(null);
	}

	public int countByExample(Object example) {
		return getDao().countByExample(example);
	}

	public int deleteByExample(Object example) {
		return getDao().deleteByExample(example);
	}

	public List<T> selectByExample(Object example) {
		return getDao().selectByExample(example);
	}

	/**
	 * 批量更新
	 *
	 * @return 更新条目
	 */
	public int updateByExampleSelective(@Param("record") T record, @Param("example") Object example) {
		return getDao().updateByExampleSelective(record, example);
	}

	/**
	 * 批量更新
	 *
	 * @return 更新条目
	 */
	public int updateByExample(@Param("record") T record, @Param("example") Object example) {
		return getDao().updateByExample(record, example);
	}

	/**
	 * * 创建分页请求.
	 */
	public PageRequest buildPageRequest(int pageNumber, int pageSize, String sortType) {
		Sort sort = null;
		if ("auto".equals(sortType)) {
			sort = new Sort(Direction.DESC, "id");
		} else if ("birthday".equals(sortType)) {
			sort = new Sort(Direction.ASC, "birthday");
		}
		// 参数1表示当前第几页,参数2表示每页的大小,参数3表示排序
		return new PageRequest(pageNumber - 1, pageSize, sort);
	}

}
