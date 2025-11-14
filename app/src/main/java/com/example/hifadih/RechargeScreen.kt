package com.example.hifadih

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import java.text.NumberFormat
import java.util.*

// -------------------- Types de données pour la recharge --------------------

//Service de recharge disponible

enum class RechargeServiceType {
    WAVE, MTN_MOMO, ORANGE_MONEY, MOOV_MONEY
}

//Étape du processus de recharge

enum class RechargeStep {
    FORM, SUMMARY, SUCCESS
}

// Service de recharge

data class RechargeService(
    val type: RechargeServiceType,
    val name: String,
    val logo: Int, // Resource drawable
    val color: String,
    val description: String
)

// Données de la recharge

data class RechargeData(
    val amount: Int = 0,
    val sourceNumber: String = "", // Le numéro depuis lequel on recharge
    val service: RechargeService? = null
) {
    val feeAmount: Int
        get() = (amount * 0.015).toInt() // 1.5% de frais

    val totalToDebit: Int
        get() = amount + feeAmount // Total à débiter du service externe

    val amountToReceive: Int
        get() = amount // Montant qui arrive sur Hifadih
}

// -------------------- Utilitaires --------------------

// Services de recharge disponibles

@Composable
private fun getRechargeServices(): List<RechargeService> {
    return remember {
        listOf(
            RechargeService(
                RechargeServiceType.WAVE,
                "Wave",
                R.drawable.wave_logo,
                "#00D4AA",
                "Recharge depuis Wave"
            ),
            RechargeService(
                RechargeServiceType.MTN_MOMO,
                "MTN MoMo",
                R.drawable.mtn_logo,
                "#FFD700",
                "Recharge depuis MTN Mobile Money"
            ),
            RechargeService(
                RechargeServiceType.ORANGE_MONEY,
                "Orange Money",
                R.drawable.om_logo,
                "#FF9800",
                "Recharge depuis Orange Money"
            ),
            RechargeService(
                RechargeServiceType.MOOV_MONEY,
                "Moov Money",
                R.drawable.moov_logo,
                "#1E88E5",
                "Recharge depuis Moov Money"
            )
        )
    }
}

// Montants de recharge rapides

@Composable
private fun getQuickAmounts(): List<Int> {
    return remember {
        listOf(1000, 2000, 5000, 10000, 20000, 50000)
    }
}

// -------------------- Composants de l'écran --------------------


//  Header de l'écran de recharge

@Composable
private fun RechargeHeader(
    title: String,
    onBackClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
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

// Sélecteur de service de recharge

@Composable
private fun RechargeServiceSelector(
    services: List<RechargeService>,
    selectedService: RechargeService?,
    onServiceSelected: (RechargeService) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Service source",
            fontSize = (14 * scale).sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = (8 * scale).dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy((12 * scale).dp),
            contentPadding = PaddingValues(horizontal = (4 * scale).dp)
        ) {
            items(services) { service ->
                RechargeServiceCard(
                    service = service,
                    isSelected = selectedService?.type == service.type,
                    onSelect = { onServiceSelected(service) },
                    scale = scale
                )
            }
        }
    }
}

 // Carte d'un service de recharge

@Composable
private fun RechargeServiceCard(
    service: RechargeService,
    isSelected: Boolean,
    onSelect: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width((100 * scale).dp)
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(
                width = (2 * scale).dp,
                color = Color(android.graphics.Color.parseColor(service.color))
            ) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = (2 * scale).dp),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((12 * scale).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo du service
            Box(
                modifier = Modifier
                    .size((40 * scale).dp)
                    .background(
                        Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(service.logo),
                    contentDescription = service.name,
                    modifier = Modifier.size((24 * scale).dp)
                )
            }

            Spacer(Modifier.height((8 * scale).dp))

            Text(
                service.name,
                fontSize = (11 * scale).sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                "Frais: 1.5%",
                fontSize = (9 * scale).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            )

            // Indicateur de sélection
            if (isSelected) {
                Spacer(Modifier.height((4 * scale).dp))
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Sélectionné",
                    tint = Color(android.graphics.Color.parseColor(service.color)),
                    modifier = Modifier.size((16 * scale).dp)
                )
            }
        }
    }
}

// Sélecteur de montants rapides

@Composable
private fun QuickAmountSelector(
    amounts: List<Int>,
    selectedAmount: Int,
    onAmountSelected: (Int) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Montants rapides",
            fontSize = (14 * scale).sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = (8 * scale).dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy((8 * scale).dp),
            contentPadding = PaddingValues(horizontal = (4 * scale).dp)
        ) {
            items(amounts) { amount ->
                FilterChip(
                    selected = selectedAmount == amount,
                    onClick = { onAmountSelected(amount) },
                    label = {
                        Text(
                            "${amount / 1000}K",
                            fontSize = (12 * scale).sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

//* Formulaire de recharge

@Composable
private fun RechargeForm(
    rechargeData: RechargeData,
    services: List<RechargeService>,
    quickAmounts: List<Int>,
    onAmountChange: (String) -> Unit,
    onQuickAmountSelected: (Int) -> Unit,
    onSourceNumberChange: (String) -> Unit,
    onServiceSelected: (RechargeService) -> Unit,
    onContinue: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding((16 * scale).dp),
        verticalArrangement = Arrangement.spacedBy((20 * scale).dp)
    ) {
        // Information sur le compte à recharger
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
            elevation = CardDefaults.cardElevation(defaultElevation = (2 * scale).dp),
            border = androidx.compose.foundation.BorderStroke(
                width = (1 * scale).dp,
                color = Color(0xFF4CAF50).copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = "Compte Hifadih",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size((24 * scale).dp)
                )

                Spacer(Modifier.width((12 * scale).dp))

                Column {
                    Text(
                        "Recharge vers votre compte Hifadih",
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        "L'argent sera ajouté à votre solde principal",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFF388E3C)
                    )
                }
            }
        }

        // Montant à recharger
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp)
            ) {
                Text(
                    "💰 Montant à recharger",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (12 * scale).dp)
                )

                OutlinedTextField(
                    value = if (rechargeData.amount > 0) rechargeData.amount.toString() else "",
                    onValueChange = onAmountChange,
                    label = { Text("Montant", fontSize = (12 * scale).sp) },
                    placeholder = { Text("Ex: 10000", fontSize = (12 * scale).sp) },
                    suffix = { Text("FCFA", fontSize = (12 * scale).sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((12 * scale).dp)
                )

                if (rechargeData.amount > 0) {
                    Spacer(Modifier.height((8 * scale).dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Montant reçu:",
                            fontSize = (12 * scale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            formatAmount(rechargeData.amountToReceive),
                            fontSize = (12 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(Modifier.height((12 * scale).dp))

                // Montants rapides
                QuickAmountSelector(
                    amounts = quickAmounts,
                    selectedAmount = rechargeData.amount,
                    onAmountSelected = onQuickAmountSelected,
                    scale = scale
                )
            }
        }

        // Sélection du service
        RechargeServiceSelector(
            services = services,
            selectedService = rechargeData.service,
            onServiceSelected = onServiceSelected,
            scale = scale
        )

        // Numéro source
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp)
            ) {
                Text(
                    "📱 Votre numéro ${rechargeData.service?.name ?: ""}",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (12 * scale).dp)
                )

                OutlinedTextField(
                    value = rechargeData.sourceNumber,
                    onValueChange = onSourceNumberChange,
                    label = { Text("Votre numéro", fontSize = (12 * scale).sp) },
                    placeholder = { Text("Ex: +225 01 23 45 67 89", fontSize = (12 * scale).sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((12 * scale).dp)
                )

                rechargeData.service?.let { service ->
                    Text(
                        "Un code USSD sera envoyé à ce numéro pour confirmer la recharge depuis ${service.name}",
                        fontSize = (11 * scale).sp,
                        color = Color(android.graphics.Color.parseColor(service.color)),
                        modifier = Modifier.padding(top = (4 * scale).dp)
                    )
                }
            }
        }

        Spacer(Modifier.height((8 * scale).dp))

        // Bouton Continuer
        Button(
            onClick = onContinue,
            enabled = rechargeData.amount > 0 &&
                    rechargeData.sourceNumber.isNotBlank() &&
                    rechargeData.service != null,
            modifier = Modifier
                .fillMaxWidth()
                .height((50 * scale).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape((12 * scale).dp)
        ) {
            Text(
                "Continuer",
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Récapitulatif de la recharge

@Composable
private fun RechargeSummary(
    rechargeData: RechargeData,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
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
        // Récapitulatif
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
                    "Récapitulatif de la recharge",
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (16 * scale).dp)
                )

                // Détails de la recharge
                RechargeDetailRow("Service source", rechargeData.service?.name ?: "", scale)
                RechargeDetailRow("Votre numéro", rechargeData.sourceNumber, scale)
                RechargeDetailRow("Montant demandé", formatAmount(rechargeData.amount), scale)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Frais et calculs
                RechargeDetailRow("Frais (1.5%)", formatAmount(rechargeData.feeAmount), scale)
                RechargeDetailRow("Total à débiter de ${rechargeData.service?.name}", formatAmount(rechargeData.totalToDebit), scale, valueColor = Color(0xFFE74C3C))

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Total reçu
                RechargeDetailRow(
                    "Montant reçu sur Hifadih",
                    formatAmount(rechargeData.amountToReceive),
                    scale,
                    isBold = true,
                    valueColor = Color(0xFF4CAF50)
                )
            }
        }

        // Avertissement sur les frais
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = androidx.compose.foundation.BorderStroke(
                width = (1 * scale).dp,
                color = Color(0xFFFF9800).copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((12 * scale).dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "⚠️",
                    fontSize = (16 * scale).sp,
                    modifier = Modifier.padding(end = (8 * scale).dp)
                )
                Column {
                    Text(
                        "Confirmation requise",
                        fontSize = (13 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        "Vous recevrez un code USSD sur votre numéro ${rechargeData.service?.name} pour confirmer la transaction.",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFFEF6C00),
                        modifier = Modifier.padding(top = (2 * scale).dp)
                    )
                }
            }
        }

        Spacer(Modifier.height((8 * scale).dp))

        // Boutons d'action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((12 * scale).dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height((50 * scale).dp),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Text("Modifier", fontSize = (14 * scale).sp)
            }

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .weight(2f)
                    .height((50 * scale).dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Text(
                    "Confirmer la recharge",
                    fontSize = (14 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Ligne de détail dans le récapitulatif

@Composable
private fun RechargeDetailRow(
    label: String,
    value: String,
    scale: Float,
    isBold: Boolean = false,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = (4 * scale).dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = (13 * scale).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            fontSize = (13 * scale).sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

// Écran de succès

@Composable
private fun RechargeSuccess(
    rechargeData: RechargeData,
    onFinish: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding((24 * scale).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icône de succès
        Box(
            modifier = Modifier
                .size((100 * scale).dp)
                .background(
                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Succès",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size((50 * scale).dp)
            )
        }

        Spacer(Modifier.height((24 * scale).dp))

        Text(
            "Demande de recharge envoyée !",
            fontSize = (22 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height((12 * scale).dp))

        Text(
            "Vérifiez votre téléphone ${rechargeData.service?.name} pour le code USSD de confirmation.\n\n${formatAmount(rechargeData.amountToReceive)} seront ajoutés à votre compte après validation.",
            fontSize = (14 * scale).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height((32 * scale).dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height((50 * scale).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape((12 * scale).dp)
        ) {
            Text(
                "Terminé",
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// -------------------- Écran principal de recharge --------------------

// Écran principal de recharge du compte

@Composable
fun RechargeScreen(
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive measurements
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var currentStep by remember { mutableStateOf(RechargeStep.FORM) }
    var rechargeData by remember { mutableStateOf(RechargeData()) }

    val services = getRechargeServices()
    val quickAmounts = getQuickAmounts()

    // Handlers
    val handleAmountChange: (String) -> Unit = { amount ->
        val numericAmount = amount.filter { it.isDigit() }.take(8)
        rechargeData = rechargeData.copy(
            amount = numericAmount.toIntOrNull() ?: 0
        )
    }

    val handleQuickAmountSelected: (Int) -> Unit = { amount ->
        rechargeData = rechargeData.copy(amount = amount)
    }

    val handleSourceNumberChange: (String) -> Unit = { number ->
        rechargeData = rechargeData.copy(sourceNumber = number)
    }

    val handleServiceSelected: (RechargeService) -> Unit = { service ->
        rechargeData = rechargeData.copy(service = service)
    }

    val handleContinue = { currentStep = RechargeStep.SUMMARY }
    val handleBack = { currentStep = RechargeStep.FORM }
    val handleConfirm = { currentStep = RechargeStep.SUCCESS }
    val handleFinish = { onBackPressed() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        RechargeHeader(
            title = when (currentStep) {
                RechargeStep.FORM -> "Recharger mon compte"
                RechargeStep.SUMMARY -> "Confirmer la recharge"
                RechargeStep.SUCCESS -> "Recharge en cours"
            },
            onBackClick = {
                when (currentStep) {
                    RechargeStep.FORM -> onBackPressed()
                    RechargeStep.SUMMARY -> handleBack()
                    RechargeStep.SUCCESS -> handleFinish()
                }
            },
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
            label = "Recharge step transition"
        ) { step ->
            when (step) {
                RechargeStep.FORM -> RechargeForm(
                    rechargeData = rechargeData,
                    services = services,
                    quickAmounts = quickAmounts,
                    onAmountChange = handleAmountChange,
                    onQuickAmountSelected = handleQuickAmountSelected,
                    onSourceNumberChange = handleSourceNumberChange,
                    onServiceSelected = handleServiceSelected,
                    onContinue = handleContinue,
                    scale = scale
                )

                RechargeStep.SUMMARY -> RechargeSummary(
                    rechargeData = rechargeData,
                    onConfirm = handleConfirm,
                    onBack = handleBack,
                    scale = scale
                )

                RechargeStep.SUCCESS -> RechargeSuccess(
                    rechargeData = rechargeData,
                    onFinish = handleFinish,
                    scale = scale
                )
            }
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun RechargeScreenPreview() {
    HifadihTheme {
        RechargeScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Étape formulaire")
@Composable
fun RechargeFormPreview() {
    HifadihTheme {
        RechargeScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Étape récapitulatif")
@Composable
fun RechargeSummaryPreview() {
    HifadihTheme {
        var step by remember { mutableStateOf(RechargeStep.SUMMARY) }
        RechargeScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun RechargeScreenSmallPreview() {
    HifadihTheme {
        RechargeScreen()
    }
}