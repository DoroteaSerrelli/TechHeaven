package application.NavigazioneService;

import java.sql.SQLException;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 * La classe permette di controllare l'accesso ad un oggetto della classe Prodotto, 
 * agendo come surrogato o interfaccia di sostituzione a questo oggetto nel design pattern Proxy. 
 * ProxyProdotto agisce per conto di Prodotto, memorizzando un sottoinsieme degli attributi di Prodotto 
 * (quelli offerti da ObjectProdotto) e gestisce completamente le richieste che non richiedono la conoscenza delle
 * specifiche del prodotto seguenti : top immagine, descrizione dettagliata, galleria immagini.
 * ProxyProdotto ha un riferimento ad un oggetto Prodotto, in modo che tutte le richieste legate alla manipolazione 
 * delle speicfiche del prodotto precedentemente menzionate (pagina dei dettagli del prodotto) 
 * vengono delegate a Prodotto.
 * Dopo la delega, viene creato l'oggetto Prodotto e caricato in memoria.
 * @see application.NavigazioneService.ObjectProdotto
 * @see application.NavigazioneService.Prodotto
 * 
 * @author Dorotea Serrelli
 * */

public class ProxyProdotto extends ObjectProdotto{

	private Prodotto realProdotto;
	
	/**
	 * Costruttore di classe di default.
	 * */
	public ProxyProdotto() {
		super();
		realProdotto = null;
	}

	/**
	 * Costruttore di classe per creare un oggetto ProxyProdotto noti codiceProdotto, nomeProdotto, topDescrizione, prezzo, categoria, marca, modello, quantita,
	 * inCatalogo, inVetrina.
	 * 
	 * @param codiceProdotto : l'identificativo del prodotto
	 * @param nomeProdotto : il nome del prodotto 
	 * @param topDescrizione : la descrizione in evidenza del prodotto
	 * @param prezzo : il costo del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto (Telefonia, Prodotti elettronica, Piccoli elettrodomestici,
	 * Grandi elettrodomestici)
	 * @param marca : il brand o casa produttrice del prodotto
	 * @param modello : la versione del prodotto definita dalla casa produttrice
	 * @param quantita : il numero di scorte in magazzino del prodotto
	 * @param inCatalogo : il prodotto è presente nel catalogo online del negozio
	 * @param inVetrina : il prodotto è nella vetrina online del negozio tra i prodotti di una determinata categoria
	 * 
	 * @return un oggetto surrogato che possiede le informazioni essenziali di un prodotto
	 * */
	public ProxyProdotto(int codiceProdotto, String nomeProdotto, String topDescrizione, float prezzo,
			Categoria categoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		super(codiceProdotto, nomeProdotto, topDescrizione, prezzo,
				categoria, marca, modello, quantita,
				inCatalogo, inVetrina);
	}

	/**
	 * Costruttore di classe che crea un prodotto appartenente ad una categoria, e sottocategoria, noti codiceProdotto, nomeProdotto, 
	 * topDescrizione, prezzo, marca, modello, quantita, inCatalogo, inVetrina.
	 * 
	 * @param codiceProdotto : l'identificativo del prodotto
	 * @param nomeProdotto : il nome del prodotto 
	 * @param topDescrizione : la descrizione in evidenza del prodotto
	 * @param prezzo : il costo del prodotto
	 * @param categoria : la categoria di appartenenza del prodotto (Telefonia, Prodotti elettronica, Piccoli elettrodomestici,
	 * Grandi elettrodomestici)
	 * @param sottocategoria : la sottocategoria o sottoraggruppamento di una categoria del prodotto
	 * @param marca : il brand o casa produttrice del prodotto
	 * @param modello : la versione del prodotto definita dalla casa produttrice
	 * @param quantita : il numero di scorte in magazzino del prodotto
	 * @param inCatalogo : il prodotto è presente nel catalogo online del negozio
	 * @param inVetrina : il prodotto è nella vetrina online del negozio tra i prodotti di una determinata categoria
	 * 
	 * @return un oggetto surrogato che possiede le informazioni essenziali di un prodotto
	 * */
	public ProxyProdotto(int codiceProdotto, String nomeProdotto, String topDescrizione, float prezzo,
			Categoria categoria, Sottocategoria sottocategoria, String marca, String modello, int quantita,
			boolean inCatalogo, boolean inVetrina) {
		super(codiceProdotto, nomeProdotto, topDescrizione, prezzo,
				categoria, sottocategoria, marca, modello, quantita,
				inCatalogo, inVetrina);
	}

	/**
	 * Il metodo fornisce il riferimento all'oggetto Prodotto.
	 * Se non è presente questo riferimento, allora si crea tale oggetto e se ne mantiene in memoria
	 * il riferimento.
	 * @return l'oggetto Prodotto che possiede le informazioni relative al prodotto
	 * */
	public Prodotto mostraProdotto() {
		if(realProdotto == null) {
			ProdottoDAODataSource productDao = new ProdottoDAODataSource();
			try {
				Prodotto real = productDao.doRetrieveCompleteByKey(getCodiceProdotto());
				realProdotto = real;
			} catch (SQLException e) {
				System.out.println("Errore nel recupero del prodotto");
			}
		}
		return realProdotto;
	}
}
