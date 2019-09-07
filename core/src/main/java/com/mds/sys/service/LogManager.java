package com.mds.sys.service;

import com.mds.common.service.GenericManager;
import com.mds.sys.model.Log;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@WebService
public interface LogManager extends GenericManager<Log, Long> {
	Page<Log> find(Pageable page, Map<String, Object> paramMap) ;
}