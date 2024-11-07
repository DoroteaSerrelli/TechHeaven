/*PRIMO UTENTE*/
INSERT INTO cliente_datipersonali(EMAIL, NOME, COGNOME, SESSO, TELEFONO) 
	VALUES('dorotea.serrelli@gmail.com', 'Dorotea', 'Serrelli', 'F', '111-222-3334');

INSERT INTO utente(USERNAME, USERPASSWORD, EMAIL) 
	VALUES('dorotea', '57566e0cf5aeab118f272233cd2315e416c487c47e407071e2ea9d01a380f8db3f9b146d333729d0276fd9ee276f8d1f1922e72078d8c10943fcb4d6c48625bf', 
    'dorotea.serrelli@gmail.com');
    
INSERT INTO ruolo(NOMERUOLO, UTENTE) VALUES('Cliente', 'dorotea');

INSERT INTO indirizzo(IDINDIRIZZO, VIA, NUMCIVICO, CITTA, CAP, PROVINCIA) VALUES(1, 'Roma', '21', 'Avellino', '83100', 'AV');

INSERT INTO possiede_indirizzo(UTENTE, INDIRIZZO) VALUES('dorotea', 1);