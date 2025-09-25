package com.example.Personal_Finance_App.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Personal_Finance_App.Model.Stock;
import com.example.Personal_Finance_App.Repositories.StockRepository;



@Service
public class MarketService {

    @Autowired
    private StockRepository stockRepo;

    // Mock market data loader
    // @PostConstruct
    public void loadMockData() {
        stockRepo.saveAll(List.of(
            new Stock(null, "RELIANCE", "Reliance Industries", 2600.00),
            new Stock(null, "TATAMOTORS", "Tata Motors", 780.00),
            new Stock(null, "INFY", "Infosys", 1500.00),
            new Stock(null, "HDFCBANK", "HDFC Bank", 1700.00)
        ));
    }

    public List<Stock> getAllStocks() {
        return stockRepo.findAll();
    }

    public Stock getStockBySymbol(String symbol) {
        return stockRepo.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }
}

