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

