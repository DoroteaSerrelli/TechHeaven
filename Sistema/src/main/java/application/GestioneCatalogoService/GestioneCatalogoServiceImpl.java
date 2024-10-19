package application.GestioneCatalogoService;

import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.NavigazioneService.ObjectProdotto;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoNomeException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 * La classe fornisce l'implementazione dei servizi relativi alla gestione del catalogo del
 * negozio online:
 * 
 * - visualizzazione del catalogo;
 * - inserimento di prodotti nel catalogo;
 * - eliminazione di prodotti dal catalogo;
 * - aggiornamento delle specifiche di un prodotto del catalogo.
 * 
 * @see application.GestioneCatalogoService.GestioneCatalogoServiceImpl
 * @see application.NavigazioneService.Prodotto
 * @see application.NavigazioneService.ProxyProdotto
 * @see application.NavigazioneService.ProdottoException
 * @see application.GestioneCatalogoService.CatalogoException
 * 
 * @author raffa
 * @author Dorotea Serrelli
 */
public class GestioneCatalogoServiceImpl implements GestioneCatalogoService{
	
	private ProdottoDAODataSource productDAO;
	
	public GestioneCatalogoServiceImpl(ProdottoDAODataSource productDAO) {
		this.productDAO = productDAO;
	}
	
	/**
	 * Il metodo implementa il servizio di visualizzazione
	 * dell'elenco dei prodotti presenti nel catalogo.
	 * I prodotti sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return l'insieme dei prodotti del catalogo
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria 
	 * */

	@Override
	public Collection<ProxyProdotto> visualizzaCatalogo(int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException {
		
		return productDAO.doRetrieveAllExistent("NOME", page, perPage);
	}


	/**
	 * Il metodo implementa il servizio di aggiunta di
	 * un nuovo prodotto nel catalogo dell'e-commerce.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il nuovo prodotto da inserire nel catalogo
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product
	 * 
	 * @throws SQLException 
	 * @throws ProdottoInCatalogoException : eccezione lanciata per gestire l'aggiunta di un prodotto già
	 * 										 presente nel catalogo
	 * 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria 
	 * @throws FormatoDettagliException 
	 * @throws FormatoTopDescrizioneException 
	 * @throws PrezzoProdottoException 
	 * @throws FormatoMarcaException 
	 * @throws FormatoModelloException 
	 * @throws FormatoNomeException 
	 * @throws QuantitaProdottoException 
	 * @throws AppartenenzaSottocategoriaException 
	 * @throws FormatoCodiceException 
	 * @throws NumberFormatException 
	 * */

	@Override
	public Collection<ProxyProdotto> aggiuntaProdottoInCatalogo(String codice, String nome, String marca, String modello, String topDescrizione, String dettagli, float prezzo, 
			int quantita, String categoria, String sottocategoria, boolean inCatalogo, boolean inVetrina, ProdottoDAODataSource productDAO, int page, int perPage) throws SQLException, ProdottoInCatalogoException, CategoriaProdottoException, SottocategoriaProdottoException, QuantitaProdottoException, FormatoNomeException, FormatoModelloException, FormatoMarcaException, PrezzoProdottoException, FormatoTopDescrizioneException, FormatoDettagliException, AppartenenzaSottocategoriaException, NumberFormatException, FormatoCodiceException {
		
		if(sottocategoria == null) {
			if(Prodotto.checkValidate(codice, nome, marca, modello, topDescrizione, dettagli, prezzo, quantita, categoria, productDAO)) {
				Prodotto product = new Prodotto(Integer.parseInt(codice), nome, topDescrizione, dettagli, prezzo, Categoria.valueOf(categoria), marca, modello, quantita, inCatalogo, inVetrina);
				productDAO.doSave(product);

				return productDAO.doRetrieveAllExistent(null, page, perPage);
			}
			
		}else {
			if(Prodotto.checkValidate(codice, nome, marca, modello, topDescrizione, dettagli, prezzo, quantita, categoria, sottocategoria, productDAO)) {
				Prodotto product = new Prodotto(Integer.parseInt(codice), nome, topDescrizione, dettagli, prezzo, Categoria.valueOf(categoria), Sottocategoria.valueOf(sottocategoria), marca, modello, quantita, inCatalogo, inVetrina);
				productDAO.doSave(product);

				return productDAO.doRetrieveAllExistent(null, page, perPage);
			}
		}
		
		return null;
	}


	/**
	 * Il metodo implementa il servizio di rimozione di
	 * un prodotto presente nel catalogo dell'e-commerce.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto da rimuovere dal catalogo
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, privo di product
	 * 
	 * @throws SQLException 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire l'eliminazione di un prodotto
	 * 										 	non presente nel catalogo
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria 
	 * */

	@Override
	public Collection<ProxyProdotto> rimozioneProdottoDaCatalogo(ProxyProdotto product, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException {
		
		ProxyProdotto notInCatalogue = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		if (notInCatalogue == null || !notInCatalogue.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto da rimuovere non e\' presente nel catalogo.");
		else
			productDAO.doDelete(product.getCodiceProdotto());

		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}

	
	/**
	 * Il metodo implementa il servizio di aggiornamento delle seguenti specifiche del prodotto: 
	 * modello, marca, descrizione in evidenza, descrizione dettagliata, categoria, 
	 * sottocategoria.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto sul quale effettuare le modifiche (non contiene i dati aggiornati)
	 * @param infoSelected : l’informazione, tra quelle specificate in precedenza, che si vorrebbe modificare
	 * @param updatedData : la nuova informazione da memorizzare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con il campo infoSelected aggiornato a updatedData
	 * 
	 * @throws ProdottoAggiornatoException : eccezione che gestisce il caso in cui, specificata infoSelected, il prodotto
	 * 										possiede già per infoSelected il valore updatedData
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * 
	 * @throws ErroreSpecificaAggiornamentoException : eccezione lanciata per gestire il mancato/incorretto inserimento della specifica del prodotto da
	 * 													aggiornare.
	 * 
	 */


	@Override
	public Collection<ProxyProdotto> aggiornamentoSpecificheProdotto(Prodotto product, String infoSelected,
			String updatedData, int page, int perPage) throws ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException {
		
		Prodotto retrieved = productDAO.doRetrieveCompleteByKey(product.getCodiceProdotto());
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		switch(infoSelected) {
			case "DESCRIZIONE_EVIDENZA" :
				if(retrieved.getTopDescrizione().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la descrizione in evidenza aggiornata!");
				productDAO.updateData(product.getCodiceProdotto(), "TOPDESCRIZIONE", updatedData);
				break;
				
			case "DESCRIZIONE_DETTAGLIATA" :
				if(retrieved.getDettagli().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la descrizione dettagliata aggiornata!");
				productDAO.updateData(product.getCodiceProdotto(), "DETTAGLI", updatedData);
				break;
			
			case "MODELLO" :
				if(retrieved.getModello().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' il modello aggiornato!");
				productDAO.updateData(product.getCodiceProdotto(), "MODELLO", updatedData);
				break;
			
			case "MARCA" :
				if(retrieved.getMarca().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la marca aggiornata!");
				productDAO.updateData(product.getCodiceProdotto(), "MARCA", updatedData);
				break;
		
			case "CATEGORIA" :
				if(Categoria.valueOf(updatedData) == null) //updatedData non è un elemento di Categoria
					throw new CategoriaProdottoException("Le categorie ammissibili sono TELEFONIA, PRODOTTI ELETTRONICA, GRANDI ELETTRODOMESTICI, PICCOLI ELETTRODOMESTICI");
				
				if(retrieved.getCategoriaAsString().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la categoria aggiornata!");
				productDAO.updateData(product.getCodiceProdotto(), "CATEGORIA", updatedData);
				break;
			
			case "SOTTOCATEGORIA" :
				if(Sottocategoria.valueOf(updatedData) == null) //updatedData non è un elemento di Sottocategoria
					throw new SottocategoriaProdottoException("Se specificata, le sottocategorie ammissibili per un prodotto sono:"
			        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
			        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
				
				if(retrieved.getSottocategoriaAsString().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la sottocategoria aggiornata!");
				productDAO.updateData(product.getCodiceProdotto(), "SOTTOCATEGORIA", updatedData);
				break;
			
			default:
				throw new ErroreSpecificaAggiornamentoException("La specifica del prodotto da aggiornare non esiste.");
		}
		
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
	
	
	/**
	 * Il metodo definisce il servizio di aggiornamento dell'inserimento o della
	 * rimozione di un prodotto del catalogo
	 * venduto dal negozio in vetrina.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto da inserire o da rimuovere dalla vetrina del negozio online
	 * @param updatedData : la nuova informazione da memorizzare (1 : aggiunta in vetrina, 0 : rimozione dalla vetrina)
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con il campo inVetrina = updatedData
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * 	
	 */
	
	public Collection<ProxyProdotto> aggiornamentoProdottoInVetrina(Prodotto product, int updatedData, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		else 
			productDAO.updateDataView(product.getCodiceProdotto(), updatedData);
		
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
	
	/**
	 * Il metodo implementa il servizio di aggiornamento della quantità di scorte
	 * in magazzino di un prodotto del catalogo.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto per il quale aggiornare la quantità (non contiene i dati aggiornati)
	 * @param quantity : la nuova quantità di scorte da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con la disponibilità
	 * 		   di scorte in magazzino pari a quantity
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * 	
	 * @throws QuantitaProdottoException : eccezione lanciata per gestire un valore non valido per quantity
	 * 
	 * */
	
	public Collection<ProxyProdotto> aggiornamentoDisponibilitàProdotto(Prodotto product, int quantity, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, QuantitaProdottoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		if(quantity <= 0 || quantity < retrieved.getQuantita())
			throw new QuantitaProdottoException("La quantita\' di scorte in magazzino del prodotto deve essere positiva e maggiore del numero di scorte attuali del prodotto, ovvero maggiore di " + retrieved.getQuantita());
		
		productDAO.updateQuantity(product.getCodiceProdotto(), quantity);
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
	
	/**
	 * Il metodo implementa il servizio di aggiornamento del prezzo di un prodotto
	 * presente nel catalogo del negozio.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto per il quale aggiornare il prezzo (non contiene i dati aggiornati)
	 * @param price : il prezzo del prodotto da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con il prezzo
	 * 		   pari a price
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * 	
	 * @throws PrezzoProdottoException : eccezione lanciata per gestire un valore non valido associato a price
	 * 
	 * */
	
	public Collection<ProxyProdotto> aggiornamentoPrezzoProdotto(Prodotto product, float price, int page, int perPage) throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, PrezzoProdottoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		if(price < 0.0)
			throw new PrezzoProdottoException("Il prezzo del prodotto non e\' ammissibile.");
		
		productDAO.updatePrice(product.getCodiceProdotto(), price);
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
	
	
	/**
	 * Il metodo implementa il servizio di aggiunta dell' immagine di presentazione ad un
	 * prodotto presente nel catalogo del negozio.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto per il quale aggiornare l'immagine di presentazione (non contiene i dati aggiornati)
	 * @param image : l'immagine in primo piano del prodotto da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con l'immagine image come
	 * 		   immagine di presentazione
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * */
	
	public Collection<ProxyProdotto> inserimentoTopImmagine(Prodotto product, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		PhotoControl.updateTopImage(product.getCodiceProdotto(), image);
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
	
	/**
	 * Il metodo implementa il servizio di aggiunta di un'immagine di dettaglio alla galleria immagini 
	 * di un prodotto presente nel catalogo del negozio.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto per il quale aggiungere un'immagine di dettaglio(non contiene i dati aggiornati)
	 * @param image : l'immagine di dettaglio del prodotto da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con l'immagine image presente nella galleria di
	 * 			immagini di dettaglio
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * */
	
	public Collection<ProxyProdotto> inserimentoImmagineInGalleriaImmagini(Prodotto product, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		PhotoControl.addPhotoInGallery(product.getCodiceProdotto(), image);
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
	
	
	/**
	 * Il metodo implementa il servizio di rimozione di un'immagine di dettaglio alla galleria immagini 
	 * di un prodotto presente nel catalogo del negozio.
	 * I prodotti in catalogo sono restituiti dal metodo secondo il meccanismo
	 * della paginazione.
	 * 
	 * @param product : il prodotto per il quale rimuovere un'immagine di dettaglio(non contiene i dati aggiornati)
	 * @param image : l'immagine di dettaglio del prodotto da rimuovere
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con l'immagine image non presente nella galleria di
	 * 			immagini di dettaglio
	 * 
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * @throws IOException 
	 */
	
	public Collection<ProxyProdotto> cancellazioneImmagineInGalleria(Prodotto product, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, IOException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		int imgCode = PhotoControl.retrievePhotoInGallery(product.getCodiceProdotto(), image);
		
		PhotoControl.deletePhotoInGallery(product.getCodiceProdotto(), imgCode);
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
}

