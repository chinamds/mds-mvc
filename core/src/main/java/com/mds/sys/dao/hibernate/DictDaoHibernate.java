package com.mds.sys.dao.hibernate;

import com.mds.sys.model.Dict;
import com.mds.sys.model.DictCategory;
import com.mds.sys.dao.DictDao;
import com.mds.common.dao.hibernate.GenericDaoHibernate;
import com.mds.common.model.search.SearchOperator;
import com.mds.common.model.search.Searchable;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("dictDao")
public class DictDaoHibernate extends GenericDaoHibernate<Dict, Long> implements DictDao {

    public DictDaoHibernate() {
        super(Dict.class);
    }
    
    public List<Dict> findAllList(){
		//return find("from Dict where delFlag=:p1 order by sort", new Parameter(Dict.DEL_FLAG_NORMAL));
    	return find("from Dict order by sort");
	}

	public List<String> findTypeList(){
		//return find("select type from Dict where delFlag=:p1 group by type", new Parameter(Dict.DEL_FLAG_NORMAL));
		return find("select type from Dict group by type");
	}
	
	public void saveDicts(String category, Map<String, Object> mapDict) {
		DictCategory dictCatagory = DictCategory.valueOf(category);
		if (dictCatagory == null)
			return;
		
		for (String word : mapDict.keySet()){
			Searchable searchable = Searchable.newSearchable();
			searchable.addSearchFilter("category", SearchOperator.eq, dictCatagory);
			searchable.addSearchFilter("word", SearchOperator.eq, word);
			Dict dict = new Dict(dictCatagory, word, mapDict.get(word).toString());
			
			addOrUpdate(dict, searchable);
    	}
	}

	/**
     * {@inheritDoc}
     */
    public Dict saveDict(Dict dict) {
        if (log.isDebugEnabled()) {
            log.debug("dict's id: " + dict.getId());
        }
        getSession().saveOrUpdate(dict);
        // necessary to throw a DataIntegrityViolation and catch it in DictManager
        getSession().flush();
        return dict;
    }
}
