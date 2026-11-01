package dk.careerexplore.poc.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dk.careerexplore.poc.resources.Res
import dk.careerexplore.poc.resources.inter_medium
import dk.careerexplore.poc.resources.inter_regular
import dk.careerexplore.poc.resources.inter_semibold
import dk.careerexplore.poc.resources.playfair_italic
import dk.careerexplore.poc.resources.playfair_regular
import dk.careerexplore.poc.resources.playfair_semibold_italic
import org.jetbrains.compose.resources.Font

private val CareerColorScheme = lightColorScheme(
    primary = Brand.CareerBrown,
    onPrimary = Brand.White,
    primaryContainer = Brand.Peach,
    onPrimaryContainer = Brand.CareerBrownPressed,
    secondary = Brand.CareerTan,
    onSecondary = Brand.White,
    secondaryContainer = Brand.SandMid,
    onSecondaryContainer = Brand.CareerBrown,
    background = Brand.Sand,
    onBackground = Brand.NearBlack,
    surface = Brand.Sand,
    onSurface = Brand.NearBlack,
    surfaceVariant = Brand.SandMid,
    onSurfaceVariant = Brand.DarkGray,
    surfaceContainer = Brand.White,
    outline = Brand.BorderInput,
    outlineVariant = Brand.BorderSubtle,
    error = Brand.AlertRed,
)

/** Styles outside the Material scale: brand quotes and uppercase labels (Guidelines 5.2). */
@Immutable
data class ExtraType(val quote: TextStyle, val quoteEmphasis: TextStyle, val overline: TextStyle)

val LocalExtraType = staticCompositionLocalOf { ExtraType(TextStyle.Default, TextStyle.Default, TextStyle.Default) }

object CeTheme {
    val extra: ExtraType
        @Composable get() = LocalExtraType.current
}

@Composable
fun CareerExploreTheme(content: @Composable () -> Unit) {
    val inter = FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_medium, FontWeight.Medium),
        Font(Res.font.inter_semibold, FontWeight.SemiBold),
    )
    val playfair = FontFamily(
        Font(Res.font.playfair_regular, FontWeight.Normal),
        Font(Res.font.playfair_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.playfair_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    )

    // Playfair uses old-style figures by default ("Step 1" reads like "Step ı"); "lnum" switches to lining figures
    val lining = "lnum"

    // Stitch "Warm Editorial Exploration" scale: Playfair for narrative headings only, Inter for everything interactive
    val typography = Typography(
        displaySmall = TextStyle(fontFamily = playfair, fontSize = 28.sp, lineHeight = 36.sp, fontFeatureSettings = lining),
        headlineMedium = TextStyle(fontFamily = playfair, fontSize = 24.sp, lineHeight = 32.sp, fontFeatureSettings = lining),
        headlineSmall = TextStyle(fontFamily = playfair, fontSize = 20.sp, lineHeight = 28.sp, fontFeatureSettings = lining),
        titleLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 26.sp),
        titleMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
        titleSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = TextStyle(fontFamily = inter, fontSize = 16.sp, lineHeight = 26.sp),
        bodyMedium = TextStyle(fontFamily = inter, fontSize = 14.sp, lineHeight = 22.sp),
        bodySmall = TextStyle(fontFamily = inter, fontSize = 13.sp, lineHeight = 18.sp),
        labelLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 20.sp),
        labelMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 16.sp),
        labelSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 14.sp),
    )
    val extra = ExtraType(
        quote = TextStyle(fontFamily = playfair, fontStyle = FontStyle.Italic, fontSize = 20.sp, lineHeight = 28.sp, color = Brand.CareerBrownPressed, fontFeatureSettings = lining),
        quoteEmphasis = TextStyle(fontFamily = playfair, fontStyle = FontStyle.Italic, fontWeight = FontWeight.SemiBold, color = Brand.CareerBrown),
        overline = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.12.em),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
    )

    CompositionLocalProvider(LocalExtraType provides extra) {
        MaterialTheme(colorScheme = CareerColorScheme, typography = typography, shapes = shapes, content = content)
    }
}
