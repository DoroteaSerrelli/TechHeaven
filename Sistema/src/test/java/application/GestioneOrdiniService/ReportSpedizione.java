package application.GestioneOrdiniService;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Questa classe esprime il concetto di report di spedizione di un ordine :
 * un documeno allegato all'ordine dopo la sua preparazione alla spedizione.
 * Esso viene generato dal gestore degli ordini del negozio e fornito all'azienda
 * logistica ai fini del trasporto.
 * Nel report di spedizione sono contenute informazioni riguardanti l'ordine :
 * corriere, tipologia di imballaggio, data di spedizione ed ora di spedizione,
 * le caratteristiche dell'ordine.
 * 
 * @see application.GestioneOrdiniService.ObjectOrdine
 * 
 * @author Dorotea Serrelli
 * 
 * */

public class ReportSpedizione {

	/**
	 * numeroReport : codice univoco che identifica il report
	 * */
	
	private int numeroReport;
	
	/**
	 * corriere : l'azienda logistica che prende in carico l'ordine
	 * associato al report
	 * */
	
	private String corriere;
	
	/**
	 * imballaggio : tipologia di involucro utilizzato per
	 * rivestire l'ordine per la spedizione
	 * 
	 * @see application.GestioneOrdiniService.ObjectOrdine
	 * 
	 * */
	
	private String imballaggio;
	
	/**
	 * dataSpedizione : la data in cui l'ordine è stato
	 * evaso dal negozio
	 * */
	
	private LocalDate dataSpedizione;
	
	/**
	 * oraSpedizione : l'ora in cui l'ordine è stato evaso
	 * dal negozio
	 * */
	
	private LocalTime oraSpedizione;
	
	/**
	 * ordine : l'ordine spedito dal negozio
	 * */
	
	private ObjectOrdine ordine;
	
	/**
	 * Costruttore di classe di default.
	 * */
	public ReportSpedizione() {
		this.numeroReport = -1;
		this.corriere = null;
		this.imballaggio = null;
		this.ordine = null;
		this.dataSpedizione = null;
		this.oraSpedizione = null;
	}
	
	/**
	 * Metodo costruttore della classe.
	 * Si costruisce un oggetto ReportSpedizione rappresentante 
	 * il report di spedizione associato a ordine, avente attributi numeroReport,
	 * corriere, imballaggio e data e ora correnti
	 * 
	 * @param numeroReport : è il numero del report di spedizione
	 * @param corriere: la ditta logistica che deve prendere in carico l'ordine da spedire
	 * @param imballaggio: la tipologia di imballaggio dell'ordine da spedire
	 * @param ordine : è l'ordine da spedire
	 * 
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
	 * Il metodo fornisce il numero del report di spedizione.
	 * 
	 * @return numeroReport : il codice identificativo del report
	 * **/
	
	public int getNumeroReport() {
		return numeroReport;
	}
	
	/**
	 * Il metodo imposta il numero del report di spedizione
	 * 
	 * @param numeroReport: il codice identificativo del report
	 * **/
	
	public void setNumeroReport(int numeroReport) {
		this.numeroReport = numeroReport;
	}
	
	/**
	 * Il metodo fornisce il corriere al quale è stato fornito l'ordine
	 * descritto nel report.
	 * 
	 * @return corriere : il nominativo del corriere per la presa in carico dell'ordine
	 * **/
	
	public String getCorriere() {
		return corriere;
	}
	
	/**
	 * Il metodo imposta il corriere al quale è stato fornito l'ordine
	 * descritto nel report.
	 * 
	 * @param corriere: nominativo del corriere per la presa in carico dell'ordine
	 * **/
	
	public void setCorriere(String corriere) {
		this.corriere = corriere;
	}
	
	/**
	 * Il metodo fornisce l'imballaggio dell'ordine.
	 * 
	 * @return imballaggio : il rivestimento protettivo nel quale avvolgere l'ordine
	 * **/
	
	public String getImballaggio() {
		return imballaggio;
	}
	
	/**
	 * Il metodo imposta l'imballaggio dell'ordine.
	 * 
	 * @param imballaggio: il rivestimento protettivo nel quale avvolgere l'ordine
	 * **/
	
	public void setImballaggio(String imballaggio) {
		this.imballaggio = imballaggio;
	}
	
	/**
	 * Il metodo fornisce la data in cui l'ordine è stato dato dal negozio al corriere.
	 * 
	 * @return dataSpedizione : la data di spedizione dell'ordine
	 * **/
	
	public LocalDate getDataSpedizione() {
		return dataSpedizione;
	}
	
	/**
	 * Il metodo imposta la data in cui l'ordine è stato dato dal negozio al corriere.
	 * 
	 * @param dataSpedizione: la data di spedizione dell'ordine
	 * **/
	
	public void setDataSpedizione(LocalDate dataSpedizione) {
		this.dataSpedizione = dataSpedizione;
	}
	
	/**
	 * Il metodo fornisce l'ora in cui l'ordine è stato dato al corriere.
	 * 
	 * @return oraSpedizione : l'ora di spedizione dell'ordine
	 * **/
	
	public LocalTime getOraSpedizione() {
		return oraSpedizione;
	}
	
	/**
	 * Il metodo imposta l'ora in cui l'ordine è stato dato dal negozio al corriere.
	 * 
	 * @param oraSpedizione : l'ora di spedizione dell'ordine
	 * **/
	
	public void setOraSpedizione(LocalTime oraSpedizione) {
		this.oraSpedizione = oraSpedizione;
	}
	
	/**
	 * Il metodo fornisce le informazioni essenziali dell'ordine spedito.
	 * 
	 * @return ordine : le caratteristiche generali dell'ordine spedito
	 * **/
	
	public ProxyOrdine getOrdine() {
		return (ProxyOrdine) ordine;
	}
	
	/**
	 * Il metodo imposta le informazioni essenziali dell'ordine spedito.
	 * 
	 * @param ordine : le caratteristiche generali dell'ordine spedito
	 * **/
	public void setOrdine(ObjectOrdine ordine) {
		this.ordine = ordine;
	}
}
