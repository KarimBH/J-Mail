package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.allcolor.yahp.converter.CYaHPConverter;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer;
import org.allcolor.yahp.converter.IHtmlToPdfTransformer.CConvertException;

public class MessageUtilities {

	public static Message[] getMessages(Store store, String folderAOuvrir) {

		try {
			Folder folder = store.getFolder(folderAOuvrir);
			if (!folder.isOpen())
				folder.open(Folder.READ_WRITE);

			return folder.getMessages();

		} catch (MessagingException e) {
			System.out.println("Connexion imposible ou Dossier inexistant");
			e.printStackTrace();
		}
		return null;

	}

	public static List<Object> getAllContent(Message message) {
		// retourne le contenu et les pieces jointes
		List<Object> allContent = new ArrayList<Object>();
		// Utile pour les pieces jointes
		List<BodyPart> attachments = new ArrayList<BodyPart>();
		try {
			Object content = message.getContent();
			if (content instanceof Multipart) {
				StringBuffer messageContent = new StringBuffer();
				Multipart multipart = (Multipart) content;
				for (int i = 0; i < multipart.getCount(); i++) {
					BodyPart bodyPart = multipart.getBodyPart(i);
					if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart
							.getDisposition())) {
						messageContent.append(getText((Part) bodyPart));
					} else {
						attachments.add(bodyPart);
					}
				}

				if (messageContent.toString().contains("<!DOC")) {
					allContent.add(0, "<!DOC"
							+ (messageContent.toString().split("<!DOC")[1]));
				}
				if (messageContent.toString().contains("<html")) {
					allContent.add(0, "<html"
							+ (messageContent.toString().split("<html")[1]));
				} else {
					allContent.add(0, messageContent.toString());
				}

				allContent.add(1, attachments);
			} else {
				allContent.add(0, content.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allContent;

	}

	private static String getText(Part p) throws MessagingException,
			IOException {
		if (p.isMimeType("text/*")) {
			String s = (String) p.getContent();
			return s;
		}

		if (p.isMimeType("multipart/alternative")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}
		return null;
	}

	public static void enregistrerPiecesJointes(String dossierPath,
			BodyPart bodyPart) {
		try {
			InputStream is = bodyPart.getInputStream();
			File f = new File(dossierPath
					+ General.decodeISO(bodyPart.getFileName()));
			FileOutputStream fos = new FileOutputStream(f);
			byte[] buf = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buf)) != -1) {
				fos.write(buf, 0, bytesRead);
			}
			System.out.println("Piece jointe enregitré!!");
			fos.close();
		} catch (FileNotFoundException e) {
			System.out.println(" Dossier introuvable");
		} catch (IOException e) {
			System.out.println(" Erreur!!");
		} catch (MessagingException e) {
			System.out.println(" Erreur 2 !!!");
		}
	}

	public static Folder[] getAvailableFolders(Store store)
			throws MessagingException {

		return store.getDefaultFolder().list();

	}

	public static Store connect(String adresse, String password, String type,
			String serveur) {
		Store store = null;
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", type + "s");
		Session session = Session.getDefaultInstance(props, null);
		try {
			store = session.getStore(type + "s");
			store.connect(serveur, adresse, password);
			return store;
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static boolean htmlToPdf(String aExtraire, String path) {
		CYaHPConverter converter = new CYaHPConverter();
		File fout = new File(path + ".pdf");
		try {
			FileOutputStream out = new FileOutputStream(fout);
			Map<String, String> properties = new HashMap<String, String>();
			List<?> headerFooterList = new ArrayList();

			properties.put(IHtmlToPdfTransformer.PDF_RENDERER_CLASS,
					IHtmlToPdfTransformer.FLYINGSAUCER_PDF_RENDERER);
			// properties.put(IHtmlToPdfTransformer.FOP_TTF_FONT_PATH,
			// fontPath);
			converter.convertToPdf(aExtraire, IHtmlToPdfTransformer.A4P,
					headerFooterList, "file:///temp/", // root for relative
														// external
														// CSS and IMAGE
					out, properties);
			out.flush();
			out.close();
			return true; // extraction réussie
		} catch (FileNotFoundException e) {

			System.out.println("Fichier non trouvee");
			 // extraction impossible
		} catch (CConvertException e) {
			System.out.println("Convert Problem");
			 // extraction impossible
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// extraction impossible
		}
		return false;
	}

	public static Boolean envoiMail(String[] infosCompte, String destinataires,
			String cc,String cci, String objet, String contenu, ArrayList<File> filePaths)
			throws MessagingException {
		/*
		 * infosCompte[0]: adresse compte infosCompte[1]: mot de passe crypte
		 * infosCompte[2]: type: imap ou pop3 infosCompte[3]: serveur entrant
		 * infosCompte[4]: port infosCompte[5]: serveur smtp infosCompte[6]:
		 * port smtp
		 */

		String host = infosCompte[5];
		String Password = Security.decrypt(infosCompte[1]);
		String from = infosCompte[0];
		String[] toAddress = destinataires.split(",");
		String[] ccAddress = cc.split(",");
		String[] cciAddress = cci.split(",");
		// Get system properties

		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtps.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getInstance(props, null);

		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(from));

		for (int i = 0; i < toAddress.length; i++) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toAddress[i]));
		}
		try {
			if (ccAddress.length > 0) {
				for (int i = 0; i < ccAddress.length; i++) {
					message.addRecipient(Message.RecipientType.CC,
							new InternetAddress(ccAddress[i]));
				}
			}
			if (cciAddress.length > 0) {
				for (int i = 0; i < cciAddress.length; i++) {
					message.addRecipient(Message.RecipientType.BCC,
							new InternetAddress(cciAddress[i]));
				}
			}
		} catch (Exception e) {
		}

		message.setSubject(objet);

		BodyPart messageBodyPart = new MimeBodyPart();
		MimeBodyPart messageFilePart = new MimeBodyPart();
		messageBodyPart.setText(contenu);

		// ////////-----------------------------------
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		for (int i = 0; i < filePaths.size(); i++) {
			System.out.println("<<<<<<<<<<" + filePaths.get(i));
			// (messageFilePart).attachFile(filePaths.get(i));

			messageFilePart = new MimeBodyPart();

			String filename = filePaths.get(i).getName();
			String fileloc = filePaths.get(i).getPath();
			DataSource source = new FileDataSource(fileloc);
			messageFilePart.setDataHandler(new DataHandler(source));
			messageFilePart.setFileName(filename);
			multipart.addBodyPart(messageFilePart);

		}

		// Set the body and file

		// 6. set the multiplart object to the message object

		message.setContent(multipart, "text/html");
		// ////////-----------------------------------F

		try {
			Transport tr = session.getTransport("smtps");
			tr.connect(host, from, Password);
			tr.sendMessage(message, message.getAllRecipients());
			System.out.println("Mail Sent Successfully");
			tr.close();
			return true;
		} catch (SendFailedException sfe) {
			sfe.printStackTrace();
			System.out.println("Envoi Impossible");
			return false;

		}
	}

	public static boolean verifMail(String toVerify) {
		return Pattern.matches(
				"^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)+$",
				toVerify);
	}
}
