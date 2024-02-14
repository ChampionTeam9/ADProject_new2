package com.ad.teamnine.service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.*;
import com.ad.teamnine.repository.AdminRepository;
import com.ad.teamnine.repository.ReportRepository;

import java.util.List;
import java.util.Properties;
import java.util.Random;

@Service
public class EmailService {
	@Autowired
	AdminRepository adminRepo;
	@Autowired
	ReportRepository reportRepo;

	public void MemberReportNotificationToMemberReported(MemberReport memberReport) {
		String recipientEmail = memberReport.getMemberReported().getEmail();
		final String username = "zhangten0131@gmail.com"; // 发送邮件的邮箱地址
		final String password = "c o s f uvao ofrk etnj"; // 邮箱密码或授权码
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// 创建 MimeMessage 对象
			Message message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(username));
			// 设置收件人
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			// 设置邮件主题
			message.setSubject("You've been reported! please login to check!");
			// 设置邮件内容
			message.setText("Dear member " + memberReport.getMemberReported().getUsername() + ",\n"
					+ "You have been reported!\n" + "Reason:\"" + memberReport.getReason() + "\",\n"
					+ "Please login to check!");

			// 发送邮件
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public void MemberReportApprovedNotificationToMemberReported(MemberReport memberReport) {
		String recipientEmail = memberReport.getMemberReported().getEmail();
		final String username = "zhangten0131@gmail.com"; // 发送邮件的邮箱地址
		final String password = "c o s f uvao ofrk etnj"; // 邮箱密码或授权码
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// 创建 MimeMessage 对象
			Message message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(username));
			// 设置收件人
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			// 设置邮件主题
			message.setSubject("Account Deleted！");
			// 设置邮件内容
			message.setText("Dear member " + memberReport.getMemberReported().getUsername() + ",\n"
					+ "You Account has been deleted"+",\n"
					+ "Please contact us if any questions!");

			// 发送邮件
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void RecipeReportNotificationToMember(RecipeReport recipeReport) {
		String recipientEmail = recipeReport.getRecipeReported().getMember().getEmail();
		final String username = "zhangten0131@gmail.com"; // 发送邮件的邮箱地址
		final String password = "c o s f uvao ofrk etnj"; // 邮箱密码或授权码
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// 创建 MimeMessage 对象
			Message message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(username));
			// 设置收件人
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			// 设置邮件主题
			message.setSubject("Your Recipe has been reported！");
			// 设置邮件内容
			message.setText(
					"Dear member " + recipeReport.getMember().getUsername() + ",\n" + "Your recipe has been reported!\n"
							+ "Reason:\"" + recipeReport.getReason() + "\",\n" + "Please login to check!");
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public void RecipeReportApprovedNotificationToMember(RecipeReport recipeReport) {
		String recipientEmail = recipeReport.getRecipeReported().getMember().getEmail();
		final String username = "zhangten0131@gmail.com"; // 发送邮件的邮箱地址
		final String password = "c o s f uvao ofrk etnj"; // 邮箱密码或授权码
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// 创建 MimeMessage 对象
			Message message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(username));
			// 设置收件人
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			// 设置邮件主题
			message.setSubject("Recipe Deleted");
			// 设置邮件内容
			message.setText(
					"Dear member " + recipeReport.getMember().getUsername() + ",\n" + "Your recipe "+recipeReport.getRecipeReported()+" has been deleted!\n"
							+ "Please contact us if any question!");
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void ReportNotificationToAdmin(Report report, String reportType) {
		List<Admin> admins = adminRepo.findAll();
		for (Admin admin : admins) {
			String recipientEmail = admin.getEmail();
			if (recipientEmail != null) {
				final String username = "zhangten0131@gmail.com";
				final String password = "c o s f uvao ofrk etnj";
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");
				Session session = Session.getInstance(props, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
				try {
					// 创建 MimeMessage 对象
					Message message = new MimeMessage(session);
					// 设置发件人
					message.setFrom(new InternetAddress(username));
					// 设置收件人
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
					// 设置邮件主题
					message.setSubject("One new report is created!");
					// 设置邮件内容
					message.setText("Dear admin, There is a new " + reportType + " created by member \""
							+ report.getMember().getUsername() + "\",\n"
							+ "The number of reports pending for approval : " + reportRepo.countByStatus(Status.PENDING)
							+ ",\n" + "Please login to check!");
					// 发送邮件
					Transport.send(message);
					System.out.println("Email sent successfully!");
				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public void SendEmailVerificationCodeToMember(Member member, String code) {
		String recipientEmail = member.getEmail();
		final String username = "zhangten0131@gmail.com"; // 发送邮件的邮箱地址
		final String password = "c o s f uvao ofrk etnj"; // 邮箱密码或授权码
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// 创建 MimeMessage 对象
			Message message = new MimeMessage(session);
			// 设置发件人
			message.setFrom(new InternetAddress(username));
			// 设置收件人
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			// 设置邮件主题
			message.setSubject("Email Verify");
			// 设置邮件内容
			message.setText("Your Email Verification Code is :" + code);
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String generateVerificationCode() {
		int codeLength = 4;
		Random random = new Random();
		StringBuilder codeBuilder = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			int digit = random.nextInt(10);
			codeBuilder.append(digit);
		}
		return codeBuilder.toString();
	}
}