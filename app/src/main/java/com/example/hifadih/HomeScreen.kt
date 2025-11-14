package com.example.hifadih

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import java.util.*
import androidx.core.graphics.toColorInt

// -------------------- Composants utilitaires --------------------

// Formater les montants en FCFA

fun formatMontant(montant: Int): String {
    return "${String.format(Locale.FRENCH, "%,d", Math.abs(montant))} FCFA"
}

// Obtenir le message de salutation selon l'heure

fun getSalutation(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Bonjour"
        hour < 18 -> "Bon après-midi"
        else -> "Bonsoir"
    }
}

// -------------------- Composants de services --------------------


// Carte de service bancaire

@Composable
private fun ServiceCard(
    service: BankService,
    scale: Float,
    onServiceClick: (BankService) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "Service card scale"
    )

    Card(
        modifier = modifier
            .width((90 * scale).dp)
            .height((110 * scale).dp)
            .scale(scaleAnim)
            .clickable(
                onClick = {
                    isPressed = true
                    onServiceClick(service)
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding((12 * scale).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icône du service
            Box(
                modifier = Modifier
                    .size((50 * scale).dp)
                    .background(
                        color = Color(android.graphics.Color.parseColor(service.color)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = service.icon,
                    fontSize = (24 * scale).sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height((8 * scale).dp))

            // Nom du service
            Text(
                text = service.name,
                fontSize = (12 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}


// Section des services bancaires

@Composable
private fun ServicesSection(
    onServiceClick: (BankService) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val services = remember {
        listOf(
            BankService("transfer", "Transfert", "💸", "#4CAF50", PageType.TRANSFERT),
            BankService("recharge", "Recharge", "💳", "#2196F3", PageType.RECHARGE),
            BankService("subscription", "Abonnement", "📋", "#FF9800", PageType.ABONNEMENT),
            BankService("bills", "Factures", "🧾", "#9C27B0", PageType.FACTURE)
        )
    }

    Column(modifier = modifier) {
        Text(
            "Services",
            fontSize = (18 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = (16 * scale).dp,
                vertical = (8 * scale).dp
            )
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy((12 * scale).dp),
            contentPadding = PaddingValues(horizontal = (16 * scale).dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(services) { service ->
                ServiceCard(
                    service = service,
                    scale = scale,
                    onServiceClick = onServiceClick
                )
            }
        }
    }
}

// -------------------- Composants de la page d'accueil --------------------

// Carte d'en-tête avec solde et informations utilisateur

@Composable
private fun HeaderCard(
    soldeCompte: Int,
    soldeVisible: Boolean,
    onToggleSoldeVisibility: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .padding(top = (20 * scale).dp),
        elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape((16 * scale).dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2196F3),
                            Color(0xFF1976D2)
                        )
                    ),
                    shape = RoundedCornerShape((16 * scale).dp)
                )
                .padding((20 * scale).dp)
        ) {
            Column {
                // Salutation et actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            "${getSalutation()} 👋",
                            fontSize = (18 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Dountchè KONE",
                            fontSize = (14 * scale).sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onProfileClick,
                            modifier = Modifier
                                .size((40 * scale).dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Text("👤", fontSize = (16 * scale).sp)
                        }
                    }
                }

                Spacer(Modifier.height((20 * scale).dp))

                // Solde
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Solde disponible",
                            fontSize = (12 * scale).sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        IconButton(
                            onClick = onToggleSoldeVisibility,
                            modifier = Modifier.size((24 * scale).dp)
                        ) {
                            Icon(
                                imageVector = if (soldeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (soldeVisible) "Masquer le solde" else "Afficher le solde",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size((18 * scale).dp)
                            )
                        }
                    }

                    Text(
                        if (soldeVisible) formatMontant(soldeCompte) else "****** FCFA",
                        fontSize = (24 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Bannière promotionnelle

@Composable
private fun PromoBanner(
    onInviteClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8F0)
        ),
        shape = RoundedCornerShape((12 * scale).dp),
        border = androidx.compose.foundation.BorderStroke(
            width = (1 * scale).dp,
            color = Color(0xFFFFE4CC)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "🎁 Offre Spéciale",
                    fontSize = (14 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B35)
                )

                Spacer(Modifier.height((4 * scale).dp))

                Text(
                    "Parrainez un ami et gagnez 1 000 FCFA tous les deux!",
                    fontSize = (12 * scale).sp,
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onInviteClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35)
                ),
                shape = RoundedCornerShape((15 * scale).dp),
                contentPadding = PaddingValues(
                    horizontal = (12 * scale).dp,
                    vertical = (6 * scale).dp
                )
            ) {
                Text(
                    "Inviter",
                    fontSize = (11 * scale).sp,
                    color = Color.White
                )
            }
        }
    }
}

// Item de transaction

@Composable
private fun TransactionItem(
    transaction: Transaction,
    scale: Float,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTransactionClick(transaction) }
            .padding(
                horizontal = (16 * scale).dp,
                vertical = (12 * scale).dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône de transaction
        Box(
            modifier = Modifier
                .size((40 * scale).dp)
                .background(
                    Color(transaction.couleur.toColorInt()).copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = transaction.icone,
                fontSize = (16 * scale).sp
            )
        }

        Spacer(Modifier.width((12 * scale).dp))

        // Détails de la transaction
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.nom,
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = transaction.date,
                fontSize = (12 * scale).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Montant
        Text(
            text = "${if (transaction.montant > 0) "+" else ""}${formatMontant(transaction.montant)}",
            fontSize = (14 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.montant > 0) Color(0xFF00B894) else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Section des transactions récentes

@Composable
private fun TransactionsSection(
    transactions: List<Transaction>,
    onSeeAllClick: () -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "💳 Transactions récentes",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = onSeeAllClick) {
                    Text(
                        "Voir tout",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFF2196F3)
                    )
                }
            }

            Spacer(Modifier.height((8 * scale).dp))

            // Transactions
            if (transactions.isNotEmpty()) {
                transactions.take(4).forEachIndexed { index, transaction ->
                    TransactionItem(
                        transaction = transaction,
                        scale = scale,
                        onTransactionClick = onTransactionClick,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (index < minOf(3, transactions.size - 1)) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            thickness = (1 * scale).dp,
                            modifier = Modifier.padding(horizontal = (16 * scale).dp)
                        )
                    }
                }
            } else {
                // État vide
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((20 * scale).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "📝",
                        fontSize = (32 * scale).sp
                    )

                    Spacer(Modifier.height((8 * scale).dp))

                    Text(
                        "Aucune transaction récente",
                        fontSize = (14 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

//  Conseil financier du jour

@Composable
private fun FinancialTip(
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F7FF)
        ),
        shape = RoundedCornerShape((12 * scale).dp),
        border = androidx.compose.foundation.BorderStroke(
            width = (1 * scale).dp,
            color = Color(0xFFE8E5FF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Text(
                "💡 Conseil du jour",
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6C5CE7)
            )

            Spacer(Modifier.height((8 * scale).dp))

            Text(
                "Épargnez automatiquement 10% de vos revenus dans votre coffre pour atteindre vos objectifs plus rapidement.",
                fontSize = (13 * scale).sp,
                color = Color(0xFF666666),
                lineHeight = (16 * scale).sp
            )
        }
    }
}

// -------------------- Page d'accueil principale --------------------

// Page d'accueil de l'application

@Composable
fun PageAccueil(
    onServiceClick: (BankService) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTransactionClick: (Transaction) -> Unit = {},
    onSeeAllTransactionsClick: () -> Unit = {},
    onInviteClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    soldeCompteState: MutableState<Int>? = null,
) {
    // Responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var soldeCompte by remember { mutableStateOf(soldeCompteState?.value ?: 125750) }
    var soldeVisible by remember { mutableStateOf(true) }

    //sync
    LaunchedEffect(soldeCompteState?.value) {
        soldeCompteState?.value?.let { soldeCompte = it }
    }

    val transactions = remember {
        listOf(
            Transaction(
                id = "1",
                type = TransactionType.RECEPTION,
                nom = "Koffi Jean-Marc",
                montant = 25000,
                date = "10:30",
                statut = StatutTransaction.REUSSIE,
                icone = "📥",
                couleur = "#00B894"
            ),
            Transaction(
                id = "2",
                type = TransactionType.ACHAT,
                nom = "Orange Money",
                montant = -5000,
                date = "09:15",
                statut = StatutTransaction.REUSSIE,
                icone = "📱",
                couleur = "#FF9800"
            ),
            Transaction(
                id = "3",
                type = TransactionType.ENVOI,
                nom = "Aya Fatou",
                montant = -15000,
                date = "Hier",
                statut = StatutTransaction.REUSSIE,
                icone = "📤",
                couleur = "#E74C3C"
            ),
            Transaction(
                id = "4",
                type = TransactionType.DEPOT,
                nom = "Dépôt Agence",
                montant = 50000,
                date = "2j",
                statut = StatutTransaction.REUSSIE,
                icone = "🏦",
                couleur = "#6C5CE7"
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
            // Header avec arrière-plan bleu et carte
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                // Arrière-plan bleu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((120 * scale).dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2196F3),
                                    Color(0xFF1565C0)
                                )
                            ),
                            shape = RoundedCornerShape(
                                bottomStart = (30 * scale).dp,
                                bottomEnd = (30 * scale).dp
                            )
                        )
                )

                // Carte flottante
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = (60 * scale).dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    HeaderCard(
                        soldeCompte = soldeCompte,
                        soldeVisible = soldeVisible,
                        onToggleSoldeVisibility = { soldeVisible = !soldeVisible },
                        onNotificationClick = onNotificationClick,
                        onProfileClick = onProfileClick,
                        scale = scale
                    )
                }
            }

            Spacer(Modifier.height((24 * scale).dp))

            // Services
            ServicesSection(
                onServiceClick = onServiceClick,
                scale = scale,
                modifier = Modifier.padding(bottom = (16 * scale).dp)
            )

            // Bannière promotionnelle
            PromoBanner(
                onInviteClick = onInviteClick,
                scale = scale,
                modifier = Modifier.padding(horizontal = (16 * scale).dp)
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Transactions récentes
            TransactionsSection(
                transactions = transactions,
                onSeeAllClick = onSeeAllTransactionsClick,
                onTransactionClick = onTransactionClick,
                scale = scale,
                modifier = Modifier.padding(horizontal = (16 * scale).dp)
            )

            Spacer(Modifier.height((16 * scale).dp))

            // Conseil financier
            FinancialTip(
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
fun PageAccueilPreview() {
    HifadihTheme {
        PageAccueil()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun PageAccueilSmallScreenPreview() {
    HifadihTheme {
        PageAccueil()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 480)
@Composable
fun PageAccueilLargeScreenPreview() {
    HifadihTheme {
        PageAccueil()
    }
}