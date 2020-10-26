/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mds.aiotplayer.sys.exception.DynamicTaskException;
import com.mds.aiotplayer.sys.model.TaskDefinition;
import com.mds.aiotplayer.sys.service.TaskDefinitionManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scripting.groovy.GroovyScriptFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MethodInvoker;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-17
 * <p>Version: 1.0
 */
@Service("dynamicTaskApi")
public class DynamicTaskApiImpl implements DynamicTaskApi {

    private final Logger logger = LoggerFactory.getLogger("es-error");

    private Map<Long, ScheduledFuture<?>> taskMap = Maps.newConcurrentMap();


    @Autowired
    private TaskDefinitionManager taskDefinitionManager;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void initTask() { //系统启动后，自动加载任务
        List<Long> ids = Lists.newArrayList();
        for(TaskDefinition td : taskDefinitionManager.getAll()) {
            if(Boolean.TRUE.equals(td.getStart())) {
                ids.add(td.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(ids)) {
            startTask(true, ids.toArray(new Long[0]));
        }
    }

    @Override
    public void addTaskDefinition(TaskDefinition taskDefinition) {
        taskDefinitionManager.save(taskDefinition);
    }

    @Override
    public void updateTaskDefinition(TaskDefinition taskDefinition) {
        taskDefinitionManager.save(taskDefinition);
    }

    @Override
    public void removeTaskDefinition(boolean forceTermination, Long... taskDefinitionIds) {
        stopTask(forceTermination, taskDefinitionIds);
        taskDefinitionManager.removeByIds(taskDefinitionIds);
    }

    private synchronized void startTask(boolean forceStart, Long... taskDefinitionIds) {
        for(Long taskDefinitionId : taskDefinitionIds) {
            TaskDefinition td = taskDefinitionManager.get(taskDefinitionId);
            if(td == null || (forceStart == false && Boolean.TRUE.equals(td.getStart()))) {
                return;
            }

            try {
                ScheduledFuture<?> future = taskScheduler.schedule(createTask(td), new CronTrigger(td.getCron()));
                taskMap.put(taskDefinitionId, future);
                td.setStart(Boolean.TRUE);
            } catch (Exception e) {
                logger.error("start task error, task id:" + taskDefinitionId, e);
                td.setDescription(e.getMessage());
            }
            taskDefinitionManager.save(td);
        }
    }
    
    @Override
    public synchronized void startTask(Long... taskDefinitionIds) {
        startTask(false, taskDefinitionIds);
    }


    @Override
    public synchronized void stopTask(boolean forceTermination, Long... taskDefinitionIds) {
        for(Long taskDefinitionId : taskDefinitionIds) {
            TaskDefinition td = taskDefinitionManager.get(taskDefinitionId);

            if(td == null || Boolean.FALSE.equals(td.getStart())) {
                return;
            }

            try {
                ScheduledFuture<?> future = taskMap.get(taskDefinitionId);
                if(future != null) {
                    future.cancel(forceTermination);
                }
                td.setStart(Boolean.FALSE);
            } catch (Exception e) {
                logger.error("stop task error, task id:" + taskDefinitionId, e);
                td.setDescription(e.getMessage());
            }
            taskDefinitionManager.save(td);
        }

    }

    private Runnable createTask(TaskDefinition td) {
        final MethodInvoker methodInvoker = new MethodInvoker();
        final Long taskId = td.getId();
        try {
            methodInvoker.setTargetMethod(td.getMethodName());
            Object bean = null;
            if(StringUtils.isNotEmpty(td.getBeanName())) {
                bean = applicationContext.getBean(td.getBeanName());
            } else {
                bean = applicationContext.getAutowireCapableBeanFactory().createBean(Class.forName(td.getBeanClass()));
            }
            methodInvoker.setTargetObject(bean);
            methodInvoker.prepare();
        } catch (Exception e) {
            throw new DynamicTaskException("create task runnable error, task id is : " + taskId, e);
        }
        return new Runnable() {
            @Override
            public void run() {
                try {
                    methodInvoker.invoke();
                } catch (Exception e) {
                    logger.error("run dynamic task error, task id is:" + taskId, e);
                    throw new DynamicTaskException("run dynamic task error, task id is:" + taskId, e);
                }
            }
        };
    }



}