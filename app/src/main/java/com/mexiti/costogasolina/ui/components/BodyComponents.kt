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
fun AddOptionalData(
    flagData: Boolean,
    onflagCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    question:String = stringResource(id = R.string.agregar_propina)
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
            text = question,
            modifier = Modifier.padding(20.dp)
        )
        Switch(
            checked = flagData ,
            onCheckedChange = onflagCheckedChange,

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
fun AddOptionalDataPreview(){
    AddOptionalData(flagData = true, onflagCheckedChange = {})
}