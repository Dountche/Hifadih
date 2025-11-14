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
import androidx.compose.ui.window.Dialog
import com.example.hifadih.ui.theme.HifadihTheme
import java.text.NumberFormat
import java.util.*

// -------------------- Types de données pour les abonnements --------------------

// Type de service d'abonnement

enum class SubscriptionServiceType {
    CANAL_PLUS, NETFLIX, SOTRA
}

// Étapes du processus d'abonnement

enum class SubscriptionStep {
    SERVICE_LIST, SUBSCRIPTION_FORM, SUMMARY, SUCCESS
}


// Plan Canal+

data class CanalPlan(
    val name: String,
    val price: Int, // en FCFA
    val description: String
)


// Plan Netflix

data class NetflixPlan(
    val name: String,
    val price: Int, // en FCFA
    val description: String
)


// Durée d'abonnement

data class SubscriptionDuration(
    val name: String,
    val months: Int,
    val multiplier: Int
)


// Service d'abonnement

data class SubscriptionService(
    val type: SubscriptionServiceType,
    val name: String,
    val logo: Int, // Resource drawable
    val color: String,
    val description: String
)


// Données d'abonnement Canal+

data class CanalSubscriptionData(
    val subscriptionNumber: String = "",
    val selectedPlan: CanalPlan? = null,
    val selectedDuration: SubscriptionDuration? = null
) {
    val totalPrice: Int
        get() = (selectedPlan?.price ?: 0) * (selectedDuration?.multiplier ?: 1)
}

// Données d'abonnement Netflix

data class NetflixSubscriptionData(
    val selectedPlan: NetflixPlan? = null,
    val selectedDuration: SubscriptionDuration? = null
) {
    val totalPrice: Int
        get() = (selectedPlan?.price ?: 0) * (selectedDuration?.multiplier ?: 1)
}


// * Données d'abonnement Sotra

data class SotraSubscriptionData(
    val cardNumber: String = "",
    val amount: Int = 0
) {
    val isValidAmount: Boolean
        get() = amount > 0 && amount % 3000 == 0
}


// Données globales d'abonnement

data class SubscriptionData(
    val service: SubscriptionService? = null,
    val canalData: CanalSubscriptionData = CanalSubscriptionData(),
    val netflixData: NetflixSubscriptionData = NetflixSubscriptionData(),
    val sotraData: SotraSubscriptionData = SotraSubscriptionData()
) {
    val totalPrice: Int
        get() = when (service?.type) {
            SubscriptionServiceType.CANAL_PLUS -> canalData.totalPrice
            SubscriptionServiceType.NETFLIX -> netflixData.totalPrice
            SubscriptionServiceType.SOTRA -> sotraData.amount
            else -> 0
        }
}

// -------------------- Données statiques --------------------

@Composable
private fun getSubscriptionServices(): List<SubscriptionService> {
    return remember {
        listOf(
            SubscriptionService(
                SubscriptionServiceType.CANAL_PLUS,
                "Canal+",
                R.drawable.canal,
                "#FFD700",
                "Télévision premium"
            ),
            SubscriptionService(
                SubscriptionServiceType.NETFLIX,
                "Netflix",
                R.drawable.netflix,
                "#E50914",
                "Streaming vidéo"
            ),
            SubscriptionService(
                SubscriptionServiceType.SOTRA,
                "SOTRA",
                R.drawable.sotra,
                "#2196F3",
                "Transport urbain"
            )
        )
    }
}

@Composable
private fun getCanalPlans(): List<CanalPlan> {
    return remember {
        listOf(
            CanalPlan("Access", 5000, "Chaînes essentielles"),
            CanalPlan("Evasion", 10000, "Sports et divertissement"),
            CanalPlan("Access+", 15000, "Chaînes premium"),
            CanalPlan("Tout Canal+", 25000, "Toutes les chaînes")
        )
    }
}

@Composable
private fun getNetflixPlans(): List<NetflixPlan> {
    return remember {
        listOf(
            NetflixPlan("Standard avec pub", 3500, "HD, publicités"),
            NetflixPlan("Standard sans pub", 5500, "HD, sans publicité"),
            NetflixPlan("Premium", 8500, "4K, 4 écrans simultanés")
        )
    }
}

@Composable
private fun getSubscriptionDurations(): List<SubscriptionDuration> {
    return remember {
        listOf(
            SubscriptionDuration("1 mois", 1, 1),
            SubscriptionDuration("3 mois", 3, 3)
        )
    }
}

// -------------------- Composants utilitaires --------------------


// Header de l'écran d'abonnements

@Composable
private fun SubscriptionHeader(
    title: String,
    onBackClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF9C27B0)),
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


// Liste des services d'abonnement
@Composable
private fun SubscriptionServiceList(
    services: List<SubscriptionService>,
    onServiceSelected: (SubscriptionService) -> Unit,
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
                "Choisissez votre abonnement",
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = (8 * scale).dp)
            )
        }

        items(services) { service ->
            SubscriptionServiceCard(
                service = service,
                onSelect = { onServiceSelected(service) },
                scale = scale
            )
        }
    }
}


// Carte d'un service d'abonnement

@Composable
private fun SubscriptionServiceCard(
    service: SubscriptionService,
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


// Sélecteur avec modal pour Canal+ et Netflix

@Composable
private fun <T> ModalSelector(
    label: String,
    selectedItem: T?,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    itemDisplayName: (T) -> String,
    itemPrice: ((T) -> Int)? = null,
    scale: Float,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedItem?.let { item ->
            val name = itemDisplayName(item)
            val price = itemPrice?.let { "${it(item)} FCFA" } ?: ""
            if (price.isNotEmpty()) "$name - $price" else name
        } ?: "",
        onValueChange = { },
        label = { Text(label, fontSize = (12 * scale).sp) },
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Ouvrir sélection",
                modifier = Modifier.clickable { showDialog = true }
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape((12 * scale).dp)
    )

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape((16 * scale).dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((16 * scale).dp)
                ) {
                    Text(
                        "Sélectionner $label",
                        fontSize = (16 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = (12 * scale).dp)
                    )

                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemSelected(item)
                                    showDialog = false
                                }
                                .padding(vertical = (8 * scale).dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                itemDisplayName(item),
                                fontSize = (14 * scale).sp,
                                modifier = Modifier.weight(1f)
                            )
                            itemPrice?.let { priceFunc ->
                                Text(
                                    "${priceFunc(item)} FCFA",
                                    fontSize = (13 * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (item != items.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = (4 * scale).dp))
                        }
                    }
                }
            }
        }
    }
}

// Formulaire Canal+

@Composable
private fun CanalPlusForm(
    data: CanalSubscriptionData,
    plans: List<CanalPlan>,
    durations: List<SubscriptionDuration>,
    onSubscriptionNumberChange: (String) -> Unit,
    onPlanSelected: (CanalPlan) -> Unit,
    onDurationSelected: (SubscriptionDuration) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
    ) {
        // Numéro d'abonnement
        OutlinedTextField(
            value = data.subscriptionNumber,
            onValueChange = { value ->
                val filtered = value.filter { it.isDigit() }.take(14)
                onSubscriptionNumberChange(filtered)
            },
            label = { Text("Numéro d'abonnement", fontSize = (12 * scale).sp) },
            placeholder = { Text("14 chiffres", fontSize = (12 * scale).sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape((12 * scale).dp)
        )

        // Sélection du plan
        ModalSelector(
            label = "Plan Canal+",
            selectedItem = data.selectedPlan,
            items = plans,
            onItemSelected = onPlanSelected,
            itemDisplayName = { it.name },
            itemPrice = { it.price },
            scale = scale
        )

        // Sélection de la durée
        ModalSelector(
            label = "Durée",
            selectedItem = data.selectedDuration,
            items = durations,
            onItemSelected = onDurationSelected,
            itemDisplayName = { it.name },
            scale = scale
        )

        // Affichage du total
        if (data.selectedPlan != null && data.selectedDuration != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                border = androidx.compose.foundation.BorderStroke(
                    width = (1 * scale).dp,
                    color = Color(0xFFFFD700).copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((12 * scale).dp)
                ) {
                    Text(
                        "Récapitulatif:",
                        fontSize = (13 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        "${data.selectedPlan.name} × ${data.selectedDuration.name}",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFFEF6C00)
                    )
                    Text(
                        "Total: ${formatAmount(data.totalPrice)}",
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}


// Formulaire Netflix

@Composable
private fun NetflixForm(
    data: NetflixSubscriptionData,
    plans: List<NetflixPlan>,
    durations: List<SubscriptionDuration>,
    onPlanSelected: (NetflixPlan) -> Unit,
    onDurationSelected: (SubscriptionDuration) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
    ) {
        // Sélection du plan Netflix
        ModalSelector(
            label = "Plan Netflix",
            selectedItem = data.selectedPlan,
            items = plans,
            onItemSelected = onPlanSelected,
            itemDisplayName = { it.name },
            itemPrice = { it.price },
            scale = scale
        )

        // Description du plan sélectionné
        data.selectedPlan?.let { plan ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(
                    width = (1 * scale).dp,
                    color = Color(0xFFE50914).copy(alpha = 0.3f)
                )
            ) {
                Text(
                    plan.description,
                    fontSize = (12 * scale).sp,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.padding((12 * scale).dp)
                )
            }
        }

        // Sélection de la durée
        ModalSelector(
            label = "Durée",
            selectedItem = data.selectedDuration,
            items = durations,
            onItemSelected = onDurationSelected,
            itemDisplayName = { it.name },
            scale = scale
        )

        // Affichage du total
        if (data.selectedPlan != null && data.selectedDuration != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(
                    width = (1 * scale).dp,
                    color = Color(0xFFE50914).copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding((12 * scale).dp)
                ) {
                    Text(
                        "Récapitulatif:",
                        fontSize = (13 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        "${data.selectedPlan.name} × ${data.selectedDuration.name}",
                        fontSize = (12 * scale).sp,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        "Total: ${formatAmount(data.totalPrice)}",
                        fontSize = (14 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }
            }
        }
    }
}

// Formulaire SOTRA

@Composable
private fun SotraForm(
    data: SotraSubscriptionData,
    onCardNumberChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy((16 * scale).dp)
    ) {
        // Numéro de carte
        OutlinedTextField(
            value = data.cardNumber,
            onValueChange = { value ->
                val filtered = value.filter { it.isLetterOrDigit() }.take(14)
                onCardNumberChange(filtered)
            },
            label = { Text("Numéro de carte SOTRA", fontSize = (12 * scale).sp) },
            placeholder = { Text("14 caractères", fontSize = (12 * scale).sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape((12 * scale).dp)
        )

        // Montant
        OutlinedTextField(
            value = if (data.amount > 0) data.amount.toString() else "",
            onValueChange = onAmountChange,
            label = { Text("Montant", fontSize = (12 * scale).sp) },
            placeholder = { Text("Multiple de 3000", fontSize = (12 * scale).sp) },
            suffix = { Text("FCFA", fontSize = (12 * scale).sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape((12 * scale).dp),
            isError = data.amount > 0 && !data.isValidAmount
        )

        // Message d'aide pour le montant
        if (data.amount > 0 && !data.isValidAmount) {
            Text(
                "Le montant doit être un multiple de 3 000 FCFA",
                color = MaterialTheme.colorScheme.error,
                fontSize = (11 * scale).sp
            )
        }

        // Suggestions de montant
        Text(
            "Montants suggérés:",
            fontSize = (12 * scale).sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)
        ) {
            listOf(3000, 6000, 9000, 15000).forEach { amount ->
                FilterChip(
                    selected = data.amount == amount,
                    onClick = { onAmountChange(amount.toString()) },
                    label = {
                        Text(
                            "${amount / 1000}K",
                            fontSize = (11 * scale).sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2196F3),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

// Formulaire d'abonnement selon le service

@Composable
private fun SubscriptionForm(
    subscriptionData: SubscriptionData,
    plans: List<CanalPlan>,
    netflixPlans: List<NetflixPlan>,
    durations: List<SubscriptionDuration>,
    userBalance: Int,
    onCanalSubscriptionNumberChange: (String) -> Unit,
    onCanalPlanSelected: (CanalPlan) -> Unit,
    onCanalDurationSelected: (SubscriptionDuration) -> Unit,
    onNetflixPlanSelected: (NetflixPlan) -> Unit,
    onNetflixDurationSelected: (SubscriptionDuration) -> Unit,
    onSotraCardNumberChange: (String) -> Unit,
    onSotraAmountChange: (String) -> Unit,
    onContinue: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val service = subscriptionData.service ?: return

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
                        "Abonnement ${service.name}",
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

        // Formulaire selon le type de service
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
                when (service.type) {
                    SubscriptionServiceType.CANAL_PLUS -> CanalPlusForm(
                        data = subscriptionData.canalData,
                        plans = plans,
                        durations = durations,
                        onSubscriptionNumberChange = onCanalSubscriptionNumberChange,
                        onPlanSelected = onCanalPlanSelected,
                        onDurationSelected = onCanalDurationSelected,
                        scale = scale
                    )

                    SubscriptionServiceType.NETFLIX -> NetflixForm(
                        data = subscriptionData.netflixData,
                        plans = netflixPlans,
                        durations = durations,
                        onPlanSelected = onNetflixPlanSelected,
                        onDurationSelected = onNetflixDurationSelected,
                        scale = scale
                    )

                    SubscriptionServiceType.SOTRA -> SotraForm(
                        data = subscriptionData.sotraData,
                        onCardNumberChange = onSotraCardNumberChange,
                        onAmountChange = onSotraAmountChange,
                        scale = scale
                    )
                }
            }
        }

        // Vérification du solde
        if (subscriptionData.totalPrice > 0) {
            if (subscriptionData.totalPrice > userBalance) {
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
                        Column {
                            Text(
                                "Solde insuffisant",
                                fontSize = (14 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE74C3C)
                            )
                            Text(
                                "Solde: ${formatAmount(userBalance)} | Requis: ${formatAmount(subscriptionData.totalPrice)}",
                                fontSize = (12 * scale).sp,
                                color = Color(0xFFE74C3C)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height((8 * scale).dp))

        // Bouton Continuer
        val isFormValid = when (service.type) {
            SubscriptionServiceType.CANAL_PLUS -> {
                subscriptionData.canalData.subscriptionNumber.length == 14 &&
                        subscriptionData.canalData.selectedPlan != null &&
                        subscriptionData.canalData.selectedDuration != null
            }
            SubscriptionServiceType.NETFLIX -> {
                subscriptionData.netflixData.selectedPlan != null &&
                        subscriptionData.netflixData.selectedDuration != null
            }
            SubscriptionServiceType.SOTRA -> {
                subscriptionData.sotraData.cardNumber.length == 14 &&
                        subscriptionData.sotraData.isValidAmount
            }
        }

        Button(
            onClick = onContinue,
            enabled = isFormValid && subscriptionData.totalPrice <= userBalance,
            modifier = Modifier
                .fillMaxWidth()
                .height((50 * scale).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9C27B0)
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

// Récapitulatif de l'abonnement

@Composable
private fun SubscriptionSummary(
    subscriptionData: SubscriptionData,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val service = subscriptionData.service ?: return

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
                    "Récapitulatif de l'abonnement",
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = (16 * scale).dp)
                )

                // Service
                SubscriptionDetailRow("Service", service.name, scale)

                // Détails selon le type
                when (service.type) {
                    SubscriptionServiceType.CANAL_PLUS -> {
                        SubscriptionDetailRow("Numéro d'abonnement", subscriptionData.canalData.subscriptionNumber, scale)
                        SubscriptionDetailRow("Plan", subscriptionData.canalData.selectedPlan?.name ?: "", scale)
                        SubscriptionDetailRow("Durée", subscriptionData.canalData.selectedDuration?.name ?: "", scale)
                    }
                    SubscriptionServiceType.NETFLIX -> {
                        SubscriptionDetailRow("Plan", subscriptionData.netflixData.selectedPlan?.name ?: "", scale)
                        SubscriptionDetailRow("Durée", subscriptionData.netflixData.selectedDuration?.name ?: "", scale)
                    }
                    SubscriptionServiceType.SOTRA -> {
                        SubscriptionDetailRow("Numéro de carte", subscriptionData.sotraData.cardNumber, scale)
                        SubscriptionDetailRow("Montant de recharge", formatAmount(subscriptionData.sotraData.amount), scale)
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = (12 * scale).dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                // Total
                SubscriptionDetailRow(
                    "Total à payer",
                    formatAmount(subscriptionData.totalPrice),
                    scale,
                    isBold = true,
                    valueColor = Color(android.graphics.Color.parseColor(service.color))
                )

                // Sans frais
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
                    containerColor = Color(0xFF9C27B0)
                ),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Text(
                    "Payer l'abonnement",
                    fontSize = (14 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Ligne de détail dans le récapitulatif

@Composable
private fun SubscriptionDetailRow(
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

// Écran de succès

@Composable
private fun SubscriptionSuccess(
    subscriptionData: SubscriptionData,
    onFinish: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val service = subscriptionData.service ?: return

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
            "Abonnement payé !",
            fontSize = (24 * scale).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height((12 * scale).dp))

        Text(
            "Votre abonnement ${service.name} de ${formatAmount(subscriptionData.totalPrice)} a été payé avec succès.\n\nL'activation sera effective dans quelques minutes.",
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
                containerColor = Color(0xFF9C27B0)
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

// -------------------- Écran principal d'abonnements --------------------

// * Écran principal de paiement d'abonnements

@Composable
fun SubscribeScreen(
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive measurements
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États
    var currentStep by remember { mutableStateOf(SubscriptionStep.SERVICE_LIST) }
    var subscriptionData by remember { mutableStateOf(SubscriptionData()) }

    // Solde utilisateur simulé
    val userBalance = 125750

    // Services et données
    val services = getSubscriptionServices()
    val canalPlans = getCanalPlans()
    val netflixPlans = getNetflixPlans()
    val durations = getSubscriptionDurations()

    // Handlers
    val handleServiceSelected: (SubscriptionService) -> Unit = { service ->
        subscriptionData = subscriptionData.copy(service = service)
        currentStep = SubscriptionStep.SUBSCRIPTION_FORM
    }

    val handleCanalSubscriptionNumberChange: (String) -> Unit = { number ->
        subscriptionData = subscriptionData.copy(
            canalData = subscriptionData.canalData.copy(subscriptionNumber = number)
        )
    }

    val handleCanalPlanSelected: (CanalPlan) -> Unit = { plan ->
        subscriptionData = subscriptionData.copy(
            canalData = subscriptionData.canalData.copy(selectedPlan = plan)
        )
    }

    val handleCanalDurationSelected: (SubscriptionDuration) -> Unit = { duration ->
        subscriptionData = subscriptionData.copy(
            canalData = subscriptionData.canalData.copy(selectedDuration = duration)
        )
    }

    val handleNetflixPlanSelected: (NetflixPlan) -> Unit = { plan ->
        subscriptionData = subscriptionData.copy(
            netflixData = subscriptionData.netflixData.copy(selectedPlan = plan)
        )
    }

    val handleNetflixDurationSelected: (SubscriptionDuration) -> Unit = { duration ->
        subscriptionData = subscriptionData.copy(
            netflixData = subscriptionData.netflixData.copy(selectedDuration = duration)
        )
    }

    val handleSotraCardNumberChange: (String) -> Unit = { cardNumber ->
        subscriptionData = subscriptionData.copy(
            sotraData = subscriptionData.sotraData.copy(cardNumber = cardNumber)
        )
    }

    val handleSotraAmountChange: (String) -> Unit = { amount ->
        val numericAmount = amount.filter { it.isDigit() }.take(8)
        subscriptionData = subscriptionData.copy(
            sotraData = subscriptionData.sotraData.copy(
                amount = numericAmount.toIntOrNull() ?: 0
            )
        )
    }

    val handleContinue = { currentStep = SubscriptionStep.SUMMARY }
    val handleBack = {
        when (currentStep) {
            SubscriptionStep.SUBSCRIPTION_FORM -> currentStep = SubscriptionStep.SERVICE_LIST
            SubscriptionStep.SUMMARY -> currentStep = SubscriptionStep.SUBSCRIPTION_FORM
            else -> onBackPressed()
        }
    }
    val handleConfirm = { currentStep = SubscriptionStep.SUCCESS }
    val handleFinish = { onBackPressed() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header
        SubscriptionHeader(
            title = when (currentStep) {
                SubscriptionStep.SERVICE_LIST -> "Abonnements"
                SubscriptionStep.SUBSCRIPTION_FORM -> "Nouvel abonnement"
                SubscriptionStep.SUMMARY -> "Confirmer le paiement"
                SubscriptionStep.SUCCESS -> "Paiement effectué"
            },
            onBackClick = {
                when (currentStep) {
                    SubscriptionStep.SERVICE_LIST -> onBackPressed()
                    SubscriptionStep.SUBSCRIPTION_FORM -> handleBack()
                    SubscriptionStep.SUMMARY -> handleBack()
                    SubscriptionStep.SUCCESS -> handleFinish()
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
            label = "Subscription step transition"
        ) { step ->
            when (step) {
                SubscriptionStep.SERVICE_LIST -> SubscriptionServiceList(
                    services = services,
                    onServiceSelected = handleServiceSelected,
                    scale = scale
                )

                SubscriptionStep.SUBSCRIPTION_FORM -> SubscriptionForm(
                    subscriptionData = subscriptionData,
                    plans = canalPlans,
                    netflixPlans = netflixPlans,
                    durations = durations,
                    userBalance = userBalance,
                    onCanalSubscriptionNumberChange = handleCanalSubscriptionNumberChange,
                    onCanalPlanSelected = handleCanalPlanSelected,
                    onCanalDurationSelected = handleCanalDurationSelected,
                    onNetflixPlanSelected = handleNetflixPlanSelected,
                    onNetflixDurationSelected = handleNetflixDurationSelected,
                    onSotraCardNumberChange = handleSotraCardNumberChange,
                    onSotraAmountChange = handleSotraAmountChange,
                    onContinue = handleContinue,
                    scale = scale
                )

                SubscriptionStep.SUMMARY -> SubscriptionSummary(
                    subscriptionData = subscriptionData,
                    onConfirm = handleConfirm,
                    onBack = handleBack,
                    scale = scale
                )

                SubscriptionStep.SUCCESS -> SubscriptionSuccess(
                    subscriptionData = subscriptionData,
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
fun SubscribeScreenPreview() {
    HifadihTheme {
        SubscribeScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Liste des services")
@Composable
fun SubscriptionServiceListPreview() {
    HifadihTheme {
        SubscribeScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun SubscribeScreenSmallPreview() {
    HifadihTheme {
        SubscribeScreen()
    }
}