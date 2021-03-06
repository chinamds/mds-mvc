/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import com.google.common.collect.Sets;
import com.mds.aiotplayer.common.model.IdEntity;
import com.mds.aiotplayer.common.repository.hibernate.type.CollectionToStringUserType;
import com.mds.aiotplayer.common.repository.support.annotation.EnableQueryCache;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Set;

/**
 * 组织机构 工作职位  用户  角色 关系表
 * 1、授权的五种情况
 * 只给组织机构授权 (orgnizationId=? and jobId=0)
 * 只给工作职务授权 (orgnizationId=0 and jobId=?)
 * 给组织机构和工作职务都授权 (orgnizationId=? and jobId=?)
 * 给用户授权  (userId=?)
 * 给组授权 (groupId=?)
 * <p/>
 * 因此查询用户有没有权限 就是
 * where (orgnizationId=? and jobId=0) or (organizationId = 0 and jobId=?) or (orgnizationId=? and jobId=?) or (userId=?) or (groupId=?)
 * <p/>
 * <p/>
 * 2、为了提高性能
 * 放到一张表
 * 此处不做关系映射（这样需要配合缓存）
 * <p/>
 * 3、如果另一方是可选的（如只选组织机构 或 只选工作职务） 那么默认0 使用0的目的是为了也让走索引
 * <p/>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-4-24 下午2:14
 * <p>Version: 1.0
 */
@Entity
@TypeDefs({
	@TypeDef(
	        name = "SetToStringUserType",
	        typeClass = CollectionToStringUserType.class,
	        parameters = {
	                @Parameter(name = "separator", value = ","),
	                @Parameter(name = "collectionType", value = "java.util.HashSet"),
	                @Parameter(name = "elementType", value = "java.lang.Long")
	        }
	)
})
@Table(name = "sys_auth")
@EnableQueryCache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Auth extends IdEntity {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * area
     */
    private Long areaId = 0L;

    /**
     * organization
     */
    private Long organizationId = 0L;

    /**
     * department
     */
    private Long departmentId = 0L;

    /**
     * User
     */
    private Long userId = 0L;

    /**
     * group
     */
    private Long groupId = 0L;

    private Set<Long> roleIds;

    private AuthType type;

    @Column(name = "area_id")
    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    @Column(name = "organization_id")
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Column(name = "department_id")
    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Type(type = "SetToStringUserType")
    @Column(name = "role_ids")
    public Set<Long> getRoleIds() {
        if (roleIds == null) {
            roleIds = Sets.newHashSet();
        }
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @Transient
    public void addRoleId(Long roleId) {
        getRoleIds().add(roleId);
    }


    @Transient
    public void addRoleIds(Set<Long> roleIds) {
        getRoleIds().addAll(roleIds);
    }

    @Enumerated(EnumType.STRING)
    public AuthType getType() {
        return type;
    }

    public void setType(AuthType type) {
        this.type = type;
    }

    @Column(name = "group_id")
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

}
