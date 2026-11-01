package dk.careerexplore.poc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.theme.Brand

private val ButtonShape = RoundedCornerShape(8.dp)
private val ButtonPadding = PaddingValues(horizontal = 16.dp)

@Composable
private fun ButtonContent(text: String, leadingIcon: ImageVector?, trailingIcon: ImageVector?) {
    leadingIcon?.let {
        Icon(it, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
    }
    Text(text, style = MaterialTheme.typography.labelLarge)
    trailingIcon?.let {
        Spacer(Modifier.width(8.dp))
        Icon(it, contentDescription = null, modifier = Modifier.size(18.dp))
    }
}

/** Main CTA (Save, Continue, Generate). */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        colors = ButtonDefaults.buttonColors(
            containerColor = Brand.CareerBrown,
            contentColor = Brand.White,
            disabledContainerColor = Brand.Disabled,
            disabledContentColor = Brand.MidGray,
        ),
    ) { ButtonContent(text, leadingIcon, trailingIcon) }
}

/** Secondary actions (Back, View). */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        border = BorderStroke(1.dp, if (enabled) Brand.CareerBrown else Brand.Disabled),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Brand.White, contentColor = Brand.CareerBrown),
    ) { ButtonContent(text, leadingIcon, trailingIcon) }
}

/** Tertiary actions (Skip, Cancel, inline links). */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 44.dp),
        shape = ButtonShape,
        colors = ButtonDefaults.textButtonColors(contentColor = Brand.CareerBrown),
    ) { ButtonContent(text, leadingIcon, trailingIcon) }
}
