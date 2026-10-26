package com.example.orderflow.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.orderflow.product.Product;
import com.example.orderflow.product.ProductRepository;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository; // Mock de OrderRepository pour simuler le comportement du repository sans avoir besoin d'une base de données réelle.

    @InjectMocks
    private OrderService orderService; // Mock de OrderService pour tester la logique métier de la création de commandes sans dépendre d'une base de données réelle.

    @Mock
    private ProductRepository productRepository; // Mock de ProductRepository pour simuler le comportement du repository sans avoir besoin d'une base de données réelle.

    @Test
    void shouldCreateOrderWhenTotalIsPositive() {
        Order order = new Order(new BigDecimal("149.90"));

        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class)))
                .thenReturn(order);

        Order createdOrder = orderService.createOrder(
                new BigDecimal("149.90")
        );

        assertEquals(new BigDecimal("149.90"), createdOrder.getTotal());
        verify(orderRepository).save(org.mockito.ArgumentMatchers.any(Order.class));
    }

    @Test
    void shouldRejectOrderWhenTotalIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(new BigDecimal("-10.00"))
        );
    }

    @Test
    void shouldCalculateTotalFromOrderItems() {
        Product product = new Product(
            "Clavier",
            new BigDecimal("79.90"),
            10
        );

        when(productRepository.findById(1L)) // Simule le comportement du repository pour retourner un produit spécifique lorsque l'identifiant du produit existe dans la base de données.
            .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<OrderLineRequest> lines = List.of(
            new OrderLineRequest(1L, 2)
        );

        Order createdOrder = orderService.createOrder(lines);

        assertEquals(new BigDecimal("159.80"), createdOrder.getTotal());
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(2, createdOrder.getItems().get(0).getQuantity());
    }
}