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
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import * as _ from 'lodash';
import {SnackbarService} from "../../../../services/snackbar.service";

@Component({
  selector: 'application-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})
export class ApplicationOverviewComponent implements OnInit {
  domain: any;
  application: any;
  applicationOAuthSettings: any = {};
  applicationAdvancedSettings: any = {};
  redirectUris: any[] = [];

  constructor(private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.domain = this.route.snapshot.data['domain'];
    this.application = this.route.snapshot.parent.data['application'];
    this.applicationOAuthSettings = this.application.settings == null ? {} : this.application.settings.oauth || {};
    this.applicationAdvancedSettings = this.application.settings == null ? {} : this.application.settings.advanced || {};
    this.applicationOAuthSettings.redirectUris = this.applicationOAuthSettings.redirectUris || [];
    this.redirectUris = _.map(this.applicationOAuthSettings.redirectUris, function (item) {
      return {value: item};
    });
  }

  isServiceApp(): boolean {
    return this.application.type.toLowerCase() === 'service';
  }
}
