package com.yiche.bigdata.entity.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 分页类
 * @author alexgaoyh
 *
 * @param <E>
 */
public class Pagination<E> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5631795318226681173L;

	private int total;

	private int pageNo;

	private int totalPage;
	
	private List<E> data;
	
	public Pagination(int total, int pageNo, int totalPage, List<E> data) {

		this.pageNo = pageNo;

		this.totalPage = totalPage;
		
		this.total = total;
		
		this.data = data;
		
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<E> getData() {
		return data;
	}

	public void setData(List<E> data) {
		this.data = data;
		if(data != null && data.size() > this.total) {
			this.data = null;
		}
	}
}
