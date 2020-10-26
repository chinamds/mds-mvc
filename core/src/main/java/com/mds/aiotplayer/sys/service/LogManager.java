/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import com.mds.aiotplayer.common.service.GenericManager;
import com.mds.aiotplayer.sys.model.Log;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@WebService
public interface LogManager extends GenericManager<Log, Long> {
	Page<Log> find(Pageable page, Map<String, Object> paramMap) ;
}