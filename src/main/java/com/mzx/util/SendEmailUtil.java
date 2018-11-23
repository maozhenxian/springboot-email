package com.mzx.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sun.mail.util.MailSSLSocketFactory;

/**
 * 邮件发送工具类
 * 
 * @author maozh
 *
 */
@Component
public class SendEmailUtil {

    private static final Logger log = LoggerFactory.getLogger(SendEmailUtil.class);
    
    @Autowired
    private Environment env;

    private static String auth;

    private static String host;

    private static String protocol;

    private static int port;

    private static String authName;

    private static String password;

    private static boolean isSSL;

    private static String charset;

    private static String timeout;
    
    @PostConstruct
    public void initParam () {
        auth = env.getProperty("mail.smtp.auth");
        host = env.getProperty("mail.host");
        protocol = env.getProperty("mail.transport.protocol");
        port = env.getProperty("mail.smtp.port", Integer.class);
        authName = env.getProperty("mail.auth.name");
        password = env.getProperty("mail.auth.password");
        charset = env.getProperty("mail.send.charset");
        isSSL = env.getProperty("mail.is.ssl", Boolean.class);
        timeout = env.getProperty("mail.smtp.timeout");
    }

    /**
     * 
     * @param item
     *            主题
     * @param users
     *            收件人
     * @param fusers
     *            抄送
     * @param content
     *            邮件内容
     * @param attachfiles
     *            附件列表
     * @return 邮件发送是否成功
     */
    public static boolean sendEmail(String item, String[] users, String[] fusers, String content,
            List<Map<String, String>> attachfiles) {
        boolean flag = true;
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(host);
            sender.setUsername(authName);
            sender.setPassword(password);
            sender.setDefaultEncoding(charset);
            sender.setProtocol(protocol);
            sender.setPort(port);
            
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", auth);
            properties.setProperty("mail.smtp.timeout", timeout);
            
            if(isSSL) {
                MailSSLSocketFactory sf = null;
                try {
                    sf = new MailSSLSocketFactory();
                    sf.setTrustAllHosts(true);
                    
                    properties.put("mail.smtp.ssl.enable", "true");
                    properties.put("mail.smtp.ssl.socketFactory", sf);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            sender.setJavaMailProperties(properties);
            
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setTo(users);
            if (fusers != null && fusers.length > 0) {
                messageHelper.setCc(fusers);
            }
            
            messageHelper.setFrom(authName);
            messageHelper.setSubject(item);
            messageHelper.setText(content, true);

            if (attachfiles != null && attachfiles.size() > 0) {
                for (Map<String, String> attachfile : attachfiles) {
                    String attachfileName = attachfile.get("name");
                    File file = new File(attachfile.get("file"));
                    messageHelper.addAttachment(attachfileName, file);
                }
            }
            sender.send(message);


        } catch (Exception e) {
            log.error("邮件发送失败");
            flag = false;
        }
        return flag;
    }
}
