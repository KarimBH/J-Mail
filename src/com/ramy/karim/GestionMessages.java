package com.ramy.karim;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import utilities.General;
import utilities.MessageUtilities;
import utilities.Security;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;

public class GestionMessages {

	// message selectionné
	static private JFrame mailsFrame;
	static private String[] infosProfil;

	static private JTabbedPane tabbedPane;
	static private JTabbedPane tabbedPaneInt;
	static private JProgressBar progressbar;

	static private ImageIcon iconGmail;
	static private ImageIcon iconHotmail;
	static private ImageIcon iconYahoo;
	static private ImageIcon parametresIcon;
	private ImageIcon profilIcon;
	private ImageIcon deconnexionIcon;
	private ImageIcon downloadImage;
	static private JLabel downloadImageLabel;
	static private JButton multiuseButton;
	static private JPanel boutonsPanel;
	static private JPanel boutonsPanelWEST;
	static private JPanel boutonsPanelEAST;
	static private Folder[] dossier;

	static private ArrayList<String> nomDossier;
	static private MessageTableModel modele;
	static private Message[] msg;

	static private JButton plusDeMsg;
	static private String[][] regex;

	public GestionMessages(final String[] infosProfil) {
		iconGmail = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/gmail3.jpg"));
		iconHotmail = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/hotmail.jpg"));
		iconYahoo = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/yahoo.jpg"));
		parametresIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/parametresIcon3.jpg"));
		profilIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/profilIcon.jpg"));
		deconnexionIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/deconnexionIcon.jpg"));
		downloadImage = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				"images/pataka5.gif"));
		progressbar = new JProgressBar();
		tabbedPane = new JTabbedPane();
		// imageLoading = new JPanel();
		downloadImageLabel = new JLabel(downloadImage);
		boutonsPanel = new JPanel();
		boutonsPanel.setBackground(Color.white);
		boutonsPanel.setLayout(new BorderLayout());

		// Bouton Profil: apparition d'un menu lors de l'appui
		final JPopupMenu popupProfil = new JPopupMenu();
		popupProfil.add(new JMenuItem(new AbstractAction("Deconnexion",
				deconnexionIcon) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showOptionDialog(mailsFrame,
						"Se déconnecter de se profil?", "Déconnexion",
						JOptionPane.YES_NO_OPTION, 0, deconnexionIcon, null, e) == JOptionPane.OK_OPTION) {
					mailsFrame.dispose();
					try {
						NativeInterface.close();
						General.restartApplication();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					// new Acceuil();
				}
			}
		}));
		popupProfil.add(new JMenuItem(new AbstractAction("Paramètres",
				parametresIcon) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				Object[] message = new Object[2];
				message[0] = "Mot de passe";
				message[1] = new JPasswordField();
				String option[] = { "ok", "Annuler" };

				int result = JOptionPane.showOptionDialog(null, message,
						"Insérer", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, parametresIcon,
						option, message[1]);
				if (result == 0) {
					String motdepassetape = new String(
							((JPasswordField) message[1]).getPassword());
					System.out.println(motdepassetape);

					if (Security.hash(motdepassetape).equals(infosProfil[1])) {
						new GestionProfil(infosProfil);
					} else {
						JOptionPane.showMessageDialog(mailsFrame,
								"Mot de passe incorrecte", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}));
		boutonsPanelEAST = new JPanel();
		multiuseButton = new JButton(infosProfil[0], profilIcon);
		multiuseButton.setBackground(Color.white);
		boutonsPanelEAST.add(multiuseButton);
		multiuseButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				popupProfil.show(e.getComponent(), e.getY(), e.getY());
			}
		});
		boutonsPanel.add(BorderLayout.EAST, boutonsPanelEAST);

		// Bouton nouveau(mail) (ajouté a boutonsPanelWest)
		boutonsPanelWEST = new JPanel();
		multiuseButton = new JButton("Nouveau");
		multiuseButton.setBackground(Color.white);
		boutonsPanelWEST.add(multiuseButton);
		multiuseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("nouveau");
				new NouveauMessage(infosProfil, null, null);
			}
		});

		boutonsPanel.add(BorderLayout.WEST, boutonsPanelWEST);

		progressbar.setIndeterminate(true);
		progressbar.setString(infosProfil[2].split(";")[0]);
		progressbar.setStringPainted(true);
		GestionMessages.infosProfil = infosProfil;
		mailsFrame = new JFrame();
		mailsFrame.getContentPane().add(BorderLayout.NORTH, boutonsPanel);
		boutonsPanel.setVisible(false);

		// mailsFrame.getContentPane().add(BorderLayout.EAST,downloadImageLabel);
		mailsFrame.getContentPane().add(tabbedPane);
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());

		southPanel.add(BorderLayout.NORTH, downloadImageLabel);
		southPanel.add(BorderLayout.SOUTH, progressbar);
		mailsFrame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		mailsFrame.getContentPane().setBackground(Color.white);
		mailsFrame.setSize(700, 400);
		mailsFrame.setLocationRelativeTo(null);
		mailsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mailsFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images/logo.jpg"));
		mailsFrame.setVisible(true);
		updateAffichage();

	}

	// -----------------------------------------------------------------------
	public static ArrayList<String> getFolderNames(Folder[] dossier) {
		ArrayList<String> nomDossier = new ArrayList<>();
		for (int i = 0; i < dossier.length; i++) {
			nomDossier.add(dossier[i].getName());
		}
		return nomDossier;
	}

	// -----------------------------------------------------------------------

	public static void updateAffichage() {
		SwingWorker<JTabbedPane, JTabbedPane> sw = new SwingWorker<JTabbedPane, JTabbedPane>() {
			protected JTabbedPane doInBackground() throws Exception {
				// JPanel tabPannel = new JPanel();

				final JTable[][] tableau = new JTable[infosProfil.length][10];
				regex = new String[infosProfil.length][10];
				Store connect;

				for (int i = 2; i < infosProfil.length; i++) {
					final int a = i;
					String[] infosCompte = infosProfil[i].split(";");
					/*
					 * infosCompte[0]: adresse compte infosCompte[1]: mot de
					 * passe crypte infosCompte[2]: type: imap ou pop3
					 * infosCompte[3]: serveur entrant infosCompte[4]: port
					 * infosCompte[5]: serveur smtp infosCompte[6]: port smtp
					 */
					tabbedPaneInt = new JTabbedPane(2);

					connect = MessageUtilities.connect(infosCompte[0],
							Security.decrypt(infosCompte[1]), infosCompte[2],
							infosCompte[3]);
					dossier = MessageUtilities.getAvailableFolders(connect);
					nomDossier = getFolderNames(dossier);
					for (int j = 0; j < nomDossier.size(); j++) {
						final int b = j;

						regex[i - 2][j] = new String("^([0-4])$");
						if (nomDossier.get(j).equals("[Gmail]")) {
							nomDossier.add("[Gmail]/Spam");
							nomDossier.add("[Gmail]/Messages envoyés");
							nomDossier.add("[Gmail]/Corbeille");
							continue;
						}

						// System.out.println(">> "+dossier[j].getName());
						msg = MessageUtilities.getMessages(connect,
								nomDossier.get(j));
						if (msg.length == 0)
							continue; // ne continuer que si le dossier n'est
										// vide
						System.out.println(nomDossier.get(j));
						modele = new MessageTableModel(msg);
						tableau[i][j] = new JTable(modele) {
							private static final long serialVersionUID = 1L;

							public String getToolTipText(MouseEvent e) {
								String tip = null;
								java.awt.Point p = e.getPoint();
								int rowIndex = rowAtPoint(p);
								int colIndex = columnAtPoint(p);
								int realColumnIndex = convertColumnIndexToModel(colIndex);

								try {
									if (realColumnIndex == 0) { // Source
										tip = "Email envoyée par:  "
												+ getValueAt(rowIndex, colIndex);
									} else if (realColumnIndex == 1) { // Veggie
																		// column
										tip = (String) getValueAt(rowIndex,
												colIndex);

									} else if (realColumnIndex == 2) { // Veggie
																		// column
										tip = "Recu le:"
												+ getValueAt(rowIndex, colIndex);

									}
								} catch (IndexOutOfBoundsException e1) {
								}
								return tip;
							}
						};
						tableau[i][j].addMouseListener(new MouseListener() {
							public void mouseClicked(MouseEvent e) {
								// get the coordinates of the mouse click
								Point p = e.getPoint();
								// get the row index that contains that
								// coordinate
								if (tableau[a][b].rowAtPoint(p) == -1) {
									System.out
											.println("clic hors d'une cellule");
									return;
								}

								if (e.getClickCount() == 2) {
									try {

										new AffichMessage(infosProfil,
												tableau[a][b]);
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							}

							@Override
							public void mouseEntered(MouseEvent e) {
								// TODO Auto-generated method stub
							}

							@Override
							public void mouseExited(MouseEvent arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void mousePressed(MouseEvent arg0) {
								// TODO Auto-generated method stub

							}

							@Override
							public void mouseReleased(MouseEvent arg0) {
								// TODO Auto-generated method stub

							}
						});

						tableau[i][j].setAutoCreateRowSorter(true);

						final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
								tableau[a][b].getModel());
						tableau[a][b].setRowSorter(sorter);
						sorter.setRowFilter(RowFilter.regexFilter(
								regex[i - 2][j], 3));
						JPanel pane = new JPanel();
						pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
						pane.add(new JScrollPane(tableau[i][j]));

						tableau[a][b].getColumnModel().getColumn(3)
								.setMinWidth(0);
						tableau[a][b].getColumnModel().getColumn(3)
								.setMaxWidth(0);
						tableau[a][b].getColumnModel().getColumn(3).setWidth(0);

						tableau[a][b].getColumnModel().getColumn(2)
								.setMaxWidth(150);
						tableau[a][b].getColumnModel().getColumn(2)
								.setMinWidth(115);
						tableau[a][b].getColumnModel().getColumn(2)
								.setWidth(115);
						plusDeMsg = new JButton("Plus de messages");
						plusDeMsg.setBackground(Color.white);

						plusDeMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
						pane.add(plusDeMsg);
						pane.setBackground(Color.WHITE);
						plusDeMsg.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								regex[a - 2][b] = General
										.replaceRegex(regex[a - 2][b]);
								sorter.setRowFilter(RowFilter.regexFilter(
										regex[a - 2][b], 3));
							}
						});

						tabbedPaneInt.addTab(nomDossier.get(j), pane);

					}
					setProgress(i);

					publish(tabbedPaneInt);
					System.out.println("\n Compte suivant -------");
				}

				return tabbedPane;
			}

			// ---------------------------------------------------------------
			public void done() {

				try {
					get().repaint();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Une erreur s'est produite lors de la connection.",
							"Erreur", JOptionPane.ERROR_MESSAGE);
				}

				progressbar.setVisible(false);
				progressbar.getParent().remove(progressbar);

			}

			// ---------------------------------------------------------------
			public void process(List<JTabbedPane> list) {
				for (JTabbedPane jTabbedPane : list) {

					downloadImageLabel.setVisible(false);
					boutonsPanel.setVisible(true);

					// mise en place de l'icone de chaque compte
					ImageIcon icon;
					if (infosProfil[getProgress()].split(";")[0]
							.contains("gmail")) {
						icon = iconGmail;
					} else if (infosProfil[getProgress()].split(";")[0]
							.contains("hotmail")) {
						icon = iconHotmail;
					} else if (infosProfil[getProgress()].split(";")[0]
							.contains("yahoo")) {
						icon = iconYahoo;
					} else
						icon = null;
					tabbedPane
							.addTab(infosProfil[getProgress()].split(";")[0]
									.split("@")[0], icon, jTabbedPane);
					try {
						progressbar.setString(infosProfil[getProgress() + 1]
								.split(";")[0]);
					} catch (ArrayIndexOutOfBoundsException e) {
						progressbar.setString(infosProfil[getProgress()]
								.split(";")[0]);
					}
				}
			}
		};
		sw.addPropertyChangeListener(new PropertyChangeListener() {
			// Méthode de l'interface
			public void propertyChange(PropertyChangeEvent event) {
				// On vérifie tout de même le nom de la propriété
				System.out.println(event.getNewValue());
			}
		});
		// On lance le SwingWorker
		sw.execute();
	}
}
