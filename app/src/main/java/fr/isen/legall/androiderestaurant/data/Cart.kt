package fr.isen.legall.androiderestaurant.data

import java.io.Serializable

data class Cart(
    val items: MutableList<CartItem>
): Serializable

data class CartItem(
    val dish: Item,
    var quantity: Int
): Serializable