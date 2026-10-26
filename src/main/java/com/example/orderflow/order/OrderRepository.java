package com.example.orderflow.order;

import org.springframework.data.jpa.repository.JpaRepository;

// pour engistrement des commandes en base de donnée.

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Cette interface étend JpaRepository, ce qui permet d'utiliser les méthodes CRUD (Create, Read, Update, Delete) pour gérer les entités Order dans la base de données. JpaRepository fournit des méthodes prêtes à l'emploi pour effectuer des opérations sur les entités, telles que save(), findById(), findAll(), deleteById(), etc.
}// pas besoin d'écrire les requetes SQL, Spring Data JPA s'en charge automatiquement en fonction des méthodes définies dans l'interface.