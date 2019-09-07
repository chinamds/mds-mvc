package com.mds.wf.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.mds.common.service.BaseService;

/**
 * 工作流中流程以及流程实例相关Service
 * 
 * @author HenryYan
 *
 */
public interface WorkflowProcessDefinitionService {


	/**
	 * 根据流程实例ID查询流程定义对象{@link ProcessDefinition}
	 * @param processInstanceId	流程实例ID
	 * @return	流程定义对象{@link ProcessDefinition}
	 */
	ProcessDefinition findProcessDefinitionByPid(String processInstanceId) ;

	/**
	 * 根据流程定义ID查询流程定义对象{@link ProcessDefinition}
	 * @param processDefinitionId	流程定义对象ID
	 * @return	流程定义对象{@link ProcessDefinition}
	 */
	ProcessDefinition findProcessDefinition(String processDefinitionId) ;

	/**
	 * 部署classpath下面的流程定义
	 * <p>从属性配置文件中获取属性<b>workflow.modules</b>扫描**deployments**</p>
	 * <p>然后从每个**deployments/${module}**查找在属性配置文件中的属性**workflow.module.keys.${submodule}**
	 * <p>配置实例：
	 * <pre>
	 *	#workflow for deploy
	 *	workflow.modules=budget,erp,oa
	 *	workflow.module.keys.budget=budget
	 *	workflow.module.keys.erp=acceptInsurance,billing,effectInsurance,endorsement,payment
	 *	workflow.module.keys.oa=caruse,leave,officalstamp,officesupply,out,overtime
	 *	</pre></p>
	 * @param processKey	流程定义KEY
	 * @throws Exception
	 */
	void deployFromClasspath(String... processKey) throws Exception;

	/**
	 * 重新部署单个流程定义
	 * @param processKey	流程定义KEY
	 * @throws Exception
	 * @see #deployFromClasspath
	 */
	void redeploy(String... processKey) throws Exception ;

	/**
	 * 重新部署所有流程定义，调用：{@link #deployFromClasspath()}完成功能
	 * @throws Exception
	 * @see #deployFromClasspath
	 */
	void deployAllFromClasspath() throws Exception;

}
