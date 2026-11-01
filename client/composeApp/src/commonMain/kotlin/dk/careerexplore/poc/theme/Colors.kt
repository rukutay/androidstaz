package dk.careerexplore.poc.theme

import androidx.compose.ui.graphics.Color

// Brand colours, Development Guidelines section 5.1 (+ supporting tones from the Stitch design system)
object Brand {
    val CareerBrown = Color(0xFF8B5E3C)
    val CareerBrownPressed = Color(0xFF704A2F)
    val CareerTan = Color(0xFFC4956A)
    val Sand = Color(0xFFF5F0E8)
    val SandMid = Color(0xFFEDE5D8)
    val NearBlack = Color(0xFF1A1A1A)
    val DarkGray = Color(0xFF555555)
    val MidGray = Color(0xFF999999)
    val White = Color(0xFFFFFFFF)
    val AlertRed = Color(0xFFC0392B)
    val SoftGreen = Color(0xFF27AE60)

    val BorderSubtle = Color(0xFFE0D8CE)
    val BorderInput = Color(0xFFD0C8BE)
    val Disabled = Color(0xFFE0E0E0)

    /** Soft peach badge background used in Stitch for counters and step badges. */
    val Peach = Color(0xFFFFDCC5)

    /** Progress ring segments, one shade per step. */
    val RingSegments = listOf(Color(0xFF8B5E3C), Color(0xFFA9774E), Color(0xFFC4956A), Color(0xFFD9B48F))
}
