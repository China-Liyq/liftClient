package com.saite.dispatching_4.common.bean.page;


import lombok.Data;

/**
 * 分页查询bean
 * @author yangjian
 */
@Data
//@ApiModel(description = "分页信息")
public class PageQuery {
	
//	@ApiModelProperty(value = "每页记录数")
	public int pageSize = 10;
//	@ApiModelProperty(value = "当前页")
	public int pageNum = 1;

}