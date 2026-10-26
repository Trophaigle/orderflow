package com.example.orderflow.order;

import com.example.orderflow.product.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

// une ligne dans la commande, quel produit a été commandé et combien
@Entity
@Table(name = "order_items") // nom explicite à la table
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // plusieurs ligne peuvent référence la meme commande ou meme produit
    private Order order; // reference une commande, pour savoir à quelle commande appartient cette ligne de commande

    @ManyToOne
    private Product product;

    private int quantity;

    protected OrderItem() {
    }

    public OrderItem(Order order, Product product, int quantity) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public Order getOrder() {
        return order;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}