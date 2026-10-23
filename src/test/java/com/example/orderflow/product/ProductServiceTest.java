package com.example.orderflow.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock // Mock de ProductRepository pour simuler le comportement du repository sans avoir besoin d'une base de données réelle.
    private ProductRepository productRepository;

    @InjectMocks // Injection du mock ProductRepository dans ProductService pour tester la logique métier de ProductService.
    private ProductService productService;

    @Test // Test unitaire pour vérifier que la création d'un produit avec des données valides fonctionne correctement.
    void shouldCreateProductWhenDataIsValid() {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product product = productService.createProduct(
                "Clavier mécanique",
                new BigDecimal("79.90"),
                10
        );

        assertEquals("Clavier mécanique", product.getName());
        assertEquals(new BigDecimal("79.90"), product.getPrice());
        assertEquals(10, product.getStock());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldRejectProductWithNegativePrice() {
        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(
                        "Clavier",
                        new BigDecimal("-10.00"),
                        10
                )
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldUpdateProductWhenDataIsValid() {
        Product product = new Product(
                "Clavier",
                new BigDecimal("79.90"),
                10
        );

        when(productRepository.findById(1L)) // Simule le comportement du repository pour retourner un produit spécifique lorsque l'identifiant du produit existe dans la base de données.
                .thenReturn(java.util.Optional.of(product));

        when(productRepository.save(any(Product.class))) // Simule le comportement du repository pour sauvegarder le produit mis à jour et retourner l'entité persistée.
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = productService.updateProduct(
                1L,
                "Clavier mécanique",
                new BigDecimal("89.90"),
                15
        );

        assertEquals("Clavier mécanique", updatedProduct.getName()); // Vérifie que le nom du produit mis à jour est correct.
        assertEquals(new BigDecimal("89.90"), updatedProduct.getPrice()); // Vérifie que le prix du produit mis à jour est correct.
        assertEquals(15, updatedProduct.getStock()); // Vérifie que le stock du produit mis à jour est correct.

        verify(productRepository).save(product); // Vérifie que la méthode save du repository a été appelée avec le produit mis à jour.
    }


    @Test
    void shouldThrowNotFoundWhenUpdatingMissingProduct() {
        when(productRepository.findById(999L))
                .thenReturn(java.util.Optional.empty()); // Simule le comportement du repository pour retourner un Optional vide lorsque l'identifiant du produit n'existe pas dans la base de données.

        org.springframework.web.server.ResponseStatusException exception =
                org.junit.jupiter.api.Assertions.assertThrows( // Vérifie que l'exception ResponseStatusException est levée lorsque l'on tente de mettre à jour un produit qui n'existe pas dans la base de données.
                        org.springframework.web.server.ResponseStatusException.class,
                        () -> productService.updateProduct(
                                999L,
                                "Clavier",
                                new BigDecimal("79.90"),
                                10
                        )
                );

        assertEquals( // Vérifie que le code de statut de l'exception est bien NOT_FOUND (404) lorsque l'on tente de mettre à jour un produit qui n'existe pas dans la base de données.
                org.springframework.http.HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }
}
