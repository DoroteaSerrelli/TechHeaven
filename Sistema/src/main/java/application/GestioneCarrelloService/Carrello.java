package application.GestioneCarrelloService;

import java.util.ArrayList;
import java.util.List;

public class Carrello {

	private List<ItemCarrello> products;
	
	public Carrello() {
		products = new ArrayList<>();
	}
	
	public void addProduct(ItemCarrello product) {
		if(products == null) {
			products.add(product);
			
		}else {
			boolean exist = false;
			for(ItemCarrello c: products) {
				if(c.getCodice() == product.getCodice()) {
					exist = true;
					ItemCarrello cart2 = c;
					products.remove(c);
					cart2.setQuantità(1+cart2.getQuantità());
					products.add(cart2);
					break;
				}
			}
			
			if(!exist) {
				products.add(product);
			}
		}
	}
	
	public void deleteProduct(ItemCarrello product) {
		for(ItemCarrello prod : products) {
			if(prod.getCodice() == product.getCodice()) {
				products.remove(prod);
				break;
			}
		}
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