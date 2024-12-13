package com.example.michel_brevis_herpm1305

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProductListScreen()
        }
    }
}

// Pantalla principal con lista de productos
@Composable
fun ProductListScreen() {
    val sampleProducts = listOf(
        Product("Manzanas", 100, 1.2, "España"),
        Product("Peras", 50, 2.5, "Francia"),
        Product("Uvas", 200, 1.8, "Alemania"),
        Product("Naranjas", 80, 3.0, "Italia")
    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(sampleProducts.size) { index ->
            ProductCard(product = sampleProducts[index])
        }
    }
}

// Modelo de datos para un producto
data class Product(
    val name: String,
    val quantity: Int,
    val priceEuro: Double,
    val destination: String
)

// Tarjeta que muestra información de un producto
@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Producto: ${product.name}", fontSize = 20.sp)
            Text(text = "Cantidad: ${product.quantity}")
            Text(text = "Precio: ${product.priceEuro} €")
            Text(text = "Lugar de exportación: ${product.destination}")
        }
    }
}


