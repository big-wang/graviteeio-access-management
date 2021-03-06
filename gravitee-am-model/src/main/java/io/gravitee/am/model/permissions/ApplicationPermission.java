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
package io.gravitee.am.model.permissions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public enum ApplicationPermission implements Permission {
    SETTINGS("SETTINGS", "settings"),
    IDENTITY_PROVIDER("IDENTITY_PROVIDER", "identity_provider"),
    FORM("FORM", "form"),
    EMAIL_TEMPLATE("EMAIL_TEMPLATE", "email_template"),
    METADATA("METADATA", "metadata"),
    OAUTH2("OAUTH2", "oauth2"),
    USER_ACCOUNT("USER_ACCOUNT", "user_account"),
    CERTIFICATE("CERTIFICATE", "certificate"),
    MEMBER("MEMBER", "member"),
    FACTOR("FACTOR", "factor");

    String name;
    String mask;

    ApplicationPermission(String name, String mask) {
        this.name = name;
        this.mask = mask;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMask() {
        return mask;
    }

    public static List<String> permissions() {
        return Arrays.asList(values()).stream().map(ApplicationPermission::getMask).collect(Collectors.toList());
    }

}
