package application.RegistrazioneService;

public class Indirizzo {
	private int IDIndirizzo = 0;
	private String via = "";
	private String numCivico = "";
	private String citta = "";
	private String cap = "";
	private String provincia = "";
	
	String viaPattern = "^[A-Za-z\\\\s]+$" ;
	String numCivicoPattern = "\"^(([0-9])|(([0-9]+|\\\\w)(\\\\w|[0-9]+)))$\"";
	String cittaPattern = "^[A-Za-z\\s]+$";
	String capPattern = "^\\d{5}$";
	String provinciaPattern = "^[A-Za-z\\\\s]+$" ;
	
	public boolean checkValidate(String via, String numCivico, String citta, String cap, String provincia) {
		return (!via.matches(viaPattern) || !numCivico.matches(numCivicoPattern) ||
				!citta.matches(cittaPattern) || !cap.matches(capPattern) || 
				!provincia.matches(provinciaPattern)) ? false : true;
	}
	
	public boolean checkValidate(Indirizzo indirizzo) {
		return checkValidate(indirizzo.getVia(), indirizzo.getNumCivico(), 
				indirizzo.getCitta(), indirizzo.getCap(), indirizzo.getProvincia());
	}
	
	public Indirizzo(int iDIndirizzo, String via, String numCivico, String citta, String cap, String provincia) {
		IDIndirizzo = iDIndirizzo;
		this.via = via;
		this.numCivico = numCivico;
		this.citta = citta;
		this.cap = cap;
		this.provincia = provincia;
	}
	
	/*
	 * Da vedere per generare l'ID dell'indirizzo.
	 * */
	public Indirizzo(String via, String numCivico, String citta, String cap, String provincia) {
		this.via = via;
		this.numCivico = numCivico;
		this.citta = citta;
		this.cap = cap;
		this.provincia = provincia;
	}

	public int getIDIndirizzo() {
		return IDIndirizzo;
	}

	public void setIDIndirizzo(int iDIndirizzo) {
		IDIndirizzo = iDIndirizzo;
	}

	public String getVia() {
		return via;
	}
	
	public void setVia(String via) {
		this.via = via;
	}
	
	public String getNumCivico() {
		return numCivico;
	}
	
	public void setNumCivico(String numCivico) {
		this.numCivico = numCivico;
	}
	
	public String getCitta() {
		return citta;
	}
	
	public void setCitta(String citta) {
		this.citta = citta;
	}
	
	public String getCap() {
		return cap;
	}
	
	public void setCap(String string) {
		this.cap = string;
	}
	
	public String getProvincia() {
		return provincia;
	}
	
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	
	@Override
	public String toString() {
		return "Indirizzo [via=" + via + ", numCivico=" + numCivico + ", città=" + citta + ", cap=" + cap
				+ ", provincia=" + provincia + "]";
	}	
}
