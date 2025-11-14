package com.example.hifadih.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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

data class SecurityOption(
    val id: String,
    val titre: String,
    val description: String,
    val icone: String,
    var isActive: Boolean,
    val hasToggle: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageSecurite(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    var securityOptions by remember {
        mutableStateOf(
            listOf(
                SecurityOption(
                    "pin",
                    "Code PIN",
                    "Modifier votre code PIN",
                    "🔢",
                    true,
                    false
                ),
                SecurityOption(
                    "biometrie",
                    "Authentification biométrique",
                    "Empreinte digitale et reconnaissance faciale",
                    "👆",
                    true,
                    true
                ),
                SecurityOption(
                    "notification_connexion",
                    "Notifications de connexion",
                    "Recevoir une alerte à chaque connexion",
                    "🔔",
                    true,
                    true
                ),
                SecurityOption(
                    "double_auth",
                    "Double authentification",
                    "Vérification par SMS pour les transactions",
                    "📱",
                    false,
                    true
                ),
                SecurityOption(
                    "sessions",
                    "Sessions actives",
                    "Gérer vos connexions actives",
                    "🖥️",
                    true,
                    false
                )
            )
        )
    }

    val securityTips = remember {
        listOf(
            "💡" to "Utilisez un code PIN unique que vous seul connaissez",
            "💡" to "Activez la double authentification pour plus de sécurité",
            "💡" to "Vérifiez régulièrement vos sessions actives"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        TopAppBar(
            title = {
                Text(
                    "Sécurité",
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
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF00B894)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            // Options de sécurité
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
                shape = RoundedCornerShape((16 * scale).dp)
            ) {
                Column {
                    securityOptions.forEachIndexed { index, option ->
                        SecurityOptionItem(
                            option = option,
                            onClick = {
                                if (!option.hasToggle) {
                                    // Navigation vers page spécifique
                                    when (option.id) {
                                        "pin" -> { /* Navigation vers changement de PIN */ }
                                        "sessions" -> { /* Navigation vers gestion des sessions */ }
                                    }
                                }
                            },
                            onToggle = {
                                if (option.hasToggle) {
                                    securityOptions = securityOptions.map {
                                        if (it.id == option.id) it.copy(isActive = !it.isActive) else it
                                    }
                                }
                            },
                            scale = scale
                        )

                        if (index < securityOptions.size - 1) {
                            HorizontalDivider(
                                color = Color(0xFFF1F2F6),
                                modifier = Modifier.padding(horizontal = (20 * scale).dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Section conseils de sécurité
            SecurityTipsSection(securityTips, scale)
        }
    }
}

@Composable
private fun SecurityOptionItem(
    option: SecurityOption,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    scale: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!option.hasToggle) onClick() }
            .padding((20 * scale).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône avec background
        Box(
            modifier = Modifier
                .size((45 * scale).dp)
                .background(
                    Color(0xFF00B894).copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                option.icone,
                fontSize = (20 * scale).sp
            )
        }

        Spacer(modifier = Modifier.width((16 * scale).dp))

        // Contenu principal
        Column(modifier = Modifier.weight(1f)) {
            Text(
                option.titre,
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3436)
            )
            Text(
                option.description,
                fontSize = (13 * scale).sp,
                color = Color(0xFF636E72),
                modifier = Modifier.padding(top = (2 * scale).dp)
            )
        }

        // Toggle ou flèche
        if (option.hasToggle) {
            Switch(
                checked = option.isActive,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF00B894),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        } else {
            Text(
                "›",
                fontSize = (20 * scale).sp,
                color = Color(0xFFDDD)
            )
        }
    }
}

@Composable
private fun SecurityTipsSection(
    tips: List<Pair<String, String>>,
    scale: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
        shape = RoundedCornerShape((16 * scale).dp)
    ) {
        Column(
            modifier = Modifier.padding((20 * scale).dp)
        ) {
            Text(
                "Conseils de sécurité",
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
                modifier = Modifier.padding(bottom = (16 * scale).dp)
            )

            Surface(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape((12 * scale).dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding((16 * scale).dp)
                ) {
                    tips.forEachIndexed { index, (icon, tip) ->
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                icon,
                                fontSize = (16 * scale).sp,
                                modifier = Modifier.padding(end = (12 * scale).dp)
                            )
                            Text(
                                tip,
                                fontSize = (14 * scale).sp,
                                color = Color(0xFF636E72),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (index < tips.size - 1) {
                            Spacer(modifier = Modifier.height((12 * scale).dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PageSecuritePreview() {
    HifadihTheme {
        PageSecurite()
    }
}