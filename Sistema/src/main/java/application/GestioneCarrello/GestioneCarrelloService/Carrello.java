package application.GestioneCarrello.GestioneCarrelloService;

import java.util.ArrayList;
import java.util.List;

import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrello.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;

/**
 * Classe che esprime il concetto 'carrello': si occupa di operazioni volte 
 * a manipolare il carrello ed i suoi prodotti.
 * 
 * @see application.GestioneCarrello.GestioneCarrelloService.GestioneCarrelloService
 * @see application.GestioneCarrello.GestioneCarrelloService.GestioneCarrelloServiceImpl
 * @see application.GestioneCarrello.GestioneCarrelloService.ItemCarrello
 * @see application.GestioneCarrello.GestioneCarrelloService.CarrelloException
 * 
 * @author Dorotea Serrelli
 * */

public class Carrello {
	
	/**
	 * products : i prodotti presenti nel carrello.
	 * */
	
	private ArrayList<ItemCarrello> products;
	
	/**
	 * Costruttore di classe
	 * */
	
	public Carrello() {
		products = new ArrayList<>();
	}
	
	/**
	 * Questo metodo consente l'aggiunta di un prodotto all'interno del carrello.
	 * 
	 * @param product : il prodotto da aggiungere al carrello
	 * 
	 * @throws ProdottoNulloException : gestisce il caso in cui il prodotto product è null
	 * @throws ProdottoPresenteException : gestisce il caso in cui il prodotto da aggiungere 
	 * 										è già presente nel carrello
	 * */
	
	public void addProduct(ItemCarrello product) throws ProdottoPresenteException, ProdottoNulloException{
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto da aggiungere al carrello.");
		if(products != null && isPresent(product))
			throw new ProdottoPresenteException("Prodotto già presente nel carrello");
		else
			products.add(product);
	}
	
	/**
	 * Questo metodo consente la rimozione di un prodotto all'interno del carrello.
	 * 
	 * @param product : il prodotto da rimuovere dal carrello
	 * 
	 * @throws ProdottoNulloException : gestisce il caso in cui il prodotto product è null
	 * @throws ProdottoNonPresenteException : gestisce il caso in cui il prodotto non è presente nel carrello
	 * @throws CarrelloVuotoException : gestisce il caso in cui il carrello è vuoto
	 * */
	
	public void deleteProduct(ItemCarrello product) throws ProdottoNonPresenteException, CarrelloVuotoException, ProdottoNulloException{
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto da rimuovere dal carrello.");
		if(products == null)
			throw new CarrelloVuotoException("Il carrello e\' vuoto.");
		else if(!isPresent(product))
			throw new ProdottoNonPresenteException("Il prodotto " + product.getNomeProdotto() + " non e\' presente nel carrello.");
		else
			for(ItemCarrello prod : products) {
				if(prod.getCodiceProdotto() == product.getCodiceProdotto()) {
					products.remove(prod);
					break;
				}
			}
	}
	
	/**
	 * Questo metodo consente di controllare se un prodotto si trova all'interno del carrello.
	 * 
	 * @param product : il prodotto da verificare dal carrello
	 * 
	 * @return exist : true se product è presente nel carrello; false altrimenti.
	 * 
	 * @throws ProdottoNulloException : gestisce il caso in cui il prodotto product è null
	 * */
	
	public boolean isPresent(ItemCarrello product) throws ProdottoNulloException{
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto da verificare all'interno del carrello.");
		
		boolean exist = false;
		
		if(products == null || products.size() == 0)
			return exist;
		
		for(ItemCarrello c: products) {
			if(c.getCodiceProdotto() == product.getCodiceProdotto()) {
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	/**
	 * Questo metodo aggiorna la quantità di un prodotto all'interno del carrello.
	 * 
	 * @param product : il prodotto la cui quantità deve essere aggiornata
	 * @param quantity : la quantità da impostare
	 * 
	 * @throws ProdottoNulloException : gestisce il caso in cui il prodotto product è null
	 * @throws ProdottoNonPresenteException : gestisce il caso in cui il prodotto non è presente nel carrello
	 * @throws CarrelloVuotoException : gestisce il caso in cui il carrello è vuoto
	 * */

	public void updateProductQuantity(ItemCarrello product, int quantity) throws ProdottoNulloException, CarrelloVuotoException, ProdottoNonPresenteException {
		if(product == null)
			throw new ProdottoNulloException("Non e\' stato specificato nessun prodotto del carrello per il quale aggiornare la quantità.");
		if(products == null)
			throw new CarrelloVuotoException("Il carrello e\' vuoto.");
		else if(!isPresent(product))
			throw new ProdottoNonPresenteException("Il prodotto " + product.getNomeProdotto() + " non e\' presente nel carrello.");
		else {
			for(ItemCarrello prod : products) {
				if(prod.getCodiceProdotto() == product.getCodiceProdotto()) {
					prod.setQuantita(quantity);
					break;
				}
			}
		}
	}


	/**
	 * Questo metodo restituisce i prodotti presenti nel carrello.
	 * 
	 * @return products : i prodotti del carrello
	 * */
	
	public ArrayList<ItemCarrello> getProducts() {
		return  products;
	}
	
	/**
	 * Questo metodo determina il numero di pezzi presenti nel carrello.
	 * 
	 * @return itemsNo : il numero totale di pezzi nel carrello
	 * */
	
	public int getNumProdotti() {
		int itemsNo = 0;
		if(products == null)
			return 0;
		for(ItemCarrello prod : products) {
			itemsNo += prod.getQuantita();
		}
		return itemsNo;
	}
	
	/**
	 * Metodo per impostare una lista di prodotti nel carrello
	 * 
	 * @param products : la lista di prodotti
	 * */
	
	public void setProdotti(ArrayList<ItemCarrello> products) {
		this.products = products;
	}
	
	/**
	 * Questo metodo determina il costo totale dei prodotti presenti nel carrello.
	 * 
	 * @return il costo totale della spesa
	 * */
	
	public double totalAmount() {
		double price = 0.00;
		List<ItemCarrello> prodotti = this.getProducts();
		for(ItemCarrello i : prodotti) {
			price += i.getPrezzo()*i.getQuantita();
		}
				
		return Math.round(price*100.00)/100.00;
	}
	
	/**
	 * Confronta l'oggetto corrente con un altro oggetto per determinare 
	 * se sono uguali. Due carrelli sono considerati uguali se contengono 
	 * gli stessi prodotti nella stessa quantità.
	 *
	 * @param obj l'oggetto da confrontare con l'istanza corrente
	 * @return true se l'oggetto specificato è uguale all'istanza corrente; 
	 *         false altrimenti
	 */
	@Override
	public boolean equals(Object obj) {
	    
	    if (this == obj) {
	        return true;
	    }

	    if (obj == null || getClass() != obj.getClass()) {
	        return false;
	    }


	    Carrello other = (Carrello) obj;
	    
	    // Controlla se la dimensione dei prodotti è uguale
	    if (this.products.size() != other.products.size()) {
	        return false;
	    }
	    
	    if(this.products.size() == 0 && other.products.size() == 0)
	    	return true;
	    
	    // Verifica se tutti i prodotti presenti sono gli stessi
	    for (ItemCarrello item : this.products) {
	        if (!other.products.contains(item)) {
	            return false;
	        }
	    }
	    return true;
	}

	
}