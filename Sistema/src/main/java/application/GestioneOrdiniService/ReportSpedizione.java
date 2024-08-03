package application.GestioneOrdiniService;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReportSpedizione {
	private int numeroReport;
	private String corriere;
	private String imballaggio;
	private LocalDate dataSpedizione;
	private LocalTime oraSpedizione;
	private ObjectOrdine ordine;
	
	/**
	 * Metodo costruttore della classe
	 * 
	 * @param numeroReport : è il numero del report di spedizione
	 * @param corriere: la ditta logistica che deve prendere in carico l'ordine da spedire
	 * @param imballaggio: la tipologia di imballaggio dell'ordine da spedire
	 * @param ordine : è l'ordine da spedire
	 * 
	 * @return il repost di spedizione associato a ordine
	 * **/
	
	public ReportSpedizione(int numeroReport, String corriere, String imballaggio, ObjectOrdine ordine) {

		this.numeroReport = numeroReport;
		this.corriere = corriere;
		this.imballaggio = imballaggio;
		this.ordine = ordine;
		this.dataSpedizione = LocalDate.now();
		this.oraSpedizione = LocalTime.now();
	}
	
	/**
	 * Il metodo fornisce il numero del report di spedizione
	 * @return il codice identificativo del report
	 * **/
	public int getNumeroReport() {
		return numeroReport;
	}
	
	/**
	 * Il metodo imposta il numero del report di spedizione
	 * @param numeroReport: il codice identificativo del report
	 * **/
	public void setNumeroReport(int numeroReport) {
		this.numeroReport = numeroReport;
	}
	
	/**
	 * Il metodo fornisce il corriere al quale è stato fornito l'ordine
	 * descritto nel report
	 * @return il nominativo del corriere per la presa in carico dell'ordine
	 * **/
	public String getCorriere() {
		return corriere;
	}
	
	/**
	 * Il metodo imposta il il corriere al quale è stato fornito l'ordine
	 * descritto nel report.
	 * @param corriere: nominativo del corriere per la presa in carico dell'ordine
	 * **/
	public void setCorriere(String corriere) {
		this.corriere = corriere;
	}
	
	/**
	 * Il metodo fornisce l'imballaggio dell'ordine
	 * @return il rivestimento protettivo nel quale avvolgere l'ordine
	 * **/
	public String getImballaggio() {
		return imballaggio;
	}
	
	/**
	 * Il metodo imposta l'imballaggio dell'ordine
	 * @param imballaggio: il rivestimento protettivo nel quale avvolgere l'ordine
	 * **/
	public void setImballaggio(String imballaggio) {
		this.imballaggio = imballaggio;
	}
	
	/**
	 * Il metodo fornisce la data in cui l'ordine è stato dato al corriere
	 * @return la data di spedizione dell'ordine
	 * **/
	public LocalDate getDataSpedizione() {
		return dataSpedizione;
	}
	
	/**
	 * Il metodo imposta la data in cui l'ordine è stato dato al corriere
	 * @param dataSpedizione: la data di spedizione dell'ordine
	 * **/
	public void setDataSpedizione(LocalDate dataSpedizione) {
		this.dataSpedizione = dataSpedizione;
	}
	
	/**
	 * Il metodo fornisce l'ora in cui l'ordine è stato dato al corriere
	 * @return l'ora di spedizione dell'ordine
	 * **/
	public LocalTime getOraSpedizione() {
		return oraSpedizione;
	}
	
	/**
	 * Il metodo imposta l'ora in cui l'ordine è stato dato al corriere
	 * @param oraSpedizione : l'ora di spedizione dell'ordine
	 * **/
	public void setOraSpedizione(LocalTime oraSpedizione) {
		this.oraSpedizione = oraSpedizione;
	}
	
	/**
	 * Il metodo fornisce le informazioni essenziali dell'ordine spedito.
	 * @return le caratteristiche generali dell'ordine spedito
	 * **/
	public ProxyOrdine getOrdine() {
		return (ProxyOrdine) ordine;
	}
	
	/**
	 * Il metodo imposta le informazioni essenziali dell'ordine spedito.
	 * @param ordine : le caratteristiche generali dell'ordine spedito
	 * **/
	public void setOrdine(ObjectOrdine ordine) {
		this.ordine = ordine;
	}
}
