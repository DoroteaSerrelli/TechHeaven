/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.GestioneCatalogoService;

import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.SearchResult;
import java.util.Collection;

/**
 *
 * @author raffy
 */
public interface GestioneCatalogoService {
    /**
	 * Questo metodo si occupa di fornire l'elenco dei prodotti
	 * presenti nel catalogo.
	 * @return l'insieme dei prodotti del catalogo
	 * */	
	public Collection<Prodotto> visualizzaCatalogo(int page, int pr_pagina);
}
