package com.mexiti.costogasolina.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mexiti.costogasolina.R

@Composable
fun MainTitle(title: String){
    Text(text = title, fontSize = 30.sp  , fontWeight = FontWeight.Bold)
}


@Composable
fun AddTip(
    darPropina: Boolean,
    onTipCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 2.dp)
            .size(70.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.agregar_propina),
            modifier = Modifier.padding(20.dp)
        )
        Switch(
            checked = darPropina ,
            onCheckedChange = onTipCheckedChange,

            )
    }


}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainTitlePreview(){
    MainTitle(title = "Gasolina")
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddTipPreview(){
    AddTip(darPropina = true, onTipCheckedChange = {})
}