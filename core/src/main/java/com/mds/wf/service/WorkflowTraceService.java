package com.mds.wf.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工作流跟踪相关Service
 * @author HenryYan
 */
public interface WorkflowTraceService {

	/**
	 * 流程跟踪图
	 * @param processInstanceId		流程实例ID
	 * @return	封装了各种节点信息
	 */
	List<Map<String, Object>> traceProcess(String processInstanceId) throws Exception ;
}
