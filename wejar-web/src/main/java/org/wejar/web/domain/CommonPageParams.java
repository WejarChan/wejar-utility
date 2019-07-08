package org.wejar.web.domain;

import java.io.Serializable;
import java.util.List;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

public class CommonPageParams implements Serializable {

	private static final long serialVersionUID = -2289894284907750935L;

	private Integer pageNum = 1;
	
	private Integer pageSize = 20;
	
	private String orderByCause;
	
	/**
	 * 默认构造方法
	 * @Title:  CommonPageParams   
	 * @throws
	 */
	public CommonPageParams() {
		super();
	}

	/**
	 * 分页参数构造方法
	 * @Title:  CommonPageParams   
	 * @param:  pageNum 当前页数，从1开始
	 * @param:  pageSize  每页记录数
	 * @throws
	 */
	public CommonPageParams(Integer pageNum, Integer pageSize) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}
	
	/**
	 * 使用Page分页信息构造方法
	 * @Title:  CommonPageParams   
	 * @param:  page 分页信息
	 * @throws
	 */
	public CommonPageParams(Page page) {
		setPage(page);
	}

	/**
	 * 从PageHelper获取当前分页参数
	 * @Title: getCurrentPageParams   
	 * @return CommonPageParams 分页参数类      
	 * @throws
	 */
	public static CommonPageParams getCurrentPageParams() {
		CommonPageParams pageParams = new CommonPageParams(PageHelper.getLocalPage());
		return pageParams;
	}
	
	/**
	 * 判断分页参数是否有设置分页
	 * @Title: isPaging   
	 * @return 分页参数是否有设置     
	 * @throws
	 */
	public boolean isPaging() {
		if(this.pageNum == null && this.pageSize == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 从Page读取分页参数
	 * @Title: setPage   
	 * @param page 分页信息     
	 * @throws
	 */
	@SuppressWarnings("rawtypes")
	public void setPage(Page page) {
		this.pageNum = page.getPageNum();
		this.pageSize = page.getPageSize();
	}
	
	/**
	 * 调用pageHelper清楚分页设置
	 * @Title: clearPage   
	 * @throws
	 */
	public void clearPage() {
		PageHelper.clearPage();
	}
	
	/**
	 * 根据分页参数判断是否分页，并使用pageHelper分页
	 * @Title: startPage   
	 * @return boolean 分页返回true，无分页返回false      
	 * @throws
	 */
	public boolean startPage(){
//		if(!isPaging()) {
//			return false;
//		}
		if(this.pageNum == null){
			this.pageNum = 1;
		}
		
		if(this.pageSize == null){
			this.pageSize = 20;
		}
		
		PageHelper.startPage(this.getPageNum(),this.getPageSize());
		if(this.orderByCause != null && this.orderByCause.length() > 0) {
			PageHelper.orderBy(orderByCause);
		}
		return true;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderByCause() {
		return orderByCause;
	}

	public void setOrderByCause(String orderByCause) {
		this.orderByCause = orderByCause;
	}
	
	@Override
	public String toString() {
		return String.format("{pageNum:\"%d\",pageSize:\"%d\",orderByCause:\"%s\"}", pageNum,pageSize,orderByCause);
	}
	
	/**
	 * 更换分页查询出来的结果集Page类的列表
	 * @Title: changePageListType   
	 * @param sourceList
	 * @param resultList
	 * @return List<T>      
	 * @throws
	 */
	public static <T> List<T> changePageListType(@SuppressWarnings("rawtypes") List sourceList,List<T> resultList){
		if(sourceList instanceof Page) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Page<T> page = (Page) sourceList;
			page.clear();
			page.addAll(resultList);
			return page;
		}else {
			return resultList;
		}
	}
}
