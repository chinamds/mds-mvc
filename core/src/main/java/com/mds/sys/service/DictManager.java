package com.mds.sys.service;

import com.mds.common.exception.RecordExistsException;
import com.mds.common.service.GenericManager;
import com.mds.sys.model.Dict;

import java.util.List;
import javax.jws.WebService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

//@WebService
public interface DictManager extends GenericManager<Dict, Long> {
	Page<Dict> find(Pageable page, Dict dict) ;
	
	List<String> findTypeList();
	
	void removeDict(Long id);

	/**
     * Retrieves a list of all dicts.
     *
     * @return List
     */
	@Cacheable(value="sysCache", key="#root.target.getCacheKey()")
    List<Dict> getDicts();
    
   	/**
     * Saves a dict's information
     *
     * @param dict the dict's information
     * @return updated dict
     * @throws DictExistsException thrown when dict already exists
     */
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
    Dict saveDict(Dict dict) throws RecordExistsException;
    
	@CacheEvict(value="sysCache", key="#root.target.getCacheKey()")
	void removeDict(String dictIds);
}