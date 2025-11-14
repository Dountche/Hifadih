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

// -------------------- Types de données pour le transfert --------------------

// Type de service de transfert

enum class TransferServiceType {
    HIFADIH, WAVE, MTN_MOMO, ORANGE_MONEY, MOOV_MONEY
}

// Étape du processus de transfert

enum class TransferStep {
    FORM, SUMMARY, SUCCESS
}

//Service de transfert disponible

data class TransferService(
    val type: TransferServiceType,
    val name: String,
    val logo: Int, // Resource drawable
    val color: String,
    val description: String
)

// Données du transfert
data class TransferData(
    val amount: Int = 0,
    val receiverNumber: String = "",
    val service: TransferService? = null,
    val payFees: Boolean = true
) {
    val feeAmount: Int
        get() = if (payFees) (amount * 0.015).toInt() else 0

    val totalAmount: Int
        get() = amount + feeAmount
}

// -------------------- Composants utilitaires --------------------

// Services de transfert disponibles

@Composable
private fun getTransferServices(): List<TransferService> {
    return remember {
        listOf(
            TransferService(
                TransferServiceType.HIFADIH,
                "Hifadih",
                R.drawable.ic_launcher_foreground,
                "#2196F3",
                "Transfert gratuit vers Hifadih"
            ),
            TransferService(
                TransferServiceType.WAVE,
                "Wave",
                R.drawable.wave_logo,
                "#00D4AA",
                "Vers compte Wave"
            ),
            TransferService(
                TransferServiceType.MTN_MOMO,
                "MTN MoMo",
                R.drawable.mtn_logo,
                "#FFD700",
                "Vers MTN Mobile Money"
            ),
            TransferService(
                TransferServiceType.ORANGE_MONEY,
                "Orange Money",
                R.drawable.om_logo,
                "#FF9800",
                "Vers Orange Money"
            ),
            TransferService(
                TransferServiceType.MOOV_MONEY,
                "Moov Money",
                R.drawable.moov_logo,
                "#1E88E5",
                "Vers Moov Money"
            )
        )
    }
}

// Formater un montant en FCFA

fun formatAmount(amount: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE)
    return "${formatter.format(amount)} FCFA"
}

// -------------------- Composants de l'écran --------------------

// Header de l'écran de transfert
@Composable
private fun TransferHeader(
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

// Sélecteur de service de transfert

@Composable
private fun ServiceSelector(
    services: List<TransferService>,
    selectedService: TransferService?,
    onServiceSelected: (TransferService) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            "Service de destination",
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
                ServiceCard(
                    service = service,
                    isSelected = selectedService?.type == service.type,
                    onSelect = { onServiceSelected(service) },
                    scale = scale
                )
            }
        }
    }
}

// Carte d'un service de transfert

@Composable
private fun ServiceCard(
    service: TransferService,
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

            if (service.type == TransferServiceType.HIFADIH) {
                Text(
                    "Gratuit",
                    fontSize = (9 * scale).sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            }

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

// Formulaire de transfert

@Composable
private fun TransferForm(
    transferData: TransferData,
    services: List<TransferService>,
    onAmountChange: (String) -> Unit,
    onReceiverNumberChange: (String) -> Unit,
    onServiceSelected: (TransferService) -> Unit,
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
        // Montant à transférer
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
                    "Montant à transférer",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (12 * scale).dp)
                )

                OutlinedTextField(
                    value = if (transferData.amount > 0) transferData.amount.toString() else "",
                    onValueChange = onAmountChange,
                    label = { Text("Montant", fontSize = (12 * scale).sp) },
                    placeholder = { Text("Ex: 10000", fontSize = (12 * scale).sp) },
                    suffix = { Text("FCFA", fontSize = (12 * scale).sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((12 * scale).dp)
                )

                if (transferData.amount > 0) {
                    Text(
                        "Montant: ${formatAmount(transferData.amount)}",
                        fontSize = (12 * scale).sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = (4 * scale).dp)
                    )
                }
            }
        }

        // Sélection du service
        ServiceSelector(
            services = services,
            selectedService = transferData.service,
            onServiceSelected = onServiceSelected,
            scale = scale
        )

        // Numéro du destinataire
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
                    "Numéro du destinataire",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (12 * scale).dp)
                )

                OutlinedTextField(
                    value = transferData.receiverNumber,
                    onValueChange = onReceiverNumberChange,
                    label = { Text("Numéro", fontSize = (12 * scale).sp) },
                    placeholder = { Text("Ex: +225 01 23 45 67 89", fontSize = (12 * scale).sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape((12 * scale).dp)
                )

                transferData.service?.let { service ->
                    Text(
                        "Vers ${service.name} - ${service.description}",
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
            enabled = transferData.amount > 0 &&
                    transferData.receiverNumber.isNotBlank() &&
                    transferData.service != null,
            modifier = Modifier
                .fillMaxWidth()
                .height((50 * scale).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
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

//  Récapitulatif du transfert

@Composable
private fun TransferSummary(
    transferData: TransferData,
    onPayFeesToggle: (Boolean) -> Unit,
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
                    "Récapitulatif du transfert",
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (16 * scale).dp)
                )

                // Détails du transfert
                TransferDetailRow("Montant", formatAmount(transferData.amount), scale)
                TransferDetailRow("Vers", transferData.service?.name ?: "", scale)
                TransferDetailRow("Numéro", transferData.receiverNumber, scale)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Frais
                if (transferData.service?.type != TransferServiceType.HIFADIH) {
                    TransferDetailRow(
                        "Frais (1.5%)",
                        if (transferData.payFees) formatAmount(transferData.feeAmount) else "Payés par le destinataire",
                        scale
                    )
                } else {
                    TransferDetailRow("Frais", "Gratuit", scale, valueColor = Color(0xFF4CAF50))
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Total
                TransferDetailRow(
                    "Total à débiter",
                    formatAmount(transferData.totalAmount),
                    scale,
                    isBold = true
                )
            }
        }

        // Option de paiement des frais (only pour les services tiers)
        if (transferData.service?.type != TransferServiceType.HIFADIH) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((16 * scale).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Paiement des frais",
                            fontSize = (14 * scale).sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Je paie les frais de transfert",
                            fontSize = (12 * scale).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = (2 * scale).dp)
                        )
                    }

                    Switch(
                        checked = transferData.payFees,
                        onCheckedChange = onPayFeesToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2196F3)
                        )
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
                    "Confirmer le transfert",
                    fontSize = (14 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Ligne de détail dans le récapitulatif

@Composable
private fun TransferDetailRow(
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

//Écran de succès

@Composable
private fun TransferSuccess(
    transferData: TransferData,
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
            "Transfert réussi !",
            fontSize = (24 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height((12 * scale).dp))

        Text(
            "Votre transfert de ${formatAmount(transferData.amount)} vers ${transferData.receiverNumber} a été effectué avec succès.",
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
                containerColor = Color(0xFF2196F3)
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

// -------------------- Écran principal de transfert --------------------

// Écran principal de transfert d'argent

@Composable
fun TransferScreen(
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var currentStep by remember { mutableStateOf(TransferStep.FORM) }
    var transferData by remember { mutableStateOf(TransferData()) }

    val services = getTransferServices()

    // Handlers
    val handleAmountChange: (String) -> Unit = { amount ->
        val numericAmount = amount.filter { it.isDigit() }.take(8)
        transferData = transferData.copy(
            amount = numericAmount.toIntOrNull() ?: 0
        )
    }

    val handleReceiverNumberChange: (String) -> Unit = { number ->
        transferData = transferData.copy(receiverNumber = number)
    }

    val handleServiceSelected: (TransferService) -> Unit = { service ->
        transferData = transferData.copy(service = service)
    }

    val handlePayFeesToggle: (Boolean) -> Unit = { payFees ->
        transferData = transferData.copy(payFees = payFees)
    }

    val handleContinue = { currentStep = TransferStep.SUMMARY }
    val handleBack = { currentStep = TransferStep.FORM }
    val handleConfirm = { currentStep = TransferStep.SUCCESS }
    val handleFinish = { onBackPressed() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        TransferHeader(
            title = when (currentStep) {
                TransferStep.FORM -> "Transfert d'argent"
                TransferStep.SUMMARY -> "Confirmer le transfert"
                TransferStep.SUCCESS -> "Transfert effectué"
            },
            onBackClick = {
                when (currentStep) {
                    TransferStep.FORM -> onBackPressed()
                    TransferStep.SUMMARY -> handleBack()
                    TransferStep.SUCCESS -> handleFinish()
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
            label = "Transfer step transition"
        ) { step ->
            when (step) {
                TransferStep.FORM -> TransferForm(
                    transferData = transferData,
                    services = services,
                    onAmountChange = handleAmountChange,
                    onReceiverNumberChange = handleReceiverNumberChange,
                    onServiceSelected = handleServiceSelected,
                    onContinue = handleContinue,
                    scale = scale
                )

                TransferStep.SUMMARY -> TransferSummary(
                    transferData = transferData,
                    onPayFeesToggle = handlePayFeesToggle,
                    onConfirm = handleConfirm,
                    onBack = handleBack,
                    scale = scale
                )

                TransferStep.SUCCESS -> TransferSuccess(
                    transferData = transferData,
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
fun TransferScreenPreview() {
    HifadihTheme {
        TransferScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Avec données")
@Composable
fun TransferScreenWithDataPreview() {
    HifadihTheme {
        TransferScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun TransferScreenSmallPreview() {
    HifadihTheme {
        TransferScreen()
    }
}