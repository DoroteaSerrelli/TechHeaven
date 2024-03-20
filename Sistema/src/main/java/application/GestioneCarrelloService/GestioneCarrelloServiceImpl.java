package application.GestioneCarrelloService;

import java.util.Collection;

public class GestioneCarrelloServiceImpl implements GestioneCarrelloService{

	@Override
	public Collection<ItemCarrello> visualizzaCarrello(Carrello cart) {
		return cart.getProducts();
	}

	@Override
	public Carrello aggiungiAlCarrello(Carrello cart, ItemCarrello item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Carrello rimuoviDalCarrello(Carrello cart, ItemCarrello item) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Carrello aumentaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Carrello decrementaQuantitaNelCarrello(Carrello cart, ItemCarrello item, int quantity) {
		// TODO Auto-generated method stub
		return null;
	}

}
