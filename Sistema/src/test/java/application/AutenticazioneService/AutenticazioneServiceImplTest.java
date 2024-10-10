package application.AutenticazioneService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import storage.AutenticazioneDAO.UtenteDAODataSource;
import storage.AutenticazioneDAO.RuoloDAODataSource;
import storage.AutenticazioneDAO.ClienteDAODataSource;
import storage.AutenticazioneDAO.IndirizzoDAODataSource;
import application.AutenticazioneService.AutenticazioneException.UtenteInesistenteException;
import application.RegistrazioneService.ProxyUtente;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AutenticazioneServiceImplTest {

    private AutenticazioneServiceImpl autenticazioneService;
    private UtenteDAODataSource userDAO;
    private RuoloDAODataSource roleDAO;
    private ClienteDAODataSource profileDAO;
    private IndirizzoDAODataSource addressDAO;

    @BeforeEach
    public void setUp() {
        userDAO = Mockito.mock(UtenteDAODataSource.class);
        roleDAO = Mockito.mock(RuoloDAODataSource.class);
        profileDAO = Mockito.mock(ClienteDAODataSource.class);
        addressDAO = Mockito.mock(IndirizzoDAODataSource.class);
        autenticazioneService = new AutenticazioneServiceImpl(userDAO, roleDAO, profileDAO, addressDAO);
    }

    @Test
    public void testLogin_CredenzialiCorrette() throws SQLException, UtenteInesistenteException {
        // Arrange
        String username = "dorotea";
        String password = "dorotea0";
        
        
    		StringBuilder hashString = new StringBuilder();
    		try {
    			MessageDigest md = MessageDigest.getInstance("SHA-512");
    			byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
    			for (int i = 0; i < bytes.length; i++) {
    				hashString.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).toLowerCase(), 1, 3);
    			}
    		} catch (NoSuchAlgorithmException e) {
    			System.out.println(e);
    		}
    		
    		ProxyUtente expectedUser = new ProxyUtente(username, password, new ArrayList<>());
            
            
        
        // Simula il comportamento del DAO per restituire l'utente corretto
        Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(expectedUser);
        Mockito.when(roleDAO.doRetrieveByKey(username)).thenReturn(new ArrayList<>());

        // Act
        ProxyUtente actualUser = autenticazioneService.login(username, password);
        
        System.out.println("EXPECTED: " + expectedUser.getPassword());
        System.out.println("ACTUAL: " + actualUser.getPassword());
        System.out.println("actualUsername: "+ actualUser.getUsername());
        
        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
    }

    @Test
    public void testLogin_UtenteInesistente() throws SQLException {
        // Arrange
        String username = "nonExistentUser";
        String password = "anyPassword";

        // Simula il comportamento del DAO per non trovare l'utente
        Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UtenteInesistenteException.class, () -> {
            autenticazioneService.login(username, password);
        });
    }

    @Test
    public void testLogin_PasswordErrata() throws SQLException {
        // Arrange
        String username = "testUser";
        String correctPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        
        ProxyUtente existingUser = new ProxyUtente(username, correctPassword, new ArrayList<>());

        // Simula il comportamento del DAO per restituire l'utente corretto
        Mockito.when(userDAO.doRetrieveProxyUserByKey(username)).thenReturn(existingUser);
        
        // Act & Assert
        assertThrows(UtenteInesistenteException.class, () -> {
            autenticazioneService.login(username, wrongPassword);
        });
    }
}
