package fr.isen.legall.androiderestaurant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.legall.androiderestaurant.ui.theme.AndroidERestaurantTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidERestaurantTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RestaurantScreen(navigateToCategory = { mealType ->
                        navigateToCategoryScreen(mealType)
                    })
                }
            }
        }
    }

    private fun navigateToCategoryScreen(mealType: String) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("mealType", mealType)
        this.startActivity(intent)
    }
}

@Composable
fun RestaurantScreen(navigateToCategory: (String) -> Unit) {
    Scaffold(
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "AppLogo"
                )
                Text(stringResource(id = R.string.home_screen_title), fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navigateToCategory("Entrées") },
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB7A57A))
                ) {
                    Text(text = "Entrées", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navigateToCategory("Plats") },
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB7A57A))
                ) {
                    Text(text = "Plats", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navigateToCategory("Desserts") },
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB7A57A))
                ) {
                    Text(text = "Desserts", fontSize = 20.sp)
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidERestaurantTheme {
        RestaurantScreen(navigateToCategory = { })
    }
}