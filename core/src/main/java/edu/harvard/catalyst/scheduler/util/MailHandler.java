/**
 * Copyright (c) 2015-2016, President and Fellows of Harvard College
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.harvard.catalyst.scheduler.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;



@Component
public class MailHandler {

	private static final Logger logUtil = Logger.getLogger(MailHandler.class.getName());
	private final Boolean mailEnabled;
	private final String mailFrom;

	private final JavaMailSender mailSender;



	@Autowired
	public MailHandler(@Qualifier("mailEnabled") final Boolean mailEnabled,
					   @Qualifier("mailFrom") final String mailFrom,
					   @Qualifier("javaMailSender") final JavaMailSender mailSender) {

		this.mailEnabled = mailEnabled;
		this.mailFrom = normalizeFrom(mailFrom);
		this.mailSender = mailSender;
	}


	private String normalizeFrom(final String from) {
		return from == null || from.equals("") ? "AnonymousSender@harvard.edu" : from;
	}

	public void sendOptionalEmails(final SimpleMailMessage message) {
		if (mailEnabled) {
			sendEmail(message);
		} else {
			logUtil.info("Trigger to send email has been set to: " + mailEnabled);
		}
	}

	public void sendMandatoryEmails(final SimpleMailMessage message) {
		sendEmail(message);
	}

	void sendEmail(final SimpleMailMessage message) {
		try {
			if (message.getText().contains("html") || message.getSubject().contains("html")) {
				final MimeMessage mimeMessage = mailSender.createMimeMessage();
				final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
				messageHelper.setText(message.getText(), true);
				messageHelper.setTo(message.getTo());
				messageHelper.setFrom(mailFrom);
				if (message.getCc() != null) {
					messageHelper.setCc(message.getCc());
				}
				if (message.getBcc() != null) {
					messageHelper.setBcc(message.getBcc());
				}
				messageHelper.setSubject(message.getSubject());
				mailSender.send(mimeMessage);
			} else {
				message.setFrom(mailFrom);
				mailSender.send(message);
			}
		} catch (final javax.mail.MessagingException me) {
			final String complaint = "ERROR: sending mail to: " + message.getTo() + ", from: " + mailFrom + ", with subject: " + message.getSubject();
			logUtil.info(complaint);
		}
	}



	public void sendCalendarInvite( CalendarRequest calendarRequest) {
	try {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		mimeMessage.addHeaderLine("method=REQUEST");
		mimeMessage.addHeaderLine("charset=UTF-8");
		mimeMessage.addHeaderLine("component=VEVENT");
		mimeMessage.setFrom(new InternetAddress(mailFrom));
		mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(calendarRequest.getToEmail()));
		mimeMessage.setSubject(calendarRequest.getSubject());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
		StringBuilder builder = new StringBuilder();
		builder.append("BEGIN:VCALENDAR\n" +
				"METHOD:REQUEST\n" +
				"PRODID:-//CCTSI//Scheduler//EN\n" +
				"VERSION:2.0\n" +
				"BEGIN:VTIMEZONE\n" +
				"TZID:America/Denver\n" +
				"END:VTIMEZONE\n" +
				"BEGIN:VEVENT\n" +
				"ATTENDEE;ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:" + calendarRequest.getToEmail() + "\n" +
				"ORGANIZER;CN=CCTSI Scheduler:MAILTO:" + mailFrom + "\n" +
				"DESCRIPTION;LANGUAGE=en-US:" + calendarRequest.getBody() + "\n" +
				"UID:" + calendarRequest.getUid() + "\n" +
				"SUMMARY;LANGUAGE=en-US:Appointment Reminder\n" +
				"DTSTART:" + formatter.format(calendarRequest.getMeetingStartTime()).replace(" ", "T") + "\n" +
				"DTEND:" + formatter.format(calendarRequest.getMeetingEndTime()).replace(" ", "T") + "\n" +
				"CLASS:PUBLIC\n" +
				"PRIORITY:5\n" +
				"DTSTAMP:" + LocalDateTime.now() + "\n" +
				"TRANSP:OPAQUE\n" +
				"STATUS:CONFIRMED\n" +
				"SEQUENCE:$sequenceNumber\n" +
				"BEGIN:VALARM\n" +
				"DESCRIPTION:REMINDER\n" +
				"TRIGGER;RELATED=START:-PT15M\n" +
				"ACTION:DISPLAY\n" +
				"END:VALARM\n" +
				"END:VEVENT\n" +
				"END:VCALENDAR");

		MimeBodyPart messageBodyPart = new MimeBodyPart();

		messageBodyPart.setHeader("Content-Class", "urn:content-classes:calendarmessage");
		messageBodyPart.setHeader("Content-ID", "calendar_message");
		messageBodyPart.setDataHandler(new DataHandler(
				new ByteArrayDataSource(builder.toString(), "text/calendar;method=REQUEST;name=\"invite.ics\"")));

		MimeMultipart multipart = new MimeMultipart();

		multipart.addBodyPart(messageBodyPart);

		mimeMessage.setContent(multipart);

		mailSender.send(mimeMessage);
	}
	catch (Exception e) {
		final String complaint = "ERROR: sending mail to: " + calendarRequest.getToEmail() + ", from: " + mailFrom + ", with subject: " + calendarRequest.getSubject();
		logUtil.info(complaint);

	}

	}

}
