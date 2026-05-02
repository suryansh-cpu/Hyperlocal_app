package com.example.hyperlocalecom.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hyperlocalecom.data.model.VariantResponse

@Composable
fun VariantCard(
    variant: VariantResponse,
    onIncrease: (VariantResponse) -> Unit,
    onDecrease: (VariantResponse) -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Size: ${variant.size}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Stock: ${variant.stock}", style = MaterialTheme.typography.bodyMedium)
                if (variant.price != null) {
                    Text(
                        text = "Price: ₹${variant.price}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                IconButton(onClick = { onDecrease(variant) }) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }

                IconButton(onClick = { onIncrease(variant) }) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}
