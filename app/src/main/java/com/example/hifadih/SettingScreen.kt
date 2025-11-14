package com.example.hifadih.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme

// Data classes pour la gestion des paramètres
data class SettingItem(
    val icon: String,
    val title: String,
    val currentValue: String,
    val action: () -> Unit
)

enum class Theme(val displayName: String, val key: String) {
    LIGHT("Clair", "light"),
    DARK("Sombre", "dark")
}

enum class Language(val displayName: String, val key: String, val flag: String) {
    FRENCH("Français", "fr", "🇫🇷"),
    ENGLISH("English", "en", "🇬🇧"),
    SPANISH("Español", "es", "🇪🇸")
}

// Simulateur de gestionnaire de paramètres
object SettingsManager {
    private var currentTheme = Theme.LIGHT
    private var currentLanguage = Language.FRENCH

    fun getTheme(): Theme = currentTheme
    fun setTheme(theme: Theme) { currentTheme = theme }

    fun getLanguage(): Language = currentLanguage
    fun setLanguage(language: Language) { currentLanguage = language }
}

// PAGE PARAMETRES PRINCIPALE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageParametres(
    onBackClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    var currentTheme by remember { mutableStateOf(SettingsManager.getTheme()) }
    var currentLanguage by remember { mutableStateOf(SettingsManager.getLanguage()) }

    val settingItems = listOf(
        SettingItem(
            icon = "🎨",
            title = "Thème",
            currentValue = currentTheme.displayName,
            action = onThemeClick
        ),
        SettingItem(
            icon = "🌍",
            title = "Langue",
            currentValue = currentLanguage.displayName,
            action = onLanguageClick
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Paramètres",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Contenu
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            settingItems.forEach { item ->
                SettingItemCard(
                    item = item,
                    scale = scale,
                    modifier = Modifier.padding(bottom = (8 * scale).dp)
                )
            }
        }
    }
}

@Composable
private fun SettingItemCard(
    item: SettingItem,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { item.action() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (2 * scale).dp),
        shape = RoundedCornerShape((8 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                item.icon,
                fontSize = (20 * scale).sp,
                modifier = Modifier.padding(end = (16 * scale).dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    item.currentValue,
                    fontSize = (14 * scale).sp,
                    color = Color(0xFF666666)
                )
            }

            Text(
                "›",
                fontSize = (18 * scale).sp,
                color = Color(0xFFCCCCCC)
            )
        }
    }
}

// PAGE THEME
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageTheme(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    var selectedTheme by remember { mutableStateOf(SettingsManager.getTheme()) }

    val themes = listOf(
        Theme.LIGHT to "Interface claire et lumineuse",
        Theme.DARK to "Interface sombre pour les yeux"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Thème",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            themes.forEach { (theme, description) ->
                ThemeOptionCard(
                    theme = theme,
                    description = description,
                    isSelected = selectedTheme == theme,
                    onSelect = {
                        selectedTheme = theme
                        SettingsManager.setTheme(theme)
                    },
                    scale = scale,
                    modifier = Modifier.padding(bottom = (8 * scale).dp)
                )
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    theme: Theme,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (2 * scale).dp),
        shape = RoundedCornerShape((8 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    theme.displayName,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    description,
                    fontSize = (14 * scale).sp,
                    color = Color(0xFF666666)
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// PAGE LANGUE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageLangue(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    var selectedLanguage by remember { mutableStateOf(SettingsManager.getLanguage()) }

    val languages = listOf(
        Language.FRENCH,
        Language.ENGLISH,
        Language.SPANISH
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Langue",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            languages.forEach { language ->
                LanguageOptionCard(
                    language = language,
                    isSelected = selectedLanguage == language,
                    onSelect = {
                        selectedLanguage = language
                        SettingsManager.setLanguage(language)
                    },
                    scale = scale,
                    modifier = Modifier.padding(bottom = (8 * scale).dp)
                )
            }
        }
    }
}

@Composable
private fun LanguageOptionCard(
    language: Language,
    isSelected: Boolean,
    onSelect: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (2 * scale).dp),
        shape = RoundedCornerShape((8 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                language.flag,
                fontSize = (24 * scale).sp,
                modifier = Modifier.padding(end = (16 * scale).dp)
            )

            Text(
                language.displayName,
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

// PREVIEWS
@Preview(showBackground = true)
@Composable
fun PageParametresPreview() {
    HifadihTheme {
        PageParametres()
    }
}

@Preview(showBackground = true)
@Composable
fun PageThemePreview() {
    HifadihTheme {
        PageTheme()
    }
}

@Preview(showBackground = true)
@Composable
fun PageLanguePreview() {
    HifadihTheme {
        PageLangue()
    }
}

@Preview(showBackground = true, name = "Paramètres - Small Screen", widthDp = 320)
@Composable
fun PageParametresSmallScreenPreview() {
    HifadihTheme {
        PageParametres()
    }
}

@Preview(showBackground = true, name = "Thème - Large Screen", widthDp = 480)
@Composable
fun PageThemeLargeScreenPreview() {
    HifadihTheme {
        PageTheme()
    }
}