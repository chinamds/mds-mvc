/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

import com.mds.aiotplayer.sys.util.UserAccount;

/**
 * @since 0.1
 */
public class LacksRoleTag extends RoleTag {

    //TODO - complete JavaDoc

    public LacksRoleTag() {
    }

    protected boolean showTagBody(String roleName) {
        boolean hasRole = false;
        if (getSubject() != null && getSubject().getPrincipal() instanceof UserAccount)
        	hasRole =   ((UserAccount)getSubject().getPrincipal()).hasRole(roleName);
        
        return !hasRole;
    }

}