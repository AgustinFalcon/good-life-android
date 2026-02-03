package com.agusstkd.goodlife.presentation.components.header

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agusstkd.goodlife.R

/**
 * Icono de calendario personalizado que muestra el día del mes.
 *
 * Combina una imagen de cabecera con un cuerpo dibujado en Canvas
 * y el número del día centrado con efecto de borde.
 *
 * @param day Número del día a mostrar (1-31)
 * @param modifier Modificador de Compose (usar .size() para definir tamaño)
 */
@Composable
fun CalendarDayIcon(
    day: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val iconSize = this.maxWidth

        // Cabecera del calendario (imagen)
        Image(
            painter = painterResource(id = R.drawable.ic_header_calendar_red),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.38f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds
        )

        // Cuerpo del calendario (Canvas)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val radius = width * 0.22f
            val bodyTop = height * 0.38f
            val border = width * 0.04f

            // Fondo del cuerpo
            drawRect(
                color = Color(0xFFF5F5F5),
                topLeft = Offset(0f, bodyTop),
                size = Size(width, height - bodyTop - radius)
            )

            // Esquinas redondeadas inferiores
            drawRoundRect(
                color = Color(0xFFF5F5F5),
                topLeft = Offset(0f, height - radius * 2f),
                size = Size(width, radius * 2f),
                cornerRadius = CornerRadius(radius, radius)
            )

            val roundedStartY = height - radius * 2f

            // Bordes laterales
            drawRect(
                color = Color.Black,
                topLeft = Offset(0f, bodyTop),
                size = Size(border, roundedStartY - bodyTop)
            )
            drawRect(
                color = Color.Black,
                topLeft = Offset(width - border, bodyTop),
                size = Size(border, roundedStartY - bodyTop)
            )

            // Borde inferior curvo
            val bottomPath = Path().apply {
                moveTo(border / 2f, roundedStartY)
                lineTo(border / 2f, height - radius)
                quadraticTo(border / 2f, height - border / 2f, radius, height - border / 2f)
                lineTo(width - radius, height - border / 2f)
                quadraticTo(width - border / 2f, height - border / 2f, width - border / 2f, height - radius)
                lineTo(width - border / 2f, roundedStartY)
            }
            drawPath(
                path = bottomPath,
                color = Color.Black,
                style = Stroke(width = border, join = StrokeJoin.Round)
            )
        }

        // Número del día
        val baseFontSize = iconSize.value * 0.55f
        val fontSize = if (day < 10) baseFontSize.sp else (baseFontSize * 0.9f).sp
        val strokeWidth = iconSize.value * 0.09f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            // Borde del número
            Text(
                text = day.toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(drawStyle = Stroke(width = strokeWidth, join = StrokeJoin.Round))
            )
            // Número con gradiente
            Text(
                text = day.toString(),
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF6A6A6A), Color(0xFF4A4A4A), Color(0xFF3A3A3A))
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarDayIconPreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarDayIcon(day = 2, modifier = Modifier.size(42.dp))
        CalendarDayIcon(day = 15, modifier = Modifier.size(42.dp))
        CalendarDayIcon(day = 22, modifier = Modifier.size(42.dp))
        CalendarDayIcon(day = 31, modifier = Modifier.size(42.dp))
    }
}
