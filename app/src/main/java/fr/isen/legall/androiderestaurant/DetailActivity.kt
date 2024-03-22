package fr.isen.legall.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.isen.legall.androiderestaurant.data.Item
import fr.isen.legall.androiderestaurant.data.Cart
import fr.isen.legall.androiderestaurant.data.CartItem
import fr.isen.legall.androiderestaurant.ui.theme.AndroidERestaurantTheme

class DetailActivity : ComponentActivity() {
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dish = intent.getSerializableExtra("dish")
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailScreen(dish as Item, saveToCart = { cartItem ->
                        saveItemToCart(cartItem)
                    })
                    Box(modifier = Modifier.fillMaxSize()) {
                        FloatingActionButton(
                            onClick = {
                                val intent = Intent(this@DetailActivity, CartActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(56.dp)
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            }
        }
    }

    private fun saveItemToCart(dish: CartItem) {
        // Retrieve the existing Cart object from SharedPreferences
        val sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("cart", null)
        val type = object : TypeToken<Cart>() {}.type
        val cart: Cart = gson.fromJson(json, type) ?: Cart(mutableListOf())

        // Add the CartItem to the Cart
        cart.items.add(dish)

        // Convert the updated Cart back to JSON
        val updatedJson = gson.toJson(cart)

        // Save the updated JSON back to SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("cart", updatedJson)
        editor.apply()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@ExperimentalPagerApi
@Composable
fun DetailScreen(dish: Item, saveToCart: (CartItem) -> Unit = {}) {
    val pagerState = rememberPagerState()
    val counter = remember { mutableStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Carousel pour les images
        HorizontalPager(
            count = dish.images.size.coerceAtLeast(1), // Assurez-vous d'avoir au moins 1 pour éviter les erreurs
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val imageUrl = dish.images.getOrNull(page) ?: "" // Utilisez une URL vide si index out of bounds
            AsyncImage(
                model = imageUrl,
                contentDescription = "dish__image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.not_found) // Utilisez votre image par défaut ici
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Titre du plat
        Text(
            text = dish.name_fr,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.headlineMedium.fontSize),
            modifier = Modifier.padding(16.dp)
        )

        // Liste des ingrédients
        FlowRow(
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        ) {
            dish.ingredients.forEach { ingredient ->
                ChipView(text = ingredient.name_fr)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Affichage du prix
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            dish.prices.firstOrNull()?.let { price ->
                ChipView(text = price.price + "€")
            }
        }

        // Ajout des boutons pour incrémenter et décrémenter le compteur
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { if (counter.value > 1) counter.value-- }) {
                Text("-")
            }
            Text(text = counter.value.toString(), modifier = Modifier.padding(horizontal = 16.dp))
            Button(onClick = { counter.value++ }) {
                Text("+")
            }
        }

        // Affichage du bouton avec le prix total
        Button(onClick = { saveToCart(CartItem(dish, counter.value))}) {
            Text(text = "Total: ${dish.prices.firstOrNull()?.price?.toDoubleOrNull()?.times(counter.value) ?: 0}€")
        }
    }
}

@Composable
fun ChipView(text: String) {
    Surface(
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 8.dp, bottom = 8.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun PreviewDetailScreen() {
    AndroidERestaurantTheme {
        DetailScreen(
            Item(
                "1",
                "Pizza",
                "Pizza",
                "1",
                "Pizza",
                "Pizza",
                listOf("https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"),
                listOf(),
                listOf()
            )
        )
    }
}