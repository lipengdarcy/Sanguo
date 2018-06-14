package cn.smarthse.business.model.system;

import java.util.Date;

/**
 * 行政区域
 */
public class SysAreaStandard {
	/**
	 * ID
	 */
	private Long id;

	/**
	 * 父编号
	 */
	private Long pid;

	/**
	 * 邮政编码
	 */
	private String postcode;

	/**
	 * 简称
	 */
	private String shortName;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 省简称
	 */
	private String markName;

	/**
	 * 简拼
	 */
	private String py;

	/**
	 * 全拼
	 */
	private String pyFull;

	/**
	 * 经度
	 */
	private String lng;

	/**
	 * 纬度
	 */
	private String lat;

	/**
	 * 级别(0-国家,1-省,2-市,3-区、县,4-乡、镇、街道,5-村、居委会)
	 */
	private Byte level;

	/**
	 * 热度
	 */
	private Integer sort;

	/**
	 * 省编号
	 */
	private Long provinceId;

	/**
	 * 省名称
	 */
	private String provinceName;

	/**
	 * 市编号
	 */
	private Long cityId;

	/**
	 * 市名称
	 */
	private String cityName;

	/**
	 * 区域ID
	 */
	private Long areaId;

	/**
	 * 区域名称
	 */
	private String areaName;

	/**
	 * 乡镇街道编号
	 */
	private Long streetId;

	/**
	 * 乡镇街道名称
	 */
	private String streetName;

	/**
	 * 是否有效（0-无效，1-有效）
	 */
	private Boolean isValid;

	/**
	 * 创建者
	 */
	private Long createBy;

	/**
	 * 创建时间
	 */
	private Date createDate;

	/**
	 * 更新者
	 */
	private Long updateBy;

	/**
	 * 更新时间
	 */
	private Date updateDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode == null ? null : postcode.trim();
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName == null ? null : shortName.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getMarkName() {
		return markName;
	}

	public void setMarkName(String markName) {
		this.markName = markName == null ? null : markName.trim();
	}

	public String getPy() {
		return py;
	}

	public void setPy(String py) {
		this.py = py == null ? null : py.trim();
	}

	public String getPyFull() {
		return pyFull;
	}

	public void setPyFull(String pyFull) {
		this.pyFull = pyFull == null ? null : pyFull.trim();
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng == null ? null : lng.trim();
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat == null ? null : lat.trim();
	}

	public Byte getLevel() {
		return level;
	}

	public void setLevel(Byte level) {
		this.level = level;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName == null ? null : provinceName.trim();
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName == null ? null : cityName.trim();
	}

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName == null ? null : areaName.trim();
	}

	public Long getStreetId() {
		return streetId;
	}

	public void setStreetId(Long streetId) {
		this.streetId = streetId;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName == null ? null : streetName.trim();
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}