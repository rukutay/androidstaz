package dk.careerexplore.poc.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.theme.Brand

/**
 * Text field per Guidelines 5.4: visible label above (never placeholder-only), 48dp, 8dp radius,
 * brown focus border, error text below in red.
 */
@Composable
fun CeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    trailingLabel: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLength: Int? = null,
    showCounter: Boolean = false,
    error: String? = null,
    leading: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(modifier) {
        if (label != null || trailingLabel != null || (showCounter && maxLength != null)) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label.orEmpty(), style = MaterialTheme.typography.titleSmall, color = Brand.NearBlack)
                val right = trailingLabel ?: if (showCounter && maxLength != null) "${value.length} / $maxLength" else null
                if (right != null) Text(right, style = MaterialTheme.typography.labelSmall, color = Brand.DarkGray)
            }
        }
        OutlinedTextField(
            value = value,
            onValueChange = { if (maxLength == null || it.length <= maxLength) onValueChange(it) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            placeholder = placeholder?.let { { Text(it, style = MaterialTheme.typography.bodyMedium, color = Brand.MidGray) } },
            leadingIcon = leading,
            singleLine = singleLine,
            minLines = minLines,
            isError = error != null,
            textStyle = MaterialTheme.typography.bodyLarge,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand.CareerBrown,
                unfocusedBorderColor = Brand.BorderInput,
                errorBorderColor = Brand.AlertRed,
                focusedContainerColor = Brand.White,
                unfocusedContainerColor = Brand.White,
                errorContainerColor = Brand.White,
                cursorColor = Brand.CareerBrown,
            ),
        )
        if (error != null) {
            Text(error, style = MaterialTheme.typography.bodySmall, color = Brand.AlertRed, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
