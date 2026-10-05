package com.mexiti.costogasolina.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun MainTitle(title: String){
    Text(text = title, fontSize = 30.sp  , fontWeight = FontWeight.Bold)
}

@Preview(showBackground = true)
@Composable
fun MainTitlePreview(){
    MainTitle(title = "Gasolina")
}
