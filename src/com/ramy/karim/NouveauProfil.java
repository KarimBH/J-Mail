package com.ramy.karim;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import utilities.General;
import utilities.GestionFichier;
import utilities.Security;

public class NouveauProfil {
	JFrame nouveauProfilFrame;

	private JLabel texteLabel;

	protected JButton fermerButton;

	private JLabel nomDeProfilLabel;
	private JTextField nomDeProfilTextField;

	private JLabel motDePasseLabel;
	private JPasswordField motDePassePasswordField;

	private JLabel reMotDePasseLabel;
	private JPasswordField reMotDePassePasswordField;

	private JLabel nombreComptesLabel;

	private JSpinner nombreComptesSpinner;
	int spinnerValue;

	private JButton suivantButton;

	public NouveauProfil() {
		suivantButton = new JButton("Suivant");
		nouveauProfilFrame = new JFrame("Nouveau profil");
		texteLabel = new JLabel("Créer votre nouveau profil:");
		fermerButton = new JButton("X");
		nomDeProfilLabel = new JLabel("Nom de Profil");
		nomDeProfilTextField = new JTextField();
		motDePasseLabel = new JLabel("Mot de Passe");
		motDePassePasswordField = new JPasswordField();
		reMotDePasseLabel = new JLabel("Confirmer mot de paase");
		reMotDePassePasswordField = new JPasswordField();
		nombreComptesLabel = new JLabel("Nombre de comptes mails associciés:");

		nombreComptesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));

		nouveauProfilFrame.setLayout(null);

		texteLabel.setForeground(Color.GRAY);
		texteLabel.setBounds(40, 5, 230, 20);
		nouveauProfilFrame.getContentPane().add(texteLabel);

		fermerButton.setBounds(380, 0, 20, 12);
		fermerButton.setBorder(null);
		fermerButton.setBackground(Color.LIGHT_GRAY);
		fermerButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);
			}
		});

		nouveauProfilFrame.getContentPane().add(fermerButton);

		nomDeProfilLabel.setBounds(100, 40, 120, 20);
		nouveauProfilFrame.getContentPane().add(nomDeProfilLabel);

		nomDeProfilTextField.setBounds(190, 40, 120, 20);
		nouveauProfilFrame.getContentPane().add(nomDeProfilTextField);

		motDePasseLabel.setBounds(100, 70, 100, 20);
		nouveauProfilFrame.getContentPane().add(motDePasseLabel);

		motDePassePasswordField.setBounds(190, 70, 120, 20);
		nouveauProfilFrame.getContentPane().add(motDePassePasswordField);

		reMotDePasseLabel.setBounds(40, 100, 140, 20);
		nouveauProfilFrame.getContentPane().add(reMotDePasseLabel);

		reMotDePassePasswordField.setBounds(190, 100, 120, 20);
		nouveauProfilFrame.getContentPane().add(reMotDePassePasswordField);

		nombreComptesLabel.setBounds(40, 130, 220, 20);
		nouveauProfilFrame.getContentPane().add(nombreComptesLabel);

		nombreComptesSpinner.setBounds(265, 130, 40, 20);
		nombreComptesSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				spinnerValue = (int) (nombreComptesSpinner.getValue());

			}
		});
		nouveauProfilFrame.getContentPane().add(nombreComptesSpinner);
		suivantButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (getNomDeProfil().trim().length() > 0
						&& getPassword().length() > 0
						&& getRePassword().length() > 0) {

					if (getPassword().equals(getRePassword())) { /*
																 * if nom compte
																 * n'existe pas
																 * déja
																 */
						if (GestionFichier.lireFichier(getNomDeProfil().trim())
								.equals("fichier inexistant")) { /*
																 * un fichier
																 * portant le mm
																 * nom existe
																 */
							Profil profil = new Profil();
							profil.setName(getNomDeProfil());
							profil.setHashedPassword(Security
									.hash(getPassword()));
							nouveauProfilFrame.setVisible(false);
							new ComptesAssocies(spinnerValue, profil);
						} else {
							texteLabel.setForeground(Color.red);
							texteLabel.setText("Ce profil existe déja.");
						}

					} else {

						texteLabel.setForeground(Color.red);
						texteLabel.setText("Verifier le mot de passe");
						reMotDePassePasswordField.setText("");
					}
				} else {
					texteLabel.setForeground(Color.red);
					texteLabel.setText("Un ou plusieurs paramètres manquants");
				}
			}
		});

		JPanel panneauSuivantButton = new JPanel();
		panneauSuivantButton.setBackground(General.BLEU);
		panneauSuivantButton.setBounds(0, 165, 400, 45);
		panneauSuivantButton.add(suivantButton);
		nouveauProfilFrame.getContentPane().add(panneauSuivantButton);
		nouveauProfilFrame.getContentPane().setBackground(
				new Color(246, 246, 246));
		nouveauProfilFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		nouveauProfilFrame.setSize(400, 200);
		nouveauProfilFrame.setResizable(false);
		nouveauProfilFrame.setLocationRelativeTo(null);
		nouveauProfilFrame.setUndecorated(true);
		nouveauProfilFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		nouveauProfilFrame.setVisible(true);
	}

	public String getNomDeProfil() {
		return nomDeProfilTextField.getText();
	}

	public String getPassword() {
		return new String(motDePassePasswordField.getPassword());
	}

	public String getRePassword() {
		return new String(reMotDePassePasswordField.getPassword());
	}
}
