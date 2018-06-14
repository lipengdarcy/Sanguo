package cn.smarthse.business.service.security;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.codec.DecoderException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.smarthse.business.dao.AdminUserMapper;
import cn.smarthse.business.model.AdminUser;
import cn.smarthse.business.model.AdminUserExample;
import cn.smarthse.framework.Constant;
import cn.smarthse.framework.model.JqGridData;
import cn.smarthse.framework.model.JqGridParam;
import cn.smarthse.framework.util.Digests;
import cn.smarthse.framework.util.Encodes;

/**
 * 用户管理类
 * 
 * @author lipeng
 */
@CacheConfig(cacheNames = Constant.redisCacheName)
@Service
public class UserManager {

	@Resource
	AdminUserMapper userMapper;

	public static final String HASH_ALGORITHM = "SHA-1";
	public static final int HASH_INTERATIONS = 1024;
	private static final int SALT_SIZE = 8;

	/**
	 * 分页查询所有用户,返回grid格式的数据
	 * 
	 * @param param
	 *            jqgrid的相关参数
	 * @param name
	 *            用户名或者姓名
	 */
	public JqGridData<AdminUser> getUserGridData(JqGridParam param, String name) {
		PageHelper.startPage((int) param.getPage(), (int) param.getRows());
		Page<AdminUser> list = (Page<AdminUser>) userMapper.query(name);
		JqGridData<AdminUser> data = new JqGridData<AdminUser>(list, param);
		return data;
	}

	/**
	 * 根据用户id，获取用户实体
	 * 
	 * @param id
	 * @return SecUser
	 */
	@Cacheable(key = "'User-' + #id")
	public AdminUser getUserById(Integer id) {
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 根据用户id，获取存在的用户实体
	 * 
	 * @param id
	 * @return SecUser
	 */
	public AdminUser selectUserById(Integer id) {
		AdminUserExample e = new AdminUserExample();
		e.createCriteria().andIsValidEqualTo(Constant.ACTIVE_YES).andIdEqualTo(id);
		List<AdminUser> list = userMapper.selectByExample(e);
		if (list.isEmpty())
			return null;
		return list.get(0);
	}

	@CacheEvict(key = "'User-byname-' + #name")
	public void clearUserByNameCache(String name) {
	}

	/**
	 * 根据用户名，获取用户实体
	 * 
	 * @param name
	 *            用户名
	 * @return User
	 */
	@Cacheable(key = "'AdminUser-byname-' + #name")
	public AdminUser getUserByName(String name) {
		AdminUserExample e = new AdminUserExample();
		e.createCriteria().andUserNameEqualTo(name).andIsValidEqualTo(Constant.ACTIVE_YES);
		List<AdminUser> list = userMapper.selectByExample(e);
		if (list.isEmpty())
			return null;
		AdminUser user = list.get(0);
		return user;
	}

	public int insert(AdminUser user) {
		// 判断是否重复
		AdminUser existUser = this.getUserByName(user.getUserName());
		if (existUser != null) {
			return -1;
		}
		// 密码处理
		byte[] saltbyte = Digests.generateSalt(SALT_SIZE);
		user.setSalt(Encodes.hexEncode(saltbyte));
		String newMd5Password = (new SimpleHash("MD5", user.getPassWord())).toString();// 新的密码单次md5加密
		String newPassword = Encodes.hexEncode(Digests.sha1(newMd5Password.getBytes(), saltbyte, HASH_INTERATIONS));
		user.setPassWord(newPassword);
		user.setCreateTime(new Date());
		return userMapper.insertSelective(user);
	}
	
	public int update(AdminUser user) {		
		return userMapper.updateByPrimaryKeySelective(user);
	}
	
	public int delete(Integer uid) {
		AdminUser user = this.getUserById(uid);
		user.setIsValid(Constant.ACTIVE_NO);
		return this.update(user);
	}
	
	public int changePassword(AdminUser user, String oldPassword) {
		AdminUser record = this.getUserById(user.getId());
		int HASH_INTERATIONS = 1024;
		String salt = record.getSalt();
		String md5Password = (new SimpleHash("MD5", oldPassword)).toString();// md5
		byte[] saltbyte = null;
		try {
			saltbyte = Encodes.decodeHex(salt);
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		byte[] hashPassword = Digests.sha1(md5Password.getBytes(), saltbyte, HASH_INTERATIONS);
		String pass = Encodes.hexEncode(hashPassword);
		// 原密码错误，直接返回
		if (!pass.equals(record.getPassWord())) {
			return -1;
		}
		// 原密码正确，更新密码
		String newMd5Password = (new SimpleHash("MD5", user.getPassWord())).toString();// 新的密码单次md5加密
		String newPassword = Encodes.hexEncode(Digests.sha1(newMd5Password.getBytes(), saltbyte, HASH_INTERATIONS));
		user.setPassWord(newPassword);
		this.update(user);
		return 0;
		// 1: 111111
		// 2: 96e79218965eb72c92a549dd5a330112
		// 3: 453144c00bf7dd0a289898bbdf5f6cd0b3a94aa4

	}

}
