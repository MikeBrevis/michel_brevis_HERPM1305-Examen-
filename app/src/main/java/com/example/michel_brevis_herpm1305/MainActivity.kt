package com.example.michel_brevis_herpm1305

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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

import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert

// Funcion para obtener el valor de Euro

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

suspend fun fetchEuroValue(): Double? {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://mindicador.cl/api/euro")
                .build()
            val response = client.newCall(request).execute()
            val bodyString = response.body?.string()
            response.close()

            if (bodyString != null) {
                // Parseamos el JSON
                val json = JSONObject(bodyString)
                val serie = json.getJSONArray("serie")
                if (serie.length() > 0) {
                    val firstElement = serie.getJSONObject(0)
                    // El valor del euro está en "valor"
                    return@withContext firstElement.getDouble("valor")
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}



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
    // Estado compartido para almacenar productos
    val productList = remember { mutableStateListOf<Product>() }

    NavHost(navController = navController, startDestination = "productList") {
        composable("productList") {
            ProductListScreen(navController, productList)
        }
        composable("addProduct") {
            AddProductScreen(navController, productList)
        }
        composable("chart") {
            ProductChartScreen(productList)
        }
    }
}

// Pantalla principal con lista de productos
@Composable
fun ProductListScreen(navController: NavHostController, productList: MutableList<Product>) {
    var euroValue by remember { mutableStateOf<Double?>(null) }

    // Al iniciar la composable, obtenemos el valor del euro
    LaunchedEffect(Unit) {
        val value = fetchEuroValue()
        euroValue = value
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = { navController.navigate("addProduct") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text(text = "Agregar Producto")
        }

        Button(
            onClick = { navController.navigate("chart") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text(text = "Ver Gráfico")
        }

        // Mostramos un indicador si aún no tenemos el valor del euro
        if (euroValue == null) {
            Text("Cargando valor del euro...")
        } else {
            LazyColumn {
                items(productList.size) { index ->
                    ProductCard(
                        product = productList[index],
                        onDelete = { productList.removeAt(index) },
                        euroValue = euroValue
                    )
                }
            }
        }
    }
}



// Tarjeta que muestra información de un producto
@Composable
fun ProductCard(product: Product, onDelete: () -> Unit, euroValue: Double?) {
    var expanded by remember { mutableStateOf(false) }

    val priceInCLP = if (euroValue != null) product.priceEuro * euroValue else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Producto: ${product.name}", fontSize = 20.sp)
            Text(text = "Cantidad: ${product.quantity}")
            Text(text = "Precio: ${product.priceEuro} €")
            if (priceInCLP != null) {
                Text(text = "Precio en CLP: $priceInCLP")
            } else {
                Text(text = "Cargando precio en CLP...")
            }
            Text(text = "Lugar de exportación: ${product.destination}")

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menú")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar") },
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}



// Pantalla para agregar un producto
@Composable
fun AddProductScreen(navController: NavHostController, productList: MutableList<Product>) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var priceEuro by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) } // Controla cuándo mostrar el Snackbar

    // Mostrar el Snackbar cuando showSnackbar sea true
    if (showSnackbar) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("Producto cargado correctamente")
            kotlinx.coroutines.delay(500) // Tiempo en milisegundos (1.5 segundos)
            showSnackbar = false // Restablecer el estado
            navController.navigateUp() // Navegar después del retraso
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Agregar Producto", fontSize = 24.sp)

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

            Button(
                onClick = {
                    if (name.isNotEmpty() && quantity.isNotEmpty() && priceEuro.isNotEmpty() && destination.isNotEmpty()) {
                        val product = Product(
                            name = name,
                            quantity = quantity.toIntOrNull() ?: 0,
                            priceEuro = priceEuro.toDoubleOrNull() ?: 0.0,
                            destination = destination
                        )
                        productList.add(product) // Agregar el producto a la lista compartida

                        // Activar el Snackbar
                        showSnackbar = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Producto")
            }

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}


@Composable
fun ProductChartScreen(productList: List<Product>) {
    val topProducts = remember(productList) { productList.sortedByDescending { it.priceEuro }.take(4) }
    val maxPrice = topProducts.maxOfOrNull { it.priceEuro } ?: 1.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gráfico de los 4 Productos Más Costosos",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            topProducts.forEach { product ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .height((product.priceEuro / maxPrice * 200).dp)
                            .width(40.dp)
                            .background(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = product.name,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "${product.priceEuro} €",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


