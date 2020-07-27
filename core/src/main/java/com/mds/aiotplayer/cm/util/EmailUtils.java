package com.mds.aiotplayer.cm.util;

import java.text.MessageFormat;
import java.util.Properties;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.cm.rest.EmailTemplate;
import com.mds.aiotplayer.cm.rest.EmailTemplateForm;
import com.mds.aiotplayer.common.service.MailEngine;
import com.mds.aiotplayer.common.utils.SpringContextHolder;
import com.mds.aiotplayer.core.ResourceId;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

/// <summary>
/// Contains e-mail related functionality.
/// </summary>
public final class EmailUtils{
	private static JavaMailSenderImpl mailSender = SpringContextHolder.getBean(JavaMailSenderImpl.class);
	
	//#region Public Static Methods

	///// <summary>
	///// Send a plain text email with the specified properties. The email will appear to come from the name and email specified in the
	///// EmailFromName and EmailFromAddress configuration settings. The email
	///// is sent to the address configured in the emailToAddress setting in the configuration file. If
	///// <paramref name="sendOnBackgroundThread"/> is true, the e-mail is sent on a background thread and the function
	///// returns immediately. An exception is thrown if an error occurs while sending the e-mail, unless <paramref name="sendOnBackgroundThread"/>
	///// is true, in which case the error is logged but the exception does not propagate back to the UI thread.
	///// </summary>
	///// <param name="subject">The text to appear in the subject of the email.</param>
	///// <param name="body">The body of the email. If the body is HTML, specify true for the isBodyHtml parameter.</param>
	///// <param name="galleryId">The gallery ID.</param>
	///// <param name="sendOnBackgroundThread">If set to <c>true</c> send e-mail on a background thread. This causes any errors
	///// to be silently handled by the error logging system, so if it is important for any errors to propogate to the UI,
	///// such as when testing the e-mail function in the Site Administration area, set to <c>false</c>.</param>
	///// <overloads>
	///// Send an e-mail message.
	///// </overloads>
	///// <exception cref="WebException">Thrown when a SMTP Server is not specified. (Not thrown when
	///// <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	///// <exception cref="SmtpException">Thrown when the connection to the SMTP server failed, authentication failed,
	///// or the operation timed out. (Not thrown when <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	///// <exception cref="SmtpFailedRecipientsException">The message could not be delivered to one or more
	///// recipients. (Not thrown when <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	//public static void sendEmail(String subject, String body, long galleryId, boolean sendOnBackgroundThread)
	//{
	//  MailAddress recipient = new MailAddress(Config.GetCore().EmailToAddress, Config.GetCore().EmailToName);

	//  sendEmail(recipient, subject, body, galleryId, sendOnBackgroundThread);
	//}

	/// <overloads>
	/// Send an e-mail.
	/// </overloads>
	/// <summary>
	/// Send a plain text e-mail to the <paramref name="user" />. The email will appear to come from the name and email specified in the
	/// <see cref="IAppSetting.EmailFromName" /> and <see cref="IAppSetting.EmailFromAddress" /> configuration settings.
	/// The e-mail is sent on a background thread, so if an error occurs on that thread no exception bubbles to the caller (the error, however, is
	/// recorded in the error log). If it is important to know if the e-mail was successfully sent, use the overload of this
	/// method that specifies a sendOnBackgroundThread parameter.
	/// </summary>
	/// <param name="user">The user to receive the email. If the user does not have a valid e-mail, no action is taken.</param>
	/// <param name="subject">The text to appear in the subject of the email.</param>
	/// <param name="body">The body of the email. Must be plain text.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="user" /> is null.</exception>
	public static void sendEmail(UserAccount user, String subject, String body) throws MessagingException	{
		if (user == null)
			throw new ArgumentNullException("user");

		if (HelperFunctions.isValidEmail(user.getEmail())){
			sendEmail(user.getDisplayName() + "<" + user.getEmail() + ">", subject, body, false);
		}
	}

	/// <summary>
	/// Send a plain text e-mail with the specified properties. The email will appear to come from the name and email specified in the
	/// <see cref="IAppSetting.EmailFromName" /> and <see cref="IAppSetting.EmailFromAddress" /> configuration settings.
	/// The e-mail is sent on a background thread, so if an error occurs on that thread no exception bubbles to the caller (the error, however, is
	/// recorded in the error log). If it is important to know if the e-mail was successfully sent, use the overload of this
	/// method that specifies a sendOnBackgroundThread parameter.
	/// </summary>
	/// <param name="emailRecipient">The recipient of the email.</param>
	/// <param name="subject">The text to appear in the subject of the email.</param>
	/// <param name="body">The body of the email. Must be plain text.</param>
	/// <param name="galleryId">The gallery ID containing the e-mail configuration settings to use.</param>
	public static void sendEmail(String emailRecipient, String subject, String body, long galleryId) throws MessagingException	{
		sendEmail(new String[] {emailRecipient}, subject, body, false, true);
	}

	/// <summary>
	/// Send an e-mail with the specified properties. The e-mail will appear to come from the name and email specified in the
	/// <see cref="IAppSetting.EmailFromName" /> and <see cref="IAppSetting.EmailFromAddress" /> configuration settings.
	/// If <paramref name="sendOnBackgroundThread"/> is <c>true</c>, the e-mail is sent on a background thread and the function
	/// returns immediately. An exception is thrown if an error occurs while sending the e-mail, unless <paramref name="sendOnBackgroundThread"/>
	/// is true, in which case the error is logged but the exception does not propagate back to the calling thread.
	/// </summary>
	/// <param name="emailRecipient">The recipient of the email.</param>
	/// <param name="subject">The text to appear in the subject of the email.</param>
	/// <param name="body">The body of the email. Must be plain text.</param>
	/// <param name="sendOnBackgroundThread">If set to <c>true</c>, send e-mail on a background thread. This causes any errors
	/// to be silently handled by the error logging system, so if it is important for any errors to propogate to the caller,
	/// such as when testing the e-mail function in the Site Administration area, set to <c>false</c>.</param>
	/// <exception cref="WebException">Thrown when a SMTP Server is not specified. (Not thrown when
	/// <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	/// <exception cref="SmtpException">Thrown when the connection to the SMTP server failed, authentication failed,
	/// or the operation timed out. (Not thrown when <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	/// <exception cref="SmtpFailedRecipientsException">The message could not be delivered to of the <paramref name="emailRecipient"/>.
	/// (Not thrown when <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	public static void sendEmail(String emailRecipient, String subject, String body, boolean sendOnBackgroundThread) throws MessagingException{
		sendEmail(new String[] {emailRecipient}, subject, body, false, sendOnBackgroundThread);
	}

	/// <summary>
	/// Send an e-mail with the specified properties. The e-mail will appear to come from the name and email specified in the
	/// <see cref="IAppSetting.EmailFromName" /> and <see cref="IAppSetting.EmailFromAddress" /> configuration settings.
	/// If <paramref name="sendOnBackgroundThread"/> is <c>true</c>, the e-mail is sent on a background thread and the function
	/// returns immediately. An exception is thrown if an error occurs while sending the e-mail, unless <paramref name="sendOnBackgroundThread"/>
	/// is true, in which case the error is logged but the exception does not propagate back to the calling thread.
	/// </summary>
	/// <param name="emailRecipients">The e-mail recipients.</param>
	/// <param name="subject">The text to appear in the subject of the email.</param>
	/// <param name="body">The body of the e-mail. If the body is HTML, specify true for <paramref name="isBodyHtml" />.</param>
	/// <param name="isBodyHtml">Indicates whether the body of the e-mail is in HTML format. When false, the body is
	///   assumed to be plain text.</param>
	/// <param name="sendOnBackgroundThread">If set to <c>true</c>, send e-mail on a background thread. This causes any errors
	///   to be silently handled by the error logging system, so if it is important for any errors to propogate to the caller,
	///   such as when testing the e-mail function in the Site Administration area, set to <c>false</c>.</param>
	/// <exception cref="WebException">Thrown when a SMTP Server is not specified. (Not thrown when
	/// <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	/// <exception cref="SmtpException">Thrown when the connection to the SMTP server failed, authentication failed,
	/// or the operation timed out. (Not thrown when <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	/// <exception cref="SmtpFailedRecipientsException">The message could not be delivered to one or more of the
	/// <paramref name="emailRecipients"/>. (Not thrown when <paramref name="sendOnBackgroundThread"/> is true.)</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="emailRecipients" /> is null.</exception>
	public static void sendEmail(String[] emailRecipients, String subject, String body, boolean isBodyHtml, boolean sendOnBackgroundThread) throws MessagingException	{
		if (emailRecipients == null)
			throw new ArgumentNullException("emailRecipients");

		AppSettings appSettings = AppSettings.getInstance();

		MimeMessage mail = ((JavaMailSenderImpl) mailSender).createMimeMessage();

        // use the true flag to indicate you need a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        
        helper.setTo(emailRecipients);;
        helper.setFrom(appSettings.getEmailFromAddress());
        helper.setSubject(subject);
        helper.setText(body, isBodyHtml);
        //helper.isBodyHtml = isBodyHtml;

		// Because sending the e-mail takes a long time, spin off a thread to send it, unless caller specifically doesn't want to.
		/*if (sendOnBackgroundThread)
		{
			//Task.CMUtils.startNew(() => sendEmail(mail, false));
			runAsync(new Runnable() {
		           public void run() {
		        	   sendEmail(mail, false);
				   }
			}); 
		}else{
			sendEmail(mail, false);
		}*/
	}

	/// <overloads>
	/// Gets the email template.
	/// </overloads>
	/// <summary>
	/// Gets the email template. Replacement parameters in the template are replaced with their appropriate values. The data
	/// in the template can be used to finalruct an e-mail.
	/// </summary>
	/// <param name="template">The template to retrieve.</param>
	/// <param name="user">The user associated with the template.</param>
	/// <returns>Returns an e-mail template.</returns>
	public static EmailTemplate getEmailTemplate(EmailTemplateForm template, UserAccount user, HttpServletRequest request) throws Exception	{
		return getEmailTemplate(template, user.getUserName(), user.getEmail(), request);
	}

	/// <summary>
	/// Gets the email template. Replacement parameters in the template are replaced with their appropriate values. The data
	/// in the template can be used to finalruct an e-mail.
	/// </summary>
	/// <param name="template">The template to retrieve.</param>
	/// <param name="userName">The name of the user associated with the template.</param>
	/// <param name="email">The email of the user associated with the template.</param>
	/// <returns>Returns an e-mail template.</returns>
	private static EmailTemplate getEmailTemplate(EmailTemplateForm template, String userName, String email, HttpServletRequest request) throws Exception{
		EmailTemplate emailTemplate = new EmailTemplate();
		emailTemplate.EmailTemplateId = template;

		String filePath = Utils.getPath(request, MessageFormat.format("/templates/{0}.txt", template));

		// Step 1: Get subject and body from text file and assign to fields. 
		BufferedReader sr = new BufferedReader(new FileReader(filePath));
		boolean subjectOrBody = false;
		String lineText = sr.readLine();
		while (lineText != null){
			String title = lineText.trim();

			if (title == "[Subject]") {
				subjectOrBody = true;
				continue;
			} else if (title == "[Body]") {
				subjectOrBody = false;
				continue;
			}
			
			if (subjectOrBody) {
				emailTemplate.Subject += lineText;
			}else {
				emailTemplate.Body += lineText;
			}
			
			lineText = sr.readLine();
		}
		
		// Step 2: Update replacement parameters with real values.
		emailTemplate.Body = emailTemplate.Body.replace("{CurrentPageUrlFull}", Utils.getCurrentPageUrlFull(request));
		emailTemplate.Body = emailTemplate.Body.replace("{UserName}", userName);
		emailTemplate.Body = emailTemplate.Body.replace("{Email}", StringUtils.isBlank(email) ? I18nUtils.getMessage("Email_Template_No_Email_For_User_Replacement_Text") : email);

		if (emailTemplate.Body.contains("{VerificationUrl}"))
			emailTemplate.Body = emailTemplate.Body.replace("{VerificationUrl}", generateVerificationLink(userName, request));

		if (emailTemplate.Body.contains("{Password}"))
			emailTemplate.Body = emailTemplate.Body.replace("{Password}", HelperFunctions.generatePassword(userName));

		if (emailTemplate.Body.contains("{ManageUserUrl}"))
			emailTemplate.Body = emailTemplate.Body.replace("{ManageUserUrl}", generateManageUserLink(userName, request));

		return emailTemplate;
	}

	/// <summary>
	/// Sends an e-mail based on the <paramref name="templateForm"/> to the specified <paramref name="user"/>.
	/// No action is taken if the user's e-mail is null or empty. The e-mail is sent on a
	/// background thread, so if an error occurs on that thread no exception bubbles to the caller (the error, however, is
	/// recorded in the error log).
	/// </summary>
	/// <param name="user">The user to receive the e-mail.</param>
	/// <param name="templateForm">The template form specifying the type of e-mail to send.</param>
	public static void sendNotificationEmail(UserAccount user, EmailTemplateForm templateForm, HttpServletRequest request) throws Exception	{
		sendNotificationEmail(user.getUserName(), user.getEmail(), templateForm, true, request);
	}

	/// <summary>
	/// Sends an e-mail based on the <paramref name="templateForm" /> to the <paramref name="userName" /> having the 
	/// <paramref name="email" />. No action is taken if the user's e-mail is null or empty. The e-mail is sent on a
	/// background thread, so if an error occurs on that thread no exception bubbles to the caller (the error, however, is
	/// recorded in the error log). If <paramref name="sendOnBackgroundThread" /> is true, the e-mail is sent on a background
	/// thread and the function returns immediately. An exception is thrown if an error occurs while sending the e-mail,
	/// unless <paramref name="sendOnBackgroundThread" /> is true, in which case the error is logged but the exception does
	/// not propagate back to the UI thread.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <param name="email">The email address of the user.</param>
	/// <param name="templateForm">The template form specifying the type of e-mail to send.</param>
	/// <param name="sendOnBackgroundThread">If set to <c>true</c> send e-mail on a background thread. This causes any errors
	/// to be silently handled by the error logging system, so if it is important for any errors to propogate to the UI,
	/// such as when testing the e-mail function in the Site Administration area, set to <c>false</c>.</param>
	/// <exception cref="System.ArgumentNullException">userName</exception>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="userName" /> is null.</exception>
	public static void sendNotificationEmail(String userName, String email, EmailTemplateForm templateForm, boolean sendOnBackgroundThread, HttpServletRequest request) throws Exception	{
		if (userName == null)
			throw new ArgumentNullException("userName");

		if (StringUtils.isBlank(email))
			return;

		EmailTemplate emailTemplate = getEmailTemplate(templateForm, userName, email, request);

		sendEmail(userName + "<" + email + ">", emailTemplate.Subject, emailTemplate.Body, sendOnBackgroundThread);
	}

	//#endregion

	//#region Private Static Methods

	/// <summary>
	/// Sends the e-mail. If <paramref name="suppressException"/> is <c>true</c>, record any exception that occurs but do not
	/// let it propagate out of this function. When <c>false</c>, record the exception and re-throw it. The caller is
	/// responsible for disposing the <paramref name="mail"/> object.
	/// </summary>
	/// <param name="mail">The mail message to send.</param>
	/// <param name="suppressException">If <c>true</c>, record any exception that occurs but do not
	/// let it propagate out of this function. When <c>false</c>, record the exception and re-throw it.</param>
	private static void sendEmail(MimeMessageHelper mail, boolean suppressException) throws Exception	{
		try
		{
			if (mail == null)
				throw new ArgumentNullException("mail");
			
			AppSettings appSettings = AppSettings.getInstance();
			
			Properties props = new Properties();		
			props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
			
			
			Authenticator auth = new Authenticator() {
				//override the getPasswordAuthentication method
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(appSettings.getEmailFromAddress(), appSettings.getEmailPassword());
				}
			};

			String smtpServer = appSettings.getSmtpServer();
			int smtpServerPort = StringUtils.toInteger(appSettings.getSmtpServerPort(), Integer.MIN_VALUE);

			// Specify SMTP server if it is specified in the gallery settings. The server might have been assigned via web.config,
			// so only update this if we have a setting.
			if (!StringUtils.isBlank(smtpServer)){
				props.put("mail.host", smtpServer); //SMTP Host
			}

			// Specify port number if it is specified in the gallery settings and it's not the default value of 25. The port 
			// might have been assigned via web.config, so only update this if we have a setting.
			if (appSettings.getSendEmailUsingSsl()) {
				props.put("mail.smtp.socketFactory.port", smtpServerPort); //SSL Port
				props.put("mail.smtp.socketFactory.class",
						"javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
			}else {
				if ((smtpServerPort > 0) && (smtpServerPort != 25))	{
					props.put("mail.smtp.port", smtpServerPort); //SMTP Port
				}
			}		
			mailSender.setJavaMailProperties(props);

			if (StringUtils.isBlank(smtpServer))
				throw new WebException("Cannot send e-mail because a SMTP Server is not specified. This can be configured in any of the following places: (1) Site Admin - General page (preferred), or (2) web.config (Ex: configuration/system.net/mailSettings/smtp/network host='your SMTP server').");

			mailSender.send(mail.getMimeMessage());
		}
		catch (Exception ex)
		{
			AppEventLogUtils.LogError(ex);

			if (!suppressException)
				throw ex;
		}
	}

	private static String generateVerificationLink(String userName, HttpServletRequest request) throws Exception	{
		return StringUtils.join(Utils.getHostUrl(request), Utils.getUrl(request, ResourceId.createaccount, "verify={0}", Utils.urlEncode(HelperFunctions.encrypt(userName))));
	}

	private static String generateManageUserLink(String userName, HttpServletRequest request){
		return StringUtils.join(Utils.getHostUrl(request), Utils.getUrl(request, ResourceId.sys_users));
	}

	//#endregion
}
