package com.test.produit.service;

import com.test.produit.model.Product;
import com.test.produit.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product newData) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(newData.getName());
            existing.setPrice(newData.getPrice());
            existing.setQuantity(newData.getQuantity());
            return productRepository.save(existing);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
