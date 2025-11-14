package com.example.hifadih.profile

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme

data class Category(
    val id: String,
    val name: String
)

data class FAQ(
    val id: String,
    val question: String,
    val reponse: String,
    val categorie: String,
    var isExpanded: Boolean = false
)

data class ContactOption(
    val id: String,
    val titre: String,
    val description: String,
    val icone: String,
    val couleur: Color,
    val disponible: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageAide(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }

    val categories = remember {
        listOf(
            Category("all", "Tout"),
            Category("transfert", "Transferts"),
            Category("frais", "Frais"),
            Category("securite", "Sécurité"),
            Category("support", "Support")
        )
    }

    var faqList by remember {
        mutableStateOf(
            listOf(
                FAQ(
                    "1",
                    "Comment effectuer un transfert d'argent ?",
                    "Pour effectuer un transfert, allez dans l'onglet \"Envoyer\", sélectionnez le destinataire, saisissez le montant et confirmez avec votre code PIN.",
                    "transfert"
                ),
                FAQ(
                    "2",
                    "Quels sont les frais de transaction ?",
                    "Les frais varient selon le montant : 0-5000 FCFA = 50 FCFA, 5001-25000 FCFA = 100 FCFA, 25001-100000 FCFA = 200 FCFA.",
                    "frais"
                ),
                FAQ(
                    "3",
                    "Comment récupérer mon code PIN oublié ?",
                    "Allez dans \"Profil\" > \"Sécurité\" > \"Code PIN\" et suivez les instructions pour réinitialiser votre code PIN.",
                    "securite"
                ),
                FAQ(
                    "4",
                    "Mes transactions sont-elles sécurisées ?",
                    "Oui, toutes les transactions sont cryptées et sécurisées. Nous utilisons les dernières technologies de sécurité bancaire.",
                    "securite"
                ),
                FAQ(
                    "5",
                    "Comment contacter le service client ?",
                    "Vous pouvez nous contacter via le chat en direct, par téléphone au +225 27 XX XX XX XX ou par email à support@djamo.com",
                    "support"
                )
            )
        )
    }

    val contactOptions = remember {
        listOf(
            ContactOption(
                "chat",
                "Chat en direct",
                "Réponse immédiate",
                "💬",
                Color(0xFF00B894),
                true
            ),
            ContactOption(
                "phone",
                "Téléphone",
                "+225 27 XX XX XX XX",
                "📞",
                Color(0xFF6C5CE7),
                true
            ),
            ContactOption(
                "email",
                "Email",
                "support@djamo.com",
                "📧",
                Color(0xFFE17055),
                true
            ),
            ContactOption(
                "whatsapp",
                "WhatsApp",
                "Support via WhatsApp",
                "📱",
                Color(0xFF25D366),
                false
            )
        )
    }

    val filteredFAQs = remember(selectedCategory, searchText, faqList) {
        faqList.filter { faq ->
            val categoryMatch = selectedCategory == "all" || faq.categorie == selectedCategory
            val searchMatch = searchText.isEmpty() ||
                    faq.question.contains(searchText, ignoreCase = true) ||
                    faq.reponse.contains(searchText, ignoreCase = true)
            categoryMatch && searchMatch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header avec recherche
        Column(
            modifier = Modifier.background(Color(0xFF74B9FF))
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Centre d'aide",
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
                    containerColor = Color.Transparent
                )
            )

            // Barre de recherche
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = (16 * scale).dp)
                    .padding(bottom = (16 * scale).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            "Rechercher une question...",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape((25 * scale).dp),
                    singleLine = true
                )

                IconButton(
                    onClick = { /* Action recherche */ },
                    modifier = Modifier
                        .padding(start = (12 * scale).dp)
                        .size((50 * scale).dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape((25 * scale).dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Rechercher",
                        tint = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            // Onglets catégories
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy((8 * scale).dp),
                modifier = Modifier.padding(bottom = (16 * scale).dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        onClick = { selectedCategory = category.id },
                        label = { Text(category.name, fontSize = (14 * scale).sp) },
                        selected = selectedCategory == category.id,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF74B9FF),
                            selectedLabelColor = Color.White,
                            containerColor = Color.Transparent,
                            labelColor = Color(0xFF74B9FF)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = Color(0xFF74B9FF),
                            selectedBorderColor = Color(0xFF74B9FF)
                        )
                    )
                }
            }

            // Section FAQ
            Text(
                "Questions fréquentes",
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436),
                modifier = Modifier.padding(bottom = (16 * scale).dp)
            )

            if (filteredFAQs.isEmpty()) {
                // État vide
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
                    shape = RoundedCornerShape((16 * scale).dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding((40 * scale).dp)
                    ) {
                        Text("🔍", fontSize = (48 * scale).sp)
                        Spacer(modifier = Modifier.height((16 * scale).dp))
                        Text(
                            "Aucune question trouvée",
                            fontSize = (16 * scale).sp,
                            color = Color(0xFF636E72),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Essayez avec d'autres mots-clés ou contactez-nous directement",
                            fontSize = (14 * scale).sp,
                            color = Color(0xFF636E72),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = (8 * scale).dp)
                        )
                    }
                }
            } else {
                // Liste des FAQ
                filteredFAQs.forEach { faq ->
                    FAQItem(
                        faq = faq,
                        onToggle = {
                            faqList = faqList.map {
                                if (it.id == faq.id) it.copy(isExpanded = !it.isExpanded) else it
                            }
                        },
                        scale = scale,
                        modifier = Modifier.padding(bottom = (12 * scale).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height((24 * scale).dp))

            // Section Contact
            Text(
                "Contactez-nous",
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
                    contactOptions.forEachIndexed { index, option ->
                        ContactOptionItem(
                            option = option,
                            scale = scale,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = option.disponible) {
                                    // Action selon l'option
                                }
                                .padding((16 * scale).dp)
                        )

                        if (index < contactOptions.size - 1) {
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
}

@Composable
private fun FAQItem(
    faq: FAQ,
    onToggle: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
        shape = RoundedCornerShape((12 * scale).dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding((16 * scale).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2D3436),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onToggle) {
                    Icon(
                        if (faq.isExpanded) Icons.Default.Remove else Icons.Default.Add,
                        contentDescription = if (faq.isExpanded) "Réduire" else "Développer",
                        tint = Color(0xFF74B9FF)
                    )
                }
            }

            if (faq.isExpanded) {
                Surface(
                    color = Color(0xFFF8F9FA),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = faq.reponse,
                        fontSize = (14 * scale).sp,
                        color = Color(0xFF636E72),
                        lineHeight = (20 * scale).sp,
                        modifier = Modifier.padding((16 * scale).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactOptionItem(
    option: ContactOption,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size((45 * scale).dp)
                .background(
                    if (option.disponible) option.couleur.copy(alpha = 0.2f) else Color(0xFFF1F2F6).copy(alpha = 0.6f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                option.icone,
                fontSize = (20 * scale).sp,
                modifier = Modifier.alpha(if (option.disponible) 1f else 0.5f)
            )
        }

        Spacer(modifier = Modifier.width((16 * scale).dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                option.titre,
                fontSize = (16 * scale).sp,
                fontWeight = FontWeight.Medium,
                color = if (option.disponible) Color(0xFF2D3436) else Color(0xFF636E72)
            )
            Text(
                option.description,
                fontSize = (13 * scale).sp,
                color = Color(0xFF636E72)
            )
        }

        if (!option.disponible) {
            Surface(
                color = Color(0xFFF1F2F6),
                shape = RoundedCornerShape((12 * scale).dp)
            ) {
                Text(
                    "Bientôt",
                    fontSize = (10 * scale).sp,
                    color = Color(0xFF636E72),
                    modifier = Modifier.padding(
                        horizontal = (8 * scale).dp,
                        vertical = (4 * scale).dp
                    )
                )
            }
        } else {
            Text(
                "›",
                fontSize = (20 * scale).sp,
                color = Color(0xFFDDD)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PageAidePreview() {
    HifadihTheme {
        PageAide()
    }
}