package com.ramy.karim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import utilities.General;
import utilities.GestionFichier;
import utilities.Security;

public class GestionProfil extends JFrame {

	private static final long serialVersionUID = 1L;
	private ImageIcon parametreImage ;
	private ImageIcon passwordIcon;
	private JLabel label;
	private JTextField nomProfil;
	private JButton button;
	private JPanel[] comptePanel;
	private JPanel panelExt;
	private JPanel nomProfilPanel;
	private final JPanel intNorthPanel;
	private JPanel passwordPanel;
	private String motDePasseModif;

	public GestionProfil(final String[] infosProfil) {
		
		 parametreImage = new ImageIcon(Toolkit
				.getDefaultToolkit().getImage("images/parametreImage1.jpg"));
		 passwordIcon = new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage("images/passwordIcon.jpg"));
	
		final JPanel panelExt2 = new JPanel();
		panelExt2.setLayout(new BorderLayout());
		final JPanel northPanel = new JPanel();

		intNorthPanel = new JPanel();
		intNorthPanel.setLayout(new BorderLayout());

		nomProfilPanel = new JPanel();
		label = new JLabel("Nom du profil:");
		nomProfil = new JTextField(infosProfil[0]);
		nomProfil.setEditable(false);
		nomProfil.setColumns(10);
		JButton modifNomProfil = new JButton("modifier");
		modifNomProfil.setBackground(Color.white);
		modifNomProfil.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				nomProfil.setEditable(true);
				nomProfil.getFocusListeners();
			}
		});
		nomProfilPanel.add(label);
		nomProfilPanel.add(nomProfil);
		nomProfilPanel.add(modifNomProfil);

		intNorthPanel.add(BorderLayout.NORTH, nomProfilPanel);

		passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));

		final JButton modifPassword = new JButton("Modifier mot de passe");
		modifPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
		modifPassword.setBorder(null);
		modifPassword.setBackground(Color.white);
		modifPassword.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] message = new Object[4];
				message[0] = "Nouveau mot de passe";
				message[1] = new JPasswordField();
				message[2] = "Vérification";
				message[3] = new JPasswordField();
				String option[] = { "Valider", "Annuler" };

				int result = JOptionPane.showOptionDialog(null, message,
						"Insérer", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, passwordIcon, option,
						message[1]);
				if (result == 0) {
					String motdepassetape = new String(
							((JPasswordField) message[1]).getPassword());
					String verifMotdepassetape = new String(
							((JPasswordField) message[3]).getPassword());

					if (motdepassetape.equals(verifMotdepassetape)) {
						motDePasseModif = new String(motdepassetape);
					} else {
						JOptionPane.showMessageDialog(null,
								"Verifier le mot de passe", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		passwordPanel.add(modifPassword);
		JSeparator js = new JSeparator(JSeparator.HORIZONTAL);
		passwordPanel.add(js);

		final JButton supprimerProfil = new JButton("Supprimer ce profil");
		supprimerProfil.setAlignmentX(Component.CENTER_ALIGNMENT);
		supprimerProfil.setBorder(null);
		supprimerProfil.setBackground(Color.white);
		supprimerProfil.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null,
						"Supprimer ce profil ?", "Confirmation",
						JOptionPane.OK_CANCEL_OPTION);
				if (n == 0) {
					GestionFichier.supprimerFichier(infosProfil[0] + ".prof");
					try {
						General.restartApplication();
					} catch (IOException e1) {
						// redémarrage impossible de l'application
						System.exit(0);
					}
				}
			}
		});

		passwordPanel.add(supprimerProfil);

		intNorthPanel.add(BorderLayout.CENTER, passwordPanel);

		northPanel.add(new JLabel(parametreImage));
		northPanel.add(intNorthPanel);

		getContentPane().add(BorderLayout.NORTH, northPanel);

		// Partie des modifications des différents comptes
		// int nombreComptes = infosProfil.length;
		comptePanel = new JPanel[infosProfil.length - 2];
		panelExt = new JPanel();
		//

		for (int i = 2; i < infosProfil.length; i++) {
			final int a = i;
			String[] infosCompte = infosProfil[i].split(";");
			/*
			 * infosCompte[0]: adresse compte infosCompte[1]: mot de passe
			 * crypte infosCompte[2]: type: imap ou pop3 infosCompte[3]: serveur
			 * entrant infosCompte[4]: port infosCompte[5]: serveur smtp
			 * infosCompte[6]: port smtp
			 */
			comptePanel[i - 2] = new JPanel();
			comptePanel[i - 2].setLayout(new BoxLayout(comptePanel[i - 2],
					BoxLayout.Y_AXIS));
			comptePanel[i - 2].setBorder(BorderFactory
					.createTitledBorder("Compte #" + (i - 1)));
			comptePanel[i - 2].setBackground(Color.white);

			label = new JLabel("<html><TABLE> <TR><TD>Adreese:</TD> <TD>"
					+ infosCompte[0] + "</TD></TR><TR><TD>MDP:</TD> <TD>"
					+ Security.decrypt(infosCompte[1])
					+ "</TD></TR> <TR><TD>Type:</TD> <TD>" + infosCompte[2]
					+ "</TD></TR><TR><TD>Serveur entrant:</TD> <TD>"
					+ infosCompte[3]
					+ "</TD></TR><TR><TD>Port entrant:</TD> <TD>"
					+ infosCompte[4]
					+ "</TD></TR><TR><TD>Serveur sortant:</TD> <TD>"
					+ infosCompte[5]
					+ "</TD></TR><TR><TD>Port sortant:</TD> <TD>"
					+ infosCompte[6] + "</TD></TR></TABLE></html> ");// addresse
			comptePanel[i - 2].add(label);
			button = new JButton("Supprimer");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int n = JOptionPane.showConfirmDialog(null,
							"Supprimer ce compte ?", "Confirmation",
							JOptionPane.OK_CANCEL_OPTION);
					if (n == 0) {
						comptePanel[a - 2].setVisible(false);
					}

				}
			});
			button.setBackground(Color.white);
			comptePanel[i - 2].add(button);

			panelExt.add(comptePanel[i - 2]);

		}

		getContentPane().add(BorderLayout.CENTER, new JScrollPane(panelExt));

		// Boutons "annuler" et "enregistrer"

		panelExt = new JPanel();
		button = new JButton("Annuler");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		panelExt.add(button);

		button = new JButton("Enregistrer");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean continuer = true;
				if (!nomProfil.getText().equals(infosProfil[0])) {

					// nom de Profil modifié

					if (!GestionFichier.lireFichier(nomProfil.getText()).equals(
							"fichier inexistant")) {
						// nom de profil déja utilisé
	
						continuer = false;
						JOptionPane.showMessageDialog(null,
								"Nouveau nom de profil déja utilisé", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					}}
					if (continuer) {
						try {
							File inputFile = new File(infosProfil[0] + ".prof");
							File tempFile = new File("myTempFile.txt");

							BufferedReader reader = new BufferedReader(new FileReader(inputFile));
							BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
							int numeroLigne = 0;
							while((reader.readLine()) != null){
								if (!(nomProfil.getText().equals(infosProfil[0])) && numeroLigne==0){
									writer.write(nomProfil.getText()+"\n");	
								}
								if (motDePasseModif != null && numeroLigne==1) {
									// mot de passe modifié
									writer.write(Security.hash(motDePasseModif)+"\n");	
								}
								if (numeroLigne > 1 && comptePanel[numeroLigne - 2].isVisible()){
									writer.write(infosProfil[numeroLigne]+"\n");	
								}
								numeroLigne++;
							}
							
							reader.close();
							writer.close();
							if (!nomProfil.getText().equals(infosProfil[0])){
								File nouveauNom = new File(nomProfil.getText()
										+ ".prof");
								tempFile.renameTo(nouveauNom);
								inputFile.delete();
							}
							else{
								File ancienNom = new File(infosProfil[0]
										+ ".prof");
								inputFile.delete();
								tempFile.renameTo(ancienNom);
						
							}
							JOptionPane.showMessageDialog(null, "Profil modifié", "",
									JOptionPane.INFORMATION_MESSAGE);
							General.restartApplication();
							
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
	

					}
				

			}
		});
		panelExt.add(button);

		getContentPane().add(BorderLayout.SOUTH, new JScrollPane(panelExt));
		getContentPane().add(BorderLayout.SOUTH, new JScrollPane(panelExt));

		getContentPane().setBackground(Color.white);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(500, 400);
		setLocationRelativeTo(null);
		setTitle("Modifier votre profil");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		setVisible(true);

	}
}
