<div style="display: flex; justify-content: center; align-items: center; border-bottom:2px solid gray">
        <img id="logo" src="img/logo.png" alt="" style="padding: 20px; width: 45%; height: 45%;">
</div>

# TechHeaven
Progetto preparato per il corso di Ingegneria del Software di un sistema software per la gestione degli ordini del negozio "TechHeaven - Il paradiso digitale".

## Scopo del progetto
L’obiettivo del progetto è la creazione di una piattaforma Web-based che sarà di supporto per il negozio “TechHeaven – Il paradiso digitale”, specializzato nella vendita di prodotti elettronici, elettrodomestici, telefonia.
Lo scopo principale del progetto è quello di fungere da strumento per gli acquisti dei clienti presso il negozio online, la gestione automatizzata degli ordini commissionati, per la catalogazione dei prodotti in vendita.

## Stato del progetto
Rilascio Deliverables e Sistema software

## Documentazione di progetto
Per la documentazione del progetto si consulti la directory Deliverables: in essa sono contenuti i documenti di progetto da rilasciare al committente (RAD, SDD, ODD, Database Design Document, documenti per Testing).
Per la documentazione Javadoc del progetto, si consulti il seguente link: https://doroteaserrelli.github.io/TechHeavenDocumentation/

### *Come iniziare*
#### Prerequisiti
- Java (versione consigliata: [8])
- Maven (versione consigliata: [3.8.6])
- Apache Tomcat (versione consigliata: [9.0])

#### Avvio  
Per avviare questa web application sulla tua macchina, segui i passaggi riportati di seguito:

1. **Clonare il repository**
   Prima di tutto, clona il repository del progetto sulla tua macchina locale utilizzando Git:

   ```bash
   git clone https://github.com/DoroteaSerrelli/TechHeaven.git

2. **Compilare il progetto con Maven**
   Questo comando scaricherà tutte le dipendenze e compilerà il progetto.

   ```bash
   cd TechHeaven
   mvn clean install

3. **Configurazione di Apache Tomcat**
   Poiché si tratta di una web application Java, il software deve essere deployata su un server web come Apache Tomcat.
   Segui i seguenti passaggi per configurare Tomcat:
        - Scarica e installa Apache Tomcat dal sito ufficiale: Tomcat Downloads.
        - Una volta installato Tomcat, apri il file di configurazione server.xml, che si trova nella cartella conf della tua installazione di Tomcat.

4. **Modificare il file server.xml per HTTPS**
   Modifica il file server.xml affinché supporti HTTPS, fornendo i dati del certificato keystore.jks presente nel branch Implementazione_Sistema/Semilavorati.

5. **Deploy dell'applicazione**
   Dopo aver configurato Tomcat, copia il file .war (il pacchetto web del progetto) generato da Maven nella cartella webapps di Tomcat.
   Il .war viene automaticamente deployato al riavvio di Tomcat, ma puoi anche farlo manualmente.

6. **Avviare il server**
   Avvia Tomcat eseguendo il comando:

   ```bash
   cd /path/to/tomcat/bin
   ./startup.sh  # Su Linux/macOS
   startup.bat   # Su Windows
   ```
   Tomcat ora avvierà il server sulla porta 8443 con HTTPS.

7. **Accedere alla web application**
   Una volta avviato Tomcat, apri il tuo browser e accedi all'applicazione all'indirizzo:

   ```bash
   https://localhost:8443/TechHeaven

La web application dovrebbe essere ora in esecuzione nel tuo browser.

*Note finali*
* Assicurati che la porta 8443 non sia bloccata da firewall o altre impostazioni di rete.
* Essendo il certificato SSL autofirmato, potresti ricevere un avviso di sicurezza nel browser. Puoi ignorarlo e procedere con il caricamento dell'applicazione.
