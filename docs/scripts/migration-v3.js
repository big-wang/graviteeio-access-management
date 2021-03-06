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

// Migrate sharding tags to default organization. Need to attach them to directly to the default organization.
db.getCollection("tags").updateMany({}, { "$set": { "organizationId": "DEFAULT" }});

// Migrate admin identities to default organization.
db.getCollection("identities").updateMany({ "domain": "admin" }, { "$unset": { "domain": "" }, "$set": { "referenceId": "DEFAULT", "referenceType" : "ORGANIZATION", "roleMapper": {} }});

// Migrate system roles. System roles are now global and attached to the platform.
db.getCollection("roles").updateMany({ "domain": "admin", "system": true }, { "$unset": { "domain": "" }, "$set": { "referenceId": "PLATFORM", "referenceType" : "PLATFORM" }});

// Migrate custom admin roles to default organization.
db.getCollection("roles").updateMany({ "domain": "admin", "system": false }, { "$unset": { "domain": "" }, "$set": { "referenceId": "DEFAULT", "referenceType" : "ORGANIZATION" }});

// Migrate admin groups to default organization.
db.getCollection("groups").updateMany({ "domain": "admin" }, { "$unset": { "domain": "" }, "$set": { "referenceId": "DEFAULT", "referenceType" : "ORGANIZATION" }});

// Migrate admin forms to default organization.
db.getCollection("forms").updateMany({ "domain": "admin" }, { "$unset": { "domain": "" }, "$set": { "referenceId": "DEFAULT", "referenceType" : "ORGANIZATION" }});

// Admin reporters can be deleted in favor of internal reporter used for organization audits.
db.getCollection('reporters').remove({ "domain": "admin" });

// Migrate admin users to default organization.
db.getCollection("users").updateMany({ "domain": "admin" }, { "$unset": { "domain": "" }, "$set": { "referenceId": "DEFAULT", "referenceType" : "ORGANIZATION" }});

// Migrate admin domain to organization.
var adminDomain = db.getCollection("domains").findOne({ "_id" : "admin"});

if(adminDomain != null) {
    var organization = {
        "_id" : "DEFAULT",
        "createdAt" : ISODate(),
        "description" : "Default organization",
        "domainRestrictions" : [],
        "identities" : adminDomain.identities,
        "name" : "Default organization",
        "updatedAt" : ISODate()
    };

    db.getCollection("organizations").update({ "_id": "DEFAULT"}, organization, { "upsert": true });
}

// Migrate all other domains to default environment.
db.getCollection("domains").updateMany({}, { "$set": { "referenceId": "DEFAULT", "referenceType": "ENVIRONMENT" }});

// Moving to referenceType / referenceId
db.getCollection("identities").updateMany({}, { "$rename": { "domain": "referenceId" }});
db.getCollection("users").updateMany({}, { "$rename": { "domain": "referenceId" }});
db.getCollection("groups").updateMany({}, { "$rename": { "domain": "referenceId" }});
db.getCollection("roles").updateMany({}, { "$rename": { "domain": "referenceId" }});
db.getCollection("forms").updateMany({}, { "$rename": { "domain": "referenceId" }});

db.getCollection("identities").updateMany({ "referenceType": { "$exists": false }}, { "$set": { "referenceType" : "DOMAIN" }});
db.getCollection("users").updateMany({ "referenceType": { "$exists": false }}, { "$set": { "referenceType" : "DOMAIN" }});
db.getCollection("groups").updateMany({ "referenceType": { "$exists": false }}, { "$set": { "referenceType" : "DOMAIN" }});
db.getCollection("roles").updateMany({ "referenceType": { "$exists": false }, "system": false}, { "$set": { "referenceType" : "DOMAIN" }});
db.getCollection("forms").updateMany({ "referenceType": { "$exists": false }}, { "$set": { "referenceType" : "DOMAIN" }});

// Migrate admin audits to internal audits collection.
db.getCollection("reporter_audits_admin").renameCollection("reporter_audits");

var collectionNames = db.getCollectionNames();

// Migrate all audits, audit actors and audit targets.
for (index = 0; index < collectionNames.length; index++) {
    var collectionName = collectionNames[index];
    if(collectionName.startsWith("reporter_audits")) {
        // Audit
        db.getCollection(collectionName).updateMany({ "domain": "admin" }, { "$unset": { "domain": "" }, "$set": { "referenceId": "DEFAULT", "referenceType" : "ORGANIZATION" }});
        db.getCollection(collectionName).updateMany({ "domain": "system" }, { "$unset": { "domain": "" }, "$set": { "referenceId": "PLATFORM", "referenceType" : "PLATFORM" }});
        db.getCollection(collectionName).updateMany({ "domain": { "$exists": true }}, { "$rename": { "domain": "referenceId" }, "$set": { "referenceType" : "DOMAIN" }});

        // Audit.actor
        db.getCollection(collectionName).updateMany({ "actor.domain": "admin" }, { "$unset": { "actor.domain": "" }, "$set": { "actor.referenceId": "DEFAULT", "actor.referenceType" : "ORGANIZATION" }});
        db.getCollection(collectionName).updateMany({ "actor.domain": "system" }, { "$unset": { "actor.domain": "" }, "$set": { "actor.referenceId": "PLATFORM", "actor.referenceType" : "PLATFORM" }});
        db.getCollection(collectionName).updateMany({ "actor.domain": { "$exists": true }}, { "$rename": { "actor.domain": "actor.referenceId" }, "$set": { "actor.referenceType" : "DOMAIN" }});

        // Audit.actor admin
        db.getCollection(collectionName).updateMany({ "actor.referenceType": { "$exists": false }, "actor.referenceId": { "$exists": false }, "actor.type": "USER", "actor.id": "admin" },
            { "$set": { "actor.referenceId": "DEFAULT", "actor.referenceType" : "ORGANIZATION" }});

        // Audit.target
        db.getCollection(collectionName).updateMany({ "target.domain": "admin" }, { "$unset": { "target.domain": "" }, "$set": { "target.referenceId": "DEFAULT", "target.referenceType" : "ORGANIZATION" }});
        db.getCollection(collectionName).updateMany({ "target.domain": "system" }, { "$unset": { "target.domain": "" }, "$set": { "target.referenceId": "PLATFORM", "target.referenceType" : "PLATFORM" }});
        db.getCollection(collectionName).updateMany({ "target.domain": { "$exists": true }}, { "$rename": { "target.domain": "target.referenceId" }, "$set": { "target.referenceType" : "DOMAIN" }});
    }
}

// Delete admin domain replaced by default organization.
db.getCollection("domains").remove({ "_id": "admin"});