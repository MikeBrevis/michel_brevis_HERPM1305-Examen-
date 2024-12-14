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


import androidx.compose.material3.*

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
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

// Navegación de la aplicación
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "productList") {
        composable("productList") {
            ProductListScreen(navController)
        }
        composable("addProduct") {
            AddProductScreen(navController)
        }
    }
}

// Pantalla principal con lista de productos
@Composable
fun ProductListScreen(navController: NavHostController) {
    val sampleProducts = listOf(
        Product("Manzanas", 100, 1.2, "España"),
        Product("Peras", 50, 2.5, "Francia"),
        Product("Uvas", 200, 1.8, "Alemania"),
        Product("Naranjas", 80, 3.0, "Italia")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate("addProduct") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text(text = "Agregar Producto")
        }
        LazyColumn {
            items(sampleProducts.size) { index ->
                ProductCard(product = sampleProducts[index])
            }
        }
    }
}

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

// Pantalla para agregar un producto
@Composable
fun AddProductScreen(navController: NavHostController) {
    // Variables para almacenar los datos ingresados
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var priceEuro by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Agregar Producto", fontSize = 24.sp)

        // Campos de texto para ingresar datos
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del Producto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = priceEuro,
            onValueChange = { priceEuro = it },
            label = { Text("Precio en Euros") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Lugar de Exportación") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para guardar el producto
        Button(
            onClick = {
                // Validamos los campos y guardamos el producto
                if (name.isNotEmpty() && quantity.isNotEmpty() && priceEuro.isNotEmpty() && destination.isNotEmpty()) {
                    // Simulamos guardar el producto en una lista temporal (pendiente almacenamiento persistente)
                    val product = Product(
                        name = name,
                        quantity = quantity.toIntOrNull() ?: 0,
                        priceEuro = priceEuro.toDoubleOrNull() ?: 0.0,
                        destination = destination
                    )
                    // Volvemos a la pantalla anterior (en el futuro, persistiremos el producto)
                    navController.navigateUp()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }

        // Botón para regresar
        Button(
            onClick = { navController.navigateUp() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
