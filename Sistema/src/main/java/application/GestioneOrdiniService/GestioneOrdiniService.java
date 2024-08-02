/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.GestioneOrdiniService;

import java.util.Collection;

/**
 *
 * @author raffa
 */
public interface GestioneOrdiniService {
    public Collection<ProxyOrdine> visualizzaOrdinidaSpedire(int page, int pr_pagina);
    public Collection<ProxyOrdine> visualizzaOrdiniSpediti(int page, int pr_pagina);
}
