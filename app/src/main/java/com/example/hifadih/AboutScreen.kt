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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme

data class AppInfo(
    val version: String,
    val buildNumber: String,
    val lastUpdate: String,
    val size: String
)

data class FeatureInfo(
    val icon: String,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageAPropos(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    val appInfo = remember {
        AppInfo(
            version = "2.1.0",
            buildNumber = "210",
            lastUpdate = "15 Août 2025",
            size = "45.2 MB"
        )
    }

    val features = remember {
        listOf(
            FeatureInfo(
                "💸",
                "Transferts d'argent",
                "Envoyez de l'argent rapidement et en sécurité"
            ),
            FeatureInfo(
                "📱",
                "Recharge mobile",
                "Rechargez tous les opérateurs de Côte d'Ivoire"
            ),
            FeatureInfo(
                "💳",
                "Paiements marchands",
                "Payez vos achats chez nos partenaires"
            ),
            FeatureInfo(
                "🔒",
                "Sécurité avancée",
                "Biométrie, codes PIN et chiffrement de bout en bout"
            )
        )
    }

    val legalOptions = remember {
        listOf(
            "📋" to "Conditions d'utilisation",
            "🔒" to "Politique de confidentialité",
            "⚖️" to "Mentions légales"
        )
    }

    val socialNetworks = remember {
        listOf(
            "📘" to Color(0xFF3B5998),
            "🐦" to Color(0xFF1DA1F2),
            "📷" to Color(0xFFE4405F),
            "💼" to Color(0xFF0077B5)
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
                    "À propos",
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
                containerColor = Color(0xFF636E72)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            // Logo et informations de base
            AppLogoSection(appInfo, scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Informations techniques
            AppInfoSection(appInfo, scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Fonctionnalités
            FeaturesSection(features, scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Liens légaux
            LegalSection(legalOptions, scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Footer avec réseaux sociaux
            FooterSection(socialNetworks, scale)
        }
    }
}

@Composable
private fun AppLogoSection(
    appInfo: AppInfo,
    scale: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
        shape = RoundedCornerShape((16 * scale).dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding((30 * scale).dp)
        ) {
            // Logo de l'app
            Box(
                modifier = Modifier
                    .size((100 * scale).dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6C5CE7),
                                Color(0xFFA29BFE)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("💰", fontSize = (40 * scale).sp)
            }

            Spacer(modifier = Modifier.height((20 * scale).dp))

            Text(
                "Hifadih",
                fontSize = (24 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )

            Text(
                "Votre portefeuille mobile de confiance",
                fontSize = (16 * scale).sp,
                color = Color(0xFF636E72),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = (8 * scale).dp)
            )

            Text(
                "Version ${appInfo.version}",
                fontSize = (14 * scale).sp,
                color = Color(0xFFA29BFE),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = (12 * scale).dp)
            )
        }
    }
}

@Composable
private fun AppInfoSection(
    appInfo: AppInfo,
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
                "Informations de l'application",
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
                    modifier = Modifier.padding((20 * scale).dp)
                ) {
                    InfoRow("Version", appInfo.version, scale)
                    HorizontalDivider(color = Color(0xFFF1F2F6))
                    InfoRow("Build", appInfo.buildNumber, scale)
                    HorizontalDivider(color = Color(0xFFF1F2F6))
                    InfoRow("Dernière mise à jour", appInfo.lastUpdate, scale)
                    HorizontalDivider(color = Color(0xFFF1F2F6))
                    InfoRow("Taille de l'app", appInfo.size, scale, isLast = true)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    scale: Float,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = (12 * scale).dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = (16 * scale).sp,
            color = Color(0xFF636E72)
        )
        Text(
            value,
            fontSize = (16 * scale).sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF2D3436)
        )
    }
}

@Composable
private fun FeaturesSection(
    features: List<FeatureInfo>,
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
                "Fonctionnalités principales",
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
                    modifier = Modifier.padding((20 * scale).dp)
                ) {
                    features.forEachIndexed { index, feature ->
                        FeatureRow(feature, scale)
                        if (index < features.size - 1) {
                            Spacer(modifier = Modifier.height((16 * scale).dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(
    feature: FeatureInfo,
    scale: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            feature.icon,
            fontSize = (20 * scale).sp,
            modifier = Modifier.padding(end = (16 * scale).dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                feature.title,
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3436)
            )
            Text(
                feature.description,
                fontSize = (13 * scale).sp,
                color = Color(0xFF636E72)
            )
        }
    }
}

@Composable
private fun LegalSection(
    legalOptions: List<Pair<String, String>>,
    scale: Float
) {
    Column {
        Text(
            "Informations légales",
            fontSize = (18 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436),
            modifier = Modifier.padding(bottom = (16 * scale).dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column {
                legalOptions.forEachIndexed { index, (icon, title) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* Navigation */ }
                            .padding((16 * scale).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            icon,
                            fontSize = (16 * scale).sp,
                            modifier = Modifier.padding(end = (12 * scale).dp)
                        )

                        Text(
                            title,
                            fontSize = (16 * scale).sp,
                            color = Color(0xFF2D3436),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            "›",
                            fontSize = (20 * scale).sp,
                            color = Color(0xFFDDD)
                        )
                    }

                    if (index < legalOptions.size - 1) {
                        HorizontalDivider(
                            color = Color(0xFFF1F2F6),
                            modifier = Modifier.padding(horizontal = (16 * scale).dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterSection(
    socialNetworks: List<Pair<String, Color>>,
    scale: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding((30 * scale).dp)
    ) {
        Text(
            "Fait avec \uD83D\uDDA4 en Côte d'Ivoire",
            fontSize = (14 * scale).sp,
            color = Color(0xFF636E72),
            textAlign = TextAlign.Center
        )

        Text(
            "© 20àé5 Hifadih. Tous droits réservés.",
            fontSize = (12 * scale).sp,
            color = Color(0xFF636E72),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = (8 * scale).dp)
        )

        // Réseaux sociaux
        Row(
            horizontalArrangement = Arrangement.spacedBy((12 * scale).dp),
            modifier = Modifier.padding(top = (20 * scale).dp)
        ) {
            socialNetworks.forEach { (icon, color) ->
                Box(
                    modifier = Modifier
                        .size((50 * scale).dp)
                        .background(color, CircleShape)
                        .clickable { /* Action réseau social */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        icon,
                        fontSize = (20 * scale).sp
                    )
                }
            }
        }
    }
}

//==================== Preview =================//
@Preview(showBackground = true)
@Composable
fun PageAProposPreview() {
    HifadihTheme {
        PageAPropos()
    }
}

@Preview(showBackground = true, name = "À propos - Small Screen", widthDp = 320)
@Composable
fun PageAProposSmallScreenPreview() {
    HifadihTheme {
        PageAPropos()
    }
}

@Preview(showBackground = true, name = "À propos - Large Screen", widthDp = 480)
@Composable
fun PageAProposLargeScreenPreview() {
    HifadihTheme {
        PageAPropos()
    }
}