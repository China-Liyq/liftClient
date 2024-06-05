package com.saite.dispatching_4.common.bean.page;

import lombok.Data;

import java.util.List;

/**
 * 分页数据对象
 * @author yangjian
 * @param <T>
 */
@Data
public class PageData<T> {

	/** 每页大小 */
	public int pageSize = 10;
	/** 第几页 */
	public int pageNum = 1;
	/** 总页数 */
	public int pageCount;
	/** 数据总数 */
	public int totalData;
	/** 分页数据列表 */
	public List<T> rows;
	
	public PageData(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

}