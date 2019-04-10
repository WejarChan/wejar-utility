package org.wejar.web.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.BeanUtils;

import com.github.pagehelper.Page;

public class PageResult<E>  implements Serializable{

	private static final long serialVersionUID = -5590324765285555523L;
	
	private Boolean paging = false;
	private Integer pageNum;
    private Integer pageSize;
    private Integer startRow;
    private Integer endRow;
    private Long total;
    private Integer pages;

    List<E> list;
    
    public PageResult(){
    	this.list = new ArrayList();
    }
    
    public PageResult(List<E> list){
    	if(list instanceof Page){
    		BeanUtils.copyProperties(list, this);
    		this.paging = true;
    	}
    	this.list = list;
    }
    
    

//	@Override
//	public int size() {
//		return this.list.size();
//	}
//
//	@Override
//	public boolean isEmpty() {
//		return this.list.isEmpty();
//	}
//
//	@Override
//	public boolean contains(Object o) {
//		return this.list.contains(o);
//	}
//
//	@Override
//	public Iterator<E> iterator() {
//		return this.list.iterator();
//	}
//
//	@Override
//	public Object[] toArray() {
//		return this.list.toArray();
//	}
//
//	@Override
//	public <T> T[] toArray(T[] a) {
//		return this.list.toArray(a);
//	}
//
//	@Override
//	public boolean add(E e) {
//		return this.list.add(e);
//	}
//
//	@Override
//	public boolean remove(Object o) {
//		return this.list.remove(o);
//	}
//
//	@Override
//	public boolean containsAll(Collection<?> c) {
//		return this.list.containsAll(c);
//	}
//
//	@Override
//	public boolean addAll(Collection<? extends E> c) {
//		return this.list.addAll(c);
//	}
//
//	@Override
//	public boolean addAll(int index, Collection<? extends E> c) {
//		return this.list.addAll(index, c);
//	}
//
//	@Override
//	public boolean removeAll(Collection<?> c) {
//		return this.list.removeAll(c);
//	}
//
//	@Override
//	public boolean retainAll(Collection<?> c) {
//		return this.list.retainAll(c);
//	}
//
//	@Override
//	public void clear() {
//		this.list.clear();
//	}
//
//	@Override
//	public E get(int index) {
//		return this.list.get(index);
//	}
//
//	@Override
//	public E set(int index, E element) {
//		return this.list.set(index, element);
//	}
//
//	@Override
//	public void add(int index, E element) {
//		this.list.add(index,element);
//	}
//
//	@Override
//	public E remove(int index) {
//		return this.list.remove(index);
//	}
//
//	@Override
//	public int indexOf(Object o) {
//		return this.list.indexOf(o);
//	}
//
//	@Override
//	public int lastIndexOf(Object o) {
//		return this.list.lastIndexOf(o);
//	}
//
//	@Override
//	public ListIterator<E> listIterator() {
//		return this.list.listIterator();
//	}
//
//	@Override
//	public ListIterator<E> listIterator(int index) {
//		return this.list.listIterator(index);
//	}
//
//	@Override
//	public List<E> subList(int fromIndex, int toIndex) {
//		return this.list.subList(fromIndex, toIndex);
//	}

	public Boolean getPaging() {
		return paging;
	}

	public void setPaging(Boolean paging) {
		this.paging = paging;
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

	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public Integer getEndRow() {
		return endRow;
	}

	public void setEndRow(Integer endRow) {
		this.endRow = endRow;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}


}
