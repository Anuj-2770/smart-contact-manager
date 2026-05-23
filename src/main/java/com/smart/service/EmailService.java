package com.smart.service;

//import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject, String message, String to) {

		boolean f = false;
		String from = "anujyadav221307vns@gmail.com";
		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();
		System.out.println("Properties " + properties);

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("anujyadav221307vns@gmail.com", "yltclrqhcnvdxxps");
			}
		});

		session.setDebug(true);

		try {
			MimeMessage m = new MimeMessage(session);

			m.setFrom(from);
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			m.setSubject(subject);

			MimeMultipart mimeMultipart = new MimeMultipart();

			MimeBodyPart textMime = new MimeBodyPart();

			// HTML content set karo
			textMime.setContent(message, "text/html; charset=utf-8");

			mimeMultipart.addBodyPart(textMime);

			m.setContent(mimeMultipart);

			Transport.send(m);

			System.out.println("send Email success-----------");
			f = true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}
}