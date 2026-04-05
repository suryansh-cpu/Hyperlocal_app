//package com.example.hyperlocalecom.ui.components
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.hyperlocalecom.data.model.Variant
//
//@Composable
//fun VariantCard(
//    variant: Variant,
//    onIncrease: () -> Unit,
//    onDecrease: () -> Unit,
//    onEdit: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(12.dp)
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//
//            Text(text = "Size: ${variant.size}")
//
////            Row {
////                IconButton(onClick = onDecrease) {
////                    Text("-")
////                }
////
////                Text(text = variant.stock.toString())
////
////                IconButton(onClick = onIncrease) {
////                    Text("+")
////                }
////            }
//            var stock by remember { mutableStateOf(variant.stock) }
//
//            Row(
//                modifier = Modifier
//                    .padding(12.dp)
//                    .fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//
//                Text(text = "Size: ${variant.size}")
//
//                Row {
//                    IconButton(onClick = {
//                        if (stock > 0) stock--
//                    }) {
//                        Text("-")
//                    }
//
//                    Text(text = stock.toString())
//
//                    IconButton(onClick = {
//                        stock++
//                    }) {
//                        Text("+")
//                    }
//                }
//
//                Button(onClick = onEdit) {
//                    Text("Edit")
//                }
//            }
//
//            Button(onClick = onEdit) {
//                Text("Edit")
//            }
//        }
//    }
//}

package com.example.hyperlocalecom.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hyperlocalecom.data.model.Variant

@Composable
fun VariantCard(
    variant: Variant,
    onIncrease: (Variant) -> Unit,
    onDecrease: (Variant) -> Unit,
    onEdit: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(text = "Size: ${variant.size}")
                Text(text = "Stock: ${variant.stock}")
            }

            Row {
                IconButton(onClick = { onDecrease(variant) }) {
                    Text("-")
                }

                IconButton(onClick = { onIncrease(variant) }) {
                    Text("+")
                }
            }
        }
    }
}