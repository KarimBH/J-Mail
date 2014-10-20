package com.ramy.karim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import utilities.General;
import utilities.GestionFichier;
import utilities.Security;

public class Acceuil extends JPanel {

	private static final long serialVersionUID = 1L;
	static Fenetre acceuil;
	private JButton fermerButton;
	private JLabel nomDeProfilLabel;
	private JTextField usernameTextField;
	private JLabel mdpLabel;
	private JPasswordField mdpPasswordField;
	private JButton connectButton;
	private JButton nouveauProfilButton;

	public static void main(String[] args) {
		General.setInterfaceColors();
	
	
		acceuil = new Fenetre();
	}

	private Image img;

	public Acceuil(Image img) {
		fermerButton = new JButton("Fermer");
		connectButton = new JButton("Connexion");
		nouveauProfilButton = new JButton("Nouveau Profil");
		nomDeProfilLabel = new JLabel("Nom de profil:");
		mdpLabel = new JLabel("Mot de passe:");
		usernameTextField = new JTextField();
		mdpPasswordField = new JPasswordField();
		
	
		this.img = img;
		this.setLayout(null); // positionnement absolu des composants

		// Boutton fermerButton
		fermerButton.setBounds(410, 0, 75, 20); // posX, posY, largeur, hauteur
		fermerButton.setBorder(null);
		fermerButton.setBackground(Color.LIGHT_GRAY);
		fermerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);
			}
		});
		add(fermerButton);

		// texte Nom de profil
		nomDeProfilLabel.setBounds(180, 110, 200, 20);
		add(nomDeProfilLabel);

		// champ Nom de profil
		usernameTextField.setBounds(280, 110, 125, 20);
		add(usernameTextField);

		// texte mot de passe
		mdpLabel.setBounds(180, 140, 200, 20);
		add(mdpLabel);

		// champ mot de passe
		mdpPasswordField.setBounds(280, 140, 125, 20);
		add(mdpPasswordField);

		// Boutton connectButtonion
		connectButton.addActionListener(new Connexion());
		connectButton.setBounds(340, 170, 100, 20); // posX, posY, largeur,
													// hauteur
		// connectButton.setEnabled(false);
		add(connectButton);

		// Boutton Nouveau profil
		nouveauProfilButton.setBounds(190, 230, 120, 20); // posX, posY,
		nouveauProfilButton.setBorder(null);
		nouveauProfilButton.setBackground(Color.white);

		nouveauProfilButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				acceuil.setVisible(false);
				acceuil.dispose();
				new NouveauProfil();
			}
		});
		add(nouveauProfilButton);

	}

	// image d'arrière plan
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	class Connexion implements ActionListener { // Action boutton connexion

		@Override
		public void actionPerformed(ActionEvent e) {
			if (getUsername().trim().length() > 0
					&& getPassword().trim().length() > 0) {
				if (!GestionFichier.lireFichier(getUsername().trim()).equals(
						"fichier inexistant")) {
					// String infosProfil =
					// GestionFichier.lireFichier(getUsername().trim());
					String[] infosProfil = (GestionFichier
							.lireFichier(getUsername().trim())).split(" ");
					if (Security.hash(getPassword().trim()).equals(
							infosProfil[1])) {

						acceuil.setVisible(false);
						acceuil.dispose();

						new GestionMessages(infosProfil);

					} else { // profil existant mm mot de passe incorrect
						Toolkit.getDefaultToolkit().beep();
						mdpPasswordField.setText("");
						mdpPasswordField
								.setBackground(new Color(246, 246, 246));
					}
				} else {// profil inexistant
					Toolkit.getDefaultToolkit().beep();
					usernameTextField.setBackground(Color.PINK);
				}
			} else { // tous les champs ne sont pas remplies
				Toolkit.getDefaultToolkit().beep();
				usernameTextField.setBackground(General.VERT);
				mdpPasswordField.setBackground(General.VERT);
			}
		}
		

	}

	public String getUsername() {
		return usernameTextField.getText();
	}

	public String getPassword() {
		return new String(mdpPasswordField.getPassword());
	}

} // fin classe Acceuil

class Fenetre extends JFrame {

	private static final long serialVersionUID = 1L;

	public Fenetre() {
		this.setContentPane(new Acceuil(new ImageIcon("images/fond3.jpg")
				.getImage()));
		this.setSize(new Dimension(484, 263));
		this.setBackground(Color.GRAY);
		this.setUndecorated(true);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		this.setLocationRelativeTo(null);

		// permet de faire apparaitre la fenetre avec un effet fondu
		float opacity = 0f;
		for (int i = 0; i <= 100; i++) {
			opacity += 0.01f;
			this.setOpacity(Math.min(1, opacity));
			this.setVisible(true);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {

			}

		}

	}

}