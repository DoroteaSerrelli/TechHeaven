package application.GestioneCatalogoControl;

import application.GestioneCatalogoService.CatalogoException;
import application.GestioneCatalogoService.CatalogoException.ErroreSpecificaAggiornamentoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoAggiornatoException;
import application.GestioneCatalogoService.CatalogoException.ProdottoNonInCatalogoException;
import application.GestioneCatalogoService.GestioneCatalogoServiceImpl;
import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProdottoException;
import application.NavigazioneService.ProdottoException.AppartenenzaSottocategoriaException;
import application.NavigazioneService.ProdottoException.CategoriaProdottoException;
import application.NavigazioneService.ProdottoException.FormatoDettagliException;
import application.NavigazioneService.ProdottoException.FormatoMarcaException;
import application.NavigazioneService.ProdottoException.FormatoModelloException;
import application.NavigazioneService.ProdottoException.FormatoTopDescrizioneException;
import application.NavigazioneService.ProdottoException.QuantitaProdottoException;
import application.NavigazioneService.ProdottoException.SottocategoriaProdottoException;
import storage.NavigazioneDAO.PhotoControl;
import storage.NavigazioneDAO.ProdottoDAODataSource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * Servlet per la modifica dei prodotti nel catalogo.
 * 
 * Questa servlet gestisce le operazioni di aggiornamento delle informazioni 
 * relative ai prodotti.
 * 
 * @author raffa
 */

public class ModificaInfoProdottoController extends HttpServlet {

	/**
	 * Serial Version UID per la serializzazione della servlet.
	 */
	private static final long serialVersionUID = 1L;
	
	private DataSource ds;
	private ProdottoDAODataSource productDAO;
	private PhotoControl photoControl;
	private GestioneCatalogoServiceImpl gcs;

	private static int pr_pagina = 50; 

	/**
	 * Inizializza la servlet, configurando photoControl, productDAO e gcs.
	 *
	 * @throws ServletException : se si verifica un errore durante l'inizializzazione
	 */

	@Override
	public void init() throws ServletException {
		ds = new DataSource();
		photoControl = new PhotoControl(ds);
		try {
			productDAO = new ProdottoDAODataSource(new DataSource(), photoControl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gcs = new GestioneCatalogoServiceImpl(productDAO, photoControl);

	}

	/**
	 * Gestisce il metodo HTTP GET per reindirizzare l'utente alla pagina 
	 * di aggiornamento delle informazioni del prodotto.
	 *
	 * @param request: la richiesta
	 * @param response : la risposta
	 * @throws ServletException: se si verifica un errore specifico del servlet
	 * @throws IOException: se si verifica un errore di I/O
	 */

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = null;

		if(request.getParameter("action") != null){
			action = request.getParameter("action");
			request.getSession().setAttribute("action", action);
		}else
			request.getSession().setAttribute("action", "modify");

		response.sendRedirect(request.getContextPath()+"/UpdateProductInfos");
	}

	/**
	 * Gestisce il metodo HTTP POST per aggiornare le informazioni del prodotto.
	 * 
	 * Questo metodo legge i dati JSON dalla richiesta, aggiorna le informazioni del prodotto
	 * e restituisce una risposta JSON al client.
	 *
	 * @param request : la richiesta
	 * @param response: la risposta
	 * 
	 * @throws ServletException: se si verifica un errore specifico
	 * @throws IOException: se si verifica un errore di I/O
	 */

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		StringBuilder jsonBuilder = new StringBuilder();

		try (BufferedReader reader = request.getReader()) {
			String line;

			while ((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}
		}

		String jsonData = jsonBuilder.toString();

		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

		JsonObject modifiedData = jsonObject.getAsJsonObject("modifiedData");
		JsonObject originalProductDetails = jsonObject.getAsJsonObject("originalProductDetails");

		// Chiamata al metodo per aggiornare le informazioni del prodotto

		String jsonResponse;
		try {
			jsonResponse = updateProductInfos(modifiedData, originalProductDetails, request, response);
			// Imposta il tipo di contenuto e la codifica della risposta
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonResponse);

		} catch (ErroreSpecificaAggiornamentoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProdottoAggiornatoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatoTopDescrizioneException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatoDettagliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatoModelloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatoMarcaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AppartenenzaSottocategoriaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SottocategoriaProdottoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CategoriaProdottoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProdottoNonInCatalogoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QuantitaProdottoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Aggiorna le informazioni del prodotto in base ai dati forniti.
	 *
	 * @param modifiedDataJson: i dati modificati del prodotto in formato JSON
	 * @param originalProductJson: i dettagli originali del prodotto in formato JSON
	 * @param request: la richiesta
	 * @param response: la risposta 
	 * 
	 * @return una stringa contenente la risposta JSON
	 * 
	 * @throws IOException: se si verifica un errore di I/O
	 * @throws ErroreSpecificaAggiornamentoException: se si è specificata non correttamente l'informazione
	 * 												  del prodotto da modificare
	 * 
	 * @throws ProdottoAggiornatoException: se il prodotto possiede per l'informazione 
	 * 										da modificare già il dato aggiornato
	 * 
	 * @throws FormatoTopDescrizioneException: se la descrizione di presentazione è vuota
	 * @throws FormatoDettagliException: se la descrizione di dettaglio è vuota
	 * @throws FormatoModelloException: se il formato del modello è errato
	 * @throws FormatoMarcaException: se il formato della marca è errato
	 * @throws AppartenenzaSottocategoriaException: se c'è un problema con l'appartenenza di una sottocategoria
	 * 												alla categoria di appartenenza del prodotto
	 * @throws SQLException 
	 * @throws QuantitaProdottoException 
	 * @throws ProdottoNonInCatalogoException 
	 * @throws CategoriaProdottoException 
	 * @throws SottocategoriaProdottoException 
	 */

	private String updateProductInfos(JsonObject modifiedDataJson, JsonObject originalProductJson, HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ErroreSpecificaAggiornamentoException, ProdottoAggiornatoException, FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException, SottocategoriaProdottoException, CategoriaProdottoException, ProdottoNonInCatalogoException, QuantitaProdottoException, SQLException {

		String outputMessage = "";
		Gson gson = new Gson();
		Map<String, String> responseMap = new HashMap<>();

		// Deserializza il JSON del prodotto originale in un oggetto Prodotto
		Prodotto originalProduct = null;
		if (originalProductJson != null && !originalProductJson.isJsonNull()) {
			originalProduct = gson.fromJson(originalProductJson, Prodotto.class);
		}

		// Aggiorna i dettagli del prodotto se presenti nei dati modificati

		if (modifiedDataJson != null && !modifiedDataJson.isJsonNull()) {

			Map<String, Map<String, String>> modifiedData = gson.fromJson(modifiedDataJson, Map.class);

			if (modifiedData.containsKey("productDetails")) {
				Map<String, String> productDetails = modifiedData.get("productDetails");
				outputMessage += updateProductDetail("MARCA", "marca", originalProduct, productDetails);
				outputMessage += updateProductDetail("MODELLO", "modello", originalProduct, productDetails);
			}

			if (modifiedData.containsKey("descriptions")) {
				Map<String, String> descriptions = modifiedData.get("descriptions");
				outputMessage += updateProductDetail("DESCRIZIONE_EVIDENZA", "topDescrizione", originalProduct, descriptions);
				outputMessage += updateProductDetail("DESCRIZIONE_DETTAGLIATA", "dettagli", originalProduct, descriptions);
			}


			if (modifiedData.containsKey("pricing")) {
				Map<String, String> pricing = modifiedData.get("pricing");
				String price = pricing.get("price");


				try {

					gcs.aggiornamentoPrezzoProdotto(originalProduct, "PREZZO",price, 1, pr_pagina);

				} catch (ProdottoException.CategoriaProdottoException | ProdottoException.SottocategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | ProdottoException.PrezzoProdottoException ex) {
					request.setAttribute("error", ex.getMessage());
					Logger.getLogger(ModificaInfoProdottoController.class.getName()).log(Level.SEVERE, null, ex);
				}

			}

			if (modifiedData.containsKey("quantita")) { 

				Map<String, String> quantitàStr = modifiedData.get("quantita");
				String quantità = quantitàStr.get("quantita");
				gcs.aggiornamentoDisponibilitàProdotto(originalProduct, "QUANTITA", quantità, 1, pr_pagina);
				outputMessage += "Aggiornamento Quantità Avvenuto con Successo!";

			}
		}

		// Crea una mappa per memorizzare la risposta JSON
		responseMap.put("message", outputMessage);
		responseMap.put("redirectUrl", request.getContextPath() + "/Catalogo");

		// Conversione della mappa in una stringa JSON
		return gson.toJson(responseMap);

	}


	/**
	 * Metodo helper per aggiornare i dettagli del prodotto in base al confronto.
	 *
	 * @param field: il campo da aggiornare
	 * @param key: la chiave nel JSON modificato
	 * @param originalProduct: il prodotto originale
	 * @param modifiedDetails: le specifiche del prodotto modificate
	 * 
	 * @return un messaggio di conferma o errore
	 * 
	 * @throws FormatoTopDescrizioneException: se la descrizione di presentazione è vuota
	 * @throws FormatoDettagliException: se la descrizione di dettaglio è vuota
	 * @throws FormatoModelloException: se il formato del modello è errato
	 * @throws FormatoMarcaException: se il formato della marca è errato
	 * @throws AppartenenzaSottocategoriaException: se c'è un problema con l'appartenenza di una sottocategoria
	 * 												alla categoria di appartenenza del prodotto
	 * */

	private String updateProductDetail(String field, String key, Prodotto originalProduct, 
			Map<String, String> modifiedDetails) throws FormatoTopDescrizioneException, FormatoDettagliException, FormatoModelloException, FormatoMarcaException, AppartenenzaSottocategoriaException {

		String modifiedValue = modifiedDetails.get(key);
		if (originalProduct != null) {
			Object originalValue = getFieldByName(field, originalProduct); // Restituisce Object per gestire differenti tipi di dati da modificare

			if (!originalValue.equals(convertToCorrectType(modifiedValue, originalValue))) {
				try {
					gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);
					return "Field aggiornata con successo:"+field+" Nuovo Valore"+modifiedValue+ "\n";

				} catch (CatalogoException.ProdottoAggiornatoException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | CatalogoException.ErroreSpecificaAggiornamentoException ex) {
					Logger.getLogger(ModificaInfoProdottoController.class.getName()).log(Level.SEVERE, null, ex);
					System.out.println(ex);
					return ex.getMessage();                  
				}
			}
		} else {
			if (modifiedValue != null && !modifiedValue.isEmpty()) {
				try {
					gcs.aggiornamentoSpecificheProdotto(originalProduct, field, modifiedValue, 1, pr_pagina);

					return "Field aggiornata con successo:"+field+" Nuovo Valore"+modifiedValue+ "\n";
				} catch (CatalogoException.ProdottoAggiornatoException | ProdottoException.SottocategoriaProdottoException | ProdottoException.CategoriaProdottoException | SQLException | CatalogoException.ProdottoNonInCatalogoException | CatalogoException.ErroreSpecificaAggiornamentoException ex) {
					Logger.getLogger(ModificaInfoProdottoController.class.getName()).log(Level.SEVERE, null, ex);
					return ex.getMessage();  
				}
			}
		}
		return "Non è stato possibile aggiornare:"+field+" \n";
	}

	/**
	 * Converte il valore modificato nello stesso tipo del valore originale.
	 *
	 * @param modifiedValue: il valore modificato
	 * @param originalValue: il valore originale
	 * @return il valore convertito nel tipo corretto
	 */

	private Object convertToCorrectType(String modifiedValue, Object originalValue) {
		if (originalValue instanceof Integer) {
			return Integer.parseInt(modifiedValue);

		} else if (originalValue instanceof Double) {
			return Double.parseDouble(modifiedValue);

		} else {
			return modifiedValue; //Si assume che sia una stringa
		}
	}            

	/**
	 * Metodo di supporto per selezionare il campo da aggiornare 
	 * in base alla stringa passata come parametro.
	 *
	 * @param field: il campo da recuperare
	 * @param prod: il prodotto da cui recuperare il campo
	 * @return il valore del campo corrispondente
	 */

	private Object getFieldByName(String field, Prodotto prod){
		
		switch(field){ 
		
		case "MARCA":    
			return prod.getMarca();

		case "MODELLO": 
			return prod.getModello();

		case "DESCRIZIONE_EVIDENZA":
			return prod.getTopDescrizione();

		case "DESCRIZIONE_DETTAGLIATA":
			return prod.getDettagli();

		case "CATEGORIA":
			return prod.getCategoriaAsString();

		case "SOTTOCATEGORIA":
			return prod.getSottocategoriaAsString();
			
		default:
			return null;
		} 
	}
}