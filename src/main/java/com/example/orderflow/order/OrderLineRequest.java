
package com.example.orderflow.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineRequest(
        @NotNull(message = "L'identifiant du produit est obligatoire")
        Long productId, // indique quel produit le client veut acheter

        @Positive(message  = "La quantité doit être supérieure à zéro")
        int quantity // indique combien de ce produit le client veut acheter
) {
}
/*exemple de JSON envoyé par le client pour créer une ligne de commande:
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
} */