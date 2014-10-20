package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GestionFichier {
	public static Boolean creerFichier(String nomFichier) {

		try {
			java.io.File fichier = new java.io.File(nomFichier + ".prof");

			fichier.createNewFile(); // Cette fonction doit être appelée au sein
										// d'un bloc TRY
			return true;
		} catch (IOException e) {
			System.out.println("Impossible de créer le fichier");
			return false;
		}

	}

	public static String lireFichier(String nomFichier) {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(nomFichier + ".prof"));
			StringBuilder line = new StringBuilder();
			// On boucle sur chaque champ detecté
			while (scanner.hasNextLine()) {
				line.append(scanner.nextLine() + " ");

				// faites ici votre traitement

			}

			scanner.close();
			return line.toString();
		} catch (FileNotFoundException e) {
			return "fichier inexistant";
		}
	}



	public static String ecrireLigne(String nomFichier, String aEcrire)
			throws IOException {

		PrintWriter pWriter = new PrintWriter(new FileWriter(nomFichier
				+ ".prof", true));
		pWriter.print(aEcrire);
		pWriter.close();

		return null;
	}
	public static void supprimerFichier (String path){
		try{
			 
    		File file = new File(path);
 
    		if(file.delete()){
    			//fichier supprimer
    		}else{
    			System.out.println("Delete operation is failed.");
    			//fichier non supprimé
    		}
 
    	}catch(Exception e){
 
    		e.printStackTrace();
 
    	}
		
	}
	
}