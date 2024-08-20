package application.GestioneCatalogoService;

import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.NavigazioneService.ObjectProdotto.Categoria;
import application.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import application.NavigazioneService.ProxyProdotto;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * @author raffa
 * @author Dorotea Serrelli
 */
public class GestioneCatalogoServiceImpl implements GestioneCatalogoService{
	
	/* Codice da verificare per paginazione catalogo
	 * 
	 * public Collection<Prodotto> visualizzaCatalogo(int page, int pr_pagina){
    Collection detailed_products = new ArrayList();
    try {
        ProdottoDAODataSource pdao = new ProdottoDAODataSource();
        Collection<ProxyProdotto> recv_products;
        recv_products = pdao.doRetrieveAll("NOME", page, pr_pagina);
        for(ProxyProdotto pr: recv_products){
           detailed_products.add( pdao.doRetrieveCompleteByKey(pr.getCodiceProdotto()));
        }                     
    } catch (SQLException ex) {
        Logger.getLogger(GestioneCatalogoServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
    return detailed_products;
}*/
	
	
	/**
	 * Il metodo implementa il servizio di visualizzazione
	 * dell'elenco dei prodotti presenti nel catalogo.
	 * 
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return l'insieme dei prodotti del catalogo
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException 
	 * @throws CategoriaProdottoException 
	 * */

	@Override
	public Collection<ProxyProdotto> visualizzaCatalogo(int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException {
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();

		return productDao.doRetrieveAllExistent("NOME", page, perPage);
	}


	/**
	 * Il metodo implementa il servizio di aggiunta di
	 * un nuovo prodotto nel catalogo dell'e-commerce.
	 * 
	 * @param product : il nuovo prodotto da inserire nel catalogo
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product
	 * 
	 * @throws SQLException 
	 * @throws ProdottoInCatalogoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws CategoriaProdottoException 
	 * 
	 * */

	@Override
	public Collection<ProxyProdotto> aggiuntaProdottoInCatalogo(Prodotto product, int page, int perPage) throws SQLException, ProdottoInCatalogoException, CategoriaProdottoException, SottocategoriaProdottoException {
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();

		ProxyProdotto InCatalogue = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		if (InCatalogue != null && InCatalogue.isInCatalogo())
			throw new ProdottoInCatalogoException("Il nuovo prodotto da inserire e\' gia\' presente nel catalogo.");
		else
			productDao.doSave(product);

		return productDao.doRetrieveAllExistent(null, page, perPage);
	}


	/**
	 * Il metodo implementa il servizio di rimozione di
	 * un prodotto presente nel catalogo dell'e-commerce.
	 * 
	 * @param product : il prodotto da rimuovere dal catalogo
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, privo di product
	 * @throws SQLException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * 
	 * */

	@Override
	public Collection<ProxyProdotto> rimozioneProdottoDaCatalogo(ProxyProdotto product, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException {
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();

		ProxyProdotto notInCatalogue = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		if (notInCatalogue == null || !notInCatalogue.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto da rimuovere non e\' presente nel catalogo.");
		else
			productDao.doDelete(product.getCodiceProdotto());

		return productDao.doRetrieveAllExistent(null, page, perPage);
	}

	
	/**
	 * Il metodo implementa il servizio di aggiornamento delle seguenti specifiche del prodotto: 
	 * modello, marca, descrizione in evidenza, descrizione dettagliata, categoria, 
	 * sottocategoria.
	 * 
	 * @param product : il prodotto sul quale effettuare le modifiche (non contiene i dati aggiornati)
	 * @param infoSelected : l’informazione, tra quelle specificate in precedenza, che si vorrebbe modificare
	 * @param updatedData : la nuova informazione da memorizzare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con il campo infoSelected aggiornato a updatedData
	 * @throws ProdottoAggiornatoException 
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws ErroreSpecificaAggiornamentoException 
	 */


	@Override
	public Collection<ProxyProdotto> aggiornamentoSpecificheProdotto(Prodotto product, String infoSelected,
			String updatedData, int page, int perPage) throws ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException {
		
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		Prodotto retrieved = productDao.doRetrieveCompleteByKey(product.getCodiceProdotto());
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		switch(infoSelected) {
			case "DESCRIZIONE_EVIDENZA" :
				if(retrieved.getTopDescrizione().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la descrizione in evidenza aggiornata!");
				productDao.updateData(product.getCodiceProdotto(), "TOPDESCRIZIONE", updatedData);
				break;
				
			case "DESCRIZIONE_DETTAGLIATA" :
				if(retrieved.getDettagli().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la descrizione dettagliata aggiornata!");
				productDao.updateData(product.getCodiceProdotto(), "DETTAGLI", updatedData);
				break;
			
			case "MODELLO" :
				if(retrieved.getModello().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' il modello aggiornato!");
				productDao.updateData(product.getCodiceProdotto(), "MODELLO", updatedData);
				break;
			
			case "MARCA" :
				if(retrieved.getMarca().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la marca aggiornata!");
				productDao.updateData(product.getCodiceProdotto(), "MARCA", updatedData);
				break;
		
			case "CATEGORIA" :
				if(Categoria.valueOf(updatedData) == null) //updatedData non è un elemento di Categoria
					throw new CategoriaProdottoException("Le categorie ammissibili sono TELEFONIA, PRODOTTI ELETTRONICA, GRANDI ELETTRODOMESTICI, PICCOLI ELETTRODOMESTICI");
				
				if(retrieved.getCategoriaAsString().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la categoria aggiornata!");
				productDao.updateData(product.getCodiceProdotto(), "CATEGORIA", updatedData);
				break;
			
			case "SOTTOCATEGORIA" :
				if(Sottocategoria.valueOf(updatedData) == null) //updatedData non è un elemento di Sottocategoria
					throw new SottocategoriaProdottoException("Se specificata, le sottocategorie ammissibili per un prodotto sono:"
			        		+ "\n- TABLET e SMARTPHONE per la categoria TELEFONIA;"
			        		+ "\n- PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.");
				
				if(retrieved.getSottocategoriaAsString().equals(updatedData))
					throw new ProdottoAggiornatoException("Il prodotto da aggiornare ha gia\' la sottocategoria aggiornata!");
				productDao.updateData(product.getCodiceProdotto(), "SOTTOCATEGORIA", updatedData);
				break;
			
			default:
				throw new ErroreSpecificaAggiornamentoException("La specifica del prodotto da aggiornare non esiste.");
		}
		
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
	
	
	/**
	 * Il metodo definisce il servizio di aggiornamento dell'inserimento o della
	 * rimozione di un prodotto del catalogo
	 * venduto dal negozio in vetrina.
	 * 
	 * @param product : il prodotto da inserire o da rimuovere dalla vetrina del negozio online
	 * @param updatedData : la nuova informazione da memorizzare (1 : aggiunta in vetrina, 0 : rimozione dalla vetrina)
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con il campo inVetrina = updatedData
	 * @throws SQLException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 */
	
	public Collection<ProxyProdotto> aggiornamentoProdottoInVetrina(Prodotto product, int updatedData, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException{
		
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		ProxyProdotto retrieved = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		else 
			productDao.updateDataView(product.getCodiceProdotto(), updatedData);
		
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
	
	/**
	 * Il metodo implementa il servizio di aggiornamento della quantità di scorte
	 * in magazzino di un prodotto del catalogo.
	 * 
	 * @param product : il prodotto per il quale aggiornare la quantità (non contiene i dati aggiornati)
	 * @param quantity : la nuova quantità di scorte da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con la disponibilità
	 * 		   di scorte in magazzino pari a quantity
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws QuantitaProdottoException 
	 * 
	 * */
	
	public Collection<ProxyProdotto> aggiornamentoDisponibilitàProdotto(Prodotto product, int quantity, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, QuantitaProdottoException{
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		ProxyProdotto retrieved = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		if(quantity <= 0 || quantity < retrieved.getQuantita())
			throw new QuantitaProdottoException("La quantita\' di scorte in magazzino del prodotto deve essere positiva e maggiore del numero di scorte attuali del prodotto, ovvero maggiore di " + retrieved.getQuantita());
		
		productDao.updateQuantity(product.getCodiceProdotto(), quantity);
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
	
	/**
	 * Il metodo implementa il servizio di aggiornamento del prezzo di un prodotto
	 * presente nel catalogo del negozio.
	 * 
	 * @param product : il prodotto per il quale aggiornare il prezzo (non contiene i dati aggiornati)
	 * @param price : il prezzo del prodotto da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con il prezzo
	 * 		   pari a price
	 * @throws SQLException 
	 * @throws SottocategoriaProdottoException 
	 * @throws CategoriaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws PrezzoProdottoException 
	 * 
	 * */
	
	public Collection<ProxyProdotto> aggiornamentoPrezzoProdotto(Prodotto product, int price, int page, int perPage) throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, PrezzoProdottoException{
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		ProxyProdotto retrieved = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		if(price < 0.0)
			throw new PrezzoProdottoException("Il prezzo del prodotto non e\' ammissibile.");
		
		productDao.updatePrice(product.getCodiceProdotto(), price);
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
	
	
	/**
	 * Il metodo implementa il servizio di aggiunta dell' immagine di presentazione ad un
	 * prodotto presente nel catalogo del negozio.
	 * 
	 * @param product : il prodotto per il quale aggiornare l'immagine di presentazione (non contiene i dati aggiornati)
	 * @param image : l'immagine in primo piano del prodotto da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con l'immagine image come
	 * 		   immagine di presentazione
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 * */
	
	public Collection<ProxyProdotto> inserimentoTopImmagine(Prodotto product, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException{
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		ProxyProdotto retrieved = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		PhotoControl.updateTopImage(product.getCodiceProdotto(), image);
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
	
	/**
	 * Il metodo implementa il servizio di aggiunta di un'immagine di dettaglio alla galleria immagini 
	 * di un prodotto presente nel catalogo del negozio.
	 * 
	 * @param product : il prodotto per il quale aggiungere un'immagine di dettaglio (non contiene i dati aggiornati)
	 * @param image : l'immagine di dettaglio del prodotto da impostare
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con l'immagine image presente nella galleria di
	 * 			immagini di dettaglio
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 */
	
	public Collection<ProxyProdotto> inserimentoImmagineInGalleriaImmagini(Prodotto product, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException{
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		ProxyProdotto retrieved = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		PhotoControl.addPhotoInGallery(product.getCodiceProdotto(), image);
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
	
	
	/**
	 * Il metodo implementa il servizio di rimozione di un'immagine di dettaglio alla galleria immagini 
	 * di un prodotto presente nel catalogo del negozio.
	 * 
	 * @param product : il prodotto per il quale rimuovere un'immagine di dettaglio(non contiene i dati aggiornati)
	 * @param image : l'immagine di dettaglio del prodotto da rimuovere
	 * @param page : numero della pagina
	 * @param perPage: numero di prodotti per pagina
	 * 
	 * @return il catalogo dei prodotti aggiornato, contenente product con l'immagine image non presente nella galleria di
	 * 			immagini di dettaglio
	 * @throws SQLException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws IOException 
	 */
	
	public Collection<ProxyProdotto> cancellazioneImmagineInGalleria(Prodotto product, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, IOException{
		ProdottoDAODataSource productDao = new ProdottoDAODataSource();
		ProxyProdotto retrieved = productDao.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		int imgCode = PhotoControl.retrievePhotoInGallery(product.getCodiceProdotto(), image);
		
		PhotoControl.deletePhotoInGallery(product.getCodiceProdotto(), imgCode);
		return productDao.doRetrieveAllExistent(null, page, perPage);
	}
}

