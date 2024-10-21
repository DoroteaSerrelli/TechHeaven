package application.GestioneCatalogoService;


import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;

import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNulloException;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.DettagliImmagineNonPresenteException;
import application.NavigazioneService.ProdottoException.ErroreDettagliImmagineException;
import application.NavigazioneService.ProdottoException.ErroreTopImmagineException;
import application.NavigazioneService.ProdottoException.FormatoCodiceException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoNomeException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.FormatoVetrinaException;
import application.NavigazioneService.ProdottoException.PrezzoProdottoException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.ProdottoDAODataSource;
import application.NavigazioneService.ProxyProdotto;

/**
 * L'interfaccia definisce le operazioni utili per la gestione del catalogo dei prodotti 
 * del negozio: 
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
 * @author raffy
 * @author Dorotea Serrelli
 */

public interface GestioneCatalogoService {
    
	/**
	 * Il metodo definisce il servizio di visualizzazione
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
	
	public Collection<ProxyProdotto> visualizzaCatalogo(int page, int perPage) throws SQLException, CategoriaProdottoException, SottocategoriaProdottoException;
	
	/**
	 * Il metodo definisce il servizio di aggiunta di
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
	
	public Collection<ProxyProdotto> aggiuntaProdottoInCatalogo(String codice, String nome, String marca, String modello, String topDescrizione, String dettagli, float prezzo, 
			int quantita, String categoria, String sottocategoria, boolean inCatalogo, boolean inVetrina, ProdottoDAODataSource productDAO, int page, int perPage) throws SQLException, ProdottoInCatalogoException, CategoriaProdottoException, SottocategoriaProdottoException, QuantitaProdottoException, FormatoNomeException, FormatoModelloException, FormatoMarcaException, PrezzoProdottoException, FormatoTopDescrizioneException, FormatoDettagliException, AppartenenzaSottocategoriaException, NumberFormatException, FormatoCodiceException;
	
	/**
	 * Il metodo definisce il servizio di rimozione di
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
	 * 
	 * @throws ProdottoNulloException : gestisce il caso in cui non venga specificato il 
	 * 									prodotto da rimuovere
	 * */
	
	public Collection<ProxyProdotto> rimozioneProdottoDaCatalogo(ProxyProdotto product, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNulloException;
	
	/**
	 * Il metodo definisce il servizio di aggiornamento delle seguenti specifiche del prodotto: 
	 * modello, marca, descrizione in evidenza, descrizione dettagliata, categoria, 
	 * sottocategoria, messa in evidenza.
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
	
	public Collection<ProxyProdotto> aggiornamentoSpecificheProdotto(Prodotto product, String infoSelected, String updatedData, int page, int perPage) throws ProdottoAggiornatoException, SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException ;
	
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
	 * 
	 * @throws ErroreSpecificaAggiornamentoException: ecceziona lanciata per gestire l'invocazione del metodo senza
	 * 												  aver selezionato "VETRINA" come informazione da modificare
	 * 
	 * @throws SottocategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 											di una sottocategoria
	 * 
	 * @throws CategoriaProdottoException : eccezione lanciata per gestire l'inserimento errato 
	 * 										di una categoria
	 * 
	 * @throws ProdottoNonInCatalogoException : eccezione lanciata per gestire la mancanza del prodotto product in catalogo
	 * @throws ProdottoAggiornatoException 
	 * 	
	 */
	
	public Collection<ProxyProdotto> aggiornamentoProdottoInVetrina(Prodotto product, String information, String updatedData, int page, int perPage) throws SQLException, ProdottoNonInCatalogoException, SottocategoriaProdottoException, CategoriaProdottoException, ErroreSpecificaAggiornamentoException, FormatoVetrinaException, ProdottoAggiornatoException;
	
	/**
	 * Il metodo definisce il servizio di aggiornamento del prezzo di un prodotto
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
	
	public Collection<ProxyProdotto> aggiornamentoPrezzoProdotto(Prodotto product, String information, String price, int page, int perPage) throws CategoriaProdottoException, SottocategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, PrezzoProdottoException, ErroreSpecificaAggiornamentoException, ProdottoAggiornatoException;
	
	
	/**
	 * Il metodo definisce il servizio di aggiornamento della quantità di scorte
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
	
	public Collection<ProxyProdotto> aggiornamentoDisponibilitàProdotto(Prodotto product, String information, String quantity, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, QuantitaProdottoException, ProdottoAggiornatoException, ErroreSpecificaAggiornamentoException;
		
	/**
	 * Il metodo definisce il servizio di aggiunta dell' immagine di presentazione ad un
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
	
	public Collection<ProxyProdotto> inserimentoTopImmagine(Prodotto product, String information, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreTopImmagineException;
		
	/**
	 * Il metodo definisce il servizio di aggiunta di un'immagine di dettaglio alla galleria immagini 
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
	
	public Collection<ProxyProdotto> inserimentoImmagineInGalleriaImmagini(Prodotto product, String information, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException;
	/**
	 * Il metodo definisce il servizio di rimozione di un'immagine di dettaglio alla galleria immagini 
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
	
	public Collection<ProxyProdotto> cancellazioneImmagineInGalleria(Prodotto product, String information, InputStream image, int page, int perPage) throws SottocategoriaProdottoException, CategoriaProdottoException, SQLException, ProdottoNonInCatalogoException, IOException, ErroreSpecificaAggiornamentoException, ErroreDettagliImmagineException, DettagliImmagineNonPresenteException;
}
