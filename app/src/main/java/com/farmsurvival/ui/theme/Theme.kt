package com.farmsurvival.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val FarmLightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFFFF9800),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = Color(0xFF2196F3),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBBDEFB),
    onTertiaryContainer = Color(0xFF0D47A1),
    error = Color(0xFFF44336),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFFF8E1),
    onBackground = Color(0xFF3E2723),
    surface = Color(0xFFFFFDE7),
    onSurface = Color(0xFF3E2723),
    surfaceVariant = Color(0xFFF1F8E9),
    onSurfaceVariant = Color(0xFF558B2F)
)

private val FarmDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFFE65100),
    secondaryContainer = Color(0xFFBF360C),
    onSecondaryContainer = Color(0xFFFFE0B2),
    tertiary = Color(0xFF64B5F6),
    onTertiary = Color(0xFF0D47A1),
    tertiaryContainer = Color(0xFF1565C0),
    onTertiaryContainer = Color(0xFFBBDEFB),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFC62828),
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF1B2E1B),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF2E3B2E),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF3E4F3E),
    onSurfaceVariant = Color(0xFFA5D6A7)
)

@Composable
fun FarmSurvivalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> FarmDarkColorScheme
        else -> FarmLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
