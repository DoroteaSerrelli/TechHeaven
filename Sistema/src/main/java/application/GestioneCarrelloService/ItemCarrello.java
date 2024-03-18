package application.GestioneCarrelloService;
import application.NavigazioneService.*;

public class ItemCarrello extends Prodotto{
private int quantità = 0;
	
	public ItemCarrello() {
		
	}

	@Override
	public int getQuantità() {
		return quantità;
	}

	@Override
	public void setQuantità(int quantity) {
		this.quantità = quantity;
	}
}
