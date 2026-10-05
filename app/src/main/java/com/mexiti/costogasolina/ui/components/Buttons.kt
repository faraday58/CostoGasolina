package com.mexiti.costogasolina.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FloatButton( onClick: () -> Unit  ){
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar")
    }
}

@Composable
fun ActionElevatedButton(
    onClick: () -> Unit,
    text: String
    ){
    ElevatedButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = text)
    }
}


@Preview(showBackground = true)
@Composable
fun FloatButtonPreview(){
    FloatButton(onClick = {})
}


@Preview(showBackground = true)
@Composable
fun ActionElevatedButtonPreview(){
    ActionElevatedButton(onClick = {}, text = "Agregar")
}