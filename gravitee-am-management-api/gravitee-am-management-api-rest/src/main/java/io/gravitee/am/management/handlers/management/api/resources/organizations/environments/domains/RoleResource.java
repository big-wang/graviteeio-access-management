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
package io.gravitee.am.management.handlers.management.api.resources.organizations.environments.domains;

import io.gravitee.am.identityprovider.api.User;
import io.gravitee.am.management.handlers.management.api.model.RoleEntity;
import io.gravitee.am.management.handlers.management.api.resources.AbstractResource;
import io.gravitee.am.management.handlers.management.api.security.Permissions;
import io.gravitee.am.model.Role;
import io.gravitee.am.model.ReferenceType;
import io.gravitee.am.model.permissions.RolePermission;
import io.gravitee.am.model.permissions.RolePermissionAction;
import io.gravitee.am.service.DomainService;
import io.gravitee.am.service.RoleService;
import io.gravitee.am.service.exception.DomainNotFoundException;
import io.gravitee.am.service.exception.RoleNotFoundException;
import io.gravitee.am.service.model.UpdateRole;
import io.gravitee.common.http.MediaType;
import io.reactivex.Maybe;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RoleResource extends AbstractResource {

    @Context
    private ResourceContext resourceContext;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DomainService domainService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get a role")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Role successfully fetched", response = Role.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @Permissions({
            @io.gravitee.am.management.handlers.management.api.security.Permission(value = RolePermission.DOMAIN_ROLE, acls = RolePermissionAction.READ)
    })
    public void get(
            @PathParam("domain") String domain,
            @PathParam("role") String role,
            @Suspended final AsyncResponse response) {
        domainService.findById(domain)
                .switchIfEmpty(Maybe.error(new DomainNotFoundException(domain)))
                .flatMap(irrelevant -> roleService.findById(role))
                .switchIfEmpty(Maybe.error(new RoleNotFoundException(role)))
                .map(role1 -> {
                    if (role1.getReferenceType() == ReferenceType.DOMAIN
                            &&!role1.getReferenceId().equalsIgnoreCase(domain)) {
                        throw new BadRequestException("Role does not belong to domain");
                    }
                    return Response.ok(role1).build();
                })
                .subscribe(
                        result -> response.resume(result),
                        error -> response.resume(error));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a role")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Role successfully updated", response = RoleEntity.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @Permissions({
            @io.gravitee.am.management.handlers.management.api.security.Permission(value = RolePermission.DOMAIN_ROLE, acls = RolePermissionAction.UPDATE)
    })
    public void update(
            @PathParam("domain") String domain,
            @PathParam("role") String role,
            @ApiParam(name = "role", required = true) @Valid @NotNull UpdateRole updateRole,
            @Suspended final AsyncResponse response) {
        final User authenticatedUser = getAuthenticatedUser();

        domainService.findById(domain)
                .switchIfEmpty(Maybe.error(new DomainNotFoundException(domain)))
                .flatMapSingle(irrelevant -> roleService.update(domain, role, updateRole, authenticatedUser))
                .map(role1 -> Response.ok(role1).build())
                .subscribe(
                        result -> response.resume(result),
                        error -> response.resume(error));
    }

    @DELETE
    @ApiOperation(value = "Delete a role")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Role successfully deleted"),
            @ApiResponse(code = 400, message = "Role is bind to existing users"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @Permissions({
            @io.gravitee.am.management.handlers.management.api.security.Permission(value = RolePermission.DOMAIN_ROLE, acls = RolePermissionAction.DELETE)
    })
    public void delete(@PathParam("domain") String domain,
                           @PathParam("role") String role,
                           @Suspended final AsyncResponse response) {
        final User authenticatedUser = getAuthenticatedUser();

        roleService.delete(ReferenceType.DOMAIN, domain, role, authenticatedUser)
                .subscribe(
                        () -> response.resume(Response.noContent().build()),
                        error -> response.resume(error));
    }
}
