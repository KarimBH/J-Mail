package com.ramy.karim;

public class Profil {
	private String name;
	private String hashedPassword;
	Compte[] comptes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	public void setComptes(Compte[] comptes) {
		this.comptes = comptes;
	}


}
