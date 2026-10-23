package com.example.orderflow.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Controller : reçoit les requêtes HTTP et renvoie les réponses.
@RestController // classe qui expose des endpoints REST HTTP pour gérer les produits, en utilisant ProductService pour la logique métier et ProductRepository pour l'accès aux données.
@RequestMapping("/api/products") // chemin de base pour tous les endpoints de ce contrôleur, ce qui signifie que toutes les requêtes vers /api/products seront dirigées vers cette classe.
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService) {
        this.productService = productService;
    }

    @PostMapping // endpoint HTTP POST pour créer un nouveau produit, en utilisant les données fournies dans le corps de la requête.
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@Valid @RequestBody CreateProductRequest request) { // converti JSON en objet Java automatiquement grâce à @RequestBody, ce qui permet de recevoir les données du produit à créer.
        //@valid: permet de valider les contraintes définies dans la classe CreateProductRequest (comme @NotBlank, @Positive, etc.) avant d'exécuter la méthode. Si les contraintes ne sont pas respectées, une exception sera levée et une réponse d'erreur sera renvoyée au client.
        return productService.createProduct(
                request.name(),
                request.price(),
                request.stock()
        );
    }

    @GetMapping // endpoint HTTP GET pour récupérer tous les produits, en utilisant ProductService pour accéder aux données.
    public List<Product> getAllProducts() { // on ne passe pas direct par le repository pour respecter le principe de séparation des responsabilités et permettre à ProductService de gérer la logique métier (ex: filtrage, tri, etc.) avant de retourner les produits.
        return productService.getAllProducts();
    }

    @GetMapping("/{id}") // endpoint HTTP GET pour récupérer un produit spécifique par son identifiant, en utilisant ProductService pour accéder aux données.
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id) // on ne passe pas direct par le repository pour respecter le principe de séparation des responsabilités et permettre à ProductService de gérer la logique métier (ex: filtrage, tri, etc.) avant de retourner le produit.
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") // endpoint HTTP PUT pour mettre à jour un produit existant par son identifiant, en utilisant ProductService pour accéder aux données.
    public Product updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {

        return productService.updateProduct(
                id,
                request.name(),
                request.price(),
                request.stock()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    public record CreateProductRequest(
        @NotBlank(message = "Le nom est obligatoire") // validation pour s'assurer que le nom du produit n'est pas vide ou null
        String name,

        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit être positif") // validation pour s'assurer que le prix est positif et non nul
        BigDecimal price,

        @Min(value = 0, message = "Le stock ne peut pas être négatif") // validation pour s'assurer que le stock est supérieur ou égal à zéro
        int stock
) {}
}
