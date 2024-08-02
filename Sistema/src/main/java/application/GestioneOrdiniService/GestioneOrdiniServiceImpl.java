/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.GestioneOrdiniService;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import storage.GestioneOrdiniDAO.OrdineDAODataSource;

/**
 *
 * @author raffa
 */
public class GestioneOrdiniServiceImpl implements GestioneOrdiniService{
    @Override
    public Collection<ProxyOrdine> visualizzaOrdinidaSpedire(int page, int pr_pagina){
        try {
            OrdineDAODataSource odao = new OrdineDAODataSource();
            return odao.doRetrieveOrderToShip("DataOrdine", page, pr_pagina);
        } catch (SQLException ex) {
            Logger.getLogger(GestioneOrdiniServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    @Override
    public Collection<ProxyOrdine> visualizzaOrdiniSpediti(int page, int pr_pagina){
         try {
            OrdineDAODataSource odao = new OrdineDAODataSource();
            return odao.doRetrieveOrderShipped("DataOrdine", page, pr_pagina);
        } catch (SQLException ex) {
            Logger.getLogger(GestioneOrdiniServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
