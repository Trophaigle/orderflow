package com.example.orderflow.product;

import org.springframework.data.jpa.repository.JpaRepository;

//Repository : communique avec la base de données.
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Cette interface étend JpaRepository, ce qui permet d'utiliser les méthodes CRUD (Create, Read, Update, Delete) pour l'entité Product sans avoir besoin de les implémenter manuellement.
    // accès aux données via les méthodes comme findAll(), findById(), save(), deleteById(), etc.
    //Lit et ecrit les données de la base de données pour l'entité Product.
}