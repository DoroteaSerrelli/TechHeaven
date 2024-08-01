/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.GestioneCatalogoService;

import application.NavigazioneService.Prodotto;
import application.NavigazioneService.ProxyProdotto;
import application.NavigazioneService.SearchResult;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import storage.NavigazioneDAO.ProdottoDAODataSource;

/**
 *
 * @author raffa
 */
public class GestioneCatalogoServiceImpl implements GestioneCatalogoService{   
    public Collection<Prodotto> visualizzaCatalogo(int page, int pr_pagina){
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
    }
}

