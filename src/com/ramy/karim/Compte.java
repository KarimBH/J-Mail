package com.ramy.karim;

public class Compte {
	private String adresse;
	private String motDePasse;
	private String type;
	private String serveur;
	private String port;
	private String serveurSmtp;
	private String portSmtp;

	public Compte() {
		adresse = new String();
		motDePasse = new String();
		type = new String();
		serveur = new String();
		port = new String();
		serveurSmtp = new String();
		portSmtp = new String();
	}

	public static void setParam(final Profil profil, final int k, String type,
			String server, String port, String smtpServer, String smtpPort) {
		profil.comptes[k].setType(type);
		profil.comptes[k].setServeur(server);
		profil.comptes[k].setPort(port);
		profil.comptes[k].setServeurSmtp(smtpServer);
		profil.comptes[k].setPortSmtp(smtpPort);
	}

	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServeur() {
		return serveur;
	}

	public void setServeur(String serveur) {
		this.serveur = serveur;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getServeurSmtp() {
		return serveurSmtp;
	}

	public void setServeurSmtp(String serveurSmtp) {
		this.serveurSmtp = serveurSmtp;
	}

	public String getPortSmtp() {
		return portSmtp;
	}

	public void setPortSmtp(String portSmtp) {
		this.portSmtp = portSmtp;
	}

}
