/**
 * Copyright (c) 2005-2012 https://github.com/zhangkaitao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.mds.aiotplayer.common.model.search;

import com.mds.aiotplayer.common.model.search.exception.SearchException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * <p>search operator</p>
 */
public enum SearchOperator {
    eq("equal", "="), eqi("equal", "="), ne("not equal", "!="), nei("not equal", "!="),
    gt("greater than", ">"), gte("greater than or equal", ">="), lt("less than", "<"), lte("less than or equal", "<="),
    bt("between", "between"),
    prefixLike("prefix match", "like"), prefixNotLike("prefix not match", "not like"),
    suffixLike("suffix match", "like"), suffixNotLike("suffix not natch", "not like"),
    like("match", "like"), notLike("not match", "not like"),
    contain("match anywhere", "like"), notContain("not match anywhere", "not like"),
    isNull("null", "is null"), isNotNull("not null", "is not null"),
    in("contain", "in"), notIn("not contain", "not in"), any("any", "any"), all("all", "all"), 
    exists("exists", "exists"), notExists("not exists", "not exists"), ftqFilter("Full text query filer", "filter"), custom("custom default", null);

    private final String info;
    private final String symbol;

    SearchOperator(final String info, String symbol) {
        this.info = info;
        this.symbol = symbol;
    }

    public String getInfo() {
        return info;
    }

    public String getSymbol() {
        return symbol;
    }

    public static String toStringAllOperator() {
        return Arrays.toString(SearchOperator.values());
    }

    /**
     * 操作符是否允许为空
     *
     * @param operator
     * @return
     */
    public static boolean isAllowBlankValue(final SearchOperator operator) {
        return operator == SearchOperator.isNotNull || operator == SearchOperator.isNull;
    }


    public static SearchOperator valueBySymbol(String symbol) throws SearchException {
        symbol = formatSymbol(symbol);
        for (SearchOperator operator : values()) {
            if (operator.getSymbol().equals(symbol)) {
                return operator;
            }
        }

        throw new SearchException("SearchOperator not method search operator symbol : " + symbol);
    }

    private static String formatSymbol(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            return symbol;
        }
        return symbol.trim().toLowerCase().replace("  ", " ");
    }
}
