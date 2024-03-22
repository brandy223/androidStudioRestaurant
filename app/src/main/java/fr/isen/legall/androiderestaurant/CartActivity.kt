package fr.isen.legall.androiderestaurant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.legall.androiderestaurant.data.Cart
import fr.isen.legall.androiderestaurant.ui.theme.AndroidERestaurantTheme

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CartScreen(getCartContent())
                }
            }
        }
    }

    private fun getCartContent(): Cart {
        // Retrieve the JSON string from SharedPreferences
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("cart", null)

        // Use Gson to convert the JSON string to a Cart object
        val type = object : TypeToken<Cart>() {}.type
        return gson.fromJson(json, type) ?: Cart(mutableListOf())
    }
}

@Composable
fun CartScreen(cart: Cart) {
    LazyColumn {
        items(cart.items.size) { index ->
            val cartItem = cart.items[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = cartItem.dish.name_fr)
                Text(text = "Quantity: ${cartItem.quantity}")
                Text(text = "Total: ${cartItem.dish.prices.firstOrNull()?.price?.toDoubleOrNull()?.times(cartItem.quantity) ?: 0}€")
            }
        }
    }
    val totalPrice = cart.items.sumByDouble { it.dish.prices.firstOrNull()?.price?.toDoubleOrNull()?.times(it.quantity) ?: 0.0 }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { /*TODO: Implement the action when the button is clicked*/ }) {
            Text(text = "Final Price: $totalPrice€")
        }
    }
}