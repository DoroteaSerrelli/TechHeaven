package application.GestioneCarrelloService;

import java.util.ArrayList;
import java.util.List;

import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;

/*Classe che si occupa di operazioni volte a manipolare
 * i prodotti presenti nel carrello.
 * 
 * @author Dorotea Serrelli
 * */

public class Carrello {
	
	private List<ItemCarrello> products;
	
	/*
	 * Costruttore di classe
	 * */
	public Carrello() {
		products = new ArrayList<>();
	}
	
	/*
	 * Questo metodo consente l'aggiunta di un prodotto all'interno del carrello.
	 * @param product è il prodotto da aggiungere al carrello
	 * @precondition product != null
	 * @precondition !this.isPresent(product)
	 * @throws ProdottoPresenteException se il prodotto è già presente nel carrello
	 * */
	
	public void addProduct(ItemCarrello product) throws ProdottoPresenteException, ProdottoNulloException{
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto da aggiungere al carrello.");
		if(products != null && isPresent(product))
			throw new ProdottoPresenteException("Il prodotto " + product.getNome() + " e\' gia\' presente nel carrello.");
		else
			products.add(product);
	}
	
	/*
	 * Questo metodo consente la rimozione di un prodotto all'interno del carrello.
	 * @param product è il prodotto da rimuovere dal carrello
	 * @precondition product != null
	 * @precondition this.isPresent(product)
	 * @throws ProdottoNulloException se product è null
	 * @throws ProdottoNonPresenteException se il prodotto non è presente nel carrello
	 * @throws CarrelloVuotoException se il carrello è vuoto
	 * */
	
	public void deleteProduct(ItemCarrello product) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException{
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto da rimuovere dal carrello.");
		if(products == null)
			throw new CarrelloVuotoException("Il carrello e\' vuoto.");
		else if(!isPresent(product))
			throw new ProdottoNonPresenteException("Il prodotto " + product.getNome() + " non e\' presente nel carrello.");
		else
			for(ItemCarrello prod : products) {
				if(prod.getCodice() == product.getCodice()) {
					products.remove(prod);
					break;
				}
			}
 	}
	
	/*
	 * Questo metodo consente di controllare se un prodotto si trova all'interno del carrello.
	 * @param product è il prodotto da verificare dal carrello
	 * @precondition product != null
	 * @throws ProdottoNulloException se product è null
	 * */
	
	public boolean isPresent(ItemCarrello product) throws ProdottoNulloException{
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto da verificare all'interno del carrello.");

		boolean exist = false;
		
		for(ItemCarrello c: products) {
			if(c.getCodice() == product.getCodice()) {
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	public List<ItemCarrello> getProducts() {
		return  products;
	}
	
	public int getNumProdotti() {
		int quantity = 0;
		if(products == null)
			return 0;
		for(ItemCarrello prod : products) {
			quantity += prod.getQuantità();
		}
		return quantity;
	}

	public void updateProduct(ItemCarrello item) {
		for(ItemCarrello prod : products) {
			if(prod.getCodice() == item.getCodice()) {
				products.remove(prod);
				products.add(item);
				break;
			}
		}
		
	}
	
	public double totaleSpesa() {
		double costo = 0.00;
		List<ItemCarrello> prodotti = this.getProducts();
		for(ItemCarrello i : prodotti) {
				costo += i.getPrezzo()*i.getQuantità();
		}
		return Math.round(costo*100.00)/100.00;
	}

	public void svuota() {
		products.clear();
	}
}