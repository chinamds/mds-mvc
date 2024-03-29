<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form id="searchForm" class="form-inline search-form" data-change-search="true">

    <mds:label path="search.id_in">编号</mds:label>
    <mds:input path="search.id_in" cssClass="input-small" placeholder="多个使用空格分隔"/>
    &nbsp;&nbsp;

    <mds:label path="search.username_like">用户名</mds:label>
    <mds:input path="search.username_like" cssClass="input-small" placeholder="模糊匹配"/>
    &nbsp;&nbsp;

    <mds:label path="search.status_eq">状态</mds:label>
    <mds:select path="search.status_eq" cssClass="input-small">
        <mds:option label="所有" value=""/>
        <mds:options items="${statusList}" itemLabel="info"/>
    </mds:select>

    &nbsp;&nbsp;
    <input type="submit" class="btn" value="查询"/>
    <a class="btn btn-link accordion-toggle" data-bs-toggle="collapse" href="#searchMore">高级查询</a>
    <a class="btn btn-link btn-clear-search">清空</a>


    <%--more--%>
    <div id="searchMore" class="accordion-body collapse">
        <div class="accordion-inner">
            <mds:label path="search.createDate_gte">创建时间从</mds:label>

            <div class="input-append date">
                <mds:input path="search.createDate_gte" cssClass="input-medium"
                              data-format="yyyy-MM-dd hh:mm:ss"
                              placeholder="大于等于"/>
                <span class="add-on"><i data-time-icon="icon-time" data-date-icon="icon-calendar"></i></span>
            </div>
            <mds:label path="search.createDate_lte">到</mds:label>
            <div class="input-append date">
                <mds:input path="search.createDate_lte" cssClass="input-medium"
                              data-format="yyyy-MM-dd hh:mm:ss"
                              data-position="bottom-left"
                              placeholder="小于等于"/>
                <span class="add-on"><i data-time-icon="icon-time" data-date-icon="icon-calendar"></i></span>
            </div>
        </div>
    </div>
</form>
