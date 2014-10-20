package utilities;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.apache.commons.codec.net.QCodec;

public class General {
	public final static Color BLEU = new Color(77, 137, 190);
	public final static Color BLEU1 = new Color(119, 181, 245);
	public final static Color VERT = new Color(190, 245, 116);
	public final static Color CAROTTE = new Color(244, 102, 27);

	static private ImageIcon iconOk = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage("images/ok.jpg"));
	static private ImageIcon iconX = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage("images/x.jpg"));

	public static final String SUN_JAVA_COMMAND = "sun.java.command";



	public static void setInterfaceColors() {
		UIManager.put("Viewport.background", Color.white);
		UIManager.put("Table.selectionBackground", General.VERT);
		UIManager.put("Table.gridColor", Color.LIGHT_GRAY);
		UIManager.put("TableHeader.background", General.BLEU1);
		UIManager.put("TableHeader.cellBorder", new Insets(0, 0, 0, 0));
		UIManager.put("Table.focusCellHighlightBorder", new Insets(0, 0, 0, 0));
		UIManager.put("ProgressBar.border", new Insets(0, 0, 0, 0));
		UIManager.put("ProgressBar.foreground", General.BLEU);
		// UIManager.put("ProgressBar.background", Color.white);
		UIManager.put("ProgressBar.selectionForeground", Color.white);
		UIManager.put("ProgressBar.selectionBackground", General.BLEU);
		// UIManager.put("TabbedPane.background", Color.white); //les tabs non
		// sélectionnés
		UIManager.put("TabbedPane.borderHightlightColor", Color.LIGHT_GRAY); // contour
																				// exterieur
																				// des
																				// tabs
		UIManager.put("TabbedPane.contentAreaColor", new Color(250, 250, 250)); // <<<<<<<<<<<
		UIManager.put("TabbedPane.darkShadow", Color.LIGHT_GRAY); // contour
																	// interieur
		UIManager.put("TabbedPane.focus", Color.LIGHT_GRAY); // contour tab
																// séléctionné
		// UIManager.put("TabbedPane.foreground", Color.GRAY); //Couleur texte
		// dans tabs
		// UIManager.put("TabbedPane.highlight", Color.black); //????
		// UIManager.put("TabbedPane.light", Color.LIGHT_GRAY); //contour tab
		// non selectionne (tab exterieur seulement)
		UIManager.put("TabbedPane.selected", new Color(215, 215, 215)); // couleur
																		// tab
																		// selectionne
		// UIManager.put("TabbedPane.selectedForeground", Color.black);
		// //couleur texte tab selectionne
		// UIManager.put("TabbedPane.selectHighlight", Color.LIGHT_GRAY);
		// //conteur exterieur superieur tab
		// UIManager.put("TabbedPane.shadow", Color.white); //e
		// UIManager.put("TabbedPane.tabAreaBackground", Color.black);
		UIManager.put("TabbedPane.unselectedBackground", Color.white); // couleur
																		// tab
																		// non
																		// selectionne
		// UIManager.put("TabbedPane.unselectedTabBackground", Color.black); //e
		// UIManager.put("TabbedPane.unselectedTabForeground", Color.blue);
		// UIManager.put("TabbedPane.unselectedTabHighlight", Color.blue);
		// UIManager.put("TabbedPane.unselectedTabShadow", Color.blue);
		UIManager.put("OptionPane.Background", Color.white);
		UIManager.put("OptionPane.Border", Color.white);

		UIManager.put("OptionPane.background", Color.white); 
		UIManager.put("OptionPane.informationIcon", iconOk); 
		UIManager.put("OptionPane.errorIcon", iconX); 
		UIManager.put("OptionPane.questionIcon", iconX); 
		UIManager.put("Panel.background", Color.white);
		//UIManager.put("Button.background", Color.white);

	}

	// Permet de rédemarrer l'application
	public static void restartApplication() throws IOException {
		try {
			// java binary
			String java = System.getProperty("java.home") + "/bin/java";
			// vm arguments
			List<String> vmArguments = ManagementFactory.getRuntimeMXBean()
					.getInputArguments();
			StringBuffer vmArgsOneLine = new StringBuffer();
			for (String arg : vmArguments) {
				// if it's the agent argument : we ignore it otherwise the
				// address of the old application and the new one will be in
				// conflict
				if (!arg.contains("-agentlib")) {
					vmArgsOneLine.append(arg);
					vmArgsOneLine.append(" ");
				}
			}
			// init the command to execute, add the vm args
			final StringBuffer cmd = new StringBuffer("\"" + java + "\" "
					+ vmArgsOneLine);

			// program main and program arguments
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(
					" ");
			// program main is a jar
			if (mainCommand[0].endsWith(".jar")) {
				// if it's a jar, add -jar mainJar
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			} else {
				// else it's a .class, add the classpath and mainClass
				cmd.append("-cp \"" + System.getProperty("java.class.path")
						+ "\" " + mainCommand[0]);
			}
			// finally add program arguments
			for (int i = 1; i < mainCommand.length; i++) {
				cmd.append(" ");
				cmd.append(mainCommand[i]);
			}
			// execute the command in a shutdown hook, to be sure that all the
			// resources have been disposed before restarting the application
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(cmd.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			System.exit(0);
		} catch (Exception e) {
			// something went wrong
			throw new IOException(
					"Error while trying to restart the application", e);
		}
	}

	public static String decodeISO(String toDecode) {
		try {
			if (toDecode.contains("ISO-8859") || toDecode.contains("utf")) {
				QCodec q = new QCodec();
				return q.decode(toDecode);
			}
		} catch (Exception e) {
			System.out.println("erreur");
		}
		return toDecode;
	}

	public static String extractBodyFromHtml(String toExtractFrom) {

		return toExtractFrom.toString().replaceAll(
				"([\\p{ASCII} ]*<body.*>)|(</body>[\\p{ASCII} ]*)", "");
	}

	// Pour l'affichage de la table contenant les messages, les expressions
	// régulières servant à filtrer la table selon une colonne d'index qui n'est
	// pas visible pour l'utilisateur
	public static String replaceRegex(String oldRegex) {
		String newRegex = oldRegex.replace("[0-4]", "[0-9]");
		if (newRegex.equals(oldRegex)) {
			int n = oldRegex.split("\\|").length;
			return newRegex.replace(")$", "|" + Math.max(1, n) + "[0-4])$");
		}
		return newRegex;

	}
}
