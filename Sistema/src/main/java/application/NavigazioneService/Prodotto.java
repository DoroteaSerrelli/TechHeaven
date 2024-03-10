package model;

import java.awt.Image;

public class Prodotto {
    private int addedToCart;
    
    private String nome;
    private int codice;
    private String top_descrizione, dettagli;
    private double prezzo;
    private String categoria, sotto_categoria;
    private String marca, modello;
    private boolean inCatalogo, inVetrina;
    private Image img;
    private int quantità;
    
    public Prodotto(){}
    
    public Prodotto(String nome, int codice, String top_descrizione, String dettagli, double prezzo, String categoria, String sotto_categoria, String marca, String modello, boolean inCatalogo, boolean inVetrina, Image img, int quantità) {
        this.nome = nome;
        this.codice = codice;
        this.top_descrizione = top_descrizione;
        this.dettagli = dettagli;
        this.prezzo = prezzo;
        this.categoria = categoria;
        this.sotto_categoria = sotto_categoria;
        this.marca = marca;
        this.modello = modello;
        this.inCatalogo = inCatalogo;
        this.inVetrina = inVetrina;
        this.img = img;
        this.quantità = quantità;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCodice() {
        return codice;
    }

    public void setCodice(int codice) {
        this.codice = codice;
    }

    public String getTop_descrizione() {
        return top_descrizione;
    }

    public void setTop_descrizione(String top_descrizione) {
        this.top_descrizione = top_descrizione;
    }

    public String getDettagli() {
        return dettagli;
    }

    public void setDettagli(String dettagli) {
        this.dettagli = dettagli;
    }

    public double getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSotto_categoria() {
        return sotto_categoria;
    }

    public void setSotto_categoria(String sotto_categoria) {
        this.sotto_categoria = sotto_categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public boolean isInCatalogo() {
        return inCatalogo;
    }

    public void setInCatalogo(boolean inCatalogo) {
        this.inCatalogo = inCatalogo;
    }

    public boolean isInVetrina() {
        return inVetrina;
    }

    public void setInVetrina(boolean inVetrina) {
        this.inVetrina = inVetrina;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public int getQuantità() {
        return quantità;
    }

    public void setQuantità(int quantità) {
        this.quantità = quantità;
    }
    
    public int getAddedToCart() {
        return addedToCart;
    }
    public void removeAll(){
        this.addedToCart = 0;
    }
    public void setAddedToCart(int addedToCart) {
        this.addedToCart += addedToCart;
    }    
}
