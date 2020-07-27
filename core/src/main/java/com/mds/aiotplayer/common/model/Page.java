/**
 * Copyright &copy; 2016-2017 <a href="https://github.com/chinamds/mdsplus">MDSPlus</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.model;

import java.util.List;

/**
 * pageable interface
 * @author John Lee
 * @version 07/07/2017
 * @param <T>
 */
public interface Page<T> {
	
	/**
	 * initialize parameter
	 */
	void initialize();
	
	/**
	 * 默认输出当前分页标签 
	 * <div class="page">${page}</div>
	 */
	String toString();
	
	/**
	 * 获取分页HTML代码
	 * @return
	 */
	String getHtml();

	/**
	 * 获取设置总数
	 * @return
	 */
	long getCount();

	/**
	 * 设置数据总数
	 * @param count
	 */
	void setCount(long count);
	
	/**
	 * 获取当前页码
	 * @return
	 */
	int getPageNo();
	
	/**
	 * 设置当前页码
	 * @param pageNo
	 */
	void setPageNo(int pageNo);
	
	/**
	 * 获取页面大小
	 * @return
	 */
	int getPageSize();

	/**
	 * 设置页面大小（最大500）
	 * @param pageSize
	 */
	void setPageSize(int pageSize);

	/**
	 * 首页索引
	 * @return
	 */
	int getFirst() ;

	/**
	 * 尾页索引
	 * @return
	 */
	int getLast();
	
	/**
	 * 获取页面总数
	 * @return getLast();
	 */
	int getTotalPage();

	/**
	 * 是否为第一页
	 * @return
	 */
	boolean isFirstPage();

	/**
	 * 是否为最后一页
	 * @return
	 */
	boolean isLastPage();
	
	/**
	 * 上一页索引值
	 * @return
	 */
	int getPrev() ;

	/**
	 * 下一页索引值
	 * @return
	 */
	int getNext() ;
	
	/**
	 * 获取本页数据对象列表
	 * @return List<T>
	 */
	List<T> getList();

	/**
	 * 设置本页数据对象列表
	 * @param list
	 */
	Page<T> setList(List<T> list);

	/**
	 * 获取查询排序字符串
	 * @return
	 */
	String getOrderBy();

	/**
	 * 设置查询排序，标准查询有效， 实例： updatedate desc, name asc
	 */
	void setOrderBy(String orderBy) ;

	/**
	 * 获取点击页码调用的js函数名称
	 * function ${page.funcName}(pageNo){location="${ctx}/list-${category.id}${urlSuffix}?pageNo="+i;}
	 * @return
	 */
	String getFuncName();

	/**
	 * 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。
	 * @param funcName 默认为page
	 */
	void setFuncName(String funcName);

	/**
	 * 设置提示消息，显示在“共n条”之后
	 * @param message
	 */
	void setMessage(String message) ;
	
	/**
	 * 分页是否有效
	 * @return this.pageSize==-1
	 */
	boolean isDisabled();
	
	/**
	 * 是否进行总数统计
	 * @return this.count==-1
	 */
	boolean isNotCount();
	
	/**
	 * 获取 Hibernate FirstResult
	 */
	int getFirstResult();
	
	int getLastResult();
	/**
	 * 获取 Hibernate MaxResults
	 */
	int getMaxResults();
	
}
