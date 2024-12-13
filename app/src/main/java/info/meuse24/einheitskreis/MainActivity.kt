package info.meuse24.einheitskreis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.meuse24.einheitskreis.ui.theme.EinheitskreisTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Hauptaktivität der App, die den Einheitskreis und die Sinus-/Kosinuskurven anzeigt.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setze den Inhalt auf die Hauptkomponente
        setContent {
            MainContent()
        }
    }
}

/**
 * Hauptinhalt der App, der das Layout und die Logik definiert.
 */
@Composable
fun MainContent() {
    EinheitskreisTheme { // Nutzt ein eigenes Thema für das Styling
        val angle = remember { Animatable(0f) } // Animierter Winkel (0 bis 360 Grad)

        // Animationslogik: Der Winkel erhöht sich kontinuierlich und wird zurückgesetzt
        LaunchedEffect(Unit) {
            while (true) {
                angle.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(
                        durationMillis = 3600 * 4, // 10 ms pro Grad für eine vollständige Rotation
                        easing = LinearEasing
                    )
                )
                angle.snapTo(0f) // Zurücksetzen auf 0 Grad
            }
        }

        // Erhalte die aktuelle Konfiguration
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

        // Layout basierend auf der Orientierung
        Scaffold { innerPadding ->
            if (isPortrait) {
                // Hochformat: Elemente untereinander anordnen
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    // Obere Hälfte: Anzeige des Einheitskreises
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EinheitskreisView(angle.value)
                    }

                    // Untere Hälfte: Anzeige der Sinus- und Kosinuskurven
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        KurvenView(angle.value)
                    }
                }
            } else {
                // Querformat: Elemente nebeneinander anordnen
                Row(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    // Linke Hälfte: Anzeige des Einheitskreises
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        EinheitskreisView(angle.value)
                    }

                    // Rechte Hälfte: Anzeige der Sinus- und Kosinuskurven
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        KurvenView(angle.value)
                    }
                }
            }
        }
    }
}

// ... (Die restlichen Composables bleiben unverändert)


/**
 * Zeichnet den Einheitskreis mit Achsen, einem beweglichen Punkt und der Legende.
 *
 * @param angle Aktueller Winkel (in Grad)
 */
@Composable
fun EinheitskreisView(angle: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Berechnung der Koordinaten basierend auf dem Winkel
        val theta = Math.toRadians(angle.toDouble())
        val xCoord = cos(theta)
        val yCoord = sin(theta)

        val sinColor = Color.Blue
        val cosColor = Color(255, 165, 0) // Orange

        val center = Offset(x = canvasWidth / 2, y = canvasHeight / 2 + 40)
        val radius = min(canvasWidth, canvasHeight) / 3

        // Zeichnen der Achsen
        drawLine(
            start = Offset(0f, center.y),
            end = Offset(canvasWidth, center.y),
            color = Color.Gray,
            strokeWidth = 2f
        )
        drawLine(
            start = Offset(center.x, 0f),
            end = Offset(center.x, canvasHeight),
            color = Color.Gray,
            strokeWidth = 2f
        )

        // Markierungen auf den Achsen (-1, 0, 1)
        val marks = listOf(-1f, 0f, 1f)
        for (mark in marks) {
            val xPos = center.x + mark * radius
            drawLine(
                start = Offset(xPos, center.y - 5),
                end = Offset(xPos, center.y + 5),
                color = Color.Black,
                strokeWidth = 2f
            )

            val yPos = center.y - mark * radius
            drawLine(
                start = Offset(center.x - 5, yPos),
                end = Offset(center.x + 5, yPos),
                color = Color.Black,
                strokeWidth = 2f
            )
        }

        // Zeichnen des Einheitskreises
        drawCircle(
            color = Color.Black,
            center = center,
            radius = radius,
            style = Stroke(width = 2f)
        )

        // Zeichnen des beweglichen Punktes und der Linien
        val pointOnCircle = Offset(
            x = (center.x + (xCoord * radius)).toFloat(),
            y = (center.y - (yCoord * radius)).toFloat()
        )

        drawLine(
            start = center,
            end = pointOnCircle,
            color = Color.Red,
            strokeWidth = 4f
        )

        val footOnX = Offset(pointOnCircle.x, center.y)
        drawLine(
            start = center,
            end = footOnX,
            color = cosColor,
            strokeWidth = 4f
        )
        drawLine(
            start = footOnX,
            end = pointOnCircle,
            color = sinColor,
            strokeWidth = 4f
        )

        // Hinzufügen der Beschriftungen bei 0°, 90°, 180°, 270°
        val labelOffset = 20f // Abstand der Labels vom Kreis

        // Gemeinsames Paint-Objekt für konsistente Textmessung und Zeichnung
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 40f
        }

        // Funktion zum Zeichnen von Text mit dynamischer Positionierung
        fun drawLabel(text: String, desiredPosition: Offset, align: android.graphics.Paint.Align) {
            // Setze die Textausrichtung
            paint.textAlign = align
            // Zeichne den Text an der gewünschten Position
            drawContext.canvas.nativeCanvas.drawText(
                text,
                desiredPosition.x,
                desiredPosition.y,
                paint
            )
        }

        // Funktion zum Messen der Textbreite
        fun measureTextWidth(text: String): Float {
            return paint.measureText(text)
        }

        // Funktion zum Messen der Texthöhe
        fun measureTextHeight(): Float {
            val bounds = android.graphics.Rect()
            paint.getTextBounds("Ay", 0, 2, bounds)
            return bounds.height().toFloat()
        }

        val textHeight = measureTextHeight()

        // 0 Grad (1, 0)
        val label0 = "+1.0"
        val label0Width = measureTextWidth(label0)
        drawLabel(
            label0,
            Offset(center.x + radius  + label0Width / 4, center.y + textHeight),
            android.graphics.Paint.Align.LEFT
        )

        // 90 Grad (0, 1)
        val label90 = "+1.0"
        val label90Width = measureTextWidth(label90)
        drawLabel(
            label90,
            Offset(center.x+label90Width/2+10, center.y - radius - labelOffset),
            android.graphics.Paint.Align.CENTER
        )

        // 180 Grad (-1, 0)
        val label180 = "-1.0"
        val label180Width = measureTextWidth(label180)
        drawLabel(
            label180,
            Offset(center.x - radius - labelOffset - label180Width, center.y + textHeight),
            android.graphics.Paint.Align.LEFT
        )

        // 270 Grad (0, -1)
        val label270 = "-1.0"
        //val label270Width = measureTextWidth(label270)
        drawLabel(
            label270,
            Offset(center.x+label90Width/2+10, center.y + radius + labelOffset + textHeight),
            android.graphics.Paint.Align.CENTER
        )

        // Beschriftung der x-Achse
        val labelX = "x"
        val labelXWidth = measureTextWidth(labelX)
        drawLabel(
            labelX,
            Offset( labelXWidth / 2, center.y + textHeight),
            android.graphics.Paint.Align.LEFT
        )

        // Beschriftung der y-Achse
        val labelY = "y"
        //val labelYWidth = measureTextWidth(labelY)
        drawLabel(
            labelY,
            Offset(center.x+label90Width/3,  textHeight),
            android.graphics.Paint.Align.CENTER
        )
    }

    // Beschriftung und Legende mit Compose-Texten
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Einheitskreis", style = TextStyle(fontSize = 23.sp))
        Text("Winkel (θ): ${angle.toInt()}°", style = TextStyle(fontSize = 17.sp))
        Text("Ankathete (cos): %.1f".format(cos(Math.toRadians(angle.toDouble()))), style = TextStyle(fontSize = 17.sp))
        Text("Gegenkathete (sin): %.1f".format(sin(Math.toRadians(angle.toDouble()))), style = TextStyle(fontSize = 17.sp))
        Text("Hypotenuse: 1.0", style = TextStyle(fontSize = 17.sp))
    }
}




/**
 * Zeichnet die Sinus- und Kosinuskurven.
 *
 * @param angle Aktueller Winkel (in Grad)
 */
@Composable
fun KurvenView(angle: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val sinColor = Color.Blue
        val cosColor = Color(255, 165, 0) // Orange

        val maxAngle = 360f
        val pointsCount = 360
        val step = maxAngle / pointsCount

        val padding = 50f
        val curveWidth = canvasWidth - 2 * padding
        val curveHeight = canvasHeight - 2 * padding

        drawLine(
            start = Offset(padding, padding),
            end = Offset(padding, canvasHeight - padding),
            color = Color.Gray,
            strokeWidth = 2f
        )
        drawLine(
            start = Offset(padding, canvasHeight / 2),
            end = Offset(canvasWidth - padding, canvasHeight / 2),
            color = Color.Gray,
            strokeWidth = 2f
        )

        val sinPath = Path()
        val cosPath = Path()

        val currentPoint = (angle / maxAngle * pointsCount).toInt().coerceIn(0, pointsCount)

        for (i in 0..currentPoint) {
            val currentAngle = i * step
            val theta = Math.toRadians(currentAngle.toDouble())
            val sinValue = sin(theta).toFloat()
            val cosValue = cos(theta).toFloat()

            val x = padding + (currentAngle / maxAngle) * curveWidth
            val sinY = canvasHeight / 2 - sinValue * (curveHeight / 2)
            val cosY = canvasHeight / 2 - cosValue * (curveHeight / 2)

            if (i == 0) {
                sinPath.moveTo(x, sinY)
                cosPath.moveTo(x, cosY)
            } else {
                sinPath.lineTo(x, sinY)
                cosPath.lineTo(x, cosY)
            }
        }

        drawPath(path = sinPath, color = sinColor, style = Stroke(width = 4f))
        drawPath(path = cosPath, color = cosColor, style = Stroke(width = 4f))

        if (currentPoint in 0..pointsCount) {
            val theta = Math.toRadians(angle.toDouble())
            val sinValue = sin(theta).toFloat()
            val cosValue = cos(theta).toFloat()

            val x = padding + (angle / maxAngle) * curveWidth
            val sinY = canvasHeight / 2 - sinValue * (curveHeight / 2)
            val cosY = canvasHeight / 2 - cosValue * (curveHeight / 2)

            drawCircle(color = sinColor, radius = 8f, center = Offset(x, sinY))
            drawCircle(color = cosColor, radius = 8f, center = Offset(x, cosY))
        }
    }

    // Legende mit Compose-Texten
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Sinus (sin)", style = TextStyle(fontSize = 18.sp, color = Color.Blue))
        Text("Kosinus (cos)", style = TextStyle(fontSize = 18.sp, color = Color(255, 165, 0)))
    }
}
