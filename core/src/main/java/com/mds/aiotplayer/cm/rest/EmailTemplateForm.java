/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.rest;

/// <summary>
/// Represents a particular e-mail template form.
/// </summary>
public enum EmailTemplateForm{
	AdminNotificationAccountCreated,
	AdminNotificationAccountCreatedRequiresApproval,
	UserNotificationAccountCreated,
	UserNotificationAccountCreatedApprovalGiven,
	UserNotificationAccountCreatedNeedsApproval,
	UserNotificationAccountCreatedNeedsVerification,
	UserNotificationPasswordChanged,
	UserNotificationPasswordChangedByAdmin,
	UserNotificationPasswordRecovery
}

