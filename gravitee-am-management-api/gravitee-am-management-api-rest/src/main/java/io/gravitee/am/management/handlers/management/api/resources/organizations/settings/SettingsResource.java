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
package io.gravitee.am.management.handlers.management.api.resources.organizations.settings;

import io.gravitee.am.identityprovider.api.User;
import io.gravitee.am.management.handlers.management.api.resources.AbstractResource;
import io.gravitee.am.management.handlers.management.api.security.Permission;
import io.gravitee.am.management.handlers.management.api.security.Permissions;
import io.gravitee.am.model.Domain;
import io.gravitee.am.model.permissions.RolePermission;
import io.gravitee.am.model.permissions.RolePermissionAction;
import io.gravitee.am.service.OrganizationService;
import io.gravitee.am.service.model.PatchOrganization;
import io.gravitee.common.http.MediaType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SettingsResource extends AbstractResource {

    @Autowired
    private OrganizationService organizationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get platform main settings")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Platform settings successfully fetched", response = Domain.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @Permissions({
            @Permission(value = RolePermission.MANAGEMENT_SETTINGS, acls = RolePermissionAction.READ)
    })
    public void get(
            @PathParam("organizationId") String organizationId,
            @Suspended final AsyncResponse response) {

        organizationService.findById(organizationId)
                .subscribe(domain -> response.resume(Response.ok(domain).build()), response::resume);
    }


    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update platform main settings")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Platform settings successfully patched", response = Domain.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @Permissions({
            @Permission(value = RolePermission.MANAGEMENT_SETTINGS, acls = RolePermissionAction.UPDATE)
    })
    public void patch(
            @PathParam("organizationId") String organizationId,
            @ApiParam(name = "domain", required = true) @Valid @NotNull final PatchOrganization patchOrganization,
            @Suspended final AsyncResponse response) {
        final User authenticatedUser = getAuthenticatedUser();

        organizationService.update(organizationId, patchOrganization, authenticatedUser)
                .subscribe(organization -> response.resume(Response.ok(organization).build()), response::resume);
    }
}
