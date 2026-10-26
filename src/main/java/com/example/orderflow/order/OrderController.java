package com.example.orderflow.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    
    @PostMapping // Endpoint pour créer une nouvelle commande. Il écoute les requêtes HTTP POST sur l'URL /api/orders.
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.items());
    }

    @GetMapping // Endpoint pour récupérer toutes les commandes. Il écoute les requêtes HTTP GET sur l'URL /api/orders.
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }


    @GetMapping("/{id}") // Endpoint pour récupérer une commande par son ID. Il écoute les requêtes HTTP GET sur l'URL /api/orders/{id}, où {id} est un paramètre de chemin représentant l'identifiant de la commande.
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record CreateOrderRequest(
            @NotEmpty(message = "La commande doit contenir au moins un produit")
            List<@Valid OrderLineRequest> items // liste des lignes de commande, chaque ligne est validée individuellement
    ) {
    }
}