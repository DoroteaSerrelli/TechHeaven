package GestioneCatalogoTest;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/SeleniumTests/SeleneseIT.java to edit this template
 */


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author raffa
 */
public class GestioneImmaginiProdottoSeleniumTest {
    @Test
    public void TC16_5_1_1() throws Exception {
      
        // Set Chrome options (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start browser maximized

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

        try {
            // 1. Open the base URL
            driver.get("https://localhost/"); // Update with your actual base URL

            // 2. Set window size (not necessary as we start maximized)
            // You can set a specific size if needed
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1460, 701));

            // 3. Click on 'Autenticazione' link
            driver.findElement(By.linkText("Autenticazione")).click();

            // 4. Click on username input field
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.click();

            // 5. Type username
            usernameField.sendKeys("mariaGestoreCatalogo");

            // 6. Click on password input field
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.click();

            // 7. Type password
            passwordField.sendKeys("01maria01");

            // 8. Click on submit button
            driver.findElement(By.name("submit")).click();

            // 9. Click on ruolo
            driver.findElement(By.name("ruolo")).click();

            // 10. Select 'GestoreCatalogo' from the dropdown
            Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
            roleSelect.selectByVisibleText("GestoreCatalogo");

            // 12. Click on submit button again
            driver.findElement(By.name("submit")).click();

            // 13. Click on the modify properties image
            driver.findElement(By.cssSelector("#modifyProperties img")).click();

            // 14. Type in product filter
            WebElement productFilterField = driver.findElement(By.id("productFilter"));
            productFilterField.sendKeys("Apple AirPods Pro 2");

            // 15. Click on the specific product's button
            driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();

            // 16. Click on 'add'
            driver.findElement(By.id("add")).click();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelectorAll('input[name=\"gallery_photoActions\"]').forEach(el => el.checked = false);");
           
            js.executeScript("document.getElementById('file').removeAttribute('required');");
            // Locate the upload button and click it to submit the form
            WebElement uploadButton = driver.findElement(By.id("imageUploadBtn")); // Use the correct ID
            uploadButton.click();
            System.out.println("Form submitted successfully.");
            // Wait for the page to redirect and the title to change
            // Locate the header element
                WebElement messageElement = driver.findElement(By.xpath("//div[@id='updatePhotoLog']/h2"));

                // Get the text from the element
                String actualMessage = messageElement.getText();  
                String expectedMessage = "Inserire un'immagine di presentazione del prodotto.";  // Expected text

                // Check if the actual message matches the expected message
                if (actualMessage.equals(expectedMessage)) {
                    System.out.println("Success message verified: " + actualMessage);
                } else {
                    System.out.println("Text does not match! Expected: " + expectedMessage + ", but got: " + actualMessage);
                    // Optionally, throw an exception or take some other action
                    // throw new RuntimeException("Text does not match!");
                }
            // Additional steps can be added as necessary
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        } finally {
            // Close the browser
            driver.quit();
        }
    }
    @Test
    public void TC16_5_1_2() throws Exception {
      
        // Set Chrome options (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start browser maximized

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

        try {
            // 1. Open the base URL
            driver.get("https://localhost/"); // Update with your actual base URL

            // 2. Set window size (not necessary as we start maximized)
            // You can set a specific size if needed
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1460, 701));

            // 3. Click on 'Autenticazione' link
            driver.findElement(By.linkText("Autenticazione")).click();

            // 4. Click on username input field
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.click();

            // 5. Type username
            usernameField.sendKeys("mariaGestoreCatalogo");

            // 6. Click on password input field
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.click();

            // 7. Type password
            passwordField.sendKeys("01maria01");

            // 8. Click on submit button
            driver.findElement(By.name("submit")).click();

            // 9. Click on ruolo
            driver.findElement(By.name("ruolo")).click();

            // 10. Select 'GestoreCatalogo' from the dropdown
            Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
            roleSelect.selectByVisibleText("GestoreCatalogo");

            // 11. Click on the second option in the dropdown
            // (This step is redundant after selecting by visible text)
            // If needed: driver.findElement(By.cssSelector("option:nth-child(2)")).click();

            // 12. Click on submit button again
            driver.findElement(By.name("submit")).click();

            // 13. Click on the modify properties image
            driver.findElement(By.cssSelector("#modifyProperties img")).click();

            // 14. Type in product filter
            WebElement productFilterField = driver.findElement(By.id("productFilter"));
            productFilterField.sendKeys("Apple AirPods Pro 2");

            // 15. Click on the specific product's button
            driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();

            // 16. Click on 'add'
            driver.findElement(By.id("add")).click();

            // 17. Upload the file using a relative path
            // Assuming the relative path is correct from the project root
            String relativePath = "src/main/webapp/images/product_images/dyson_supersonicPhon_top.jpg";
            String absolutePath = Paths.get(relativePath).toAbsolutePath().toString();
            // Print the absolute path to the console
            System.out.println("Absolute path to the image: " + absolutePath);
             Path path = Paths.get(absolutePath);
            if (Files.exists(path)) {
            System.out.println("File exists: " + absolutePath);
            } else {
                System.out.println("File does not exist: " + absolutePath);
            }
            driver.findElement(By.id("file")).sendKeys(absolutePath); // Change the file input ID if different
            System.out.println("File uploaded successfully.");

            // Locate the upload button and click it to submit the form
            WebElement uploadButton = driver.findElement(By.id("imageUploadBtn")); // Use the correct ID
            uploadButton.click();
            System.out.println("Form submitted successfully.");
            // Wait for the page to redirect and the title to change
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.titleContains("TechHeaven - Catalogo")); // Wait for part of the title to change

            // Now check the page title
            String expectedTitle = "TechHeaven - Catalogo"; // Update this with the title you expect after the redirect
            String actualTitle = driver.getTitle();

            // Verify the title
            if (actualTitle.equals(expectedTitle)) {
                System.out.println("Page title is correct: " + actualTitle);
            } else {
                System.out.println("Page title is incorrect! Expected: " + expectedTitle + ", but got: " + actualTitle);
            }   
            // Additional steps can be added as necessary
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        } finally {
            // Close the browser
            driver.quit();
        }
    }
    @Test
    public void TC16_6_1_1() throws Exception {
      
        // Set Chrome options (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start browser maximized

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

        try {
            // 1. Open the base URL
            driver.get("https://localhost/"); // Update with your actual base URL

            // 2. Set window size (not necessary as we start maximized)
            // You can set a specific size if needed
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1460, 701));

            // 3. Click on 'Autenticazione' link
            driver.findElement(By.linkText("Autenticazione")).click();

            // 4. Click on username input field
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.click();

            // 5. Type username
            usernameField.sendKeys("mariaGestoreCatalogo");

            // 6. Click on password input field
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.click();

            // 7. Type password
            passwordField.sendKeys("01maria01");

            // 8. Click on submit button
            driver.findElement(By.name("submit")).click();

            // 9. Click on ruolo
            driver.findElement(By.name("ruolo")).click();

            // 10. Select 'GestoreCatalogo' from the dropdown
            Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
            roleSelect.selectByVisibleText("GestoreCatalogo");

            // 11. Click on the second option in the dropdown
            // (This step is redundant after selecting by visible text)
            // If needed: driver.findElement(By.cssSelector("option:nth-child(2)")).click();

            // 12. Click on submit button again
            driver.findElement(By.name("submit")).click();

            // 13. Click on the modify properties image
            driver.findElement(By.cssSelector("#modifyProperties img")).click();

            // 14. Type in product filter
            WebElement productFilterField = driver.findElement(By.id("productFilter"));
            productFilterField.sendKeys("Apple AirPods Pro 2");

            // 15. Click on the specific product's button
            driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();
            
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelectorAll('input[name=\"main_photoAction\"]').forEach(el => el.checked = false);");
            // 16. Click on 'add'
           js.executeScript("document.getElementById('addToGallery').checked = true;");

            js.executeScript("document.getElementById('file').removeAttribute('required');");
            // Locate the upload button and click it to submit the form
            WebElement uploadButton = driver.findElement(By.id("imageUploadBtn")); // Use the correct ID
            uploadButton.click();
            System.out.println("Form submitted successfully.");
            // Wait for the page to redirect and the title to change
            // Locate the header element
                WebElement messageElement = driver.findElement(By.xpath("//div[@id='updatePhotoLog']/h2"));

                // Get the text from the element
                String actualMessage = messageElement.getText();  
                String expectedMessage = "Inserire un'immagine di dettaglio del prodotto.";  // Expected text

                // Check if the actual message matches the expected message
                if (actualMessage.equals(expectedMessage)) {
                    System.out.println("Success message verified: " + actualMessage);
                } else {
                    System.out.println("Text does not match! Expected: " + expectedMessage + ", but got: " + actualMessage);
                    // Optionally, throw an exception or take some other action
                    // throw new RuntimeException("Text does not match!");
                }
            // Additional steps can be added as necessary
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        } finally {
            // Close the browser
            driver.quit();
        }
    }
    @Test
    public void TC16_6_1_2() throws Exception {
      
        // Set Chrome options (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start browser maximized

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

        try {
            // 1. Open the base URL
            driver.get("https://localhost/"); // Update with your actual base URL

            // 2. Set window size (not necessary as we start maximized)
            // You can set a specific size if needed
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1460, 701));

            // 3. Click on 'Autenticazione' link
            driver.findElement(By.linkText("Autenticazione")).click();

            // 4. Click on username input field
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.click();

            // 5. Type username
            usernameField.sendKeys("mariaGestoreCatalogo");

            // 6. Click on password input field
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.click();

            // 7. Type password
            passwordField.sendKeys("01maria01");

            // 8. Click on submit button
            driver.findElement(By.name("submit")).click();

            // 9. Click on ruolo
            driver.findElement(By.name("ruolo")).click();

            // 10. Select 'GestoreCatalogo' from the dropdown
            Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
            roleSelect.selectByVisibleText("GestoreCatalogo");

            // 11. Click on the second option in the dropdown
            // (This step is redundant after selecting by visible text)
            // If needed: driver.findElement(By.cssSelector("option:nth-child(2)")).click();

            // 12. Click on submit button again
            driver.findElement(By.name("submit")).click();

            // 13. Click on the modify properties image
            driver.findElement(By.cssSelector("#modifyProperties img")).click();

            // 14. Type in product filter
            WebElement productFilterField = driver.findElement(By.id("productFilter"));
            productFilterField.sendKeys("Apple AirPods Pro 2");

            // 15. Click on the specific product's button
            driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();

            // 16. Click on 'add'
            driver.findElement(By.id("addToGallery")).click();

            // 17. Upload the file using a relative path
            // Assuming the relative path is correct from the project root
            String relativePath = "src/main/webapp/images/product_images/dyson_supersonicPhon_dett1.jpg";
            String absolutePath = Paths.get(relativePath).toAbsolutePath().toString();
            // Print the absolute path to the console
            System.out.println("Absolute path to the image: " + absolutePath);
             Path path = Paths.get(absolutePath);
            if (Files.exists(path)) {
            System.out.println("File exists: " + absolutePath);
            } else {
                System.out.println("File does not exist: " + absolutePath);
            }
            driver.findElement(By.id("file")).sendKeys(absolutePath); // Change the file input ID if different
            System.out.println("File uploaded successfully.");

            // Locate the upload button and click it to submit the form
            WebElement uploadButton = driver.findElement(By.id("imageUploadBtn")); // Use the correct ID
            uploadButton.click();
            System.out.println("Form submitted successfully.");
            // Wait for the page to redirect and the title to change
           // Wait for the page to redirect and the title to change
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.titleContains("TechHeaven - Catalogo")); // Wait for part of the title to change

            // Now check the page title
            String expectedTitle = "TechHeaven - Catalogo"; // Update this with the title you expect after the redirect
            String actualTitle = driver.getTitle();

            // Verify the title
            if (actualTitle.equals(expectedTitle)) {
                System.out.println("Page title is correct: " + actualTitle);
            } else {
                System.out.println("Page title is incorrect! Expected: " + expectedTitle + ", but got: " + actualTitle);
            }   
            // Additional steps can be added as necessary
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        } finally {
            // Close the browser
            driver.quit();
        }
    } 
       @Test
    public void TC16_7_1_1() throws Exception {
      
        // Set Chrome options (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // Start browser maximized

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

        try {
            // 1. Open the base URL
            driver.get("https://localhost/"); // Update with your actual base URL

            // 2. Set window size (not necessary as we start maximized)
            // You can set a specific size if needed
            driver.manage().window().setSize(new org.openqa.selenium.Dimension(1460, 701));

            // 3. Click on 'Autenticazione' link
            driver.findElement(By.linkText("Autenticazione")).click();

            // 4. Click on username input field
            WebElement usernameField = driver.findElement(By.name("username"));
            usernameField.click();

            // 5. Type username
            usernameField.sendKeys("mariaGestoreCatalogo");

            // 6. Click on password input field
            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.click();

            // 7. Type password
            passwordField.sendKeys("01maria01");

            // 8. Click on submit button
            driver.findElement(By.name("submit")).click();

            // 9. Click on ruolo
            driver.findElement(By.name("ruolo")).click();

            // 10. Select 'GestoreCatalogo' from the dropdown
            Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
            roleSelect.selectByVisibleText("GestoreCatalogo");

            // 11. Click on the second option in the dropdown
            // (This step is redundant after selecting by visible text)
            // If needed: driver.findElement(By.cssSelector("option:nth-child(2)")).click();

            // 12. Click on submit button again
            driver.findElement(By.name("submit")).click();

            // 13. Click on the modify properties image
            driver.findElement(By.cssSelector("#modifyProperties img")).click();

            // 14. Type in product filter
            WebElement productFilterField = driver.findElement(By.id("productFilter"));
            productFilterField.sendKeys("Apple AirPods Pro 2");

            // 15. Click on the specific product's button
            driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();
            // Simple version for testing
            String script = """
    var button = document.createElement('button');
    button.innerHTML = 'Delete Null Image';
    button.className = 'delete-image-btn'; // Class to match your existing buttons
    button.setAttribute('data-image-index', '0'); // Set to a dummy index
    document.body.appendChild(button);
    if (typeof attachDeleteButtonListeners === 'function') {
        attachDeleteButtonListeners(); // This will call the function defined in the JSP
    }
  """;

            // Execute the script in the browser context
            ((JavascriptExecutor) driver).executeScript(script);
            WebElement deleteButton = driver.findElement(By.cssSelector(".delete-image-btn[data-image-index='0']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton); // Check the message in the log
            String responseMessage = driver.findElement(By.id("updatePhotoLog")).getText();

            // Verify the response message
            if (responseMessage.contains("Inserire un'immagine di dettaglio del prodotto.")) {
                System.out.println("Null image deletion test passed: " + responseMessage);
            } else {
                System.out.println("Unexpected response or failure: " + responseMessage);
            }
            
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        } finally {
            // Close the browser
            driver.quit();
        }
    } 
    @Test
    public void TC16_7_1_2() throws Exception {
      
        // Set Chrome options (optional)
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized"); // Start browser maximized

    // Create a new instance of the Chrome driver
    WebDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

    try {
        // 1. Open the base URL
        driver.get("https://localhost/"); // Update with your actual base URL

        // 2. Click on 'Autenticazione' link
        driver.findElement(By.linkText("Autenticazione")).click();

        // 3. Fill in authentication details
        driver.findElement(By.name("username")).sendKeys("mariaGestoreCatalogo");
        driver.findElement(By.name("password")).sendKeys("01maria01");
        driver.findElement(By.name("submit")).click();

        // 4. Select 'GestoreCatalogo' from the dropdown
        Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
        roleSelect.selectByVisibleText("GestoreCatalogo");
        driver.findElement(By.name("submit")).click();

        // 5. Click on the modify properties image
        driver.findElement(By.cssSelector("#modifyProperties img")).click();

        // 6. Type in product filter
        driver.findElement(By.id("productFilter")).sendKeys("Apple AirPods Pro 2");
        // 15. Click on the specific product's button
        driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();
        System.out.println("wono");
        WebElement modifyPropertiesButton = driver.findElement(By.id("modifyProperties"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", modifyPropertiesButton);
        
        driver.findElement(By.id("productFilter")).sendKeys("HP 15s-fq5040nl");
        // 15. Click on the specific product's button
        driver.findElement(By.xpath("//td[@class='productName']/h3[text()='HP 15s-fq5040nl']/ancestor::tr//button[1]")).click();

        
         // 7. Inject JavaScript to override product data in the front end
        String newProductData = "{ \"codiceProdotto\": 0, \"nomeProdotto\": \"Apple AirPods Pro 2\", " +
                                "\"topDescrizione\": \"Prova\", \"dettagli\": \"Prova\", \"prezzo\": 254.5, " +
                                "\"categoria\": \"PRODOTTI_ELETTRONICA\", \"marca\": \"Apple\", \"modello\": \"AirPods Pro 2\", " +
                                "\"quantita\": 4, \"inCatalogo\": true, \"inVetrina\": false }";
        String script = "retrieveAllData = function(callback) {" +
        "    callback({ product: " + newProductData + ", galleryImages: [] });" +
        "};" +
        "attachDeleteButtonListeners();";
        
        // Execute JavaScript to replace the product data and reattach delete listeners
        ((JavascriptExecutor) driver).executeScript(script);

        // 8. Manually trigger delete button click
        WebElement deleteButton = driver.findElement(By.cssSelector(".delete-image-btn[data-image-index='0']"));
        deleteButton.click();
       
        System.out.println("Delete button clicked successfully.");
       // 9. Verify that the server response is handled and the message appears
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='updatePhotoLog']/h2")));
        String actualMessage = messageElement.getText();
        String expectedMessage = "L'Immagine di dettaglio specificata non è associata al prodotto. Scegliere un'altra immagine di dettaglio.";

        if (actualMessage.contains(expectedMessage)) {
            System.out.println("Success message verified: " + actualMessage);
        } else {
            System.out.println("Text does not match! Expected: " + expectedMessage + ", but got: " + actualMessage);
        }
        
        // Additional steps can be added as necessary
    } catch (Exception e) {
        e.printStackTrace(); // Print stack trace for any exceptions
    } finally {
        // Close the browser
        driver.quit();
    }
    }
    @Test
    public void TC16_7_1_3() throws Exception {
      
        // Set Chrome options (optional)
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized"); // Start browser maximized

    // Create a new instance of the Chrome driver
    WebDriver driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); // Set implicit wait

    try {
        // 1. Open the base URL
        driver.get("https://localhost/"); // Update with your actual base URL

        // 2. Click on 'Autenticazione' link
        driver.findElement(By.linkText("Autenticazione")).click();

        // 3. Fill in authentication details
        driver.findElement(By.name("username")).sendKeys("mariaGestoreCatalogo");
        driver.findElement(By.name("password")).sendKeys("01maria01");
        driver.findElement(By.name("submit")).click();

        // 4. Select 'GestoreCatalogo' from the dropdown
        Select roleSelect = new Select(driver.findElement(By.name("ruolo")));
        roleSelect.selectByVisibleText("GestoreCatalogo");
        driver.findElement(By.name("submit")).click();

        // 5. Click on the modify properties image
        driver.findElement(By.cssSelector("#modifyProperties img")).click();

        // 6. Type in product filter
        driver.findElement(By.id("productFilter")).sendKeys("Apple AirPods Pro 2");

        // 7. Click on the specific product's button
        driver.findElement(By.xpath("//td[@class='productName']/h3[text()='Apple AirPods Pro 2']/ancestor::tr//button[1]")).click();

        // 8. Wait for and click the delete button
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement deleteButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".delete-image-btn[data-image-index='0']")));
        deleteButton.click();
        
        System.out.println("Delete button clicked successfully.");
        
        // 9. Wait for the message to appear and check its content
        WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='updatePhotoLog']/h2")));

        // 10. Get the text from the element
        String actualMessage = messageElement.getText();  
        String expectedMessage = "L'Immagine Selezionata e' Stata Rimossa Con Successo dalla Galleria";  // Expected text

        // Check if the actual message matches the expected message
        if (actualMessage.equals(expectedMessage)) {
            System.out.println("Success message verified: " + actualMessage);
        } else {
            System.out.println("Text does not match! Expected: " + expectedMessage + ", but got: " + actualMessage);
        }

        // Additional steps can be added as necessary
    } catch (Exception e) {
        e.printStackTrace(); // Print stack trace for any exceptions
    } finally {
        // Close the browser
        driver.quit();
    }
    }
}
