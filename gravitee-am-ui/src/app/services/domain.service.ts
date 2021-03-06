/*
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
import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { AppConfig } from "../../config/app.config";
import { Subject , Observable} from "rxjs";
import {map} from "rxjs/operators";
import {AuthService} from "./auth.service";

@Injectable()
export class DomainService {
  private domainsURL: string = AppConfig.settings.domainBaseURL;
  private domainUpdatedSource = new Subject<any>();
  domainUpdated$ = this.domainUpdatedSource.asObservable();

  constructor(private http: HttpClient, private authService: AuthService) {}

  list(): Observable<any> {
    return this.http.get<any>(this.domainsURL);
  }

  get(id: string): Observable<any> {
    return this.http.get<any>(this.domainsURL + id);
  }

  create(domain): Observable<any> {
    return this.http.post<any>(this.domainsURL, domain);
  }

  enable(id, domain): Observable<any> {
    return this.http.patch<any>(this.domainsURL + id, {
      'enabled': domain.enabled,
    });
  }

  patchGeneralSettings(id, domain): Observable<any> {
    return this.http.patch<any>(this.domainsURL + id, {
      'name': domain.name,
      'description': domain.description,
      'path': domain.path,
      'enabled': domain.enabled,
      'tags': domain.tags
    });
  }

  patchOpenidDCRSettings(id, domain): Observable<any> {
    return this.http.patch<any>(this.domainsURL + id, {
       'oidc':domain.oidc
    });
  }

  patchScimSettings(id, domain): Observable<any> {
    return this.http.patch<any>(this.domainsURL + id, {
      'scim': domain.scim
    });
  }

  patchLoginSettings(id, domain): Observable<any> {
    return this.http.patch<any>(this.domainsURL + id, {
      'loginSettings': domain.loginSettings
    });
  }

  patchAccountSettings(id, accountSettings): Observable<any> {
    return this.http.patch<any>(this.domainsURL + id, {
      'accountSettings': accountSettings
    });
  }

  delete(id): Observable<any> {
    return this.http.delete<any>(this.domainsURL + id);
  }

  notify(domain): void {
    this.domainUpdatedSource.next(domain);
  }

  members(id): Observable<any> {
    return this.http.get<any>(this.domainsURL + id + '/members');
  }

  addMember(id, memberId, memberType, role) {
    return this.http.post<any>(this.domainsURL + id + '/members', {
      'memberId': memberId,
      'memberType': memberType,
      'role': role
    });
  }

  removeMember(id, membershipId) {
    return this.http.delete<any>(this.domainsURL + id + '/members/' + membershipId);
  }

  permissions(id): Observable<any> {
    return this.http.get<any>(this.domainsURL + id + '/members/permissions')
      .pipe(map(perms => {
        this.authService.reloadDomainPermissions(perms);
        return perms;
      }));
  }
}
