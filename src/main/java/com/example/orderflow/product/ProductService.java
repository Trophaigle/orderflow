package com.example.orderflow.product;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// Service : contient la logique métier.
@Service // Indique que cette classe est un service Spring (logique metier), ce qui permet à Spring de la détecter et de la gérer comme un bean.
public class ProductService {

    private final ProductRepository productRepository; // Déclaration d'une dépendance vers ProductRepository, qui sera injectée via le constructeur.

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(String name, BigDecimal price, int stock) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) { // Vérifie que le prix est positif et non nul
            throw new IllegalArgumentException("Le prix doit être positif");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas être négatif");
        }

        Product product = new Product(name, price, stock);

        return productRepository.save(product); // Sauvegarde le produit dans la base de données et retourne l'entité persistée. (enregistrement delegué au repository)
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public Optional<Product> getProductById(Long id) { 
        return productRepository.findById(id); // Retourne un Optional<Product> pour gérer le cas où le produit n'existe pas dans la base de données.
    }
}