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
import androidx.compose.material.icons.filled.ContentCopy
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

data class ParrainageStats(
    val nombreFilleuls: Int,
    val gainTotal: Int,
    val gainEnAttente: Int,
    val prochainBonus: Int
)

data class Filleul(
    val nom: String,
    val prenom: String,
    val dateInscription: String,
    val bonus: Int,
    val statut: StatutFilleul
)

enum class StatutFilleul {
    ACTIF,
    INACTIF
}

data class ShareOption(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageParrainage(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    val stats = remember {
        ParrainageStats(
            nombreFilleuls = 5,
            gainTotal = 5000,
            gainEnAttente = 2000,
            prochainBonus = 1000
        )
    }

    val filleuls = remember {
        listOf(
            Filleul(
                "DIALLO",
                "Amadou",
                "15/08/2025",
                1000,
                StatutFilleul.ACTIF
            ),
            Filleul(
                "TRAORE",
                "Fatou",
                "22/07/2025",
                1000,
                StatutFilleul.ACTIF
            ),
            Filleul(
                "OUATTARA",
                "Ibrahim",
                "10/06/2025",
                1000,
                StatutFilleul.INACTIF
            )
        )
    }

    val codeParrainage = remember { "DK2024789" }

    val shareOptions = remember {
        listOf(
            ShareOption("whatsapp", "WhatsApp", "💬", Color(0xFF25D366)),
            ShareOption("sms", "SMS", "📱", Color(0xFF007AFF)),
            ShareOption("email", "Email", "📧", Color(0xFFEA4335)),
            ShareOption("more", "Plus", "📤", Color(0xFF636E72))
        )
    }

    fun formatMontant(montant: Int): String {
        return "${montant.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")} FCFA"
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
                    "Parrainer un ami",
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
                containerColor = Color(0xFFA29BFE)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            // Cartes de statistiques
            StatsCards(stats, ::formatMontant, scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Section code de parrainage
            CodeParrainageSection(codeParrainage, shareOptions, scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Comment ça marche
            HowItWorksSection(scale)

            Spacer(modifier = Modifier.height((16 * scale).dp))

            // Liste des filleuls
            FilleulsListSection(filleuls, ::formatMontant, scale)
        }
    }
}

@Composable
private fun StatsCards(
    stats: ParrainageStats,
    formatMontant: (Int) -> String,
    scale: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy((12 * scale).dp)
    ) {
        // Nombre de filleuls
        Card(
            modifier = Modifier.weight(0.3f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding((16 * scale).dp)
            ) {
                Text("👥", fontSize = (24 * scale).sp)

                Spacer(modifier = Modifier.height((8 * scale).dp))

                Text(
                    stats.nombreFilleuls.toString(),
                    fontSize = (24 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )

                Text(
                    "Filleuls",
                    fontSize = (12 * scale).sp,
                    color = Color(0xFF636E72)
                )
            }
        }

        // Gains totaux
        Card(
            modifier = Modifier.weight(0.7f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding((16 * scale).dp)
            ) {
                Text("💰", fontSize = (24 * scale).sp)

                Spacer(modifier = Modifier.height((8 * scale).dp))

                Text(
                    formatMontant(stats.gainTotal),
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00B894)
                )

                Text(
                    "Gains totaux",
                    fontSize = (12 * scale).sp,
                    color = Color(0xFF636E72)
                )
            }
        }
    }
}

@Composable
private fun CodeParrainageSection(
    codeParrainage: String,
    shareOptions: List<ShareOption>,
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
                "Votre code de parrainage",
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
                modifier = Modifier.padding(bottom = (16 * scale).dp)
            )

            // Code avec bouton de copie
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFA29BFE).copy(alpha = 0.2f),
                    shape = RoundedCornerShape((12 * scale).dp)
                ) {
                    Text(
                        codeParrainage,
                        fontSize = (24 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA29BFE),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding((16 * scale).dp)
                    )
                }

                IconButton(
                    onClick = { /* Copier le code */ },
                    modifier = Modifier
                        .padding(start = (12 * scale).dp)
                        .size((50 * scale).dp)
                        .background(
                            Color(0xFFA29BFE),
                            RoundedCornerShape((12 * scale).dp)
                        )
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copier",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height((20 * scale).dp))

            // Boutons de partage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                shareOptions.forEach { option ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* Action de partage */ }
                    ) {
                        Box(
                            modifier = Modifier
                                .size((60 * scale).dp)
                                .background(option.color, RoundedCornerShape((12 * scale).dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    option.icon,
                                    fontSize = (20 * scale).sp
                                )
                                Text(
                                    option.name,
                                    fontSize = (10 * scale).sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = (4 * scale).dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HowItWorksSection(scale: Float) {
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
                "Comment ça marche ?",
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
                    val steps = listOf(
                        "1" to ("Partagez votre code" to "Envoyez votre code à vos amis"),
                        "2" to ("Ils s'inscrivent" to "Avec votre code lors de l'inscription"),
                        "3" to ("Vous gagnez 1 000 FCFA" to "Pour chaque ami qui s'inscrit")
                    )

                    steps.forEachIndexed { index, (number, texts) ->
                        StepItem(number, texts.first, texts.second, scale)
                        if (index < steps.size - 1) {
                            Spacer(modifier = Modifier.height((16 * scale).dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    number: String,
    title: String,
    description: String,
    scale: Float
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size((30 * scale).dp)
                .background(Color(0xFFA29BFE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number,
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width((16 * scale).dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = (15 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = if (number == "3") Color(0xFF00B894) else Color(0xFF2D3436)
            )
            Text(
                description,
                fontSize = (13 * scale).sp,
                color = Color(0xFF636E72)
            )
        }
    }
}

@Composable
private fun FilleulsListSection(
    filleuls: List<Filleul>,
    formatMontant: (Int) -> String,
    scale: Float
) {
    Column {
        Text(
            "Vos filleuls",
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
                filleuls.forEachIndexed { index, filleul ->
                    FilleulItem(filleul, formatMontant, scale)

                    if (index < filleuls.size - 1) {
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
private fun FilleulItem(
    filleul: Filleul,
    formatMontant: (Int) -> String,
    scale: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding((16 * scale).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size((40 * scale).dp)
                .background(
                    Color(0xFFA29BFE).copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${filleul.prenom.first()}${filleul.nom.first()}",
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA29BFE)
            )
        }

        Spacer(modifier = Modifier.width((12 * scale).dp))

        // Informations
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${filleul.prenom} ${filleul.nom}",
                fontSize = (15 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D3436)
            )
            Text(
                "Inscrit le ${filleul.dateInscription}",
                fontSize = (12 * scale).sp,
                color = Color(0xFF636E72)
            )
        }

        // Bonus et statut
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                "+${formatMontant(filleul.bonus)}",
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00B894)
            )
            Text(
                if (filleul.statut == StatutFilleul.ACTIF) "🟢 Actif" else "⚪ Inactif",
                fontSize = (10 * scale).sp,
                color = Color(0xFF636E72)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PageParrainagePreview() {
    HifadihTheme {
        PageParrainage()
    }
}