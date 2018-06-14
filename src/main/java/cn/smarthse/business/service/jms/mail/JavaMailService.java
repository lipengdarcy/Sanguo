package cn.smarthse.business.service.jms.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import cn.smarthse.framework.model.mail.Mail;

@Component
public class JavaMailService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SimpleMailMessage message;
	@Autowired
	private ThreadPoolTaskExecutor threadPool;

	/**
	 * 发送邮件
	 * 
	 * @param mail
	 */
	public void sendMail(final Mail mail) {
		threadPool.execute(new Runnable() {
			public void run() {
				try {
					if (mail.getFrom() != null) {
						message.setFrom(mail.getFrom());
					}
					message.setTo(mail.getTo());
					message.setSubject(mail.getSubject());
					message.setText(mail.getContent());
					mailSender.send(message);
				} catch (MailException e) {
					throw e;
				}
			}
		});
	}

}