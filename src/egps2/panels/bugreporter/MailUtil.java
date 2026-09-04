package egps2.panels.bugreporter;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

/**
 * MailUtil is a reusable Swing panel or dialog within eGPS.
 */
public class MailUtil {
	private final static String FROM = "reportBug@egps.desktop.com";

	public static void main(String[] args) {
		setMail("这是邮件的内容!", "example@qq.com");
	}

	public static void setMail(String text, String toMail) {
		String smtp = getSmtpByEmail(toMail);
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", smtp);
		props.put("mai.smtp.auth", "false");

		Session session = Session.getInstance(props, null);
		MimeMessage msg = new MimeMessage(session);

		try {
			msg.setFrom(FROM);
			msg.setSubject("紧急通知", "gb2312");
			Multipart multipart = new MimeMultipart();
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setText(text, "gb2312");
			multipart.addBodyPart(bodyPart);
			msg.setContent(multipart);
			msg.addHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
			msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toMail));
			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private static String getSmtpByEmail(String mail) {
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put(Context.PROVIDER_URL, "dns://");
		hashtable.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");

		String domain = mail.substring(mail.lastIndexOf('@') + 1);
		Attributes attrs = null;
		String smtp = null;
		try {
			InitialDirContext dirContext = new InitialDirContext(hashtable);
			attrs = dirContext.getAttributes(domain, new String[] { "MX" });
			NamingEnumeration<? extends Attribute> attrsAll = attrs.getAll();

			while (attrsAll.hasMore()) {
				Attribute next = attrsAll.next();
				for (int i = 0; i < next.size(); i++) {
					String s = (String) next.get(i);
					smtp = (s).substring(s.lastIndexOf(' ') + 1);
					break;
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return smtp;

	}

	public static void sentEmailWithAnnomusHost(Map<String, String> inforMap, String toMail) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : inforMap.entrySet()) {
			sb.append(entry.getKey()).append("\t").append(entry.getValue()).append("\n");
		}

		setMail(sb.toString(), toMail);

	}
}
