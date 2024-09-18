/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/ServletListener.java to edit this template
 */
package application.NavigazioneControl;

import application.NavigazioneService.NavigazioneServiceImpl;

import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author raffa
 */
public class NewServletListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // This is the code you want to execute
        String categoria = "TELEFONIA"; 
        System.out.println("SONO QUI");
        try {
			sce.getServletContext().setAttribute("telefoni", PaginationUtils.performPagination(new NavigazioneServiceImpl(), categoria, 1, 6, "menu"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Assuming request and response objects are not available here,
        // so you might need to adjust this part according to your requirement
        //PaginationUtils.setPaginationAttributes(request, searchResult, categoria, 10);

        // Storing searchResult in ServletContext for later retrieval
        //DOPPIO FETCH ???? PAGINA INIZIALE
        categoria = "GRANDI_ELETTRODOMESTICI"; 
        System.out.println("SONO QUIx2");
        try {
			sce.getServletContext().setAttribute("gr_elettr", PaginationUtils.performPagination(new NavigazioneServiceImpl(), categoria, 1, 6, "menu"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Storing searchResult in ServletContext for later retrieval
        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
