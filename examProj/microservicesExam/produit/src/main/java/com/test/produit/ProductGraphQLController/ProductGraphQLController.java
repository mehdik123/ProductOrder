package com.test.produit.ProductGraphQLController;

// produits-service/src/main/java/com/example/produitsservice/graphql/ProductGraphQLController.java

import com.test.produit.model.Product;
import com.test.produit.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ProductGraphQLController {

    @Autowired
    private ProductService productService;

    @QueryMapping
    public List<Product> products() {
        return productService.findAll();
    }

    @QueryMapping
    public Product productById(@Argument Long id) {
        return productService.findById(id);
    }

    @MutationMapping
    public Product createProduct(@Argument String name,
                                 @Argument double price,
                                 @Argument int quantity) {
        Product p = new Product(name, price, quantity);
        return productService.create(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id,
                                 @Argument String name,
                                 @Argument Double price,
                                 @Argument Integer quantity) {
        // Retrieve
        Product existing = productService.findById(id);
        if (existing == null) return null;

        // Update only the non-null fields
        if (name != null) existing.setName(name);
        if (price != null) existing.setPrice(price);
        if (quantity != null) existing.setQuantity(quantity);

        // Save
        return productService.create(existing);
    }

    @MutationMapping
    public boolean deleteProduct(@Argument Long id) {
        return productService.delete(id);
    }
}