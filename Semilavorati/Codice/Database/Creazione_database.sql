DROP SCHEMA IF EXISTS techHeaven;
CREATE SCHEMA techHeaven;
USE techHeaven;

CREATE TABLE Cliente_DatiPersonali(
email varchar(50) NOT NULL,
nome varchar(40) NOT NULL,
cognome varchar(50) NOT NULL,
sesso enum('M', 'F') NOT NULL,
telefono char(12) NOT NULL,

PRIMARY KEY(email)
) ENGINE=InnoDB;

CREATE TABLE utente (
username varchar(25) NOT NULL,
Userpassword varchar(129) NOT NULL,
email varchar(50) NOT NULL,

PRIMARY KEY(username),
FOREIGN KEY (email) REFERENCES Cliente_DatiPersonali(email)
						ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE ruolo(
nomeRuolo varchar(30) NOT NULL,
utente varchar(25) NOT NULL,

PRIMARY KEY(nomeRuolo, utente),
FOREIGN KEY (utente) REFERENCES utente(username)
						ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE =InnoDB;

CREATE TABLE indirizzo(
idIndirizzo int NOT NULL AUTO_INCREMENT, 
via varchar(50) NOT NULL,
numCivico varchar(4) NOT NULL,
citta varchar(50) NOT NULL,
CAP char(5) NOT NULL,
provincia char(2) NOT NULL,

PRIMARY KEY(idIndirizzo)
)ENGINE=InnoDB AUTO_INCREMENT = 1;

CREATE TABLE Possiede_Indirizzo(
utente varchar(25) NOT NULL,
indirizzo int NOT NULL,

PRIMARY KEY(utente, indirizzo),
FOREIGN KEY (indirizzo) REFERENCES indirizzo(idIndirizzo)
			ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (utente) REFERENCES utente(username) 
			ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE wishlist(
utente varchar(25) NOT NULL,
idWishlist int NOT NULL,

PRIMARY KEY(utente, idWishlist),
FOREIGN KEY(utente) REFERENCES utente(username) 
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE = InnoDB;


CREATE TABLE prodotto(
CodiceProdotto int NOT NULL,
Nome varchar(80) NOT NULL,
TopDescrizione text NOT NULL,
Dettagli text,
Prezzo float NOT NULL,
Categoria varchar(30) NOT NULL,
Sottocategoria varchar(50),
Marca varchar(50) NOT NULL,
Modello varchar(40),
InCatalogo boolean NOT NULL,
InVetrina boolean NOT NULL,
TopImmagine mediumblob ,
Quantità int NOT NULL,

PRIMARY KEY(CodiceProdotto)
)ENGINE=InnoDB;

CREATE TABLE Composizione_Wishlist(
utente varchar(25) NOT NULL,
wishlist int NOT NULL,
prodotto int NOT NULL,

PRIMARY KEY(utente, wishlist, prodotto),
FOREIGN KEY(utente, wishlist) REFERENCES wishlist(utente, idWishlist) 
						ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY(prodotto) REFERENCES prodotto(CodiceProdotto)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE = InnoDB;

CREATE TABLE Richiesta_fornitura(
CodiceRichiesta int NOT NULL AUTO_INCREMENT,
Fornitore varchar(50) NOT NULL,
EmailFornitore varchar(50) NOT NULL,
Descrizione text NOT NULL,
QuantitàRifornimento int NOT NULL,
Prodotto int NOT NULL,

PRIMARY KEY(CodiceRichiesta),
FOREIGN KEY(Prodotto) REFERENCES prodotto(CodiceProdotto)
						ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT = 1;

CREATE TABLE Immagine_di_dettaglio(
CodiceImmagine int AUTO_INCREMENT NOT NULL,
Prodotto int NOT NULL,
Contenuto mediumblob NOT NULL,

PRIMARY KEY(CodiceImmagine, Prodotto),
FOREIGN KEY(Prodotto) REFERENCES Prodotto(CodiceProdotto) 
						ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=InnoDB AUTO_INCREMENT=1;

CREATE TABLE Ordine(
CodiceOrdine int AUTO_INCREMENT NOT NULL,
Stato enum('Richiesta effettuata', 'In lavorazione', 'Spedito', 'Preparazione incompleta') NOT NULL,
email varchar(50) NOT NULL,
IndirizzoSpedizione int NOT NULL,
TipoSpedizione enum('Spedizione standard', 'Spedizione assicurata', 'Spedizione prime') NOT NULL,
TipoConsegna enum('Domicilio', 'Punto di ritiro', 'Priority') NOT NULL,
DataOrdine date NOT NULL,
OraOrdine time NOT NULL,

PRIMARY KEY(CodiceOrdine),
FOREIGN KEY(email) REFERENCES cliente_DatiPersonali(email)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT = 1;

CREATE TABLE Composizione_Ordine(
Ordine int NOT NULL, 
Prodotto int NOT NULL,
QuantitàAcquistata smallint NOT NULL,
PrezzoAcquistato float NOT NULL,

PRIMARY KEY(Ordine, Prodotto),
FOREIGN KEY(Ordine) REFERENCES ordine(CodiceOrdine)
						ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY(Prodotto) REFERENCES prodotto(CodiceProdotto)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE Report_di_spedizione(
NumeroReport int NOT NULL AUTO_INCREMENT,
Corriere varchar(60) NOT NULL,
Imballaggio varchar(100) NOT NULL,
DataSpedizione date NOT NULL,
OraSpedizione time NOT NULL,
Ordine int NOT NULL,

PRIMARY KEY(NumeroReport),
FOREIGN KEY(Ordine) REFERENCES ordine(CodiceOrdine)
						ON DELETE CASCADE ON UPDATE CASCADE

)ENGINE=InnoDB AUTO_INCREMENT = 1;

CREATE TABLE Pagamento(
CodicePagamento int AUTO_INCREMENT NOT NULL,
Ordine int NOT NULL,
DataPagamento date NOT NULL,
OraPagamento time NOT NULL,
Importo float NOT NULL,

PRIMARY KEY(CodicePagamento),
FOREIGN KEY (Ordine) REFERENCES ordine(CodiceOrdine)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE = InnoDB AUTO_INCREMENT=1;

CREATE TABLE Paypal(
CodicePagamento int NOT NULL,

PRIMARY KEY(CodicePagamento),
FOREIGN KEY (CodicePagamento) REFERENCES Pagamento(CodicePagamento)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE Contrassegno(
CodicePagamento int NOT NULL,

PRIMARY KEY(CodicePagamento),
FOREIGN KEY (CodicePagamento) REFERENCES Pagamento(CodicePagamento)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;

CREATE TABLE Carta_di_credito(
CodicePagamento int NOT NULL,
Titolare varchar(80) NOT NULL,
NumeroCarta char(16) NOT NULL,

PRIMARY KEY(CodicePagamento),
FOREIGN KEY (CodicePagamento) REFERENCES Pagamento(CodicePagamento)
						ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB;
