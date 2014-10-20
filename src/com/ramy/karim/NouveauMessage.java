package com.ramy.karim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import utilities.General;
import utilities.MessageUtilities;

public class NouveauMessage extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ImageIcon iconPJ;
	private JComboBox<String> choixCompte;

	private JTextField aTextField;
	private JTextField ccTextField;
	private JTextField cciTextField;
	private JTextField objetTextField;

	private JButton multiUseButton;
	private JButton ccCciButton;
	private JPanel multiUsePanel;
	private JPanel ccPanel;
	private JPanel cciPanel;
	private JPanel southPanel;

	private JEditorPane contenu;
	private JLabel piecesJointes;

	private JPanel fenetre;

	private String[] adressesCompte;

	private ArrayList<File> piecesJointesPaths;

	private int maxSize = 0;

	String destinataire;
	String compteParDefault = null;

	public NouveauMessage(final String[] infosProfil,
			final Message messageToReply, final Message messageToTransfer) {

		UIManager.put("ComboBox.selectionBackground", Color.white);
	iconPJ = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage("images/attachmentIcon.jpg"));
		piecesJointesPaths = new ArrayList<File>();
		adressesCompte = new String[infosProfil.length - 2];

		fenetre = new JPanel();
		fenetre.setLayout(new BoxLayout(fenetre, BoxLayout.Y_AXIS));

		multiUsePanel = new JPanel();
		multiUsePanel.setBackground(Color.white);
		multiUsePanel.setBorder(BorderFactory.createTitledBorder("Compte:"));

		// Tableau contenant les adresses possibles de l'envoyeur
		for (int i = 2; i < infosProfil.length; i++) {
			adressesCompte[i - 2] = (infosProfil[i].split(";"))[0];
		}

		if (messageToReply == null && messageToTransfer == null) {
			// nouveau message: on a le choix du compte depuis le quel le
			// message
			// va être envoyé
			choixCompte = new JComboBox<String>(adressesCompte);
			choixCompte.setPreferredSize(new Dimension(250, 20));
			multiUsePanel.add(choixCompte);
		} else if (messageToReply != null) {
			// on va répondre à un mail, pas de choix de compte, ni du
			// destinataire
			try {
				destinataire = General.decodeISO(messageToReply.getFrom()[0]
						.toString());
				if (!MessageUtilities.verifMail(destinataire)) {

					destinataire = (destinataire.split("<")[1]).split(">")[0]
							.toString();
					// destinataire = "ff";
				}
				// le compte par défaut est celui qui a recu le msg auquel on va
				// répondre
				try {

					compteParDefault = messageToReply
							.getRecipients(Message.RecipientType.TO)[0]
							.toString().split("<")[1].split(">")[0];
				} catch (ArrayIndexOutOfBoundsException e1) {
					compteParDefault = messageToReply
							.getRecipients(Message.RecipientType.TO)[0]
							.toString();
				}
				multiUsePanel.add(new JLabel(compteParDefault,
						SwingConstants.CENTER));
			} catch (MessagingException e2) {
				// messageToReply = null;
			}
		} else if (messageToTransfer != null) {

			// on va transférer un message recu, pas de choix de compte
			try {
				try {
					compteParDefault = messageToTransfer
							.getRecipients(Message.RecipientType.TO)[0]
							.toString().split("<")[1].split(">")[0];
				} catch (ArrayIndexOutOfBoundsException e1) {
					compteParDefault = messageToTransfer
							.getRecipients(Message.RecipientType.TO)[0]
							.toString();
				}
				multiUsePanel.add(new JLabel(compteParDefault,
						SwingConstants.CENTER));
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		fenetre.add(multiUsePanel);

		multiUsePanel = new JPanel();

		multiUsePanel.add(new JLabel("A:        "));
		aTextField = new JTextField(destinataire);
		aTextField.setColumns(28);
		multiUsePanel.add(aTextField);
		ccCciButton = new JButton("Cc / Cci");
		ccCciButton.setBackground(Color.white);
		multiUsePanel.add(ccCciButton);

		fenetre.add(multiUsePanel);

		ccPanel = new JPanel();
		ccPanel.setBackground(new Color(237,237,237));
		ccPanel.add(new JLabel("Cc:     "));
		ccTextField = new JTextField();
		ccTextField.setColumns(35);
		ccPanel.add(ccTextField);
		fenetre.add(ccPanel);
		ccPanel.setVisible(false);

		cciPanel = new JPanel();
		cciPanel.setBackground(new Color(237,237,237));
		cciPanel.add(new JLabel("Cci:    "));
		cciTextField = new JTextField();
		cciTextField.setColumns(35);
		cciPanel.add(cciTextField);
		fenetre.add(cciPanel);
		cciPanel.setVisible(false);

		ccCciButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (ccPanel.isVisible()) {
					ccCciButton.setBackground(Color.white);
					ccPanel.setVisible(false);
					cciPanel.setVisible(false);
					
				} else {
					ccCciButton.setBackground(new Color(237,237,237));
					ccPanel.setVisible(true);
					cciPanel.setVisible(true);
				}
			}
		});

		multiUsePanel = new JPanel();
		multiUsePanel.add(new JLabel("Objet: "));
		objetTextField = new JTextField();
		objetTextField.setColumns(35);
		multiUsePanel.add(objetTextField);
		fenetre.add(multiUsePanel);
		getContentPane().add(fenetre, BorderLayout.NORTH);

		multiUsePanel = new JPanel();
		multiUsePanel.setBackground(Color.white);
		multiUsePanel.setBorder(BorderFactory.createTitledBorder("Contenu:"));
		contenu = new JEditorPane();

		if (messageToTransfer != null) {
			try {
				String ls = System.lineSeparator();
				contenu.setText(" ---------Message transféré---------"
						+ ls
						+ "De: "
						+ (General.decodeISO(messageToTransfer.getFrom()[0]
								.toString())).split(" <")[0]
						+ ls
						+ "Date: "
						+ messageToTransfer.getSentDate()
						+ ls
						+ "Objet: "
						+ messageToTransfer.getSubject()
						+ ls
						+ "A: "
						+ (messageToTransfer
								.getRecipients(Message.RecipientType.TO)[0]
								.toString()).split(" <")[0]
						+ ls
						+ ls
						+ General.extractBodyFromHtml(MessageUtilities
								.getAllContent(messageToTransfer).get(0)
								.toString()));
				System.out.println(General.extractBodyFromHtml(MessageUtilities
						.getAllContent(messageToTransfer).get(0).toString()));
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("Erreur, transfert impossible.");
			}

		}

		contenu.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
				Color.LIGHT_GRAY));
		multiUsePanel.setLayout(new BorderLayout());
		// contenu.setBorder(BorderFactory.);
		contenu.setBackground(new Color(245, 245, 245));
		contenu.setSize(getWidth(), getHeight());
		multiUsePanel.add(contenu);
		getContentPane().add(new JScrollPane(multiUsePanel),
				BorderLayout.CENTER);

		southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		piecesJointes = new JLabel("Déposer ici vos fichiers ",
				SwingConstants.CENTER);
		piecesJointes.setPreferredSize(new Dimension(getWidth(), 80));
		southPanel.add(piecesJointes, BorderLayout.NORTH);
		piecesJointes.setVisible(false);
		PieceJointeDragDropListener pieceJointeDragDropListener = new PieceJointeDragDropListener();
		new DropTarget(piecesJointes, pieceJointeDragDropListener);

		fenetre = new JPanel();
		fenetre.setBackground(General.BLEU);
		multiUseButton = new JButton("Annuler");
		multiUseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(getChoixCompte());
				setVisible(false);
				dispose();
			}
		});
		fenetre.add(multiUseButton);

		multiUseButton = new JButton(iconPJ);
		multiUseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!piecesJointes.isVisible()) {
					piecesJointes.setVisible(true);
				} else {
					piecesJointes.setVisible(false);
				}
			}
		});
		fenetre.add(multiUseButton);

		multiUseButton = new JButton("Envoi");
		multiUseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean envoye = false;
				try {

					if (getObjet().trim().length() > 0
							|| getDestinataires().trim().length() > 0) {
						if (messageToReply != null) {
							envoye = MessageUtilities.envoiMail(
									(infosProfil[getChoixCompte()].split(";")),
									destinataire, getCC(), getCCi(),
									getObjet(), getContent().toString(),
									piecesJointesPaths);

							System.out.println(getContent());
						} else if (messageToTransfer != null) {
							envoye = MessageUtilities.envoiMail(
									(infosProfil[getChoixCompte()].split(";")),
									getDestinataires(), getCC(), getCCi(),
									getObjet(), getContent().toString(),
									piecesJointesPaths);
							System.out.println(getCC());
						} else {
				
							envoye = MessageUtilities.envoiMail(
									(infosProfil[getChoixCompte()].split(";")),
									getDestinataires(), getCC(), getCCi(),
									getObjet(), getContent(),
									piecesJointesPaths);
							System.out.println(getCC());
						}
					} else {
					}
					
				} catch (MessagingException e1) {
					e1.printStackTrace();
					envoye = false;
				}
				if (envoye) {
					JOptionPane.showMessageDialog(null, "Message envoyé", "",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "Envoi Impossible :'(",
							"", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		fenetre.add(multiUseButton);
		southPanel.add(fenetre, BorderLayout.SOUTH);
		getContentPane().add(southPanel, BorderLayout.SOUTH);

		getContentPane().setBackground(Color.white);
		setSize(500, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		setTitle("Nouveau Message");
		setVisible(true);
	}

	public String getDestinataires() {
		return aTextField.getText();
	}

	public String getCC() {
		return ccTextField.getText();
	}

	public String getCCi() {
		return cciTextField.getText();
	}

	public String getObjet() {
		return objetTextField.getText();
	}

	public int getChoixCompte() {
		try {
			return Arrays.asList(adressesCompte).indexOf(
					choixCompte.getSelectedItem().toString()) + 2;
		} catch (NullPointerException e) {
			// cas de réponse ou de transfert à un email. le choix du compte ne
			// se fait pas
			return Arrays.asList(adressesCompte).indexOf(
					compteParDefault.toString()) + 2;
		}
	}

	public String getContent() {
		return contenu.getText();
	}

	class PieceJointeDragDropListener implements DropTargetListener {

		@Override
		public void drop(DropTargetDropEvent event) {

			// Accept copy drops
			event.acceptDrop(DnDConstants.ACTION_COPY);

			// Get the transfer which can provide the dropped item data
			Transferable transferable = event.getTransferable();

			// Get the data formats of the dropped item
			DataFlavor[] flavors = transferable.getTransferDataFlavors();

			// Loop through the flavors
			for (DataFlavor flavor : flavors) {

				try {

					// If the drop items are files
					if (flavor.isFlavorJavaFileListType()) {

						// Get all of the dropped files
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) transferable
								.getTransferData(flavor);

						// Loop them through
						for (File file : files) {
							// 25Mo taille maximale pieces jointes
							if (maxSize + file.length() / (1024 * 1024) < 25) {
								// Print out the file path
								System.out.println("File path is '"
										+ file.getPath() + "'.");
								piecesJointesPaths.add(file);
								piecesJointes.setText("<html>"
										+ piecesJointes.getText() + "<br/>"
										+ file.getName() + "(" + file.length()
										/ 1024 + "," + file.length() % 1024
										+ "ko)<html/>");
								maxSize += file.length() / (1024 * 1024);
							} else {
								piecesJointes
										.setText("<html>"
												+ piecesJointes.getText()
												+ "<br/>Taille maximale dépassée.<html/>");
							}
						}

					}

				} catch (Exception e) {

					// Print out the error stack
					e.printStackTrace();

				}
			}

			// Inform that the drop is complete
			event.dropComplete(true);

		}

		@Override
		public void dragEnter(DropTargetDragEvent event) {
		}

		@Override
		public void dragExit(DropTargetEvent event) {
		}

		@Override
		public void dragOver(DropTargetDragEvent event) {
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent event) {
		}

	}
}
