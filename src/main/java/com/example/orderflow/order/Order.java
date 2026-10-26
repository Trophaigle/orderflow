package com.example.orderflow.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

// représente une commande complete
@Entity // indique à JPA que cette classe doit être enregistrée en base de données.
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>(); // permet à une commande de contenir plusieurs lignes de commande (OrderItem). 

    protected Order() { // constructeur pour JPA
    }

    public Order(BigDecimal total) {
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public BigDecimal calculateTotal() {
        BigDecimal calculatedTotal = BigDecimal.ZERO;

        for (OrderItem item : items) {
            BigDecimal itemTotal = item.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            calculatedTotal = calculatedTotal.add(itemTotal);
        }

        return calculatedTotal;
    }

    public void updateTotal() {
        this.total = calculateTotal();
    }
}