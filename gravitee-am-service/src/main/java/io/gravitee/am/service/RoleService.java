/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.am.service;

import io.gravitee.am.identityprovider.api.User;
import io.gravitee.am.model.Role;
import io.gravitee.am.model.ReferenceType;
import io.gravitee.am.model.permissions.RoleScope;
import io.gravitee.am.model.permissions.SystemRole;
import io.gravitee.am.service.model.NewRole;
import io.gravitee.am.service.model.UpdateRole;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.List;
import java.util.Set;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface RoleService {

    Single<Set<Role>> findAll(ReferenceType referenceType, String referenceId);

    Single<Set<Role>> findAllSystem();

    Single<Set<Role>> findByDomain(String domain);

    Single<Role> findById(ReferenceType referenceType, String referenceId, String id);

    Maybe<Role> findById(String id);

    Maybe<Role> findSystemRole(SystemRole systemRole, RoleScope roleScope);

    Single<Set<Role>> findByIdIn(List<String> ids);

    Single<Role> createSystemRole(SystemRole systemRole, RoleScope roleScope, List<String> permissions, User principal);

    Single<Role> create(ReferenceType referenceType, String referenceId, NewRole newRole, User principal);

    Single<Role> create(String domain, NewRole role, User principal);

    Single<Role> update(ReferenceType referenceType, String referenceId, String id, UpdateRole updateRole, User principal);

    Single<Role> update(String domain, String id, UpdateRole role, User principal);

    Completable delete(ReferenceType referenceType, String referenceId, String roleId, User principal);

    Completable createOrUpdateSystemRoles();

    default Single<Role> createSystemRole(SystemRole systemRole, RoleScope roleScope, List<String> permissions) {
        return createSystemRole(systemRole, roleScope, permissions, null);
    }

    default Single<Role> create(String domain, NewRole role) {
        return create(domain, role, null);
    }

    default Single<Role> update(String domain, String id, UpdateRole role) {
        return update(domain, id, role, null);
    }

    default Completable delete(ReferenceType referenceType, String referenceId, String roleId) {
        return delete(referenceType, referenceId, roleId, null);
    }

}
