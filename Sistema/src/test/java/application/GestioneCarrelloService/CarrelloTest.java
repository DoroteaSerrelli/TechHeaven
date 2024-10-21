package application.GestioneCarrelloService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.GestioneCarrelloService.CarrelloException.CarrelloVuotoException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNonPresenteException;
import application.GestioneCarrelloService.CarrelloException.ProdottoNulloException;
import application.GestioneCarrelloService.CarrelloException.ProdottoPresenteException;
import static org.junit.jupiter.api.Assertions.*;

class CarrelloTest {

    private Carrello carrello;
    private ItemCarrello prodotto;

    @BeforeEach
    void setUp() {
        carrello = new Carrello();
        
        prodotto = new ItemCarrello();
        prodotto.setCodiceProdotto(1);
        prodotto.setNomeProdotto("Samsung Galaxy A34 5G");
        prodotto.setPrezzo(Float.parseFloat("234.50"));
        prodotto.setMarca("Samsung");
        prodotto.setModello("Galaxy A34");
       
    }
    
    @Test
    void testAddNullProduct() {
        assertThrows(ProdottoNulloException.class, () -> {
            carrello.addProduct(null);
        });
    }

    @Test
    void testAddDuplicateProduct() throws ProdottoNulloException, ProdottoPresenteException {
        carrello.addProduct(prodotto);
        assertThrows(ProdottoPresenteException.class, () -> {
            carrello.addProduct(prodotto);
        });
    }

    
    @Test
    void testAddProduct() throws ProdottoNulloException, ProdottoPresenteException {
        carrello.addProduct(prodotto);
        assertTrue(carrello.isPresent(prodotto));
    }

    
    @Test
    void testDeleteProduct() throws ProdottoNulloException, ProdottoPresenteException, ProdottoNonPresenteException, CarrelloVuotoException {
        carrello.addProduct(prodotto);
        carrello.deleteProduct(prodotto);
        assertFalse(carrello.isPresent(prodotto));
    }

    @Test
    void testDeleteNonExistentProduct() {
        assertThrows(ProdottoNonPresenteException.class, () -> {
            carrello.deleteProduct(prodotto);
        });
    }

    @Test
    void testUpdateProductQuantity() throws ProdottoNulloException, ProdottoPresenteException, ProdottoNonPresenteException, CarrelloVuotoException {
        carrello.addProduct(prodotto);
        carrello.updateProductQuantity(prodotto, 5);
        assertEquals(5, prodotto.getQuantita());
    }

    @Test
    void testUpdateNonExistentProduct() {
        assertThrows(ProdottoNonPresenteException.class, () -> {
            carrello.updateProductQuantity(prodotto, 5);
        });
    }

    @Test
    void testTotalAmount() throws ProdottoNulloException, ProdottoPresenteException {
    	ItemCarrello product2 = new ItemCarrello();
    	product2.setCodiceProdotto(12);
    	product2.setNomeProdotto("HP 15s-fq5040nl");
    	product2.setPrezzo(Float.parseFloat("454.50"));
    	product2.setMarca("HP");
    	product2.setModello("15s-fq5040nl");
    	product2.setQuantita(2);
    	carrello.addProduct(product2);
        carrello.addProduct(prodotto);
        assertEquals(1143.50, carrello.totalAmount());
    }

    @Test
    void testGetNumProdotti() throws ProdottoNulloException, ProdottoPresenteException {
        carrello.addProduct(prodotto);
        assertEquals(1, carrello.getNumProdotti());
    }
    
    @Test
    void testGetProducts() throws ProdottoNulloException, ProdottoPresenteException {
        carrello.addProduct(prodotto);
        
        ItemCarrello product2 = new ItemCarrello();
        
    	product2.setCodiceProdotto(12);
    	product2.setNomeProdotto("HP 15s-fq5040nl");
    	product2.setPrezzo(Float.parseFloat("454.50"));
    	product2.setMarca("HP");
    	product2.setModello("15s-fq5040nl");
    	product2.setQuantita(2);
    	carrello.addProduct(product2);
        
        assertEquals(2, carrello.getProducts().size());
        assertTrue(carrello.getProducts().contains(product2));
        assertTrue(carrello.getProducts().contains(prodotto));
    }
}
