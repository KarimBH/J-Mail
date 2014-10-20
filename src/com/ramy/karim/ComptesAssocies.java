package com.ramy.karim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import utilities.General;
import utilities.GestionFichier;
import utilities.MessageUtilities;
import utilities.Security;

public class ComptesAssocies extends NouveauProfil {

	private JFrame comptesFrame;
	private JLabel texteLabel;
	private JButton retourButton;
	private JButton suivantButton;
	private JButton fermerButton;

	public ComptesAssocies(int spinnerValue, final Profil profil) {
		comptesFrame = new JFrame(
				"Comptes mails associés à votre nouveau profil");
		texteLabel = new JLabel("Entrer les paramètres de vos comptes:");

		retourButton = new JButton("Précédent");
		suivantButton = new JButton("Suivant");
		fermerButton = new JButton("X");
		nouveauProfilFrame.setVisible(false);
		if (spinnerValue == 0) {
			spinnerValue++;
		}
		this.spinnerValue = spinnerValue;

		/* on crée les objets Comptes au nbre de Spinner value */
		profil.comptes = new Compte[spinnerValue];
		comptesFrame.setLayout(null);

		texteLabel.setForeground(Color.GRAY);
		texteLabel.setBounds(35, 3, 260, 20);
		comptesFrame.getContentPane().add(texteLabel);

		fermerButton.setBounds(380, 0, 20, 12);
		fermerButton.setBorder(null);
		fermerButton.setBackground(Color.LIGHT_GRAY);
		fermerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);
			}
		});

		comptesFrame.getContentPane().add(fermerButton);

		final JTextField[] userName = new JTextField[spinnerValue];
		final JPasswordField[] passwords = new JPasswordField[spinnerValue];
		JLabel[] usernameLabel = new JLabel[spinnerValue];
		JLabel[] PassLabel = new JLabel[spinnerValue];
		JButton[] avancees = new JButton[spinnerValue];
		JSeparator[] separateur = new JSeparator[spinnerValue];

		for (int i = 0; i < spinnerValue; i++) {
			profil.comptes[i] = new Compte();
			JLabel compteLabel = new JLabel("Compte #" + (i + 1));
			compteLabel.setFont(new Font("arial", Font.PLAIN, 18));
			compteLabel.setBounds(50, 30 + i * 100, 100, 20);

			usernameLabel[i] = new JLabel("Adreese:");
			usernameLabel[i].setBounds(85, 60 + i * 100, 100, 20);
			userName[i] = new JTextField();
			userName[i].setBounds(150, 60 + i * 100, 120, 20);

			PassLabel[i] = new JLabel("Password:");
			PassLabel[i].setBounds(75, 90 + i * 100, 100, 20);
			passwords[i] = new JPasswordField();
			passwords[i].setBounds(150, 90 + i * 100, 120, 20);

			avancees[i] = new JButton("Avancée");
			avancees[i].setBounds(280, 90 + i * 100, 85, 20);
			final int j = i;

			avancees[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					new ParamAvancee(j, profil);

				}
			});

			separateur[i] = new JSeparator();
			separateur[i].setBounds(30, 125 + i * 100, 340, 10);

			comptesFrame.getContentPane().add(compteLabel);
			comptesFrame.getContentPane().add(usernameLabel[i]);
			comptesFrame.getContentPane().add(userName[i]);
			comptesFrame.getContentPane().add(PassLabel[i]);
			comptesFrame.getContentPane().add(passwords[i]);
			comptesFrame.getContentPane().add(avancees[i]);
			comptesFrame.getContentPane().add(separateur[i]);

		}

		JPanel boutons = new JPanel();
		boutons.setBounds(0, 30 + spinnerValue * 100, 400, 50);
		boutons.setBackground(new Color(77, 137, 190));

		boutons.add(retourButton);

		retourButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				profil.setName(null);
				profil.setHashedPassword(null);
				profil.setComptes(null);

				comptesFrame.setVisible(false);
				comptesFrame.dispose();
				nouveauProfilFrame.setVisible(true);
			}
		});

		boutons.add(suivantButton);
		final int sv = spinnerValue;

		class suivantAction implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				for (int k = 0; k < sv; k++) {
					if (getUserName(userName, k).trim().length() > 0
							&& getPassword(passwords, k).length() > 0) {/*
																		 * tous
																		 * les
																		 * champs
																		 * sont
																		 * remplies
																		 */
						if (MessageUtilities
								.verifMail(getUserName(userName, k))) { // adresses
							// mails

							// valides

							profil.comptes[k].setAdresse(getUserName(userName,
									k));
							profil.comptes[k].setMotDePasse(Security
									.encrypt(getPassword(passwords, k)));

							if (profil.comptes[k].getType().equals("")) { // pas
																			// de
																			// param
																			// avancee

								if (getUserName(userName, k).contains("gmail")) {
									Compte.setParam(profil, k, "imap",
											"imap.gmail.com", "993",
											"smtp.gmail.com", "465");
								} else if (getUserName(userName, k).contains(
										"hotmail")) {
									Compte.setParam(profil, k, "imap",
											"imap-mail.outlook.com", "993",
											"smtp-mail.outlook.com", "25");
								} else if (getUserName(userName, k).contains(
										"yahoo")) {
									Compte.setParam(profil, k, "imap",
											"imap.mail.yahoo.com", "993",
											"smtp.mail.yahoo.fr", "465");
								} else {
									// Compte.setParam(profil, k,pa.paraType(),
									// server,
									// port,smtpServer, smtpPort);

								}

							}
							switch (verifReceptionServer(
									profil.comptes[k].getType(),
									profil.comptes[k].getServeur(),
									profil.comptes[k].getAdresse(),
									getPassword(passwords, k))) {
							case 0:
								switch (verifSendServer(profil.comptes[k]
										.getServeurSmtp())) {
								case 0:
									try {
										if (k == 0) {
											GestionFichier.creerFichier(profil
													.getName());
											try {
												GestionFichier
														.ecrireLigne(
																profil.getName(),
																profil.getName()
																		+ "\n"
																		+ profil.getHashedPassword());
											} catch (IOException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}

										}
										GestionFichier
												.ecrireLigne(
														profil.getName(),
														"\n"
																+ /*
																 * Security.encrypt
																 */(profil.comptes[k]
																		.getAdresse()
																		+ ";"
																		+ profil.comptes[k]
																				.getMotDePasse()
																		+ ";"
																		+ profil.comptes[k]
																				.getType()
																		+ ";"
																		+ profil.comptes[k]
																				.getServeur()
																		+ ";"
																		+ profil.comptes[k]
																				.getPort()
																		+ ";"
																		+ profil.comptes[k]
																				.getServeurSmtp()
																		+ ";" + profil.comptes[k]
																			.getPortSmtp()));

										if (k == sv - 1) {
											comptesFrame.setVisible(false);
											comptesFrame.dispose();
											JOptionPane
													.showMessageDialog(
															comptesFrame,
															"Votre Profil a bien été enregistré");
											String[] infosProfil = (GestionFichier
													.lireFichier(profil
															.getName().trim()))
													.split(" ");
											comptesFrame.setVisible(false);
											comptesFrame.dispose();
											new GestionMessages(infosProfil);
										}

									} catch (IOException e) { /*
															 * impossible d
															 * ecrire les
															 * données
															 */
									}/* .... */
									break;
								case 1:// UnknownHostException
									texteLabel.setForeground(Color.red);
									texteLabel
											.setText("erreur lors du connexion au serveur SMTP réessayer ou adresse serveur SMTP incorrect");
									break;
								case 2:// IOException
									texteLabel.setForeground(Color.red);
									texteLabel
											.setText("erreur lors du connexion au serveur SMTP réessayer ou adresse serveur SMTP incorrect");
									break;
								}
								break;
							case 1: // NoSuchProviderException
								texteLabel.setForeground(Color.red);
								texteLabel
										.setText("erreur lors du connexion au serveur d'envoie réessayer ou adresse serveur incorrect");
								break;

							case 2: // AuthenticationFailedException
								texteLabel.setForeground(Color.red);
								texteLabel.setText("mot de passe incorrect");
								break;
							case 3: // MessagingException
								texteLabel.setForeground(Color.red);
								texteLabel
										.setText("erreur lors du connexion au serveur réessayer ou adresse serveur incorrect");
								break;
							default:
								System.exit(0);
								break;
							}
						} else {
							texteLabel.setForeground(Color.red);
							texteLabel
									.setText("Une ou plusieurs adresses mails incorrectes");
							return;
						}
					} else {
						texteLabel.setForeground(Color.red);
						texteLabel.setText("Un ou plusieurs champs vides");
						return;
					}
				}
				// fin du for
			}
		}
		suivantButton.addActionListener(new suivantAction());

		comptesFrame.getContentPane().add(boutons);

		comptesFrame.getContentPane().setBackground(new Color(246, 246, 246));
		comptesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		comptesFrame.setSize(400, spinnerValue * 100 + 68);
		comptesFrame.setLocationRelativeTo(null);
		comptesFrame.setUndecorated(true);
		comptesFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		comptesFrame.setVisible(true);

	}

	public int verifSendServer(String serveurSmtp) {
		Socket s = null;
		try {
			InetAddress address = InetAddress.getByName(serveurSmtp);
			s = new Socket(address, 25);
			return 0;
		} catch (UnknownHostException e) {
			return 1;
		} catch (IOException e) {
			return 2;
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException e) {
				}
		}
	}

	public int verifReceptionServer(String typeServeuReception,
			String serveurReception, String adresseEmail, String password) {
		Session session = null;
		Store store = null;
		if (typeServeuReception.equalsIgnoreCase("imap")) {
			Properties props = System.getProperties();
			props.setProperty("mail.store.protocol", "imaps");
			session = Session.getInstance(props, null);
		} else if (typeServeuReception.equalsIgnoreCase("pop")
				|| typeServeuReception.equalsIgnoreCase("pop3")) {
			Properties props = new Properties();
			props.put("mail.host", serveurReception);
			props.put("mail.store.protocol", "pop3s");
			props.put("mail.pop3s.auth", "true");
			props.put("mail.pop3s.port", "995");
			session = Session.getDefaultInstance(props, null);
		}
		try {
			if (typeServeuReception.equalsIgnoreCase("imap")) {
				store = session.getStore("imaps");
				store.connect(serveurReception, adresseEmail, password);
			} else if (typeServeuReception.equalsIgnoreCase("pop")
					|| typeServeuReception.equalsIgnoreCase("pop3")) {
				store = session.getStore();
				store.connect(adresseEmail, password);
			}
		} catch (NoSuchProviderException e1) {
			return 1;
		} catch (AuthenticationFailedException e2) {
			return 2;
		} catch (MessagingException e3) {
			return 3;
		} finally {
			if (store != null)
				try {
					store.close();
				} catch (MessagingException e) {
				}
		}
		return 0;
	}

	public String getPassword(JPasswordField[] password, final int i) {
		return new String(password[i].getPassword());
	}

	public String getUserName(JTextField[] UserName, final int i) {
		return UserName[i].getText();
	}

	class ParamAvancee {

		JFrame ParamAvanceeFrame;

		JButton annulerButton ;
		private JLabel texteLabel;
		JButton validerButton ;
		JPanel boutons;
		private JLabel typeLabel;
		private JComboBox<String> typeComboBox ;

		private JLabel serverLabel ;
		private JTextField serverTextField ;

		private JLabel portLabel ;
		private JTextField portTextField ;

		private JLabel smtpServerLabel ;
		private JTextField smtpServerTextField ;

		private JLabel smtpPortLabel ;
		private JTextField smtpPortTextField ;

		public ParamAvancee(final int i, final Profil profil) {
		 ParamAvanceeFrame = new JFrame();

		 annulerButton = new JButton("Annuler");
			
			 validerButton = new JButton("Valider");
			 boutons = new JPanel();

			 String[] TYPES = { "pop3", "imap" };
		typeLabel = new JLabel("Type: ");
			 typeComboBox = new JComboBox<String>(TYPES);

			 serverLabel = new JLabel("Serveur:");
			 serverTextField = new JTextField();

			portLabel = new JLabel("Port: ");
			 portTextField = new JTextField();

			 smtpServerLabel = new JLabel("Serveur SMTP:");
		 smtpServerTextField = new JTextField();

			 smtpPortLabel = new JLabel("Port SMTP:");
			 smtpPortTextField = new JTextField();

			comptesFrame.setEnabled(false);
			ParamAvanceeFrame.setLayout(null);

			texteLabel = new JLabel("Paramètres avancés du compte #" + (i + 1));
			texteLabel.setFont(new Font("arial", Font.BOLD, 13));
			texteLabel.setBounds(95, 3, 250, 20);
			ParamAvanceeFrame.getContentPane().add(texteLabel);

			typeLabel.setBounds(43, 30, 50, 20);
			ParamAvanceeFrame.getContentPane().add(typeLabel);

			typeComboBox.setBounds(80, 30, 60, 20);
			ParamAvanceeFrame.getContentPane().add(typeComboBox);

			portLabel.setBounds(270, 60, 90, 20);
			ParamAvanceeFrame.getContentPane().add(portLabel);

			portTextField.setBounds(310, 60, 60, 20);
			ParamAvanceeFrame.getContentPane().add(portTextField);

			serverLabel.setBounds(55, 60, 50, 20);
			ParamAvanceeFrame.getContentPane().add(serverLabel);

			serverTextField.setBounds(110, 60, 120, 20);
			ParamAvanceeFrame.getContentPane().add(serverTextField);

			smtpServerLabel.setBounds(20, 90, 90, 20);
			ParamAvanceeFrame.getContentPane().add(smtpServerLabel);

			smtpServerTextField.setBounds(110, 90, 120, 20);
			ParamAvanceeFrame.getContentPane().add(smtpServerTextField);

			smtpPortLabel.setBounds(240, 90, 90, 20);
			ParamAvanceeFrame.getContentPane().add(smtpPortLabel);

			smtpPortTextField.setBounds(310, 90, 60, 20);
			ParamAvanceeFrame.getContentPane().add(smtpPortTextField);

			annulerButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					comptesFrame.setEnabled(true);
					ParamAvanceeFrame.setVisible(false);
					ParamAvanceeFrame.dispose();

				}
			});
			validerButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if (getServer().trim().length() > 0
							&& getSmtpServer().trim().length() > 0
							&& getPort().trim().length() > 0
							&& getSmtpPort().trim().length() > 0) {
						Compte.setParam(profil, i, getTypes(), getServer(),
								getPort(), getSmtpServer(), getSmtpPort());

						ParamAvanceeFrame.setVisible(false);
						comptesFrame.setVisible(true);
						comptesFrame.setEnabled(true);

					} else {
						texteLabel.setForeground(Color.red);
						texteLabel
								.setText("Un ou plusieurs paramètres manquants");
					}

				}
			});

			boutons.setBounds(0, 120, 400, 50);
			boutons.setBackground(General.VERT);

			boutons.add(annulerButton);
			boutons.add(validerButton);
			ParamAvanceeFrame.getContentPane().add(boutons);

			ParamAvanceeFrame.getContentPane().setBackground(General.VERT);

			ParamAvanceeFrame.setUndecorated(true);
			ParamAvanceeFrame.setAlwaysOnTop(true);

			ParamAvanceeFrame.setSize(400, 168);

			ParamAvanceeFrame.setLocation(comptesFrame.getLocation().x + 400,
					comptesFrame.getLocation().y + i * 100);

			ParamAvanceeFrame.setVisible(true);
		}

		public String getTypes() {
			return (String) typeComboBox.getSelectedItem();
		}

		public String getServer() {
			return serverTextField.getText();
		}

		public String getSmtpServer() {
			return smtpServerTextField.getText();
		}

		public String getPort() {
			return portTextField.getText();
		}

		public String getSmtpPort() {
			return smtpPortTextField.getText();
		}

	}

}