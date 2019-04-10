package org.wejar.web.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.wejar.commons.exception.BusinessException;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

@SuppressWarnings("deprecation")
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CommonResponseBody implements Serializable {

	private static final long serialVersionUID = 6629245234855121364L;
	
	private Long timestamp;
	private Boolean success;
	private Object data;
	private String code;
	private String message;

	//layui 的 统计数量
	private Long count;
	private Integer pageSize;
	private Integer pageNum;
	private Integer totalPages;
	
	
	public static void main(String args[]) {
		
		CommonResponseBody resp = new CommonResponseBody();
		resp.timestamp = System.currentTimeMillis();
		resp.success = true;
		
		List<String> names = new ArrayList<String>();
		names.add("tom");
		names.add("lily");
		names.add("lisa");
		resp.setData(names);
		resp.setCode("SUCCESS");
		resp.setMessage("查询成功");
		resp.setCount(10L);
		resp.setPageSize(3);
		resp.setPageNum(1);
		resp.setTotalPages(4);
		
		System.out.println(JSON.toJSONString(resp));
	}
	
	
	private CommonResponseBody(){
		this.timestamp = System.currentTimeMillis();
	}
	
	public static CommonResponseBody success(){
		CommonResponseBody res = new CommonResponseBody();
		res.success = true;
		return res;
	}
	
	public static CommonResponseBody success(Object data){
		CommonResponseBody res = new CommonResponseBody();
		res.success = true;
		res.setData(data);
		return res;
	}
	
	public static CommonResponseBody success(String code,String message){
		CommonResponseBody res = new CommonResponseBody();
		res.success = true;
		res.code = code;
		res.message = message;
		return res;
	}
	
	public static CommonResponseBody success(Object data,String code,String message){
		CommonResponseBody res = new CommonResponseBody();
		res.success = true;
		res.setData(data);
		res.code = code;
		res.message = message;
		return res;
	}
	
	public static CommonResponseBody fail(){
		CommonResponseBody res = new CommonResponseBody();
		res.success = false;
		return res;
	}
	
	public static CommonResponseBody fail(String code,String message){
		CommonResponseBody res = new CommonResponseBody();
		res.success = false;
		res.code = code;
		res.message = message;
		return res;
	}
	
	public static CommonResponseBody fail(BusinessException e){
		CommonResponseBody res = new CommonResponseBody();
		res.success = false;
		res.code = e.getCode();
		res.message = e.getMessage();
		return res;
	}
	
	public static CommonResponseBody fail(Object data){
		CommonResponseBody res = new CommonResponseBody();
		res.success = false;
		res.setData(data);
		return res;
	}
	
	public static CommonResponseBody fail(Object data,String code,String message){
		CommonResponseBody res = new CommonResponseBody();
		res.success = false;
		res.setData(data);
		res.code = code;
		res.message = message;
		return res;
	}


	public Object getData() {
		return data;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}


	protected void setData(Object data) {
		//处理类型为Page的参数
		if(data instanceof Page){
			Page page = (Page)data;
			//Layui 分页参数
			this.count = page.getTotal();
			this.pageSize = page.getPageSize();
			this.pageNum = page.getPageNum();
			this.totalPages = page.getPages();
			
			this.data = data;
			return;
		}
		
		if(data instanceof PageInfo){
			PageInfo pageInfo = (PageInfo)data;
			//Layui 分页参数
			this.count = pageInfo.getTotal();
			this.pageSize = pageInfo.getPageSize();
			this.pageNum = pageInfo.getPageNum();
			this.totalPages = pageInfo.getPages();
			
			this.data = pageInfo.getList();
			return;
		}
		
		if(data instanceof PageResult){
			PageResult pageResult = (PageResult) data;
			
			//Layui 分页参数
			this.count = pageResult.getTotal();
			this.pageSize = pageResult.getPageSize();
			this.pageNum = pageResult.getPageNum();
			this.totalPages = pageResult.getPages();
			
			this.data = pageResult.getList();
			
			return;
		}
		
		if(this.data == null){
			this.data = data;
		}
		
	}

	protected void setCode(String code) {
		this.code = code;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	public String toString(){
		
//		Map container = new HashMap();
//		if(this.success != null){
//			container.put("success", this.success);
//		}
//		if(this.data != null){
//			container.put("data", this.data);
//		}
//		if(this.code != null){
//			container.put("code", this.code);
//		}
//		if(this.message != null){
//			container.put("message", this.message);
//		}
//		if(this.count != null){
//			container.put("count", this.count);
//		}
//		if(this.pageSize != null){
//			container.put("pageSize", this.pageSize);
//		}
//		if(this.pageNum != null){
//			container.put("pageNum", this.pageNum);
//		}
//		if(this.totalPages != null){
//			container.put("totalPages", this.totalPages);
//		}
		return JSON.toJSONString(this);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}