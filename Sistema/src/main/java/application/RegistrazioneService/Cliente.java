package application.RegistrazioneService;

import java.util.ArrayList;

import application.GestioneOrdiniService.Ordine;

/*
 * Questa classe detiene le informazioni personali dell'utente
 * come cliente del negozio.
 * */

public class Cliente {
	
	public enum Sesso{
		F,
		M
	}
	
	private String email;
	private String nome;
	private String cognome;
	private Sesso sex;
	private String telefono;
	private ArrayList<Indirizzo> indirizzi;
	private ArrayList<Ordine> ordini;
	
	public static boolean checkValidate(String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) {
		
		String emailPattern = "/^\\S+@\\S+\\.\\S+$/";
		String nomeCognomePattern = "^[A-Za-z\s]+$";
		String telefonoPattern = "^([0-9]{3}-[0-9]{3}-[0-9]{4})$";
		
		if(!email.matches(emailPattern) || !nome.matches(nomeCognomePattern) || !cognome.matches(nomeCognomePattern)
				|| !telefono.matches(telefonoPattern))
			return false;

		if(!Indirizzo.checkValidate(indirizzo))
			return false;
		return true;
	}
	
	public static boolean checkValidateEmail(String email) {
		String emailPattern = "/^\\S+@\\S+\\.\\S+$/";
		return email.matches(emailPattern);
	}
	
	public static boolean checkValidateTelefono(String telefono) {
		String telefonoPattern = "^([0-9]{3}-[0-9]{3}-[0-9]{4})$";
		return telefono.matches(telefonoPattern);
	}
	
	public Cliente(String email, String nome, String cognome, Sesso sex, String telefono,
			Indirizzo indirizzo) {
		
		this.email = email;
		this.nome = nome;
		this.cognome = cognome;
		this.sex = sex;
		this.telefono = telefono;
		this.indirizzi = new ArrayList<>();
		indirizzi.add(indirizzo);
		this.ordini = new ArrayList<>();
	}

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public Sesso getSex() {
		return sex;
	}

	public String getSexAsString() {
		return sex.toString();
	}

	public void setSex(Sesso sex) {
		this.sex = sex;
	}

	public void setSex(String sex) {
	    if(sex.equalsIgnoreCase("F")) {
	        this.sex = Sesso.F;
	    } else if(sex.equalsIgnoreCase("M")) {
	        this.sex = Sesso.M;
	    } else {
	        throw new IllegalArgumentException("Il valore del sesso deve essere 'F' o 'M'.");
	    }
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public ArrayList<Indirizzo> getIndirizzi() {
		return indirizzi;
	}

	public void setIndirizzi(ArrayList<Indirizzo> indirizzi) {
		this.indirizzi = indirizzi;
	}

	public ArrayList<Ordine> getOrdini() {
		return ordini;
	}

	public void setOrdini(ArrayList<Ordine> ordini) {
		this.ordini = ordini;
	}

	@Override
	public String toString() {
		return "Cliente [email=" + email + ", nome=" + nome + ", cognome=" + cognome + ", sex=" + sex + ", telefono="
				+ telefono + ", indirizzi=" + toStringIndirizzi() + "]";
	}
	
	public String toStringIndirizzi() {
		String rubricaIndirizzi = "";
		for(Indirizzo d : this.indirizzi)
			rubricaIndirizzi = rubricaIndirizzi.concat("\n" + d.toString());
		
		return rubricaIndirizzi;
	}
	
	public String toStringNominativo() {
		return "Cliente [Nome = " + nome + ", Cognome = " + cognome + "]";
	}
}