package application.GestioneOrdiniService;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReportSpedizione {
	private int numeroReport;
	private String corriere;
	private String imballaggio;
	private LocalDate dataSpedizione;
	private LocalTime oraSpedizione;
	private Ordine ordine;
	public ReportSpedizione(int numeroReport, String corriere, String imballaggio, Ordine ordine) {

		this.numeroReport = numeroReport;
		this.corriere = corriere;
		this.imballaggio = imballaggio;
		this.ordine = ordine;
		this.dataSpedizione = LocalDate.now();
		this.oraSpedizione = LocalTime.now();
	}
	
	
}
