package com.example.hifadih

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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

// -------------------- Utilitaires pour le profil --------------------


// Obtenir la couleur du niveau VIP

fun getVipColor(niveau: NiveauVip): Color {
    return when (niveau) {
        NiveauVip.Bronze -> Color(0xFFCD7F32)
        NiveauVip.Silver -> Color(0xFFC0C0C0)
        NiveauVip.Gold -> Color(0xFFFFD700)
    }
}


// Obtenir l'emoji du niveau VIP
fun getVipEmoji(niveau: NiveauVip): String {
    return when (niveau) {
        NiveauVip.Bronze -> "🥉"
        NiveauVip.Silver -> "🥈"
        NiveauVip.Gold -> "🥇"
    }
}

// -------------------- Composants du profil --------------------


// En-tête du profil avec informations utilisateur
@Composable
private fun ProfileHeader(
    userInfo: UserInfo,
    soldeCompte: Int,
    soldeVisible: Boolean,
    onToggleSoldeVisibility: () -> Unit,
    onEditProfileClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(
            bottomStart = (24 * scale).dp,
            bottomEnd = (24 * scale).dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6C5CE7),
                            Color(0xFFA29BFE)
                        )
                    )
                )
                .padding((20 * scale).dp)
        ) {
            Column {
                // Photo de profil et informations de base
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar avec initiales
                    Box(
                        modifier = Modifier
                            .size((60 * scale).dp)
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
                        Text(
                            "${userInfo.prenom.first()}${userInfo.nom.first()}",
                            fontSize = (20 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.width((12 * scale).dp))

                    // Informations utilisateur
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${userInfo.prenom} ${userInfo.nom}",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            "Client ${userInfo.numeroClient}",
                            fontSize = (12 * scale).sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        // Badge niveau VIP
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = (6 * scale).dp)
                        ) {
                            Text(
                                getVipEmoji(userInfo.niveauVip),
                                fontSize = (12 * scale).sp
                            )

                            Spacer(Modifier.width((4 * scale).dp))

                            Surface(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape((8 * scale).dp)
                            ) {
                                Text(
                                    "Niveau ${userInfo.niveauVip.name}",
                                    fontSize = (10 * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = getVipColor(userInfo.niveauVip),
                                    modifier = Modifier.padding(
                                        horizontal = (6 * scale).dp,
                                        vertical = (3 * scale).dp
                                    )
                                )
                            }
                        }
                    }

                }

                Spacer(Modifier.height((16 * scale).dp))

                // Solde visible avec toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Solde disponible",
                            fontSize = (12 * scale).sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Text(
                            if (soldeVisible) formatMontant(soldeCompte) else "****** FCFA",
                            fontSize = (24 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onToggleSoldeVisibility,
                        modifier = Modifier
                            .size((32 * scale).dp)
                            .background(
                                Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (soldeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (soldeVisible) "Masquer le solde" else "Afficher le solde",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size((16 * scale).dp)
                        )
                    }
                }
            }
        }
    }
}


// Actions rapides du profil
@Composable
private fun QuickActions(
    onStatistiquesClick: () -> Unit,
    onRecompensesClick: () -> Unit,
    onRechargesClick: () -> Unit,
    onSupportClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 1 - Statistiques
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onStatistiquesClick() }
                    .weight(1f)
                    .padding((8 * scale).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size((40 * scale).dp)
                        .background(
                            Color(0xFF00B894),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📊", fontSize = (16 * scale).sp)
                }

                Spacer(Modifier.height((6 * scale).dp))

                Text(
                    "Statistiques",
                    fontSize = (10 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // 2 - Recharges
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onRechargesClick() }
                    .weight(1f)
                    .padding((8 * scale).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size((40 * scale).dp)
                        .background(
                            Color(0xFFE17055),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📱", fontSize = (16 * scale).sp)
                }

                Spacer(Modifier.height((6 * scale).dp))

                Text(
                    "Recharges",
                    fontSize = (10 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // 3 - Support
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSupportClick() }
                    .weight(1f)
                    .padding((8 * scale).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size((40 * scale).dp)
                        .background(
                            Color(0xFF74B9FF),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💬", fontSize = (16 * scale).sp)
                }

                Spacer(Modifier.height((6 * scale).dp))

                Text(
                    "Support",
                    fontSize = (10 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Option de menu du profil

@Composable
private fun MenuOption(
    option: MenuOption,
    onMenuOptionClick: (MenuOption) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMenuOptionClick(option) }
            .padding(
                horizontal = (16 * scale).dp,
                vertical = (12 * scale).dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône avec background
        Box(
            modifier = Modifier
                .size((36 * scale).dp)
                .background(
                    Color(android.graphics.Color.parseColor(option.couleur)).copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                option.icone,
                fontSize = (16 * scale).sp
            )
        }

        Spacer(Modifier.width((12 * scale).dp))

        // Contenu principal
        Column(modifier = Modifier.weight(1f)) {
            Text(
                option.titre,
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            option.subtitle?.let { subtitle ->
                Text(
                    subtitle,
                    fontSize = (12 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Badge et flèche
        Row(verticalAlignment = Alignment.CenterVertically) {
            option.badge?.let { badge ->
                Surface(
                    color = Color(android.graphics.Color.parseColor(option.couleur)).copy(alpha = 0.2f),
                    shape = RoundedCornerShape((6 * scale).dp)
                ) {
                    Text(
                        badge,
                        fontSize = (9 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(option.couleur)),
                        modifier = Modifier.padding(
                            horizontal = (4 * scale).dp,
                            vertical = (2 * scale).dp
                        )
                    )
                }

                Spacer(Modifier.width((6 * scale).dp))
            }

            if (option.hasArrow) {
                Text(
                    "›",
                    fontSize = (16 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// -------------------- Page de profil principale --------------------


//Page de profil de l'utilisateur
@Composable
fun PageProfile(
    onEditProfileClick: () -> Unit = {},
    onStatistiquesClick: () -> Unit = {},
    onRecompensesClick: () -> Unit = {},
    onRechargesClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onMenuOptionClick: (MenuOption) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    // Responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    val userInfo = remember {
        UserInfo(
            nom = "KONE",
            prenom = "Dountchè",
            email = "kidt@email.com",
            telephone = "+225 07 12 34 56 78",
            numeroClient = "****7891",
            niveauVip = NiveauVip.Gold
        )
    }

    var soldeCompte by remember { mutableStateOf(245800) }
    var soldeVisible by remember { mutableStateOf(true) }

    val menuOptions = remember {
        listOf(
            MenuOption(
                id = "compte",
                icone = "👤",
                titre = "Informations personnelles",
                subtitle = "Gérez vos informations",
                couleur = "#6C5CE7",
                page = PageType.INFOPERSO
            ),
             MenuOption(
                id = "securite",
                icone = "🔐",
                titre = "Sécurité",
                subtitle = "Code PIN, Biométrie",
                couleur = "#00B894",
                 page = PageType.SECURITE
            ),
            MenuOption(
                id = "parrainage",
                icone = "👥",
                titre = "Parrainer un ami",
                subtitle = "Gagnez 1 000 FCFA",
                couleur = "#A29BFE",
                badge = "BONUS",
                page = PageType.PARRAINAGE
            ),
            MenuOption(
                id = "aide",
                icone = "❓",
                titre = "Centre d'aide",
                subtitle = "FAQ et support client",
                couleur = "#74B9FF",
                page = PageType.AIDE
            ),
            MenuOption(
                id = "apropos",
                icone = "ℹ️",
                titre = "À propos",
                subtitle = "Version 2.1.0",
                couleur = "#636E72",
                page = PageType.APROPOS
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header avec profil
            ProfileHeader(
                userInfo = userInfo,
                soldeCompte = soldeCompte,
                soldeVisible = soldeVisible,
                onToggleSoldeVisibility = { soldeVisible = !soldeVisible },
                onEditProfileClick = onEditProfileClick,
                scale = scale
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Actions rapides
            QuickActions(
                onStatistiquesClick = onStatistiquesClick,
                onRecompensesClick = onRecompensesClick,
                onRechargesClick = onRechargesClick,
                onSupportClick = onSupportClick,
                scale = scale,
                modifier = Modifier.padding(horizontal = (16 * scale).dp)
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Menu principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * scale).dp),
                elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Column {
                    menuOptions.forEachIndexed { index, option ->
                        MenuOption(
                            option = option,
                            onMenuOptionClick = onMenuOptionClick,
                            scale = scale
                        )

                        if (index < menuOptions.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                                thickness = (1 * scale).dp,
                                modifier = Modifier.padding(horizontal = (16 * scale).dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height((16 * scale).dp))

            // Bouton de déconnexion
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * scale).dp)
                    .clickable { onLogoutClick() },
                elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape((12 * scale).dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = (1 * scale).dp,
                    color = Color(0xFFE74C3C)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((16 * scale).dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚪", fontSize = (16 * scale).sp)
                    Spacer(Modifier.width((8 * scale).dp))
                    Text(
                        "Se déconnecter",
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE74C3C)
                    )
                }
            }

            Spacer(Modifier.height((32 * scale).dp))
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PageProfilePreview() {
    HifadihTheme {
        PageProfile()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Profil - Solde masqué")
@Composable
fun PageProfileHiddenBalancePreview() {
    HifadihTheme {
        PageProfile()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun PageProfileSmallScreenPreview() {
    HifadihTheme {
        PageProfile()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 480)
@Composable
fun PageProfileLargeScreenPreview() {
    HifadihTheme {
        PageProfile()
    }
}

