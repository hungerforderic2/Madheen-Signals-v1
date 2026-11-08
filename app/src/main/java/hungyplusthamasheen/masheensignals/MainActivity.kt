package com.hungyplusthamasheen.masheensignals

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Signal(
    val id: String,
    val label: String,
    val color: Color,
    val description: String,
    val pattern: LongArray
)

private val signals = listOf(
    Signal("AFFECTION", "Affection", Color(0xFFF2AFC5), "Gentle, flowing double pulse", longArrayOf(0, 45, 90, 120)),
    Signal("JOY", "Joy", Color(0xFFFFD54F), "Light, buoyant triple pulse", longArrayOf(0, 35, 55, 35, 55, 35)),
    Signal("CURIOSITY", "Curiosity", Color(0xFF4DD0E1), "Two questioning pulses", longArrayOf(0, 55, 130, 85)),
    Signal("CALM", "Calm", Color(0xFF80BFFF), "Slow, sustained settling pulse", longArrayOf(0, 180)),
    Signal("FRUSTRATION", "Frustration", Color(0xFFE85D5D), "Firm, interrupted double pulse", longArrayOf(0, 100, 45, 100)),
    Signal("SADNESS", "Sadness", Color(0xFF7986CB), "Slow, spaced pulse", longArrayOf(0, 100, 220, 100)),
    Signal("EXCITEMENT", "Excitement", Color(0xFFFFA44A), "Quick energetic sequence", longArrayOf(0, 45, 45, 45, 45, 90)),
    Signal("CELEBRATION", "Celebration", Color(0xFFCE93D8), "Layered celebratory rhythm", longArrayOf(0, 40, 65, 70, 65, 100))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MasheenSignalsApp(this) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasheenSignalsApp(context: Context) {
    var selected by remember { mutableStateOf(signals.first()) }
    var intensity by remember { mutableFloatStateOf(3f) }
    var hapticsOn by remember { mutableStateOf(true) }
    var soundOn by remember { mutableStateOf(true) }
    var status by remember { mutableStateOf("Ready to transmit a local preview.") }

    val level = intensity.toInt()
    val durationScale = 0.65f + (level - 1) * 0.25f

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF69415B),
            secondary = Color(0xFF397C83),
            background = Color(0xFFF8F5F7)
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("MASHEEN SIGNALS", fontSize = 18.sp)
                            Text("Haptic Language Protocol · v0.1", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Choose a signal", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(signals) { signal ->
                        FilterChip(
                            selected = selected.id == signal.id,
                            onClick = { selected = signal },
                            label = { Text(signal.label) }
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = selected.color.copy(alpha = 0.28f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).background(selected.color, RoundedCornerShape(36.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("●", color = Color.White, fontSize = 30.sp) }
                        Text(selected.label.uppercase(), style = MaterialTheme.typography.headlineSmall)
                        Text(selected.description, style = MaterialTheme.typography.bodyMedium)
                        Text("SIGNAL ${level}/5", style = MaterialTheme.typography.labelLarge)
                        LinearProgressIndicator(
                            progress = { level / 5f },
                            modifier = Modifier.fillMaxWidth(0.72f),
                            color = selected.color
                        )
                    }
                }

                Text("Intensity", style = MaterialTheme.typography.titleMedium)
                Slider(value = intensity, onValueChange = { intensity = it }, valueRange = 1f..5f, steps = 3)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtle", style = MaterialTheme.typography.labelSmall)
                    Text("Maximum comfortable", style = MaterialTheme.typography.labelSmall)
                }

                Card {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Haptic feedback")
                            Switch(checked = hapticsOn, onCheckedChange = { hapticsOn = it })
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sound")
                            Switch(checked = soundOn, onCheckedChange = { soundOn = it })
                        }
                    }
                }

                Button(
                    onClick = {
                        if (hapticsOn) playHaptic(context, selected.pattern, durationScale)
                        if (soundOn) playTone(level)
                        status = "Preview played: ${selected.label}, intensity $level/5."
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("PREVIEW SIGNAL") }

                Text(status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Text(
                    "Local preview only · No signal is sent to another device. Signal labels describe authored communication, not machine feelings.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun playHaptic(context: Context, basePattern: LongArray, scale: Float) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (!vibrator.hasVibrator()) return

    val pattern = basePattern.mapIndexed { index, value ->
        if (index == 0 || index % 2 == 0) value else (value * scale).toLong()
    }.toLongArray()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(pattern, -1)
    }
}

private fun playTone(level: Int) {
    val tone = ToneGenerator(AudioManager.STREAM_MUSIC, (35 + level * 10).coerceAtMost(85))
    tone.startTone(ToneGenerator.TONE_PROP_ACK, 140 + level * 35)
    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ tone.release() }, 500)
}
