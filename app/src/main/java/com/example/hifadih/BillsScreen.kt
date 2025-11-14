package com.example.hifadih

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// -------------------- Types de données pour les factures --------------------

// Type de service de factures

enum class BillServiceType {
    CIE_PREPAID, CIE_POSTPAID, SODECI
}

// Étapes du processus de paiement de factures

enum class BillStep {
    SERVICE_LIST, BILL_FORM, BILL_DETAILS, SUMMARY, SUCCESS
}


// Service de factures

data class BillService(
    val type: BillServiceType,
    val name: String,
    val logo: Int, // Resource drawable
    val color: String,
    val description: String
)

// Détails d'une facture récupérée

data class BillDetails(
    val customerName: String,
    val customerPhone: String,
    val meterNumber: String,
    val billAmount: Int,
    val lateFees: Int = 0,
    val dueDate: String,
    val isOverdue: Boolean = false,
    val contractRef: String = ""
) {
    val totalAmount: Int
        get() = billAmount + lateFees
}

// Données de recherche de facture

data class BillSearchData(
    val service: BillService? = null,
    val reference: String = "", // Numéro compteur ou référence contrat
    val amount: Int = 0, // Pour prépayé CIE seulement
    val billDetails: BillDetails? = null,
    val isSearching: Boolean = false,
    val searchError: String? = null
)

// -------------------- Données simulées --------------------

// Simulation de données de factures

private fun simulateBillLookup(service: BillService, reference: String): BillDetails? {
    return if (reference.length >= 8) {
        when (service.type) {
            BillServiceType.CIE_POSTPAID -> {
                val names = listOf(
                    "KOUAME Abdul" to "+225 01 23 45 67",
                    "KONE Mamadou" to "+225 07 89 12 34",
                    "YAO Marie Philippe" to "+225 05 67 89 01"
                )
                val randomName = names.random()
                val baseAmount = (15000..85000).random()
                val isOverdue = (1..10).random() > 7 // 30% chance d'être en retard
                val lateFees = if (isOverdue) (baseAmount * 0.05).toInt() else 0

                BillDetails(
                    customerName = randomName.first,
                    customerPhone = randomName.second,
                    meterNumber = reference,
                    billAmount = baseAmount,
                    lateFees = lateFees,
                    dueDate = "15/01/2025",
                    isOverdue = isOverdue,
                    contractRef = "CIE${reference.take(6)}"
                )
            }

            BillServiceType.SODECI -> {
                val names = listOf(
                    "DIABATE Ibrahim" to "+225 01 11 22 33",
                    "TRAORE Fatoumata" to "+225 07 44 55 66",
                    "BAMBA Sekou" to "+225 05 77 88 99"
                )
                val randomName = names.random()
                val baseAmount = (8000..45000).random()
                val isOverdue = (1..10).random() > 8 // 20% chance d'être en retard
                val lateFees = if (isOverdue) (baseAmount * 0.03).toInt() else 0

                BillDetails(
                    customerName = randomName.first,
                    customerPhone = randomName.second,
                    meterNumber = reference,
                    billAmount = baseAmount,
                    lateFees = lateFees,
                    dueDate = "20/01/2025",
                    isOverdue = isOverdue,
                    contractRef = "SODECI${reference.take(6)}"
                )
            }

            else -> null
        }
    } else null
}

@Composable
private fun getBillServices(): List<BillService> {
    return remember {
        listOf(
            BillService(
                BillServiceType.CIE_PREPAID,
                "CIE Prépayé",
                R.drawable.cie,
                "#FF9800",
                "Recharge compteur électrique"
            ),
            BillService(
                BillServiceType.CIE_POSTPAID,
                "Facture CIE",
                R.drawable.cie,
                "#FF5722",
                "Paiement facture électricité"
            ),
            BillService(
                BillServiceType.SODECI,
                "Facture SODECI",
                R.drawable.sodeci,
                "#2196F3",
                "Paiement facture eau"
            )
        )
    }
}

// -------------------- Composants de l'écran --------------------

// Header de l'écran de factures

@Composable
private fun BillsHeader(
    title: String,
    onBackClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7)),
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

 // Liste des services de factures

@Composable
private fun BillServiceList(
    services: List<BillService>,
    onServiceSelected: (BillService) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding((16 * scale).dp),
        verticalArrangement = Arrangement.spacedBy((12 * scale).dp)
    ) {
        item {
            Text(
                "🧾 Choisissez votre type de facture",
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = (8 * scale).dp)
            )
        }

        items(services) { service ->
            BillServiceCard(
                service = service,
                onSelect = { onServiceSelected(service) },
                scale = scale
            )
        }
    }
}

//* Carte d'un service de factures

@Composable
private fun BillServiceCard(
    service: BillService,
    onSelect: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo du service
            Box(
                modifier = Modifier
                    .size((50 * scale).dp)
                    .background(
                        Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(service.logo),
                    contentDescription = service.name,
                    modifier = Modifier.size((30 * scale).dp)
                )
            }

            Spacer(Modifier.width((16 * scale).dp))

            // Informations du service
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    service.name,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    service.description,
                    fontSize = (13 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = (2 * scale).dp)
                )
            }

            // Flèche
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Sélectionner",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size((24 * scale).dp)
                    .rotate(270f)
            )
        }
    }
}


// Formulaire de recherche de facture

@Composable
private fun BillForm(
    billSearchData: BillSearchData,
    onReferenceChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSearchBill: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val service = billSearchData.service ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding((16 * scale).dp),
        verticalArrangement = Arrangement.spacedBy((20 * scale).dp)
    ) {
        // Logo et nom du service
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = (1 * scale).dp,
                color = Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(service.logo),
                    contentDescription = service.name,
                    modifier = Modifier.size((40 * scale).dp)
                )

                Spacer(Modifier.width((12 * scale).dp))

                Column {
                    Text(
                        service.name,
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(service.color))
                    )
                    Text(
                        service.description,
                        fontSize = (12 * scale).sp,
                        color = Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Formulaire selon le type
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding((16 * scale).dp),
                verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
            ) {
                when (service.type) {
                    BillServiceType.CIE_PREPAID -> {
                        Text(
                            "💡 Recharge compteur CIE",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = (8 * scale).dp)
                        )

                        // Référence compteur
                        OutlinedTextField(
                            value = billSearchData.reference,
                            onValueChange = onReferenceChange,
                            label = { Text("Numéro compteur", fontSize = (12 * scale).sp) },
                            placeholder = { Text("Ex: 123456789", fontSize = (12 * scale).sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((12 * scale).dp)
                        )

                        // Montant
                        OutlinedTextField(
                            value = if (billSearchData.amount > 0) billSearchData.amount.toString() else "",
                            onValueChange = onAmountChange,
                            label = { Text("Montant", fontSize = (12 * scale).sp) },
                            placeholder = { Text("Ex: 5000", fontSize = (12 * scale).sp) },
                            suffix = { Text("FCFA", fontSize = (12 * scale).sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((12 * scale).dp)
                        )
                    }

                    BillServiceType.CIE_POSTPAID -> {
                        Text(
                            "🔍 Rechercher facture CIE",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = (8 * scale).dp)
                        )

                        OutlinedTextField(
                            value = billSearchData.reference,
                            onValueChange = onReferenceChange,
                            label = { Text("Référence contrat ou compteur", fontSize = (12 * scale).sp) },
                            placeholder = { Text("Ex: 123456789", fontSize = (12 * scale).sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((12 * scale).dp)
                        )
                    }

                    BillServiceType.SODECI -> {
                        Text(
                            "🔍 Rechercher facture SODECI",
                            fontSize = (16 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = (8 * scale).dp)
                        )

                        OutlinedTextField(
                            value = billSearchData.reference,
                            onValueChange = onReferenceChange,
                            label = { Text("Référence contrat ou compteur", fontSize = (12 * scale).sp) },
                            placeholder = { Text("Ex: 123456789", fontSize = (12 * scale).sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape((12 * scale).dp)
                        )
                    }
                }

                // Message d'erreur
                billSearchData.searchError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        border = androidx.compose.foundation.BorderStroke(
                            width = (1 * scale).dp,
                            color = Color(0xFFE74C3C)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding((12 * scale).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("❌", fontSize = (16 * scale).sp)
                            Spacer(Modifier.width((8 * scale).dp))
                            Text(
                                error,
                                fontSize = (13 * scale).sp,
                                color = Color(0xFFE74C3C)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height((8 * scale).dp))

        // Bouton de recherche ou continuer
        val buttonText = when (service.type) {
            BillServiceType.CIE_PREPAID -> "Continuer"
            else -> "Rechercher la facture"
        }

        val isEnabled = when (service.type) {
            BillServiceType.CIE_PREPAID -> billSearchData.reference.isNotBlank() && billSearchData.amount > 0
            else -> billSearchData.reference.length >= 6
        }

        Button(
            onClick = onSearchBill,
            enabled = isEnabled && !billSearchData.isSearching,
            modifier = Modifier
                .fillMaxWidth()
                .height((50 * scale).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF673AB7)
            ),
            shape = RoundedCornerShape((12 * scale).dp)
        ) {
            if (billSearchData.isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size((20 * scale).dp),
                    color = Color.White,
                    strokeWidth = (2 * scale).dp
                )
                Spacer(Modifier.width((8 * scale).dp))
                Text("Recherche en cours...")
            } else {
                if (service.type != BillServiceType.CIE_PREPAID) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size((20 * scale).dp)
                    )
                    Spacer(Modifier.width((8 * scale).dp))
                }
                Text(
                    buttonText,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Détails de la facture trouvée

@Composable
private fun BillDetailsView(
    billSearchData: BillSearchData,
    onContinue: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val billDetails = billSearchData.billDetails ?: return
    val service = billSearchData.service ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding((16 * scale).dp),
        verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
    ) {
        // Informations client
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
                    "👤 Informations du client",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (12 * scale).dp)
                )

                BillDetailRow("Nom complet", billDetails.customerName, scale)
                BillDetailRow("Téléphone", billDetails.customerPhone, scale)
                BillDetailRow("N° Compteur", billDetails.meterNumber, scale)
                if (billDetails.contractRef.isNotBlank()) {
                    BillDetailRow("Référence contrat", billDetails.contractRef, scale)
                }
            }
        }

        // Détails de la facture
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
                    "🧾 Détails de la facture",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (12 * scale).dp)
                )

                BillDetailRow("Montant facture", formatAmount(billDetails.billAmount), scale)
                BillDetailRow("Échéance", billDetails.dueDate, scale)

                if (billDetails.isOverdue && billDetails.lateFees > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = (8 * scale).dp),
                        color = Color(0xFFE74C3C).copy(alpha = 0.3f)
                    )

                    BillDetailRow(
                        "Frais de retard",
                        formatAmount(billDetails.lateFees),
                        scale,
                        valueColor = Color(0xFFE74C3C)
                    )

                    Text(
                        "⚠️ Facture en retard",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFFE74C3C),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = (4 * scale).dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                BillDetailRow(
                    "Total à payer",
                    formatAmount(billDetails.totalAmount),
                    scale,
                    isBold = true,
                    valueColor = Color(android.graphics.Color.parseColor(service.color))
                )
            }
        }

        Spacer(Modifier.height((8 * scale).dp))

        // Bouton Payer
        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height((50 * scale).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF673AB7)
            ),
            shape = RoundedCornerShape((12 * scale).dp)
        ) {
            Text(
                "Payer cette facture",
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// Ligne de détail

@Composable
private fun BillDetailRow(
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
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Récapitulatif du paiement

@Composable
private fun BillSummary(
    billSearchData: BillSearchData,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val billDetails = billSearchData.billDetails
    val service = billSearchData.service ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding((16 * scale).dp),
        verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
    ) {
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
                    "Confirmation du paiement",
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (16 * scale).dp)
                )

                BillDetailRow("Service", service.name, scale)

                if (service.type == BillServiceType.CIE_PREPAID) {
                    BillDetailRow("Numéro compteur", billSearchData.reference, scale)
                    BillDetailRow("Montant recharge", formatAmount(billSearchData.amount), scale)
                } else {
                    billDetails?.let {
                        BillDetailRow("Client", it.customerName, scale)
                        BillDetailRow("Compteur", it.meterNumber, scale)
                        BillDetailRow("Montant facture", formatAmount(it.billAmount), scale)
                        if (it.lateFees > 0) {
                            BillDetailRow("Frais retard", formatAmount(it.lateFees), scale, valueColor = Color(0xFFE74C3C))
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                val totalAmount = when (service.type) {
                    BillServiceType.CIE_PREPAID -> billSearchData.amount
                    else -> billDetails?.totalAmount ?: 0
                }

                BillDetailRow(
                    "Total à payer",
                    formatAmount(totalAmount),
                    scale,
                    isBold = true,
                    valueColor = Color(android.graphics.Color.parseColor(service.color))
                )

                Text(
                    "✅ Sans frais de transaction",
                    fontSize = (12 * scale).sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = (8 * scale).dp)
                )
            }
        }

        Spacer(Modifier.height((8 * scale).dp))

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
                    containerColor = Color(0xFF673AB7)
                ),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Text(
                    "Confirmer le paiement",
                    fontSize = (14 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Écran de succès

@Composable
private fun BillSuccess(
    billSearchData: BillSearchData,
    onFinish: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val service = billSearchData.service ?: return

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
                    Color(android.graphics.Color.parseColor(service.color)).copy(alpha = 0.2f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Succès",
                tint = Color(android.graphics.Color.parseColor(service.color)),
                modifier = Modifier.size((50 * scale).dp)
            )
        }

        Spacer(Modifier.height((24 * scale).dp))

        Text(
            "Paiement réussi !",
            fontSize = (24 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height((12 * scale).dp))

        val successMessage = when (service.type) {
            BillServiceType.CIE_PREPAID -> {
                "Votre recharge de ${formatAmount(billSearchData.amount)} a été effectuée.\n\nVotre compteur sera crédité dans quelques minutes."
            }
            else -> {
                val totalAmount = billSearchData.billDetails?.totalAmount ?: 0
                "Votre facture ${service.name} de ${formatAmount(totalAmount)} a été payée avec succès.\n\nUn reçu a été envoyé par SMS."
            }
        }

        Text(
            successMessage,
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
                containerColor = Color(0xFF673AB7)
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

// -------------------- Écran principal de paiement de factures --------------------

// Écran principal de paiement de factures

@Composable
fun BillsScreen(
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive measurements
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var currentStep by remember { mutableStateOf(BillStep.SERVICE_LIST) }
    var billSearchData by remember { mutableStateOf(BillSearchData()) }

    val services = getBillServices()

    // Handlers
    val handleServiceSelected: (BillService) -> Unit = { service ->
        billSearchData = billSearchData.copy(service = service)
        currentStep = BillStep.BILL_FORM
    }

    val handleReferenceChange: (String) -> Unit = { reference ->
        billSearchData = billSearchData.copy(
            reference = reference,
            searchError = null
        )
    }

    val handleAmountChange: (String) -> Unit = { amount ->
        val numericAmount = amount.filter { it.isDigit() }.take(8)
        billSearchData = billSearchData.copy(
            amount = numericAmount.toIntOrNull() ?: 0
        )
    }

    val handleSearchBill: () -> Unit = Unit@{
        val service = billSearchData.service ?: return@Unit

        if (service.type == BillServiceType.CIE_PREPAID) {
            // Pour le prépayé, pas besoin de recherche, on passe directement au récapitulatif
            currentStep = BillStep.SUMMARY
        } else {
            // Pour les factures, on simule la recherche
            billSearchData = billSearchData.copy(isSearching = true, searchError = null)

            // Simulation d'une recherche asynchrone
            kotlinx.coroutines.MainScope().launch {
                kotlinx.coroutines.delay(2000) // Simulation de 2 secondes

                val foundBill = simulateBillLookup(service, billSearchData.reference)

                if (foundBill != null) {
                    billSearchData = billSearchData.copy(
                        billDetails = foundBill,
                        isSearching = false,
                        searchError = null
                    )
                    currentStep = BillStep.BILL_DETAILS
                } else {
                    billSearchData = billSearchData.copy(
                        isSearching = false,
                        searchError = "Aucune facture trouvée pour cette référence. Vérifiez le numéro et réessayez."
                    )
                }
            }
        }
    }

    val handleContinue = { currentStep = BillStep.SUMMARY }
    val handleBack = {
        when (currentStep) {
            BillStep.BILL_FORM -> currentStep = BillStep.SERVICE_LIST
            BillStep.BILL_DETAILS -> currentStep = BillStep.BILL_FORM
            BillStep.SUMMARY -> {
                if (billSearchData.service?.type == BillServiceType.CIE_PREPAID) {
                    currentStep = BillStep.BILL_FORM
                } else {
                    currentStep = BillStep.BILL_DETAILS
                }
            }
            else -> onBackPressed()
        }
    }
    val handleConfirm = { currentStep = BillStep.SUCCESS }
    val handleFinish = { onBackPressed() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        BillsHeader(
            title = when (currentStep) {
                BillStep.SERVICE_LIST -> "Paiement de factures"
                BillStep.BILL_FORM -> "Recherche de facture"
                BillStep.BILL_DETAILS -> "Détails de la facture"
                BillStep.SUMMARY -> "Confirmer le paiement"
                BillStep.SUCCESS -> "Paiement effectué"
            },
            onBackClick = {
                when (currentStep) {
                    BillStep.SERVICE_LIST -> onBackPressed()
                    BillStep.BILL_FORM -> handleBack()
                    BillStep.BILL_DETAILS -> handleBack()
                    BillStep.SUMMARY -> handleBack()
                    BillStep.SUCCESS -> handleFinish()
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
            label = "Bill step transition"
        ) { step ->
            when (step) {
                BillStep.SERVICE_LIST -> BillServiceList(
                    services = services,
                    onServiceSelected = handleServiceSelected,
                    scale = scale
                )

                BillStep.BILL_FORM -> BillForm(
                    billSearchData = billSearchData,
                    onReferenceChange = handleReferenceChange,
                    onAmountChange = handleAmountChange,
                    onSearchBill = handleSearchBill,
                    scale = scale
                )

                BillStep.BILL_DETAILS -> BillDetailsView(
                    billSearchData = billSearchData,
                    onContinue = handleContinue,
                    scale = scale
                )

                BillStep.SUMMARY -> BillSummary(
                    billSearchData = billSearchData,
                    onConfirm = handleConfirm,
                    onBack = handleBack,
                    scale = scale
                )

                BillStep.SUCCESS -> BillSuccess(
                    billSearchData = billSearchData,
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
fun BillsScreenPreview() {
    HifadihTheme {
        BillsScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Liste des services")
@Composable
fun BillServiceListPreview() {
    HifadihTheme {
        BillsScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Formulaire CIE")
@Composable
fun BillFormPreview() {
    HifadihTheme {
        BillsScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun BillsScreenSmallPreview() {
    HifadihTheme {
        BillsScreen()
    }
}