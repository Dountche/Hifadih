package com.example.hifadih

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import kotlin.math.max

// -------------------- Utilitaires pour les statistiques --------------------


// Calculer la hauteur des barres pour le graphique

fun getBarHeight(valeur: Int, maxValeur: Int): Float {
    return max((valeur.toFloat() / maxValeur.toFloat()) * 100f, 8f)
}

// -------------------- Composants des statistiques --------------------

// Sélecteur de période

@Composable
private fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val periods = listOf("Cette semaine", "Ce mois", "6 mois", "1 an")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy((8 * scale).dp),
        contentPadding = PaddingValues(horizontal = (16 * scale).dp),
        modifier = modifier
    ) {
        items(periods) { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        period,
                        fontSize = (12 * scale).sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50),
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape((16 * scale).dp)
            )
        }
    }
}


//Carte de statistique individuelle
@Composable
private fun StatCard(
    stat: StatCard,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width((160 * scale).dp)
            .height((120 * scale).dp),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((14 * scale).dp)
        ) {
            // Header avec icône et évolution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stat.icone,
                    fontSize = (16 * scale).sp
                )

                Surface(
                    color = when {
                        stat.evolution.contains('+') -> Color(0xFFD4F6ED)
                        stat.evolution.contains('-') -> Color(0xFFFFE6E6)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    shape = RoundedCornerShape((6 * scale).dp)
                ) {
                    Text(
                        stat.evolution,
                        fontSize = (10 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            stat.evolution.contains('+') -> Color(0xFF00B894)
                            stat.evolution.contains('-') -> Color(0xFFE74C3C)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(
                            horizontal = (4 * scale).dp,
                            vertical = (2 * scale).dp
                        )
                    )
                }
            }

            Spacer(Modifier.height((8 * scale).dp))

            // Valeur principale
            Text(
                "${stat.valeur}${if (!stat.titre.contains("FCFA") && !stat.valeur.contains("/")) " FCFA" else ""}",
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height((4 * scale).dp))

            // Titre
            Text(
                stat.titre,
                fontSize = (11 * scale).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(Modifier.weight(1f))

            // Barre de progression si pourcentage disponible
            if (stat.pourcentage > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((4 * scale).dp)
                        .background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            RoundedCornerShape((2 * scale).dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(stat.pourcentage / 100f)
                            .fillMaxHeight()
                            .background(
                                Color(android.graphics.Color.parseColor(stat.couleur)),
                                RoundedCornerShape((2 * scale).dp)
                            )
                    )
                }
            }
        }
    }
}


// Graphique d'évolution
@Composable
private fun EvolutionChart(
    donneesGraphique: List<DonneesMensuelles>,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val maxValue = donneesGraphique.maxOfOrNull { maxOf(it.revenus, it.depenses, it.epargne) } ?: 1

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Text(
                "💹 Évolution financière",
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height((12 * scale).dp))

            // Légende
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((12 * scale).dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size((8 * scale).dp)
                            .background(Color(0xFF00B894), CircleShape)
                    )
                    Spacer(Modifier.width((4 * scale).dp))
                    Text(
                        "Revenus",
                        fontSize = (10 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size((8 * scale).dp)
                            .background(Color(0xFFE74C3C), CircleShape)
                    )
                    Spacer(Modifier.width((4 * scale).dp))
                    Text(
                        "Dépenses",
                        fontSize = (10 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size((8 * scale).dp)
                            .background(Color(0xFF6C5CE7), CircleShape)
                    )
                    Spacer(Modifier.width((4 * scale).dp))
                    Text(
                        "Épargne",
                        fontSize = (10 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height((16 * scale).dp))

            // Graphique en barres
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((120 * scale).dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                donneesGraphique.forEach { donnee ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Barres empilées
                        Column(
                            modifier = Modifier
                                .width((20 * scale).dp)
                                .height((100 * scale).dp),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Barre revenus
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((getBarHeight(donnee.revenus, maxValue) * scale).dp)
                                    .background(
                                        Color(0xFF00B894),
                                        RoundedCornerShape(
                                            topStart = (3 * scale).dp,
                                            topEnd = (3 * scale).dp
                                        )
                                    )
                            )

                            // Barre dépenses
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((getBarHeight(donnee.depenses, maxValue) * scale).dp)
                                    .background(Color(0xFFE74C3C))
                            )

                            // Barre épargne
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((getBarHeight(donnee.epargne, maxValue) * scale).dp)
                                    .background(
                                        Color(0xFF6C5CE7),
                                        RoundedCornerShape(
                                            bottomStart = (3 * scale).dp,
                                            bottomEnd = (3 * scale).dp
                                        )
                                    )
                            )
                        }

                        Spacer(Modifier.height((6 * scale).dp))

                        Text(
                            donnee.mois,
                            fontSize = (10 * scale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


//Répartition des dépenses par catégorie
@Composable
private fun CategoryExpenses(
    categoriesDepenses: List<CategorieDepense>,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val maxMontant = categoriesDepenses.maxOfOrNull { it.montant } ?: 1

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Text(
                "🏷️ Répartition des dépenses",
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height((12 * scale).dp))

            categoriesDepenses.forEachIndexed { index, categorie ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = (8 * scale).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icône et nom
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            categorie.icone,
                            fontSize = (16 * scale).sp
                        )

                        Spacer(Modifier.width((8 * scale).dp))

                        Text(
                            categorie.nom,
                            fontSize = (13 * scale).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Montant et barre
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            formatMontant(categorie.montant),
                            fontSize = (12 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(Modifier.height((3 * scale).dp))

                        Box(
                            modifier = Modifier
                                .width((60 * scale).dp)
                                .height((3 * scale).dp)
                                .background(
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    RoundedCornerShape((2 * scale).dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((categorie.montant.toFloat() / maxMontant.toFloat()))
                                    .fillMaxHeight()
                                    .background(
                                        Color(android.graphics.Color.parseColor(categorie.couleur)),
                                        RoundedCornerShape((2 * scale).dp)
                                    )
                            )
                        }
                    }
                }

                if (index < categoriesDepenses.size - 1) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        thickness = (1 * scale).dp
                    )
                }
            }
        }
    }
}


// Section des recommandations

@Composable
private fun RecommendationsSection(
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F8E9)
        ),
        shape = RoundedCornerShape((12 * scale).dp),
        border = androidx.compose.foundation.BorderStroke(
            width = (1 * scale).dp,
            color = Color(0xFFC8E6C9)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Text(
                "🎯 Recommandations",
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )

            Spacer(Modifier.height((8 * scale).dp))

            Text(
                "• Excellente épargne ce mois (+25.8%)\n• Réduisez vos dépenses alimentaires de 10%\n• Vous êtes en bonne voie pour atteindre vos objectifs",
                fontSize = (12 * scale).sp,
                color = Color(0xFF666666),
                lineHeight = (16 * scale).sp
            )
        }
    }
}

// -------------------- Page des statistiques principale --------------------

// Page des statistiques financières

@Composable
fun PageStatistiques(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    // Responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var selectedPeriod by remember { mutableStateOf("Ce mois") }

    val statsCards = remember {
        listOf(
            StatCard(
                titre = "Revenus totaux",
                valeur = "285,750",
                evolution = "+12.5%",
                couleur = "#00B894",
                icone = "📈",
                pourcentage = 75
            ),
            StatCard(
                titre = "Dépenses totales",
                valeur = "156,420",
                evolution = "-5.2%",
                couleur = "#E74C3C",
                icone = "📉",
                pourcentage = 45
            ),
            StatCard(
                titre = "Épargne réalisée",
                valeur = "45,300",
                evolution = "+25.8%",
                couleur = "#6C5CE7",
                icone = "💰",
                pourcentage = 60
            ),
            StatCard(
                titre = "Objectifs atteints",
                valeur = "3/5",
                evolution = "60%",
                couleur = "#FDCB6E",
                icone = "🎯",
                pourcentage = 60
            )
        )
    }

    val donneesGraphique = remember {
        listOf(
            DonneesMensuelles("Jan", 250000, 180000, 70000),
            DonneesMensuelles("Fév", 280000, 160000, 120000),
            DonneesMensuelles("Mar", 265000, 195000, 70000),
            DonneesMensuelles("Avr", 320000, 210000, 110000),
            DonneesMensuelles("Mai", 285000, 156000, 129000),
            DonneesMensuelles("Jun", 310000, 175000, 135000)
        )
    }

    val categoriesDepenses = remember {
        listOf(
            CategorieDepense("Alimentation", 65000, "#FF6B6B", "🍽️"),
            CategorieDepense("Transport", 35000, "#4ECDC4", "🚗"),
            CategorieDepense("Logement", 25000, "#45B7D1", "🏠"),
            CategorieDepense("Loisirs", 18000, "#FFA07A", "🎮"),
            CategorieDepense("Santé", 13420, "#98D8C8", "🏥")
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
            // Header avec arrière-plan vert
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((100 * scale).dp)
                    .background(
                        Color(0xFF4CAF50),
                        shape = RoundedCornerShape(
                            bottomStart = (30 * scale).dp,
                            bottomEnd = (30 * scale).dp
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "📊 Mes Statistiques",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height((20 * scale).dp))

            // Sélecteur de période
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it },
                scale = scale
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Cartes de statistiques principales
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy((12 * scale).dp),
                contentPadding = PaddingValues(horizontal = (16 * scale).dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statsCards) { stat ->
                    StatCard(
                        stat = stat,
                        scale = scale
                    )
                }
            }

            Spacer(Modifier.height((16 * scale).dp))

            // Graphique d'évolution
            EvolutionChart(
                donneesGraphique = donneesGraphique,
                scale = scale,
                modifier = Modifier.padding(horizontal = (16 * scale).dp)
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Répartition des dépenses
            CategoryExpenses(
                categoriesDepenses = categoriesDepenses,
                scale = scale,
                modifier = Modifier.padding(horizontal = (16 * scale).dp)
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Recommandations
            RecommendationsSection(
                scale = scale,
                modifier = Modifier.padding(horizontal = (16 * scale).dp)
            )

            Spacer(Modifier.height((32 * scale).dp))
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PageStatistiquesPreview() {
    HifadihTheme {
        PageStatistiques()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun PageStatistiquesSmallScreenPreview() {
    HifadihTheme {
        PageStatistiques()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 480)
@Composable
fun PageStatistiquesLargeScreenPreview() {
    HifadihTheme {
        PageStatistiques()
    }
}