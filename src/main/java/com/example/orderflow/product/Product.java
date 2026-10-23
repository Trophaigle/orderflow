package com.example.orderflow.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity // Indique que cette classe est une entité JPA, dans ce cas, elle sera mappée à une table de base de données.
public class Product {

    @Id // Indique que l'attribut "id" est la clé primaire de l'entité Product.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indique que la valeur de l'attribut "id" sera générée automatiquement par la base de données lors de l'insertion d'un nouvel enregistrement.
    private Long id;

    private String name;

    private BigDecimal price; // Utilisation de BigDecimal pour représenter les prix afin d'éviter les problèmes d'arrondi liés aux types à virgule flottante.

    private int stock;

    protected Product() {
        // Constructeur requis par JPA
    }

    public Product(String name, BigDecimal price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void updateDetails(String name, BigDecimal price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}