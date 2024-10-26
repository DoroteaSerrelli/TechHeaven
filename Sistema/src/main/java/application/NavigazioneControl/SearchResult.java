package application.NavigazioneControl;

import application.NavigazioneService.ProxyProdotto;
import java.util.Collection;

public class SearchResult {
    private Collection<ProxyProdotto> products;
    private int totalRecords;
    public SearchResult(){}
    public SearchResult(Collection<ProxyProdotto> products, int totalRecords) {
        this.products = products;
        this.totalRecords = totalRecords;
    }

    public Collection<ProxyProdotto> getProducts() {
        return products;
    }
    
    public void setProducts(Collection<ProxyProdotto> products){
        this.products = products;
    }

    public int getTotalRecords() {
        return totalRecords;
    }
    
    public void setTotalRecords(int totalRecords){
        this.totalRecords = totalRecords;
    }
}