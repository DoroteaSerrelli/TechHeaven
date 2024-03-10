package application.RegistrazioneService;

public class Indirizzo {
	private int IDIndirizzo = 0;
	private String via = "";
	private String numCivico = "";
	private String città = "";
	private String cap = "";
	private String provincia = "";
	
	String viaPattern = "^[A-Za-z\\\\s]+$" ;
	String numCivicoPattern = "\"^(([0-9])|(([0-9]+|\\\\w)(\\\\w|[0-9]+)))$\"";
	String cittàPattern = "^[A-Za-z\\s]+$";
	String capPattern = "^\\d{5}$";
	String provinciaPattern = "^[A-Za-z\\\\s]+$" ;
	
	public boolean checkValidate(String via, String numCivico, String città, String cap, String provincia) {
		return (!via.matches(viaPattern) || !numCivico.matches(numCivicoPattern) ||
				!città.matches(cittàPattern) || !cap.matches(capPattern) || 
				!provincia.matches(provinciaPattern)) ? false : true;
	}
	
	public boolean checkValidate(Indirizzo indirizzo) {
		return checkValidate(indirizzo.getVia(), indirizzo.getNumCivico(), 
				indirizzo.getCittà(), indirizzo.getCap(), indirizzo.getProvincia());
	}
	
	public Indirizzo(int iDIndirizzo, String via, String numCivico, String città, String cap, String provincia) {
		IDIndirizzo = iDIndirizzo;
		this.via = via;
		this.numCivico = numCivico;
		this.città = città;
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
	
	public String getCittà() {
		return città;
	}
	
	public void setCittà(String città) {
		this.città = città;
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
		return "Indirizzo [via=" + via + ", numCivico=" + numCivico + ", città=" + città + ", cap=" + cap
				+ ", provincia=" + provincia + "]";
	}	
}
