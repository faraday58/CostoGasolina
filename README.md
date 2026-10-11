# Guía de Desarrollo: Implementación de Vistas de Edición y Eliminación en CostoGasolina

Esta guía paso a paso orienta al desarrollador en la creación e integración de dos nuevas vistas en la aplicación **CostoGasolina**:
1. **Vista de Modificación (`EditView`)**: Permite editar un registro existente cargando sus datos previos y actualizándolos en la base de datos Room.
2. **Vista de Eliminación (`DeleteView`)**: Muestra los detalles del registro seleccionado y permite confirmar la eliminación permanente del mismo.

---

## Arquitectura del Proyecto

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** con **Jetpack Compose** y **Room**:
* **Modelo**: `GasolineTransaction` (`model/GasolineTransaction.kt`)
* **Acceso a Datos**: `GasolineDao` (`room/GasolineDao.kt`)
* **ViewModel**: `GasolineViewModel` (`viewmodel/GasolineViewModel.kt`)
* **Navegación**: `NavManager` (`navigation/NavManager.kt`)
* **Vistas**: Ubicadas en `ui/views/` (`HomeView.kt`, `AddView.kt`, etc.)

---

## Paso 1: Actualizar el DAO (`GasolineDao.kt`)

Agrega las funciones necesarias en la interfaz `GasolineDao` para obtener un registro por su `id`, actualizarlo y eliminarlo.

**Ubicación:** `app/src/main/java/com/mexiti/costogasolina/room/GasolineDao.kt`

```kotlin
package com.mexiti.costogasolina.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mexiti.costogasolina.model.GasolineTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GasolineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: GasolineTransaction)

    @Query("SELECT * FROM gasoline_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<GasolineTransaction>>

    // Buscar una transacción por su ID
    @Query("SELECT * FROM gasoline_transactions WHERE id = :id")
    suspend fun getTransactionById(id: Int): GasolineTransaction?

    // Actualizar un registro existente
    @Update
    suspend fun updateTransaction(transaction: GasolineTransaction)

    // Eliminar un registro de la base de datos
    @Delete
    suspend fun deleteTransaction(transaction: GasolineTransaction)
}
```

---

## Paso 2: Actualizar el ViewModel (`GasolineViewModel.kt`)

Extiende `GasolineViewModel` con métodos para consultar un registro específico por `id`, actualizarlo y eliminarlo.

**Ubicación:** `app/src/main/java/com/mexiti/costogasolina/viewmodel/GasolineViewModel.kt`

```kotlin
package com.mexiti.costogasolina.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.room.GasolineDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GasolineViewModel(private val gasolineDao: GasolineDao): ViewModel() {

    val transaction: StateFlow<List<GasolineTransaction>> = gasolineDao.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addTransaction(
        price: Double,
        liters: Double,
        tip: Double,
        date: String,
        kilometers: Double
    ) {
        viewModelScope.launch {
            val newTransaction = GasolineTransaction(
                pricePerLiter = price,
                liters = liters,
                tip = tip,
                date = date,
                kilometers = kilometers
            )
            gasolineDao.insertTransaction(newTransaction)
        }
    }

    // Obtener un registro por ID mediante corrutina
    suspend fun getTransactionById(id: Int): GasolineTransaction? {
        return gasolineDao.getTransactionById(id)
    }

    // Actualizar un registro existente
    fun updateTransaction(transaction: GasolineTransaction) {
        viewModelScope.launch {
            gasolineDao.updateTransaction(transaction)
        }
    }

    // Eliminar un registro de la base de datos
    fun deleteTransaction(transaction: GasolineTransaction) {
        viewModelScope.launch {
            gasolineDao.deleteTransaction(transaction)
        }
    }
}
```

---

## Paso 3: Crear la Vista para Modificar (`EditView.kt`)

Crea la vista donde se cargarán los datos del registro seleccionado para permitir su edición.

**Ubicación:** `app/src/main/java/com/mexiti/costogasolina/ui/views/EditView.kt`

```kotlin
package com.mexiti.costogasolina.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mexiti.costogasolina.R
import com.mexiti.costogasolina.domain.calcularMonto
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.ui.components.AddOptionalData
import com.mexiti.costogasolina.ui.components.EditNumberField
import com.mexiti.costogasolina.ui.components.MainTitle
import com.mexiti.costogasolina.viewmodel.GasolineViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditView(
    transactionId: Int,
    navController: NavController,
    viewModel: GasolineViewModel,
    modifier: Modifier = Modifier
) {
    var transaction by remember { mutableStateOf<GasolineTransaction?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar los datos iniciales del registro por ID
    LaunchedEffect(transactionId) {
        transaction = viewModel.getTransactionById(transactionId)
        isLoading = false
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { MainTitle(title = "Editar Registro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (transaction != null) {
            EditContent(
                transaction = transaction!!,
                onUpdateTransaction = { updatedItem ->
                    viewModel.updateTransaction(updatedItem)
                    scope.launch {
                        snackbarHostState.showSnackbar("✓ Registro actualizado correctamente")
                        delay(800)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Registro no encontrado")
            }
        }
    }
}

@Composable
fun EditContent(
    transaction: GasolineTransaction,
    onUpdateTransaction: (GasolineTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var precioLitroEntrada by remember { mutableStateOf(transaction.pricePerLiter.toString()) }
    var cantLitrosEntrada by remember { mutableStateOf(transaction.liters.toString()) }
    var propinaEntrada by remember { mutableStateOf(if (transaction.tip > 0) transaction.tip.toString() else "") }
    var darPropina by remember { mutableStateOf(transaction.tip > 0) }
    var guardarKilometros by remember { mutableStateOf(transaction.kilometers > 0) }
    var kilometrsRecorridos by remember { mutableStateOf(if (transaction.kilometers > 0) transaction.kilometers.toString() else "") }

    val precioLitro = precioLitroEntrada.toDoubleOrNull() ?: 0.0
    val cantLitros = cantLitrosEntrada.toDoubleOrNull() ?: 0.0
    val propina = if (darPropina) propinaEntrada.toDoubleOrNull() ?: 0.0 else 0.0
    val kilometraje = if (guardarKilometros) kilometrsRecorridos.toDoubleOrNull() ?: 0.0 else 0.0

    val total = calcularMonto(precioLitro, cantLitros, darPropina = darPropina, propina = propina)
    val isFormValid = precioLitro > 0.0 && cantLitros > 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditNumberField(
                    label = R.string.ingresa_gasolina,
                    leadingIcon = R.drawable.money_gas,
                    keyboardsOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    value = precioLitroEntrada,
                    onValueChanged = { precioLitroEntrada = it }
                )

                EditNumberField(
                    label = R.string.litros,
                    leadingIcon = R.drawable.gasolina,
                    keyboardsOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (darPropina) ImeAction.Next else ImeAction.Done
                    ),
                    value = cantLitrosEntrada,
                    onValueChanged = { cantLitrosEntrada = it }
                )

                AddOptionalData(
                    flagData = darPropina,
                    onflagCheckedChange = { darPropina = it }
                )

                AnimatedVisibility(
                    visible = darPropina,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    EditNumberField(
                        label = R.string.propina,
                        leadingIcon = R.drawable.outline_18_up_rating_24,
                        keyboardsOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        value = propinaEntrada,
                        onValueChanged = { propinaEntrada = it }
                    )
                }

                AddOptionalData(
                    flagData = guardarKilometros,
                    onflagCheckedChange = { guardarKilometros = it },
                    question = stringResource(id = R.string.guardar_kilometraje)
                )

                AnimatedVisibility(
                    visible = guardarKilometros,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    EditNumberField(
                        label = R.string.kilometros,
                        leadingIcon = R.drawable.car__kilometer,
                        keyboardsOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        value = kilometrsRecorridos,
                        onValueChanged = { kilometrsRecorridos = it }
                    )
                }
            }
        }

        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.monto_total, total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Button(
            onClick = {
                val updatedTransaction = transaction.copy(
                    pricePerLiter = precioLitro,
                    liters = cantLitros,
                    tip = propina,
                    kilometers = kilometraje
                )
                onUpdateTransaction(updatedTransaction)
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}
```

---

## Paso 4: Crear la Vista para Eliminar (`DeleteView.kt`)

Crea la vista para confirmar la eliminación del registro seleccionado.

**Ubicación:** `app/src/main/java/com/mexiti/costogasolina/ui/views/DeleteView.kt`

```kotlin
package com.mexiti.costogasolina.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mexiti.costogasolina.R
import com.mexiti.costogasolina.domain.calcularMonto
import com.mexiti.costogasolina.model.GasolineTransaction
import com.mexiti.costogasolina.ui.components.MainTitle
import com.mexiti.costogasolina.viewmodel.GasolineViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteView(
    transactionId: Int,
    navController: NavController,
    viewModel: GasolineViewModel,
    modifier: Modifier = Modifier
) {
    var transaction by remember { mutableStateOf<GasolineTransaction?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(transactionId) {
        transaction = viewModel.getTransactionById(transactionId)
        isLoading = false
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { MainTitle(title = "Eliminar Registro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    titleContentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (transaction != null) {
            DeleteContent(
                transaction = transaction!!,
                onConfirmDelete = { itemToDelete ->
                    viewModel.deleteTransaction(itemToDelete)
                    scope.launch {
                        snackbarHostState.showSnackbar("✓ Registro eliminado")
                        delay(800)
                        navController.popBackStack()
                    }
                },
                onCancel = { navController.popBackStack() },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("El registro no existe o ya fue eliminado")
            }
        }
    }
}

@Composable
fun DeleteContent(
    transaction: GasolineTransaction,
    onConfirmDelete: (GasolineTransaction) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val total = calcularMonto(
        precio = transaction.pricePerLiter,
        cantLitros = transaction.liters,
        darPropina = transaction.tip > 0,
        propina = transaction.tip
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Advertencia",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = "¿Está seguro de eliminar este registro?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error
        )

        // Tarjeta con resumen del registro a borrar
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Fecha: ${transaction.date}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "Precio/Litro: $${transaction.pricePerLiter}")
                Text(text = "Litros: ${transaction.liters} L")
                if (transaction.tip > 0) Text(text = "Propina: $${transaction.tip}")
                if (transaction.kilometers > 0) Text(text = "Kilómetros: ${transaction.kilometers} km")
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stringResource(R.string.monto_total, total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = { onConfirmDelete(transaction) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text("Eliminar")
            }
        }
    }
}
```

---

## Paso 5: Modificar `HomeView.kt` para agregar Botones de Edición y Eliminación

En `HomeView.kt`, agrega botones de acción (Editar y Eliminar) dentro del `TransactionCard` para pasar el `id` del registro a las nuevas pantallas.

**Ubicación:** `app/src/main/java/com/mexiti/costogasolina/ui/views/HomeView.kt`

Actualiza el parámetro de `TransactionCard` e integra los iconos de acción:

```kotlin
// Actualizar la firma de HomeView y TransactionCard en HomeView.kt

@Composable
fun TransactionCard(
    transaction: GasolineTransaction,
    onEditClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val total = calcularMonto(
        precio = transaction.pricePerLiter,
        cantLitros = transaction.liters,
        darPropina = transaction.tip > 0,
        propina = transaction.tip
    )

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.monto_total, total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Precio/L: $${transaction.pricePerLiter}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Litros: ${transaction.liters} L",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (transaction.tip > 0 || transaction.kilometers > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (transaction.tip > 0) {
                        Text(
                            text = "Propina: $${transaction.tip}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    if (transaction.kilometers > 0) {
                        Text(
                            text = "Km: ${transaction.kilometers} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            // Botones de Acción: Editar y Eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onEditClick(transaction.id) }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Edit,
                        contentDescription = "Editar Registro",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { onDeleteClick(transaction.id) }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                        contentDescription = "Eliminar Registro",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
```

En la llamada dentro de `LazyColumn` en `HomeView`:

```kotlin
LazyColumn(
    modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(transactions) { item ->
        TransactionCard(
            transaction = item,
            onEditClick = { id -> navController.navigate("EditView/$id") },
            onDeleteClick = { id -> navController.navigate("DeleteView/$id") }
        )
    }
}
```

---

## Paso 6: Configurar las Rutas en el Navegador (`NavManager.kt`)

Registra las rutas compuestas `"EditView/{id}"` y `"DeleteView/{id}"` en `NavManager.kt`, pasando `NavType.IntType` como argumento.

**Ubicación:** `app/src/main/java/com/mexiti/costogasolina/navigation/NavManager.kt`

```kotlin
package com.mexiti.costogasolina.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mexiti.costogasolina.room.GasolineDao
import com.mexiti.costogasolina.ui.views.AddView
import com.mexiti.costogasolina.ui.views.DeleteView
import com.mexiti.costogasolina.ui.views.EditView
import com.mexiti.costogasolina.ui.views.HomeView
import com.mexiti.costogasolina.viewmodel.GasolineViewModel

@Composable
fun NavManager(
    dao: GasolineDao,
) {
    val navController = rememberNavController()

    val viewModel: GasolineViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                GasolineViewModel(dao)
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "Home",
    ) {
        composable("Home") {
            HomeView(navController, viewModel)
        }
        composable("AddView") {
            AddView(navController, viewModel)
        }
        // Ruta para Editar con argumento {id}
        composable(
            route = "EditView/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditView(
                transactionId = id,
                navController = navController,
                viewModel = viewModel
            )
        }
        // Ruta para Eliminar con argumento {id}
        composable(
            route = "DeleteView/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DeleteView(
                transactionId = id,
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
```

---

## Verificación y Pruebas

1. **Compilación**: Realiza un `Build > Rebuild Project` para asegurarte de que no haya errores sintácticos o de importaciones.
2. **Prueba de Edición**:
   - Presiona el botón del lápiz en un registro en la pantalla principal.
   - Modifica el precio o litros y pulsa "Guardar Cambios".
   - Verifica que en la pantalla principal los datos aparezcan actualizados.
3. **Prueba de Eliminación**:
   - Presiona el botón del bote de basura en un registro.
   - Verifica los detalles en la pantalla de confirmación y presiona "Eliminar".
   - Confirma que el elemento ya no se muestre en el historial.
