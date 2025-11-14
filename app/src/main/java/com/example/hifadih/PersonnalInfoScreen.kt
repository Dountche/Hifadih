package com.example.hifadih.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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

data class UserInfo(
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val numeroClient: String,
    val niveauVip: String,
    val dateNaissance: String,
    val adresse: String,
    val ville: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageInformationsPersonnelles(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    var isEditing by remember { mutableStateOf(false) }
    var userInfo by remember {
        mutableStateOf(
            UserInfo(
                nom = "KONE",
                prenom = "Dountche",
                email = "dountche.kone@email.com",
                telephone = "+225 05 74 55 19 14",
                numeroClient = "****7891",
                niveauVip = "Gold",
                dateNaissance = "15/03/1992",
                adresse = "123 Rue des Palmiers",
                ville = "Abidjan"
            )
        )
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
                    "Informations personnelles",
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
            actions = {
                IconButton(
                    onClick = {
                        if (isEditing) {
                            // Sauvegarder les modifications
                        }
                        isEditing = !isEditing
                    }
                ) {
                    Icon(
                        if (isEditing) Icons.Default.Edit else Icons.Default.Edit,
                        contentDescription = if (isEditing) "Sauvegarder" else "Modifier",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF6C5CE7)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding((16 * scale).dp)
        ) {
            // Photo de profil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = (30 * scale).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size((100 * scale).dp)
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
                        fontSize = (36 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (isEditing) {
                    TextButton(
                        onClick = { /* Changer la photo */ },
                        modifier = Modifier.padding(top = (10 * scale).dp)
                    ) {
                        Text(
                            "Changer la photo",
                            fontSize = (14 * scale).sp,
                            color = Color(0xFF6C5CE7)
                        )
                    }
                }
            }

            // Card avec informations
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape((16 * scale).dp)
            ) {
                Column(
                    modifier = Modifier.padding((20 * scale).dp)
                ) {
                    InfoField(
                        label = "Prénom",
                        value = userInfo.prenom,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(prenom = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Nom",
                        value = userInfo.nom,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(nom = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Email",
                        value = userInfo.email,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(email = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Téléphone",
                        value = userInfo.telephone,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(telephone = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Date de naissance",
                        value = userInfo.dateNaissance,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(dateNaissance = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Adresse",
                        value = userInfo.adresse,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(adresse = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Ville",
                        value = userInfo.ville,
                        isEditing = isEditing,
                        onValueChange = { userInfo = userInfo.copy(ville = it) },
                        scale = scale
                    )

                    InfoField(
                        label = "Numéro client",
                        value = userInfo.numeroClient,
                        isEditing = false,
                        onValueChange = { },
                        scale = scale
                    )

                    InfoField(
                        label = "Niveau VIP",
                        value = userInfo.niveauVip,
                        isEditing = false,
                        onValueChange = { },
                        scale = scale,
                        isLast = true
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit,
    scale: Float,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else (20 * scale).dp)
    ) {
        Text(
            text = label,
            fontSize = (14 * scale).sp,
            color = Color(0xFF636E72),
            modifier = Modifier.padding(bottom = (5 * scale).dp)
        )

        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C5CE7),
                    unfocusedBorderColor = Color(0xFFF8F9FA)
                ),
                shape = RoundedCornerShape((8 * scale).dp)
            )
        } else {
            Text(
                text = value,
                fontSize = (16 * scale).sp,
                color = Color(0xFF2D3436),
                modifier = Modifier.padding(vertical = (8 * scale).dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PageInformationsPersonnellesPreview() {
    HifadihTheme {
        PageInformationsPersonnelles()
    }
}