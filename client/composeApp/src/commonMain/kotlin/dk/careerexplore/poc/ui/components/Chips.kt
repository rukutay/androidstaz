package dk.careerexplore.poc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.theme.Brand

private val PillShape = RoundedCornerShape(20.dp)

/** Added item tag with a remove "x" (Interests collection). */
@Composable
fun RemovableChip(text: String, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Surface(shape = PillShape, color = Brand.SandMid, modifier = modifier) {
        Row(
            Modifier.heightIn(min = 32.dp).padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium, color = Brand.CareerBrownPressed)
            Icon(
                Icons.Outlined.Close,
                contentDescription = "Remove $text",
                tint = Brand.CareerBrown,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(28.dp)
                    .clickable(role = Role.Button, onClick = onRemove)
                    .padding(6.dp)
                    .alpha(0.7f),
            )
        }
    }
}

/** Tap-to-select pill (top 2 picker, context tags, category tabs). */
@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showCheck: Boolean = true,
) {
    Surface(
        shape = PillShape,
        color = if (selected) Brand.CareerBrown else Brand.SandMid,
        modifier = modifier
            .alpha(if (enabled || selected) 1f else 0.5f)
            .clickable(enabled = enabled, role = Role.Checkbox, onClick = onClick),
    ) {
        Row(
            Modifier.heightIn(min = 36.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium, color = if (selected) Brand.White else Brand.CareerBrownPressed)
            if (selected && showCheck) Icon(Icons.Outlined.Check, contentDescription = null, tint = Brand.White, modifier = Modifier.size(16.dp))
        }
    }
}

/** Suggestion to add with a "+" (suggestion drawer, examples). */
@Composable
fun AddChip(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Surface(
        shape = PillShape,
        color = Brand.White,
        border = BorderStroke(1.dp, Brand.BorderSubtle),
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
    ) {
        Row(
            Modifier.heightIn(min = 36.dp).padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(text, style = MaterialTheme.typography.labelMedium, color = Brand.NearBlack)
            Icon(if (enabled) Icons.Outlined.Add else Icons.Outlined.Check, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(16.dp))
        }
    }
}

/** Small status pill, e.g. "12 added - aim for 20+" or "Step 2 of 4". */
@Composable
fun Badge(
    text: String,
    modifier: Modifier = Modifier,
    container: Color = Brand.Peach,
    content: Color = Brand.CareerBrownPressed,
    icon: ImageVector? = null,
) {
    Surface(shape = PillShape, color = container, modifier = modifier) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            icon?.let { Icon(it, contentDescription = null, tint = content, modifier = Modifier.size(14.dp)) }
            Text(text, style = MaterialTheme.typography.labelSmall, color = content)
        }
    }
}
