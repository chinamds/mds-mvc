<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form id="searchForm" class="form-inline search-form" data-change-search="true">


    <mds:label path="search.user.username_like">用户名</mds:label>
    <mds:input path="search.user.username_like" cssClass="input-small" placeholder="模糊匹配"/>
    &nbsp;&nbsp;

    <mds:label path="search.opDate_gte">操作时间从</mds:label>
    <div class="input-append date">
        <mds:input path="search.opDate_gte" cssClass="input-medium"
                      data-format="yyyy-MM-dd hh:mm:ss"
                      placeholder="大于等于"/>
        <span class="add-on"><i data-time-icon="icon-time" data-date-icon="icon-calendar"></i></span>
    </div>
    <mds:label path="search.opDate_lte">到</mds:label>
    <div class="input-append date">
        <mds:input path="search.opDate_lte" cssClass="input-medium"
                      data-format="yyyy-MM-dd hh:mm:ss"
                      data-position="bottom-left"
                      placeholder="小于等于"/>
        <span class="add-on"><i data-time-icon="icon-time" data-date-icon="icon-calendar"></i></span>
    </div>
    <input type="submit" class="btn" value="查询"/>
    <a class="btn btn-link btn-clear-search">清空</a>

</form>
