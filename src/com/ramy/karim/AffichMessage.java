package com.ramy.karim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import utilities.General;
import utilities.MessageUtilities;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;

public class AffichMessage extends JFrame {
	static ImageIcon iconPDF = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage("images/pdfIcon2.jpg"));
	static ImageIcon iconSupprimer = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage("images/supprimerIcon.jpg"));
	static ImageIcon iconRepondre = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage("images/repondreIcon.jpg"));
	static ImageIcon iconTransferer = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage("images/transfertIcon3.jpg"));
	private Message selectedMessage;
	private static final long serialVersionUID = 1L;

	private JButton suivantButton;
	private JButton precedentButton;
	private JPanel boutonPanel = new JPanel();
	private JButton multiButton;
	private JButton repondre;
	private JButton transferer;
	private JButton piecesJointesDownloadButton;
	private JPanel piecesJointesPanel;
	private JPanel southPanel;
	private JLabel From;

	private JPanel infoPanel = new JPanel();
	private String from;
	private JPanel webBrowserPanel;
	private List<BodyPart> piecesJointes;
	private List<Object> allContent;
	private int i = 1;
	private int nombreMessage;
	private int ligne;
	private JFileChooser chooser;
	JWebBrowser webBrowser;

	public AffichMessage(final String[] infosProfil, final JTable tableau)
			throws MessagingException {
		final int row = tableau.getSelectedRow();
		final MessageTableModel modele = (MessageTableModel) tableau.getModel();
		ligne = row;
		nombreMessage = modele.getRowCount();

		selectedMessage = modele.getMessage(row);
		suivantButton = new JButton("  >  ");
		precedentButton = new JButton("  <  ");
		boutonPanel = new JPanel();

		piecesJointesPanel = new JPanel();
		southPanel = new JPanel();
		From = new JLabel();

		infoPanel = new JPanel();

		// UIUtils.setPreferredLookAndFeel();
		NativeInterface.open();
		webBrowser = new JWebBrowser();
		webBrowser.setBarsVisible(false);
		webBrowser.setStatusBarVisible(true);
		webBrowserPanel = new JPanel();
		webBrowserPanel.setLayout(new BorderLayout());
		webBrowserPanel.add(webBrowser, BorderLayout.CENTER);

		infoPanel.setBackground(General.VERT);
		infoPanel.add(From);

		getContentPane().add(BorderLayout.NORTH, infoPanel);
		// precedentButton.setFont(new Font("Serif", Font.BOLD, 30));
		precedentButton.setForeground(Color.white);
		suivantButton.setForeground(Color.white);
		// suivantButton.setFont(new Font("Serif", Font.BOLD, 30));

		class suivantListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int j = i;
					selectedMessage = modele.getMessage(ligne + j);
					increaseI();

					setContent(selectedMessage);
				} catch (IndexOutOfBoundsException e1) {
					i = 1;
					ligne = 0;
					selectedMessage = modele.getMessage(0);

					setContent(selectedMessage);

				}
			}
		}
		class precedentListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					int j = i;
					selectedMessage = modele.getMessage(ligne - j);

					decreaseI();
					setContent(selectedMessage);
				} catch (IndexOutOfBoundsException e1) {
					i = 1;
					ligne = nombreMessage;

					selectedMessage = modele.getMessage(nombreMessage - 1);
					setContent(selectedMessage);

				}
			}
		}
		suivantButton.addActionListener(new suivantListener());
		precedentButton.setBorder(null);
		suivantButton.setBorder(null);
		precedentButton.setBackground(Color.LIGHT_GRAY);
		precedentButton.addActionListener(new precedentListener());
		suivantButton.setBackground(Color.LIGHT_GRAY);
		getContentPane().add(BorderLayout.WEST, precedentButton);
		getContentPane().add(BorderLayout.EAST, suivantButton);

		multiButton = new JButton(iconPDF);
		multiButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (MessageUtilities.htmlToPdf(webBrowser.getHTMLContent(),
						choixDossier(true))) {
					  JOptionPane.showMessageDialog(null, "Votre message a bien été extrait en PDF");
				} else {
					 JOptionPane.showMessageDialog(null,
							 "L'extraction de ce message a échoué","Erreur",
							 JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		boutonPanel.add(multiButton);

		multiButton = new JButton(iconSupprimer);
		multiButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int n = JOptionPane.showConfirmDialog(null,
						"Supprimer ce message?", "Confirmation",
						JOptionPane.OK_CANCEL_OPTION);
				if (n == 0) {
					try {
						selectedMessage.setFlag(Flags.Flag.DELETED, true);
						modele.deleteMessage(row);
						suivantButton.doClick();
					} catch (MessagingException e) {
						System.out.println("suppression impossible :/");
					}
			
				}
			}
		});
		boutonPanel.add(multiButton);
		repondre = new JButton(iconRepondre);
		repondre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				new NouveauMessage(infosProfil, selectedMessage, null);

			}
		});
		boutonPanel.add(repondre);
		transferer = new JButton(iconTransferer);
		transferer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new NouveauMessage(infosProfil, null, selectedMessage);
			}
		});
		boutonPanel.add(transferer);

		boutonPanel.setBackground(General.BLEU);

		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		southPanel.add(piecesJointesPanel);
		southPanel.add(boutonPanel);

		setContent(selectedMessage);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose();
				NativeInterface.close();
			}
		});
		getContentPane().add(BorderLayout.SOUTH, southPanel);
		getContentPane().add(webBrowserPanel, BorderLayout.CENTER);
		setSize(900, 500);
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		setVisible(true);

	}

	@SuppressWarnings({ "unchecked" })
	public void setContent(Message selectedMessage) {
		try {

			allContent = MessageUtilities.getAllContent(selectedMessage);
			piecesJointesPanel.removeAll();

			from = General.decodeISO(selectedMessage.getFrom()[0].toString());
			if (from.contains("noreply") || from.contains("no-reply")) {
				repondre.setEnabled(false);
				transferer.setEnabled(false);
			} else {
				repondre.setEnabled(true);
				transferer.setEnabled(true);
			}
			// receiver=selectedMessage.getRecipients(Message.RecipientType.TO)[0].toString();
			From.setText(General.decodeISO(from.split(" <")[0]) + " | "
					+ selectedMessage.getSubject() + " | "
					+ selectedMessage.getSentDate());

			webBrowser.setHTMLContent((String) allContent.get(0)); // <<<<<<<<<<<<<<<<<<<<<<

			System.out.println("*******" + (String) allContent.get(0)
					+ "*******");
			// piecesJointes =
			// MessageUtilities.getBodyAttachements(selectedMessage);
			piecesJointes = (List<BodyPart>) (allContent.get(1));
			for (short i = 0; i < piecesJointes.size(); i++) {
				final short a = i;
				if (i == 0)
					piecesJointesPanel.add(new JLabel("Pieces Jointes:    "));
				piecesJointesDownloadButton = new JButton(
						General.decodeISO(piecesJointes.get(i).getFileName()));
				piecesJointesDownloadButton.setBackground(Color.white);
				piecesJointesDownloadButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								MessageUtilities.enregistrerPiecesJointes(
										choixDossier(false),
										piecesJointes.get(a));

							}
						});
				piecesJointesPanel.add(piecesJointesDownloadButton);
			}
		} catch (Exception e) {
			// contenu.setText("ERREUR :'(");
		}
	}

	public String choixDossier(boolean type) {
		chooser = new JFileChooser();
		chooser.setApproveButtonText("Enregistrer");
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Enregistrer-sous");
		chooser.setAcceptAllFileFilterUsed(false);
		if (type == false) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//
			// disable the "All files" option.
			//
			//
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				System.out.println("getCurrentDirectory().getAb: "
						+ chooser.getCurrentDirectory().getAbsolutePath());
				System.out.println("getSelectedFile() : "
						+ chooser.getSelectedFile().getAbsolutePath());
				return chooser.getSelectedFile().getAbsolutePath() + "/";

			} else {
				return (".");
			}
		} else if (type == true) {
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				return chooser.getSelectedFile().getAbsolutePath();

			} else {
				return (".");
			}
		}
		return "";
	}

	public void increaseI() {
		i++;
	}

	public void decreaseI() {
		i--;
	}

}
