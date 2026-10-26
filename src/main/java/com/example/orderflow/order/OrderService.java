package com.example.orderflow.order;

import org.springframework.stereotype.Service;

import com.example.orderflow.product.Product;
import com.example.orderflow.product.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@Service // logique métier des commandes, cette classe est annotée avec @Service pour indiquer qu'il s'agit d'un service Spring, ce qui permet à Spring de gérer son cycle de vie et de l'injecter dans d'autres composants si nécessaire.  
public class OrderService {

    private final OrderRepository orderRepository; // enregistrement des commandes en base de données, ce repository est utilisé pour effectuer des opérations CRUD sur les entités Order dans la base de données.
    private final ProductRepository productRepository; // Injection du repository pour accéder aux données des produits, ce qui permet de vérifier la disponibilité des produits et de récupérer leurs informations lors de la création d'une commande.

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(BigDecimal total) {
        if (total == null || total.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Le montant de la commande doit être positif"
            );
        }

        Order order = new Order(total);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Méthode pour récupérer une commande par son ID. Elle utilise le repository pour effectuer la recherche dans la base de données et retourne un Optional<Order> qui peut contenir la commande si elle est trouvée, ou être vide si aucune commande avec l'ID spécifié n'existe.
    public java.util.Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(List<OrderLineRequest> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException(
                    "La commande doit contenir au moins un produit"
            );
        }

        Order order = new Order(BigDecimal.ZERO);

        for (OrderLineRequest line : lines) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Produit introuvable : " + line.productId()
                    ));

            product.decreaseStock(line.quantity());
                productRepository.save(product);

                OrderItem item = new OrderItem(
                        order,
                        product,
                        line.quantity()
                );

                order.addItem(item);
        }

        order.updateTotal();

        return orderRepository.save(order);
    }
}