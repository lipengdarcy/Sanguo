package cn.smarthse.framework.model;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.Page;

//1.按照JqGrid的格式定义一个bean
public class JqGridData<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 总页数 */
	private long total;
	/** 页码 */
	private long page;
	/** 记录数 */
	private long records;
	/** 数据实体 */
	private List<T> rows;

	public JqGridData() {
	}

	public JqGridData(Page<T> rows, JqGridParam param) {
		if (rows != null && param.getRows() > 0) {
			if (rows.getTotal() % param.getRows() == 0)
				this.total = rows.getTotal() / param.getRows();
			else
				this.total = rows.getTotal() / param.getRows() + 1;
			this.page = param.getPage();
			this.records = rows.getTotal();
			this.rows = rows;
		}
	}

	/**
	 * mongodb的分页对象
	 */
	public JqGridData(org.springframework.data.domain.Page<T> data) {
		this.records = data.getTotalElements();
		this.total = data.getTotalElements() / data.getNumberOfElements();
		this.page = data.getNumber() + 1;
		this.rows = data.getContent();
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public long getRecords() {
		return records;
	}

	public void setRecords(Integer records) {
		this.records = records;
	}

	public long getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

}
