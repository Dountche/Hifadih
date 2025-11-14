package com.example.hifadih

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

// -------------------- Types de données pour les transactions --------------------

/**
 * Transaction détaillée avec toutes les informations
 */
data class DetailedTransaction(
    val id: String,
    val type: TransactionType,
    val nom: String,
    val montant: Int, // en FCFA
    val frais: Int = 0, // en FCFA
    val nouveauSolde: Int, // Solde après transaction
    val date: String,
    val heure: String,
    val statut: StatutTransaction,
    val icone: String,
    val couleur: String,
    val isCredit: Boolean // true si c'est un crédit (entrée d'argent)
)

/**
 * Étapes de l'écran des transactions
 */
enum class TransactionScreenStep {
    TRANSACTION_LIST, TRANSACTION_DETAILS
}

// -------------------- Données simulées --------------------

/**
 * Génération d'un ID de transaction aléatoire
 */
private fun generateTransactionId(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val random = Random.Default
    return "Tx" + (1..8).map { chars.random(random) }.joinToString("")
}

/**
 * Génération de données de transactions fictives
 */
private fun generateMockTransactions(): List<DetailedTransaction> {
    val transactions = mutableListOf<DetailedTransaction>()
    val random = Random.Default
    var currentBalance = 125750 // Solde actuel

    val transactionTypes = listOf(
        Triple(TransactionType.RECEPTION, "Koffi Jean-Marc", true),
        Triple(TransactionType.RECEPTION, "Aya Fatou", true),
        Triple(TransactionType.RECEPTION, "Kouame Marie", true),
        Triple(TransactionType.ENVOI, "Diabate Ibrahim", false),
        Triple(TransactionType.ENVOI, "Traore Sekou", false),
        Triple(TransactionType.ENVOI, "Yao Adjoua", false),
        Triple(TransactionType.DEPOT, "Dépôt Agence Plateau", true),
        Triple(TransactionType.DEPOT, "Dépôt Mobile Money", true),
        Triple(TransactionType.RETRAIT, "Retrait Distributeur", false),
        Triple(TransactionType.RETRAIT, "Retrait Agence", false),
        Triple(TransactionType.ACHAT, "Recharge Orange Money", false),
        Triple(TransactionType.ACHAT, "Recharge MTN MoMo", false),
        Triple(TransactionType.ACHAT, "Recharge Wave", false),
        Triple(TransactionType.ACHAT, "Dépôt vers Coffre", false),
        Triple(TransactionType.RECEPTION, "Retrait depuis Coffre", true),
        Triple(TransactionType.ACHAT, "Facture CIE", false),
        Triple(TransactionType.ACHAT, "Facture SODECI", false),
        Triple(TransactionType.ACHAT, "Abonnement Canal+", false),
        Triple(TransactionType.ACHAT, "Abonnement Netflix", false),
        Triple(TransactionType.ACHAT, "Carte SOTRA", false)
    )

    val icons = mapOf(
        TransactionType.RECEPTION to "📥",
        TransactionType.ENVOI to "📤",
        TransactionType.DEPOT to "🏦",
        TransactionType.RETRAIT to "💸",
        TransactionType.ACHAT to "💳"
    )

    val colors = mapOf(
        TransactionType.RECEPTION to "#00B894",
        TransactionType.ENVOI to "#E74C3C",
        TransactionType.DEPOT to "#6C5CE7",
        TransactionType.RETRAIT to "#FF9800",
        TransactionType.ACHAT to "#2196F3"
    )

    val statuses = listOf(
        StatutTransaction.REUSSIE to 85, // 85% de succès
        StatutTransaction.EN_COURS to 10, // 10% en cours
        StatutTransaction.ECHOUEE to 5   // 5% d'échec
    )

    // Génération de 20 transactions dans l'ordre chronologique inverse
    repeat(20) { index ->
        val typeInfo = transactionTypes[index % transactionTypes.size]
        val type = typeInfo.first
        val name = typeInfo.second
        val isCredit = typeInfo.third

        // Montant aléatoire selon le type
        val baseAmount = when (type) {
            TransactionType.RECEPTION -> (5000..50000).random()
            TransactionType.ENVOI -> (2000..30000).random()
            TransactionType.DEPOT -> (10000..100000).random()
            TransactionType.RETRAIT -> (5000..50000).random()
            TransactionType.ACHAT -> (1000..25000).random()
        }

        // Frais selon le type (1.5% pour certains types)
        val fees = when (type) {
            TransactionType.ENVOI, TransactionType.RETRAIT -> (baseAmount * 0.015).toInt()
            else -> 0
        }

        // Montant final
        val finalAmount = if (isCredit) baseAmount else -(baseAmount + fees)

        // Nouveau solde après transaction
        val newBalance = if (index == 0) {
            currentBalance // Transaction la plus récente
        } else {
            currentBalance - finalAmount // Solde avant cette transaction
        }

        // Statut de la transaction (avec probabilité)
        val statusRandom = (1..100).random()
        val status = when {
            statusRandom <= 85 -> StatutTransaction.REUSSIE
            statusRandom <= 95 -> StatutTransaction.EN_COURS
            else -> StatutTransaction.ECHOUEE
        }

        // Date et heure (plus anciennes pour les index plus élevés)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -index)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.FRANCE)

        // Ajouter un peu de variation dans l'heure
        calendar.add(Calendar.HOUR_OF_DAY, random.nextInt(-12, 12))
        calendar.add(Calendar.MINUTE, random.nextInt(-30, 30))

        transactions.add(
            DetailedTransaction(
                id = generateTransactionId(),
                type = type,
                nom = name,
                montant = baseAmount,
                frais = fees,
                nouveauSolde = newBalance,
                date = dateFormat.format(calendar.time),
                heure = timeFormat.format(calendar.time),
                statut = status,
                icone = icons[type] ?: "💰",
                couleur = colors[type] ?: "#666666",
                isCredit = isCredit
            )
        )

        // Mettre à jour le solde pour la prochaine transaction
        currentBalance = newBalance
    }

    return transactions
}

// -------------------- Composants de l'écran --------------------

/**
 * Header de l'écran des transactions
 */
@Composable
private fun TransactionsHeader(
    title: String,
    onBackClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
        shape = RoundedCornerShape(
            bottomStart = (20 * scale).dp,
            bottomEnd = (20 * scale).dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size((40 * scale).dp)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White,
                    modifier = Modifier.size((20 * scale).dp)
                )
            }

            Spacer(Modifier.width((12 * scale).dp))

            Text(
                title,
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Barre de recherche
 */
@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Rechercher une transaction...",
                fontSize = (13 * scale).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Recherche",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size((20 * scale).dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        shape = RoundedCornerShape((25 * scale).dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2196F3),
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

/**
 * Item de transaction dans la liste
 */
@Composable
private fun TransactionListItem(
    transaction: DetailedTransaction,
    onTransactionClick: (DetailedTransaction) -> Unit,
    scale: Float,
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
                    Color(android.graphics.Color.parseColor(transaction.couleur)).copy(alpha = 0.2f),
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = (2 * scale).dp)
            ) {
                Text(
                    text = "${transaction.date} • ${transaction.heure}",
                    fontSize = (12 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.width((8 * scale).dp))

                // Badge de statut
                Surface(
                    color = when (transaction.statut) {
                        StatutTransaction.REUSSIE -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        StatutTransaction.EN_COURS -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        StatutTransaction.ECHOUEE -> Color(0xFFE74C3C).copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape((8 * scale).dp)
                ) {
                    Text(
                        text = when (transaction.statut) {
                            StatutTransaction.REUSSIE -> "Réussi"
                            StatutTransaction.EN_COURS -> "En cours"
                            StatutTransaction.ECHOUEE -> "Échec"
                        },
                        fontSize = (9 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = when (transaction.statut) {
                            StatutTransaction.REUSSIE -> Color(0xFF2E7D32)
                            StatutTransaction.EN_COURS -> Color(0xFFE65100)
                            StatutTransaction.ECHOUEE -> Color(0xFFD32F2F)
                        },
                        modifier = Modifier.padding(
                            horizontal = (6 * scale).dp,
                            vertical = (2 * scale).dp
                        )
                    )
                }
            }
        }

        // Montant
        Column(horizontalAlignment = Alignment.End) {
            val displayAmount = if (transaction.isCredit) transaction.montant else -(transaction.montant + transaction.frais)
            Text(
                text = "${if (transaction.isCredit) "+" else ""}${formatAmount(kotlin.math.abs(displayAmount))}",
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isCredit) Color(0xFF00B894) else MaterialTheme.colorScheme.onSurface
            )

            if (transaction.frais > 0) {
                Text(
                    text = "Frais: ${formatAmount(transaction.frais)}",
                    fontSize = (10 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Liste des transactions
 */
@Composable
private fun TransactionsList(
    transactions: List<DetailedTransaction>,
    filteredTransactions: List<DetailedTransaction>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTransactionClick: (DetailedTransaction) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding((16 * scale).dp)
    ) {
        // Barre de recherche
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            scale = scale,
            modifier = Modifier.padding(bottom = (16 * scale).dp)
        )

        // Résultats
        Text(
            "${filteredTransactions.size} transaction${if (filteredTransactions.size > 1) "s" else ""}",
            fontSize = (13 * scale).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = (8 * scale).dp)
        )

        // Liste scrollable
        LazyColumn {
            items(filteredTransactions) { transaction ->
                TransactionListItem(
                    transaction = transaction,
                    onTransactionClick = onTransactionClick,
                    scale = scale
                )

                if (transaction != filteredTransactions.last()) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                        thickness = (1 * scale).dp,
                        modifier = Modifier.padding(horizontal = (16 * scale).dp)
                    )
                }
            }
        }
    }
}

/**
 * Détails d'une transaction
 */
@Composable
private fun TransactionDetails(
    transaction: DetailedTransaction,
    onDownloadReceipt: (DetailedTransaction) -> Unit,
    onShareTransaction: (DetailedTransaction) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding((16 * scale).dp),
        verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
    ) {
        // Nom du destinataire/expéditeur
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(android.graphics.Color.parseColor(transaction.couleur)).copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = (1 * scale).dp,
                color = Color(android.graphics.Color.parseColor(transaction.couleur)).copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size((50 * scale).dp)
                        .background(
                            Color(android.graphics.Color.parseColor(transaction.couleur)).copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        transaction.icone,
                        fontSize = (20 * scale).sp
                    )
                }

                Spacer(Modifier.width((12 * scale).dp))

                Column {
                    Text(
                        transaction.nom,
                        fontSize = (18 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(transaction.couleur))
                    )
                    Text(
                        when (transaction.type) {
                            TransactionType.RECEPTION -> "Expéditeur"
                            TransactionType.ENVOI -> "Destinataire"
                            else -> "Service"
                        },
                        fontSize = (12 * scale).sp,
                        color = Color(android.graphics.Color.parseColor(transaction.couleur)).copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Boutons d'action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((12 * scale).dp)
        ) {
            OutlinedButton(
                onClick = { onDownloadReceipt(transaction) },
                modifier = Modifier
                    .weight(1f)
                    .height((45 * scale).dp),
                shape = RoundedCornerShape((12 * scale).dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF2196F3)
                )
            ) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "Télécharger",
                    modifier = Modifier.size((16 * scale).dp)
                )
                Spacer(Modifier.width((6 * scale).dp))
                Text(
                    "Télécharger",
                    fontSize = (12 * scale).sp
                )
            }

            Button(
                onClick = { onShareTransaction(transaction) },
                modifier = Modifier
                    .weight(1f)
                    .height((45 * scale).dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "Partager",
                    modifier = Modifier.size((16 * scale).dp)
                )
                Spacer(Modifier.width((6 * scale).dp))
                Text(
                    "Partager",
                    fontSize = (12 * scale).sp
                )
            }
        }

        // Détails de la transaction
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = (6 * scale).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((20 * scale).dp)
            ) {
                Text(
                    "Détails de la transaction",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (16 * scale).dp)
                )

                TransactionDetailRow("ID Transaction", transaction.id, scale)
                TransactionDetailRow(
                    "Statut",
                    when (transaction.statut) {
                        StatutTransaction.REUSSIE -> "Réussi"
                        StatutTransaction.EN_COURS -> "En cours"
                        StatutTransaction.ECHOUEE -> "Échec"
                    },
                    scale,
                    valueColor = when (transaction.statut) {
                        StatutTransaction.REUSSIE -> Color(0xFF4CAF50)
                        StatutTransaction.EN_COURS -> Color(0xFFFF9800)
                        StatutTransaction.ECHOUEE -> Color(0xFFE74C3C)
                    }
                )
                TransactionDetailRow("Montant", formatAmount(transaction.montant), scale)

                if (transaction.frais > 0) {
                    TransactionDetailRow("Frais", formatAmount(transaction.frais), scale)
                }

                TransactionDetailRow("Date", "${transaction.date} à ${transaction.heure}", scale)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                TransactionDetailRow(
                    "Nouveau solde",
                    formatAmount(transaction.nouveauSolde),
                    scale,
                    isBold = true,
                    valueColor = Color(0xFF2196F3)
                )
            }
        }
    }
}

/**
 * Ligne de détail dans les informations de transaction
 */
@Composable
private fun TransactionDetailRow(
    label: String,
    value: String,
    scale: Float,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = (6 * scale).dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = (13 * scale).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            fontSize = (13 * scale).sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}

// -------------------- Écran principal des transactions --------------------

/**
 * Écran principal de l'historique des transactions
 */
@Composable
fun TransactionsScreen(
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive measurements
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var currentStep by remember { mutableStateOf(TransactionScreenStep.TRANSACTION_LIST) }
    var selectedTransaction by remember { mutableStateOf<DetailedTransaction?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Données des transactions
    val allTransactions = remember { generateMockTransactions() }

    // Filtrage des transactions
    val filteredTransactions = remember(searchQuery, allTransactions) {
        if (searchQuery.isBlank()) {
            allTransactions
        } else {
            allTransactions.filter { transaction ->
                transaction.nom.contains(searchQuery, ignoreCase = true) ||
                        transaction.type.name.contains(searchQuery, ignoreCase = true) ||
                        when (searchQuery.lowercase()) {
                            "depot", "dépôt" -> transaction.type == TransactionType.DEPOT
                            "recharge" -> transaction.nom.contains("Recharge", ignoreCase = true)
                            "transfert", "envoi" -> transaction.type == TransactionType.ENVOI
                            "coffre" -> transaction.nom.contains("Coffre", ignoreCase = true)
                            "reception", "réception" -> transaction.type == TransactionType.RECEPTION
                            "retrait" -> transaction.type == TransactionType.RETRAIT
                            "facture" -> transaction.nom.contains("Facture", ignoreCase = true)
                            "abonnement" -> transaction.nom.contains("Abonnement", ignoreCase = true)
                            else -> false
                        }
            }
        }
    }

    // Handlers
    val handleTransactionClick: (DetailedTransaction) -> Unit = { transaction ->
        selectedTransaction = transaction
        currentStep = TransactionScreenStep.TRANSACTION_DETAILS
    }

    val handleBack = {
        when (currentStep) {
            TransactionScreenStep.TRANSACTION_LIST -> onBackPressed()
            TransactionScreenStep.TRANSACTION_DETAILS -> {
                selectedTransaction = null
                currentStep = TransactionScreenStep.TRANSACTION_LIST
            }
        }
    }

    val handleDownloadReceipt: (DetailedTransaction) -> Unit = { transaction ->
        // Simulation du téléchargement
        // TODO: Implémenter la logique de téléchargement du reçu
    }

    val handleShareTransaction: (DetailedTransaction) -> Unit = { transaction ->
        // Simulation du partage
        // TODO: Implémenter la logique de partage
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        TransactionsHeader(
            title = when (currentStep) {
                TransactionScreenStep.TRANSACTION_LIST -> "Historique des transactions"
                TransactionScreenStep.TRANSACTION_DETAILS -> "Détails de la transaction"
            },
            onBackClick = handleBack,
            scale = scale
        )

        // Contenu avec animation
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                slideInHorizontally(
                    initialOffsetX = { width -> width },
                    animationSpec = tween(300)
                ) + fadeIn() togetherWith
                        slideOutHorizontally(
                            targetOffsetX = { width -> -width },
                            animationSpec = tween(300)
                        ) + fadeOut()
            },
            label = "Transaction screen transition"
        ) { step ->
            when (step) {
                TransactionScreenStep.TRANSACTION_LIST -> TransactionsList(
                    transactions = allTransactions,
                    filteredTransactions = filteredTransactions,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onTransactionClick = handleTransactionClick,
                    scale = scale
                )

                TransactionScreenStep.TRANSACTION_DETAILS -> {
                    selectedTransaction?.let { transaction ->
                        TransactionDetails(
                            transaction = transaction,
                            onDownloadReceipt = handleDownloadReceipt,
                            onShareTransaction = handleShareTransaction,
                            scale = scale
                        )
                    }
                }
            }
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun TransactionsScreenPreview() {
    HifadihTheme {
        TransactionsScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Liste avec recherche")
@Composable
fun TransactionsListPreview() {
    HifadihTheme {
        TransactionsScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun TransactionsScreenSmallPreview() {
    HifadihTheme {
        TransactionsScreen()
    }
}