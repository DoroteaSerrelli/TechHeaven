package application.GestioneCatalogo.GestioneCatalogoService;

import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogo.GestioneCatalogoService.CatalogoException.ProdottoNulloException;
import application.Navigazione.NavigazioneService.ObjectProdotto;
import application.Navigazione.NavigazioneService.Prodotto;
import application.Navigazione.NavigazioneService.ProxyProdotto;
import application.Navigazione.NavigazioneService.ObjectProdotto.Categoria;
import application.Navigazione.NavigazioneService.ObjectProdotto.Sottocategoria;
import application.Navigazione.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.Navigazione.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.DettagliImmagineNonPresenteException;
import application.Navigazione.NavigazioneService.ProdottoException.ErroreDettagliImmagineException;
import application.Navigazione.NavigazioneService.ProdottoException.ErroreTopImmagineException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoModelloException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoNomeException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.Navigazione.NavigazioneService.ProdottoException.FormatoVetrinaException;
import application.Navigazione.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.Navigazione.NavigazioneService.ProdottoException.SottocategoriaProdottoException;

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
 * @see application.GestioneCatalogo.GestioneCatalogoService.GestioneCatalogoServiceImpl
 * @see application.Navigazione.NavigazioneService.Prodotto
 * @see application.Navigazione.NavigazioneService.ProxyProdotto
 * @see application.Navigazione.NavigazioneService.ProdottoException
 * @see application.GestioneCatalogo.GestioneCatalogoService.CatalogoException
 * 
 * @author raffa
 * @author Dorotea Serrelli
 */
public class GestioneCatalogoServiceImpl implements GestioneCatalogoService{

	private ProdottoDAODataSource productDAO;
	private PhotoControl pcDAO;

	public GestioneCatalogoServiceImpl(ProdottoDAODataSource productDAO, PhotoControl pcDAO) {
		this.productDAO = productDAO;
		this.pcDAO = pcDAO;
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
	 * @throws ProdottoNulloException : gestisce il caso in cui non venga specificato il prodotto da rimuovere
	 * */

	@Override
	public Collection<ProxyProdotto> rimozioneProdottoDaCatalogo(ProxyProdotto product, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNulloException {
		
		if(product == null)
			throw new ProdottoNulloException("Specificare il prodotto da rimuovere dal catalogo del negozio");

		ProxyProdotto notInCatalogue = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		if (notInCatalogue == null || !notInCatalogue.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto da rimuovere non è presente nel catalogo.");
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
	 * @throws FormatoTopDescrizioneException 
	 * @throws FormatoDettagliException 
	 * @throws FormatoModelloException 
	 * @throws FormatoMarcaException 
	 * @throws AppartenenzaSottocategoriaException 
	 * 
	 */

	@Override
	public Collection<ProxyProdotto> aggiornamentoSpecificheProdotto(Prodotto product, String infoSelected,
			String updatedData, int page, int perPage) throws ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {
		 System.out.println("SONO QUI IN AGGIORNAMENTO: " + product.toString());
		Prodotto retrieved = productDAO.doRetrieveCompleteByKey(product.getCodiceProdotto());
		System.out.println("SONO QUI IN AGGIORNAMENTO RETRIEVED: " + retrieved.toString());
		
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		switch(infoSelected) {
		
		case "DESCRIZIONE_EVIDENZA" :
			if(!ObjectProdotto.checkValidateTopDescrizione(updatedData))
				throw new FormatoTopDescrizioneException("La descrizione di presentazione non può essere vuota");
			
			if(retrieved.getTopDescrizione().equals(updatedData))
				throw new ProdottoAggiornatoException("Non è possibile associare la descrizione di presentazione inserita con il prodotto specificato.\nInserisci un'altra descrizione di presentazione.");
			productDAO.updateData(product.getCodiceProdotto(), "TOPDESCRIZIONE", updatedData);

			break;

		case "DESCRIZIONE_DETTAGLIATA" :

			if(!ObjectProdotto.checkValidateDettagli(updatedData))
				throw new FormatoDettagliException("La descrizione di dettaglio non può essere vuota");

			if(retrieved.getDettagli().equals(updatedData))
				throw new ProdottoAggiornatoException("Non è possibile associare la descrizione di dettaglio inserita con il prodotto specificato.\nInserisci un'altra descrizione di dettaglio.");
			productDAO.updateData(product.getCodiceProdotto(), "DETTAGLI", updatedData);
			break;

		case "MODELLO" :

			if(!ObjectProdotto.checkValidateModello(updatedData))
				throw new FormatoModelloException("Il modello deve contenere lettere e, eventualmente, numeri, spazi e trattini");

			if(retrieved.getModello().equals(updatedData))
				throw new ProdottoAggiornatoException("Non è possibile associare il modello inserito con il prodotto specificato.\nInserisci un altro modello.");
			productDAO.updateData(product.getCodiceProdotto(), "MODELLO", updatedData);
			break;

		case "MARCA" :

			if(!ObjectProdotto.checkValidateMarca(updatedData))
				throw new FormatoMarcaException("La marca del prodotto deve contenere lettere e, eventualmente, spazi");

			if(retrieved.getMarca().equals(updatedData))
				throw new ProdottoAggiornatoException("Non è possibile associare la marca inserita con il prodotto specificato.\nInserisci un'altra marca.");
			productDAO.updateData(product.getCodiceProdotto(), "MARCA", updatedData);
			break;

		case "CATEGORIA" :

			if(!ObjectProdotto.checkValidateCategoria(updatedData))
				throw new CategoriaProdottoException("La categoria inserita non esiste. Sono ammesse come categorie : TELEFONIA, PRODOTTI_ELETTRONICA, PICCOLI_ELETTRODOMESTICI, GRANDI_ELETTRODOMESTICI.");

			if(retrieved.getCategoriaAsString().equals(updatedData))
				throw new ProdottoAggiornatoException("Non è possibile associare la categoria inserita con il prodotto specificato.\nInserisci un'altra categoria.");
			productDAO.updateData(product.getCodiceProdotto(), "CATEGORIA", updatedData);
			break;

		case "SOTTOCATEGORIA" :

			if(!ObjectProdotto.checkValidateSottocategoria(updatedData))
				throw new SottocategoriaProdottoException("La sottocategoria specificata non esiste. Sono ammesse le seguenti sottocategorie: TABLET, SMARTPHONE, PC, SMARTWATCH.");

			if(retrieved.getSottocategoriaAsString().equals(updatedData))
				throw new ProdottoAggiornatoException("Non è possibile associare la sottocategoria inserita con il prodotto specificato.\nInserisci un'altro sottocategoria.");

			if(!ObjectProdotto.checkValidateAppartenenzaSottocategoria(product.getCategoriaAsString(), updatedData))
				throw new AppartenenzaSottocategoriaException("Errata sottocategoria.\n Se specificata, le sottocategorie ammissibili per un prodotto sono:\r\n"
						+ "-	TABLET e SMARTPHONE per la categoria TELEFONIA;\r\n"
						+ "-	PC e SMARTWATCH per la categoria PRODOTTI ELETTRONICA.\r\n"
						+ "");

			productDAO.updateData(product.getCodiceProdotto(), "SOTTOCATEGORIA", updatedData);

			break;

		default:
			throw new ErroreSpecificaAggiornamentoException("Seleziona un'informazione da modificare:\n-modello, \n-marca,"
					+ "\n-descrizione in evidenza, \n-descrizione dettagliata,\n-categoria,\n-sottocategoria.");
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
	 * @throws ErroreSpecificaAggiornamentoException 
	 * @throws FormatoVetrinaException 
	 * @throws ProdottoAggiornatoException 
	 * 	
	 */

	public Collection<ProxyProdotto> aggiornamentoProdottoInVetrina(Prodotto product, String information, String updatedData, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException, ErroreSpecificaAggiornamentoException, FormatoVetrinaException, ProdottoAggiornatoException{

		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		if(!information.equalsIgnoreCase("VETRINA"))
			throw new ErroreSpecificaAggiornamentoException("Per modificare la messa in evidenza di un prodotto per la vetrina virtuale, seleziona l'apposita scelta.");
		
		if(!ObjectProdotto.checkValidateVetrina(updatedData))
			throw new FormatoVetrinaException("Per aggiungere un prodotto in vetrina inserire TRUE,\nmentre per rimuovere un prodotto in vetrina inserire FALSE");
		
		double viewDouble = Double.parseDouble(updatedData);
        int updatedDataInt = (int) viewDouble;
		
        System.out.println("INVETRINA VALORE METODO : " + updatedDataInt);
		
		int productInVetrina = product.isInVetrina() ? 1 : 0; 
		/*if(updatedData.equalsIgnoreCase("TRUE"))
			updatedDataInt = 1;
		else
			updatedDataInt = 0;*/

		if(productInVetrina == updatedDataInt)
			throw new ProdottoAggiornatoException("Il valore di messa in evidenza del prodotto inserito è già associato.");
		else 
			productDAO.updateDataView(product.getCodiceProdotto(), updatedDataInt);
		System.out.println("VALORE DI PRODUCTINVETRINA: " + productInVetrina);
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
	 * @throws ErroreSpecificaAggiornamentoException 
	 * @throws ProdottoAggiornatoException 
	 * 
	 * */

	public Collection<ProxyProdotto> aggiornamentoPrezzoProdotto(Prodotto product, String information, String price, int page, int perPage) throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, PrezzoProdottoException, ErroreSpecificaAggiornamentoException, ProdottoAggiornatoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());

		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");

		if(!information.equalsIgnoreCase("PREZZO"))
			throw new ErroreSpecificaAggiornamentoException("Per modificare il prezzo di un prodotto, seleziona l'apposita scelta.");

		float priceFloat ;
		try {
			priceFloat = Float.parseFloat(price);

			if(priceFloat < 0.0)
				throw new PrezzoProdottoException("Il prezzo deve essere un numero con la virgola arrotondato in centesimi");

		}catch(NumberFormatException | NullPointerException e) {
			throw new PrezzoProdottoException("Il prezzo deve essere un numero con la virgola arrotondato in centesimi");
		}

		if(product.getPrezzo() == priceFloat)
			throw new ProdottoAggiornatoException("Il prezzo inserito è già associato al prodotto.");
		else
			productDAO.updatePrice(product.getCodiceProdotto(), priceFloat);

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
	 * @throws ProdottoAggiornatoException 
	 * @throws ErroreSpecificaAggiornamentoException 
	 * 
	 * */

	public Collection<ProxyProdotto> aggiornamentoDisponibilitàProdotto(Prodotto product, String information, String quantity, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, QuantitaProdottoException, ProdottoAggiornatoException, ErroreSpecificaAggiornamentoException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());

		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		if(!information.equalsIgnoreCase("QUANTITA"))
			throw new ErroreSpecificaAggiornamentoException("Per modificare la quantità disponibile di un prodotto, seleziona l'apposita scelta.");

		int quantityInt;

		try {
			double quantityDouble = Double.parseDouble(quantity);
	        quantityInt = (int) quantityDouble;
			
			if(quantityInt <= 0)
				throw new QuantitaProdottoException("La quantità di un prodotto disponibile deve essere almeno 1");
		
		}catch(NumberFormatException e) {
			throw new QuantitaProdottoException("La quantità di un prodotto disponibile deve essere almeno 1");
		}
		if(product.getQuantita() == quantityInt)
			throw new ProdottoAggiornatoException("Il numero di scorte inserito è già associato al prodotto.");
		else
			productDAO.updateQuantity(product.getCodiceProdotto(), quantityInt);
		
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
	 * @throws ErroreSpecificaAggiornamentoException 
	 * @throws ErroreTopImmagineException 
	 * */

	public Collection<ProxyProdotto> inserimentoTopImmagine(Prodotto product, String information, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreTopImmagineException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());

		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		if(!information.equalsIgnoreCase("TOP_IMMAGINE"))
			throw new ErroreSpecificaAggiornamentoException("Per modificare l'immagine di presentazione di un prodotto, seleziona l'apposita scelta.");
		
		if(image == null)
			throw new ErroreTopImmagineException("Inserire un'immagine di presentazione del prodotto");
			
		pcDAO.updateTopImage(product.getCodiceProdotto(), image);
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
	 * @throws ErroreSpecificaAggiornamentoException 
	 * @throws ErroreDettagliImmagineException 
	 * */

	public Collection<ProxyProdotto> inserimentoImmagineInGalleriaImmagini(Prodotto product, String information, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());
		
		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");

		if(!information.equalsIgnoreCase("AGGIUNTA_DETT_IMMAGINE"))
			throw new ErroreSpecificaAggiornamentoException("Per inserire un'immagine di dettaglio per un prodotto, seleziona l'apposita scelta.");
		
		if(image == null)
			throw new ErroreDettagliImmagineException("Inserire un'immagine di dettaglio del prodotto");
			
		pcDAO.addPhotoInGallery(product.getCodiceProdotto(), image);
		
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
	 * @throws ErroreSpecificaAggiornamentoException 
	 * @throws ErroreDettagliImmagineException 
	 * @throws DettagliImmagineNonPresenteException 
	 */

	public Collection<ProxyProdotto> cancellazioneImmagineInGalleria(Prodotto product, String information, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, IOException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException{
		ProxyProdotto retrieved = productDAO.doRetrieveProxyByKey(product.getCodiceProdotto());

		if(retrieved == null || !retrieved.isInCatalogo())
			throw new ProdottoNonInCatalogoException("Il prodotto che si intende modificare non esiste nel catalogo del negozio.");
		
		if(!information.equalsIgnoreCase("RIMOZIONE_DETT_IMMAGINE"))
			throw new ErroreSpecificaAggiornamentoException("Per rimuovere un'immagine di dettaglio per un prodotto, seleziona l'apposita scelta.");
		
		if(image == null)
			throw new ErroreDettagliImmagineException("Inserire un'immagine di dettaglio del prodotto");
			
		int imgCode = pcDAO.retrievePhotoInGallery(product.getCodiceProdotto(), image);
		
		if(imgCode == -1)
			throw new DettagliImmagineNonPresenteException("L'immagine di dettaglio specificata non è associata al prodotto. Scegliere un'altra immagine di dettaglio.");
		
		pcDAO.deletePhotoInGallery(product.getCodiceProdotto(), imgCode);
		return productDAO.doRetrieveAllExistent(null, page, perPage);
	}
}

