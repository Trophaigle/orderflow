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

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
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
}
