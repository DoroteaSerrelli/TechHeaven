package application.PagamentoService;

import java.time.LocalDate;
import java.time.LocalTime;

import application.GestioneOrdiniService.Ordine;

public class Pagamento {
	private int codicePagamento;
	private Ordine ordine;
	private LocalDate dataPagamento;
	private LocalTime oraPagamento;
	private float importo;
	
	
	
	
	public Pagamento(int codicePagamento, Ordine ordine, float importo) {
		this.codicePagamento = codicePagamento;
		this.ordine = ordine;
		this.importo = importo;
		this.dataPagamento = LocalDate.now();
		this.oraPagamento = LocalTime.now();
	}
	public int getCodicePagamento() {
		return codicePagamento;
	}
	public void setCodicePagamento(int codicePagamento) {
		this.codicePagamento = codicePagamento;
	}
	public Ordine getOrdine() {
		return ordine;
	}
	public void setOrdine(Ordine ordine) {
		this.ordine = ordine;
	}
	public LocalDate getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	public LocalTime getOraPagamento() {
		return oraPagamento;
	}
	public void setOraPagamento(LocalTime oraPagamento) {
		this.oraPagamento = oraPagamento;
	}
	public float getImporto() {
		return importo;
	}
	public void setImporto(float importo) {
		this.importo = importo;
	}
	@Override
	public String toString() {
		return "Pagamento [codicePagamento=" + codicePagamento + ", ordine n.=" + ordine.getCodiceOrdine() + " effettuato da : " + ordine.getAcquirente().toString()+ "\n dataPagamento="
				+ dataPagamento + ", oraPagamento=" + oraPagamento + ", importo=" + importo + "]";
	}
	
}
