package application.RegistrazioneService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Utente {
	
	/*
	 * Questa classe detiene le informazioni relative all'utente:
	 * username, password, ruoli associati e profilo personale.
	 * */
	
	private String username;
	private String password;
	private Cliente profile;
	private ArrayList<Ruolo> ruoli;
	
	
	public static boolean checkValidate(String username, String password) {
		
		String usernamePattern = "\\d+(\\.\\d+)?";
		String passwordOnlyNumbersPattern = "\\d+(\\.\\d+)?";
		String passwordOnlyCharsPattern = "\\D+";
		
		if(username.length() < 5 || username.isBlank() || username.matches(usernamePattern))
			return false;
		else if(password.length() < 5 || password.isBlank() || password.matches(passwordOnlyNumbersPattern) || password.matches(passwordOnlyCharsPattern))
			return false;
		
		return true;
	}
	
	public static boolean checkResetPassword(String password) {
		
		String passwordOnlyNumbersPattern = "\\d+(\\.\\d+)?";
		String passwordOnlyCharsPattern = "\\D+";
		
		return !(password.length() < 5 || password.isBlank() || password.matches(passwordOnlyNumbersPattern) 
				|| password.matches(passwordOnlyCharsPattern));
	}
	
	private StringBuilder hashPassword(String password) {
	    StringBuilder hashString = new StringBuilder();
	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-512");
	        byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
	        for (int i = 0; i < bytes.length; i++) {
	            hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
	        }
	    } catch (NoSuchAlgorithmException e) {
	        System.out.println(e);
	    }
	    return hashString;
	}

	
	public Utente(String username, String password, Cliente profile) {
		this.username = username;
		this.password = hashPassword(password).toString();
		this.profile = profile;
		this.ruoli = new ArrayList<>();
		ruoli.add(new Ruolo("Cliente"));
	}
	
	public Utente(String username, String password, Cliente profile, Ruolo role) {
		this.username = username;
		this.password = hashPassword(password).toString();
		this.profile = profile;
		this.ruoli = new ArrayList<>();
		ruoli.add(new Ruolo("Cliente"));
		ruoli.add(role);
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Cliente getProfile() {
		return profile;
	}
	public void setProfile(Cliente profile) {
		this.profile = profile;
	}
	
	@Override
	public String toString() {
		return "Utente [username=" + username + ", password=" + password + ", profile=" + profile.toString() + "]";
	}
	
}
