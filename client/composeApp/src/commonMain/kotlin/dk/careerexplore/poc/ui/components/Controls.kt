package dk.careerexplore.poc.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dk.careerexplore.poc.theme.Brand
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

/** Thin 4dp step progress bar, brown fill on a hairline track, animated (Guidelines 5.4 / 5.6). */
@Composable
fun StepProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val animated by animateFloatAsState(progress.coerceIn(0f, 1f), animationSpec = tween(400))
    Box(modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Brand.BorderSubtle)) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(animated).clip(RoundedCornerShape(2.dp)).background(Brand.CareerBrown))
    }
}

/**
 * Dashboard progress ring: 4 arc segments, one per step, each in its own shade when complete.
 * Tapping a segment opens that step (Guidelines S-05).
 */
@Composable
fun ProgressRing(
    done: List<Boolean>,
    modifier: Modifier = Modifier,
    size: Dp = 184.dp,
    stroke: Dp = 14.dp,
    onSegmentClick: (Int) -> Unit = {},
) {
    val percent = (done.count { it } * 100f / done.size).roundToInt()
    Box(
        modifier
            .size(size)
            .semantics { contentDescription = "Journey progress $percent percent" }
            .pointerInput(done) {
                detectTapGestures { offset ->
                    val c = Offset(this.size.width / 2f, this.size.height / 2f)
                    val deg = atan2((offset.y - c.y).toDouble(), (offset.x - c.x).toDouble()) * 180.0 / PI
                    val fromTop = ((deg + 90 + 360) % 360)
                    onSegmentClick((fromTop / (360.0 / done.size)).toInt().coerceIn(0, done.lastIndex))
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val strokePx = stroke.toPx()
            val inset = strokePx / 2
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            val gap = 10f
            val sweep = 360f / done.size
            done.forEachIndexed { i, complete ->
                drawArc(
                    color = if (complete) Brand.RingSegments[i % Brand.RingSegments.size] else Brand.BorderSubtle,
                    startAngle = -90f + i * sweep + gap / 2,
                    sweepAngle = sweep - gap,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", style = MaterialTheme.typography.displaySmall, color = Brand.NearBlack)
            Text("${done.count { it }} of ${done.size} steps", style = MaterialTheme.typography.bodySmall, color = Brand.DarkGray)
        }
    }
}

/** Compact pill segmented control (importance selector, timeframe). */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brand.SandMid)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        options.forEachIndexed { i, label ->
            val selected = i == selectedIndex
            Box(
                Modifier
                    .weight(1f)
                    .heightIn(min = 34.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(if (selected) Brand.CareerBrown else Brand.SandMid)
                    .clickable(role = Role.RadioButton) { onSelect(i) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) Brand.White else Brand.DarkGray,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
