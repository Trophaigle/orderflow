package com.example.orderflow.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(ProductController.class) // Annotation pour indiquer que cette classe est un test Web MVC pour le ProductController, ce qui permet de tester les endpoints REST du contrôleur sans démarrer le serveur complet.
@Import(com.example.orderflow.security.SecurityConfig.class) // Importation de la configuration de sécurité pour s'assurer que les tests prennent en compte les règles de sécurité définies dans l'application.
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc; // Injection de MockMvc, un outil fourni par Spring pour simuler des requêtes HTTP et tester les endpoints REST du contrôleur sans avoir besoin d'un serveur réel.

    @MockitoBean // Annotation pour indiquer que ProductService est un bean Mockito, ce qui permet de simuler son comportement dans les tests sans avoir besoin d'une implémentation réelle.
    private ProductService productService;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void shouldRejectProductWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/products") // Effectue une requête HTTP POST vers l'endpoint /api/products pour tester la création d'un produit avec des données invalides.
                .contentType("application/json")
                .content("""
                    {
                        "name": "",
                        "price": -10,
                        "stock": -1
                    }
                    """))
                .andExpect(status().isBadRequest()); // Vérifie que la réponse HTTP est un code 400 Bad Request, ce qui signifie que les données envoyées sont invalides et que le contrôleur a correctement rejeté la requête.
    }

    @Test
    void shouldReturnProductWhenIdExists() throws Exception {
        Product product = new Product(
                "Clavier",
                new BigDecimal("79.90"),
                10
        );

        when(productService.getProductById(1L))
                .thenReturn(Optional.of(product)); // Simule le comportement du service pour retourner un produit spécifique lorsque l'identifiant du produit existe dans la base de données.

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Clavier"));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(999L))
                .thenReturn(Optional.empty()); // Simule le comportement du repository pour retourner un Optional vide lorsque l'identifiant du produit n'existe pas dans la base de données.

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/products/999")
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateProductWhenDataIsValid() throws Exception {
        Product product = new Product(
                "Clavier",
                new BigDecimal("79.90"),
                10
        );

        when(productService.updateProduct(
                1L,
                "Clavier mécanique",
                new BigDecimal("89.90"),
                15
        )).thenReturn(product);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/api/products/1")
                        .contentType("application/json")
                        .content("""
                            {
                                "name": "Clavier mécanique",
                                "price": 89.90,
                                "stock": 15
                            }
                            """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteProductAndReturnNoContent() throws Exception {
        mockMvc.perform( // Effectue (simule) une requête HTTP DELETE vers l'endpoint /api/products/1 pour tester la suppression d'un produit existant.
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/products/1")
        )
        .andExpect(status().isNoContent()); // Vérifie que la réponse HTTP est un code 204 No Content, ce qui signifie que le produit a été supprimé avec succès et qu'il n'y a pas de contenu à retourner.

        verify(productService).deleteProduct(1L); // Vérifie que la méthode deleteProduct du service a été appelée avec l'identifiant du produit à supprimer, ce qui confirme que le contrôleur a correctement déléguer la suppression au service.
    }
}
