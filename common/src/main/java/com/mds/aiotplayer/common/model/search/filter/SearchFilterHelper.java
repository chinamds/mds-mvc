/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model.search.filter;

import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.exception.SearchException;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-24 下午4:10
 * <p>Version: 1.0
 */
public final class SearchFilterHelper {
    /**
     * 根据查询key和值生成Condition
     *
     * @param key   如 name_like
     * @param value
     * @return
     */
    public static SearchFilter newCondition(final String key, final Object value) throws SearchException {
        return Condition.newCondition(key, value);
    }

    /**
     * According to query properties, operators and values are generated Condition
     *
     * @param searchProperty
     * @param operator
     * @param value
     * @return
     */
    public static SearchFilter newCondition(final String searchProperty, final SearchOperator operator, final Object value) {
        return Condition.newCondition(searchProperty, operator, value);
    }
    
    /**
     * According to query properties, operators and values are generated Condition
     *
     * @param searchProperty
     * @param operator
     * @param value
     * @param value2
     * @return
     */
    public static SearchFilter newCondition(final String searchProperty, final SearchOperator operator, final Object value, final Object value2) {
        return Condition.newCondition(searchProperty, operator, value, value2);
    }
    
    /**
     * According to query alias, properties, operators and values are generated Condition
     *
     * @param searchAlias
     * @param searchProperty
     * @param operator
     * @param value
     * @param value2
     * @return
     */
    public static SearchFilter newCondition(final String searchAlias, final String searchProperty, final SearchOperator operator, final Object value, final Object value2) {
        return Condition.newCondition(searchAlias, searchProperty, operator, value, value2);
    }


    /**
     * 拼or条件
     *
     * @param first
     * @param others
     * @return
     */
    public static SearchFilter or(SearchFilter first, SearchFilter... others) {
        OrCondition orCondition = new OrCondition();
        orCondition.getOrFilters().add(first);
        if (ArrayUtils.isNotEmpty(others)) {
            orCondition.getOrFilters().addAll(Arrays.asList(others));
        }
        return orCondition;
    }


    /**
     * 拼and条件
     *
     * @param first
     * @param others
     * @return
     */
    public static SearchFilter and(SearchFilter first, SearchFilter... others) {
        AndCondition andCondition = new AndCondition();
        andCondition.getAndFilters().add(first);
        if (ArrayUtils.isNotEmpty(others)) {
            andCondition.getAndFilters().addAll(Arrays.asList(others));
        }
        return andCondition;
    }

}
