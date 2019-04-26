package com.yiche.bigdata.entity.dto;


public class PagedQueryItem<E> {
	/**
	 * 当前页
	 */
	private Integer pageNo = 1;

	/**
	 * 每页显示条数
	 */
	private Integer pageSize = 10;

	/**
	 * 排序属性
	 */
	private String sortCol;

	/**
	 * 排序类型，asc 或者 desc
	 */
	private String sortType;
	
	private E Condition;

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortCol() {
		return sortCol;
	}

	public void setSortCol(String sortCol) {
		this.sortCol = sortCol;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public E getCondition() {
		return Condition;
	}

	public void setCondition(E condition) {
		Condition = condition;
	}
}
