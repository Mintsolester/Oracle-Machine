package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgedGold
import com.example.ui.theme.BrightGold
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.GlassCard
import com.example.ui.theme.MutedSlate
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ParchmentWhite
import com.example.ui.theme.ShadowRed
import com.example.ui.theme.VintageInk
import kotlin.math.cos
import kotlin.math.sin

/**
 * A beautiful, deep cosmic underlay showing rotating concentric rings
 * (representing celestial dials) and glowing particles that flicker slowly.
 */
@Composable
fun AetherStarsCanvas(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    // Animating angles
    val ringAngle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val ringAngle2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val starAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = SineCrossingEasing()),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier.fillMaxSize().background(ObsidianBlack)) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2.2f)

        // Draw radial star glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x2A5E1928), Color.Transparent),
                center = center,
                radius = width * 0.9f
            ),
            center = center,
            radius = width * 1.2f
        )

        // Concentric dial 1 (Inner Dial)
        drawCircle(
            color = Color(0x33C5A059),
            center = center,
            radius = 180.dp.toPx(),
            style = Stroke(width = 1.5f.dp.toPx())
        )

        // Celestial dots orbiting ring 1
        val r1 = 180.dp.toPx()
        val a1Rad = Math.toRadians(ringAngle1.toDouble())
        val orbitPos1 = Offset(
            (center.x + r1 * cos(a1Rad)).toFloat(),
            (center.y + r1 * sin(a1Rad)).toFloat()
        )
        drawCircle(
            color = AgedGold,
            radius = 4.dp.toPx(),
            center = orbitPos1
        )

        // Concentric dial 2 (Outer Dial)
        drawCircle(
            color = Color(0x1F8B8D99),
            center = center,
            radius = 260.dp.toPx(),
            style = Stroke(
                width = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    floatArrayOf(15f, 15f), 0f
                )
            )
        )

        // Outer dot orbiting ring 2
        val r2 = 260.dp.toPx()
        val a2Rad = Math.toRadians(ringAngle2.toDouble())
        val orbitPos2 = Offset(
            (center.x + r2 * cos(a2Rad)).toFloat(),
            (center.y + r2 * sin(a2Rad)).toFloat()
        )
        drawCircle(
            color = Color(0xFFC5A059),
            radius = 5.5f.dp.toPx(),
            center = orbitPos2
        )

        // Draw deep cosmic stationary dots (stars)
        val seedPoints = listOf(
            Offset(0.15f, 0.25f), Offset(0.85f, 0.15f), Offset(0.35f, 0.75f),
            Offset(0.72f, 0.65f), Offset(0.20f, 0.55f), Offset(0.80f, 0.50f),
            Offset(0.50f, 0.12f), Offset(0.48f, 0.88f), Offset(0.90f, 0.82f)
        )

        seedPoints.forEachIndexed { index, point ->
            val factor = if (index % 2 == 0) starAlpha else (1.1f - starAlpha)
            drawCircle(
                color = when (index % 3) {
                    0 -> AgedGold
                    1 -> Color(0xFFFFFFFF)
                    else -> ShadowRed
                },
                radius = (1.5f + (index % 3)) * 4f * factor,
                center = Offset(point.x * width, point.y * height),
                alpha = factor
            )
        }
    }
}

private class SineCrossingEasing : Easing {
    override fun transform(fraction: Float): Float {
        return (sin(fraction * Math.PI - Math.PI / 2) + 1).toFloat() / 2f
    }
}

/**
 * A luxury futuristic obsidian glass terminal card with subtle gold accents
 */
@Composable
fun TerminalGlassCard(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.dp,
    borderColor: Color = AgedGold.copy(alpha = 0.5f),
    elevation: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(12.dp), ambientColor = Color.Black, spotColor = AgedGold)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = GlassCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            content()
        }
    }
}

/**
 * Physical aged parchment scroll component for historical clipping outputs.
 */
@Composable
fun ParchmentScroll(
    modifier: Modifier = Modifier,
    tornEdges: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .graphicsLayer(rotationZ = -0.5f)
            .shadow(16.dp, RoundedCornerShape(2.dp))
            .background(ParchmentWhite)
            .border(
                width = 3.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE2DCCE), Color(0xFFC7BDAC))
                ),
                shape = RoundedCornerShape(2.dp)
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Vintage print aesthetic header
            if (tornEdges) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .drawBehind {
                            drawLine(
                                color = VintageInk.copy(alpha = 0.3f),
                                start = Offset(0f, size.height + 4.dp.toPx()),
                                end = Offset(size.width, size.height + 4.dp.toPx()),
                                strokeWidth = 2.dp.toPx()
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ARCHIVAL LEAF",
                        color = VintageInk.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "CODENAME: ORACLE",
                        color = VintageInk.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            content()
        }
    }
}

/**
 * Dynamic golden subtitle or status text with subtle inner glow effects.
 */
@Composable
fun GoldGlowText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    fontWeight: FontWeight = FontWeight.Bold,
    fontSize: Float = 22f,
    fontFamily: FontFamily = FontFamily.Serif
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Drop gold-tinted shadow
        Text(
            text = text,
            color = Color(0x33FFA000),
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            textAlign = textAlign,
            modifier = Modifier.offset(y = 2.dp)
        )
        // Foreground gold
        Text(
            text = text,
            color = AgedGold,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            textAlign = textAlign,
            letterSpacing = 1.5.sp
        )
    }
}
