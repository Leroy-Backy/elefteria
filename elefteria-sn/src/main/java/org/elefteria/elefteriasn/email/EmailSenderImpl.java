package org.elefteria.elefteriasn.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderImpl implements EmailSender{

    private static Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);

    private JavaMailSender mailSender;

    @Autowired
    public EmailSenderImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void send(String to, String emailText, String subject) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setText(emailText, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("anketa.leroy@gmail.com");

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            logger.error("Failed to send email: ", e);
            throw new IllegalStateException("Failed to send email");
        }

    }

    public static String getEmailHtmlTemplate(String name, String link){
        return "<div style=\"text-align: center\">\n" +
                "    <div style=\"background-color: black; height: 50px; width: 100%; color: white; font-size: 30px; padding-top: 10px\">\n" +
                "        Confirm your email</div>\n" +
                "\n" +
                "    <div style=\"margin-left: 10px; margin-right: 10px; background-color: aliceblue; height: 200px; text-align: left; padding: 10px\">\n" +
                "        <h3>Hello " + name + "</h3>\n" +
                "\n" +
                "        <div style=\"font-size: 18px; font-weight: bold\">\n" +
                "            Thank you for registration. Please click on the link to activate your account: <a href=" + link + ">activate account</a>\n" +
                "        </div>\n" +
                "\n" +
                "    </div>\n" +
                "</div>";
    }

    public static String getChangePasswordHtmlTemplate(String name, String link){
        return "<div style=\"text-align: center\">\n" +
                "    <div style=\"background-color: black; height: 50px; width: 100%; color: white; font-size: 30px; padding-top: 10px\">\n" +
                "        Change your password</div>\n" +
                "\n" +
                "    <div style=\"margin-left: 10px; margin-right: 10px; background-color: aliceblue; height: 200px; text-align: left; padding: 10px\">\n" +
                "        <h3>Hello " + name + "</h3>\n" +
                "\n" +
                "        <div style=\"font-size: 18px; font-weight: bold\">\n" +
                "            You said that you forgot your password. Please click on the link to change your password: <a href=" + link + ">change password</a>\n" +
                "        </div>\n" +
                "\n" +
                "    </div>\n" +
                "</div>";
    }
}
