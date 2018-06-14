package cn.smarthse.business.service.jms.mail;

import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.smarthse.framework.model.mail.Mail;


@Component
public class ConsumerSessionAwareMessageListener implements
        SessionAwareMessageListener<Message> {

    private static final Log log = LogFactory
            .getLog(ConsumerSessionAwareMessageListener.class);

    @Autowired
    private JavaMailService MailService;

    public synchronized void onMessage(Message message, Session session) {
        try {
            ActiveMQTextMessage msg = (ActiveMQTextMessage) message;
            final String ms = msg.getText();
            log.info("==>receive message:" + ms);
            Mail mail = JSONObject.parseObject(ms, Mail.class);
            if (mail == null) {
                return;
            }
            try {
            	MailService.sendMail(mail);
            } catch (Exception e) {             
                log.error("==>MailException:", e);
            }
        } catch (Exception e) {
            log.error("==>", e);
        }
    }
}
