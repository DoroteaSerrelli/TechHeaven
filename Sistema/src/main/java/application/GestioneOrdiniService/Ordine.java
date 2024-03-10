package application.GestioneOrdiniService;

import java.time.LocalDate;
import java.time.LocalTime;

import application.RegistrazioneService.Cliente;
import application.RegistrazioneService.Indirizzo;

public class Ordine {
	
	public enum Stato{
		Richiesta_effettuata,
		In_lavorazione,
		Spedito,
		Preparazione_incompleta
	}
	
	public enum TipoSpedizione{
		Spedizione_standard,
		Spedizione_assicurata,
		Spedizione_prime
	}
	
	public enum TipoConsegna{
		Domicilio,
		Punto_ritiro,
		Priority
	}
	
	private int CodiceOrdine;
	private Stato stato;
	private Cliente acquirente;
	private Indirizzo indirizzoSpedizione;
	private TipoConsegna consegna;
	private TipoSpedizione spedizione;
	private LocalDate data;
	private LocalTime ora;
	
	public Ordine(int codice, Stato stato, Cliente cliente, Indirizzo indirizzo, TipoConsegna consegna, TipoSpedizione spedizione) {
		CodiceOrdine = codice;
		this.stato = stato;
		acquirente = cliente;
		indirizzoSpedizione = indirizzo;
		this.consegna = consegna;
		this.spedizione = spedizione;
		data = LocalDate.now();
		ora = LocalTime.now();
	}

	public int getCodiceOrdine() {
		return CodiceOrdine;
	}

	public void setCodiceOrdine(int codiceOrdine) {
		CodiceOrdine = codiceOrdine;
	}

	public Stato getStato() {
		return stato;
	}

	public void setStato(Stato stato) {
		this.stato = stato;
	}

	public Cliente getAcquirente() {
		return acquirente;
	}

	public void setAcquirente(Cliente client) {
		this.acquirente = client;
	}

	public Indirizzo getIndirizzoSpedizione() {
		return indirizzoSpedizione;
	}

	public void setIndirizzoSpedizione(Indirizzo indirizzoSpedizione) {
		this.indirizzoSpedizione = indirizzoSpedizione;
	}

	public TipoConsegna getConsegna() {
		return consegna;
	}

	public void setConsegna(TipoConsegna consegna) {
		this.consegna = consegna;
	}

	public TipoSpedizione getSpedizione() {
		return spedizione;
	}

	public void setSpedizione(TipoSpedizione spedizione) {
		this.spedizione = spedizione;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public LocalTime getOra() {
		return ora;
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
	}

	@Override
	public String toString() {
		return "Ordine [CodiceOrdine=" + CodiceOrdine + ", stato=" + stato + ", cliente=" + acquirente.toStringNominativo() + ", consegna="
				+ consegna + ", spedizione=" + spedizione + ", data=" + data + ", ora=" + ora + "]";
	}
	
}
