package fr.isen.legall.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.legall.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.legall.androiderestaurant.data.Category
import fr.isen.legall.androiderestaurant.data.Item
import fr.isen.legall.androiderestaurant.data.RequestObjects
import org.json.JSONObject

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mealType = intent.getStringExtra("mealType")
        fetchCategoryItems(mealType ?: "unknown")
    }

    private fun fetchCategoryItems(category: String) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val bodyContent = JSONObject().apply {
            put("id_shop", "1")
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, bodyContent,
            { response ->
                val data = Gson().fromJson(response.toString(), RequestObjects::class.java)
                val dataCategory = data.data.first { it.name_fr == category }
                setContent {
                    AndroidERestaurantTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            CategoryScreen(dataCategory, navigateToDetails = { dish ->
                                navigateToDetailsScreen(dish)
                            })
                        }
                    }
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun navigateToDetailsScreen(dish: Item) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("dish", dish)
        this.startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(category: Category, modifier: Modifier = Modifier, navigateToDetails: (Item) -> Unit = {}) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Liste des ${category.name_fr}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        val items = category.items

        LazyColumn {
            items(items.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card (onClick = { navigateToDetails(items[index]) }) {
                        Text(
                            text = items[index].name_fr,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        AsyncImage(
                            model = items[index].images.firstOrNull() ?: "",
                            contentDescription = "dish_image",
                            error = painterResource(id = R.drawable.not_found), // Image affichée en cas d'erreur
                            placeholder = painterResource(id = R.drawable.not_found), // Image affichée en attendant le chargement
                            modifier = Modifier
                                .height(150.dp) // Vous pouvez ajuster la taille comme vous le souhaitez
                                .fillMaxWidth()
                        )
                        Text(text = items[index].prices[0].price + "€", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    AndroidERestaurantTheme {
        CategoryScreen(
            Category(
                "Entrées",
                "Starters",
                listOf(
                    Item(
                        "1",
                        "Salade César",
                        "Caesar Salad",
                        "1",
                        "Entrées",
                        "Starters",
                        listOf("https://www.google.com"),
                        listOf(),
                        listOf()
                    )
                )
            )
        )
    }
}