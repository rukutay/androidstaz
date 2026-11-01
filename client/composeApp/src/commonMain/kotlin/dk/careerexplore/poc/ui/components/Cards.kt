package dk.careerexplore.poc.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.theme.Brand
import dk.careerexplore.poc.theme.CeTheme

val CardShape = RoundedCornerShape(12.dp)

/** Standard card: 12dp radius, 1dp hairline border, no shadow (Stitch "Tier 1"). */
@Composable
fun CeCard(
    modifier: Modifier = Modifier,
    color: Color = Brand.White,
    padding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = CardShape,
        color = color,
        border = BorderStroke(1.dp, Brand.BorderSubtle),
        modifier = modifier.fillMaxWidth().then(if (onClick != null) Modifier.clip(CardShape).clickable(onClick = onClick) else Modifier),
    ) {
        Column(Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

/** Editorial block with a tan left accent, for reflection prompts and quotes (Stitch custom component). */
@Composable
fun ReflectionBlock(modifier: Modifier = Modifier, color: Color = Brand.SandMid, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = CardShape, color = color, border = BorderStroke(1.dp, Brand.BorderSubtle), modifier = modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(3.dp).fillMaxHeight().background(Brand.CareerTan))
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
        }
    }
}

/** Brand quote in Playfair italic. */
@Composable
fun QuoteBlock(text: String, modifier: Modifier = Modifier, caption: String? = null) {
    ReflectionBlock(modifier) {
        Icon(Icons.Outlined.FormatQuote, contentDescription = null, tint = Brand.CareerTan, modifier = Modifier.size(20.dp))
        Text("“$text”", style = CeTheme.extra.quote)
        caption?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray) }
    }
}

/** Quiet info note with an icon (offline notice, disclaimers, nudges). */
@Composable
fun InfoNote(icon: ImageVector, text: String, modifier: Modifier = Modifier, title: String? = null, content: @Composable ColumnScope.() -> Unit = {}) {
    Surface(shape = CardShape, color = Brand.SandMid.copy(alpha = 0.6f), modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = Brand.CareerBrown, modifier = Modifier.size(22.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                title?.let { Text(it, style = MaterialTheme.typography.titleSmall, color = Brand.NearBlack) }
                Text(text, style = MaterialTheme.typography.bodyMedium, color = Brand.DarkGray)
                content()
            }
        }
    }
}

/** Uppercase label with letter spacing (Guidelines 5.2). */
@Composable
fun Overline(text: String, modifier: Modifier = Modifier, color: Color = Brand.CareerBrown) {
    Text(text.uppercase(), style = CeTheme.extra.overline, color = color, modifier = modifier)
}

/** Fixed-size numbered circle (Step 4a rows), filled brown once the row has content. */
@Composable
fun NumberBadge(number: Int, filled: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier.size(32.dp).clip(CircleShape).background(if (filled) Brand.CareerBrown else Brand.SandMid),
        contentAlignment = Alignment.Center,
    ) {
        Text("$number", style = MaterialTheme.typography.labelMedium, color = if (filled) Brand.White else Brand.DarkGray)
    }
}

/** Round icon badge used in list rows and cards. */
@Composable
fun IconBadge(icon: ImageVector, modifier: Modifier = Modifier, container: Color = Brand.Peach, tint: Color = Brand.CareerBrown, size: Dp = 36.dp) {
    Box(modifier.size(size).clip(CircleShape).background(container), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

/**
 * Placeholder for editorial photography (Stitch used stock photos; real brand imagery comes from Career Explore,
 * Guidelines 13.2). Warm gradient + icon, no network images.
 */
@Composable
fun IllustrationBox(icon: ImageVector, modifier: Modifier = Modifier, height: Dp = 180.dp, content: @Composable ColumnScope.() -> Unit = {}) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(CardShape)
            .background(Brush.linearGradient(listOf(Brand.SandMid, Brand.Peach.copy(alpha = 0.7f), Brand.CareerTan.copy(alpha = 0.45f)))),
    ) {
        Icon(icon, contentDescription = null, tint = Brand.CareerBrown.copy(alpha = 0.35f), modifier = Modifier.align(Alignment.Center).size(height * 0.4f))
        Column(Modifier.align(Alignment.BottomStart).padding(16.dp), content = content)
    }
}
