package com.example.hifadih

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.hifadih.ui.theme.HifadihTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------- Composants du coffre d'épargne --------------------

fun getProgressPercentage(objectif: ObjectifEpargne): Float {
    return minOf((objectif.montantActuel.toFloat() / objectif.montantCible.toFloat()) * 100f, 100f)
}

// Carte principale du solde du coffre
@Composable
private fun SoldeCard(
    soldeTotal: Int,
    objectifsCount: Int,
    onDepotClick: () -> Unit,
    onRetraitClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(0.85f),
        elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((16 * scale).dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((20 * scale).dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("💰", fontSize = (28 * scale).sp)
                    Spacer(Modifier.height((8 * scale).dp))
                    Text(
                        "Mon Coffre",
                        fontSize = (14 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height((4 * scale).dp))
                    Text(
                        formatMontant(soldeTotal),
                        fontSize = (24 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column {
                    Button(
                        onClick = onDepotClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape((20 * scale).dp),
                        contentPadding = PaddingValues(
                            horizontal = (12 * scale).dp,
                            vertical = (6 * scale).dp
                        ),
                        modifier = Modifier.height((32 * scale).dp)
                    ) {
                        Text("💵 Déposer", fontSize = (12 * scale).sp, color = Color.White)
                    }

                    Spacer(Modifier.height((6 * scale).dp))

                    Button(
                        onClick = onRetraitClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape((20 * scale).dp),
                        contentPadding = PaddingValues(
                            horizontal = (12 * scale).dp,
                            vertical = (6 * scale).dp
                        ),
                        modifier = Modifier.height((32 * scale).dp)
                    ) {
                        Text("💸 Retirer", fontSize = (12 * scale).sp, color = Color.White)
                    }
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                thickness = (1 * scale).dp,
                modifier = Modifier.padding(vertical = (16 * scale).dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🎯", fontSize = (16 * scale).sp)
                    Spacer(Modifier.height((4 * scale).dp))
                    Text(
                        "$objectifsCount",
                        fontSize = (18 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    Text(
                        "Objectifs",
                        fontSize = (11 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📈", fontSize = (16 * scale).sp)
                    Spacer(Modifier.height((4 * scale).dp))
                    Text(
                        "2.5%",
                        fontSize = (18 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        "Intérêts/an",
                        fontSize = (11 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("⏱️", fontSize = (16 * scale).sp)
                    Spacer(Modifier.height((4 * scale).dp))
                    Text(
                        "Auto",
                        fontSize = (18 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2196F3)
                    )
                    Text(
                        "Épargne",
                        fontSize = (11 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Carte d'objectif d'épargne
@Composable
private fun ObjectifCard(
    objectif: ObjectifEpargne,
    onAddMoneyClick: (ObjectifEpargne) -> Unit,
    onWithdrawClick: (ObjectifEpargne) -> Unit,
    onDeleteClick: (ObjectifEpargne) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val progressPercentage = getProgressPercentage(objectif)
    val isAtteint = objectif.montantActuel >= objectif.montantCible

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape((12 * scale).dp),
        border = if (isAtteint) androidx.compose.foundation.BorderStroke(
            (2 * scale).dp,
            Color(0xFF4CAF50)
        ) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(objectif.icone, fontSize = (20 * scale).sp)
                Spacer(Modifier.width((10 * scale).dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            objectif.nom,
                            fontSize = (14 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isAtteint) {
                            Text("✅", fontSize = (16 * scale).sp, modifier = Modifier.padding(start = (4 * scale).dp))
                        }
                    }
                    Text(
                        "Objectif: ${formatMontant(objectif.montantCible)}",
                        fontSize = (11 * scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${progressPercentage.toInt()}%",
                        fontSize = (12 * scale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor(objectif.couleur))
                    )
                    IconButton(
                        onClick = { onDeleteClick(objectif) },
                        modifier = Modifier.size((24 * scale).dp)
                    ) {
                        Text("🗑️", fontSize = (15 * scale).sp)
                    }
                }
            }

            Spacer(Modifier.height((10 * scale).dp))

            Text(
                formatMontant(objectif.montantActuel),
                fontSize = (18 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = if (isAtteint) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height((10 * scale).dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((6 * scale).dp)
                    .background(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape((3 * scale).dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPercentage / 100f)
                        .fillMaxHeight()
                        .background(
                            Color(android.graphics.Color.parseColor(objectif.couleur)),
                            RoundedCornerShape((3 * scale).dp)
                        )
                )
            }

            Spacer(Modifier.height((10 * scale).dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((6 * scale).dp)
            ) {
                if (!isAtteint) {
                    Button(
                        onClick = { onAddMoneyClick(objectif) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor(objectif.couleur))
                                .copy(alpha = 0.2f),
                            contentColor = Color(android.graphics.Color.parseColor(objectif.couleur))
                        ),
                        shape = RoundedCornerShape((12 * scale).dp),
                        contentPadding = PaddingValues(
                            horizontal = (10 * scale).dp,
                            vertical = (4 * scale).dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+ Ajouter", fontSize = (11 * scale).sp)
                    }

                    Button(
                        onClick = { },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape((12 * scale).dp),
                        contentPadding = PaddingValues(
                            horizontal = (10 * scale).dp,
                            vertical = (4 * scale).dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retirer", fontSize = (11 * scale).sp)
                    }
                } else {
                    Button(
                        onClick = { },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E8),
                            contentColor = Color(0xFF4CAF50),
                            disabledContainerColor = Color(0xFFE8F5E8),
                            disabledContentColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape((12 * scale).dp),
                        contentPadding = PaddingValues(
                            horizontal = (10 * scale).dp,
                            vertical = (4 * scale).dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🎉 Objectif atteint!", fontSize = (11 * scale).sp)
                    }

                    Button(
                        onClick = { onWithdrawClick(objectif) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape((12 * scale).dp),
                        contentPadding = PaddingValues(
                            horizontal = (10 * scale).dp,
                            vertical = (4 * scale).dp
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retirer tout", fontSize = (11 * scale).sp)
                    }
                }
            }
        }
    }
}

// Section des conseils d'épargne
@Composable
private fun ConseilsSection(scale: Float, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        shape = RoundedCornerShape((12 * scale).dp),
        border = androidx.compose.foundation.BorderStroke(
            width = (1 * scale).dp,
            color = Color(0xFFFFE0B2)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp)
        ) {
            Text(
                "💡 Conseils d'épargne",
                fontSize = (14 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            Spacer(Modifier.height((8 * scale).dp))
            Text(
                "• Épargnez automatiquement 10% de vos revenus\n• Fixez-vous des objectifs réalistes et atteignables\n• Profitez de nos taux d'intérêts attractifs",
                fontSize = (12 * scale).sp,
                color = Color(0xFF666666),
                lineHeight = (16 * scale).sp
            )
        }
    }
}

// -------------------- Dialogues/Modales --------------------

@Composable
private fun AlertMessage(
    message: String,
    type: String,
    onDismiss: () -> Unit,
    scale: Float
) {
    val bgColor = when (type) {
        "success" -> Color(0xFF4CAF50)
        "error" -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding((16 * scale).dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape((8 * scale).dp),
        elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding((16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                message,
                fontSize = (14 * scale).sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size((24 * scale).dp)) {
                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color.White)
            }
        }
    }
}

@Composable
private fun DepotDialog(
    soldeCompte: Int,
    montant: String,
    onMontantChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    scale: Float
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(modifier = Modifier.padding((24 * scale).dp)) {
                Text(
                    "💵 Déposer au coffre",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height((20 * scale).dp))
                Text(
                    "Solde compte principal: ${formatMontant(soldeCompte)}",
                    fontSize = (14 * scale).sp,
                    color = Color(0xFF666666)
                )
                Spacer(Modifier.height((16 * scale).dp))
                OutlinedTextField(
                    value = montant,
                    onValueChange = onMontantChange,
                    label = { Text("Montant à déposer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height((24 * scale).dp))
                Row(horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = Color(0xFF666666))
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Déposer", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun RetraitDialog(
    soldeCoffre: Int,
    montant: String,
    onMontantChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    scale: Float
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(modifier = Modifier.padding((24 * scale).dp)) {
                Text(
                    "💸 Retirer du coffre",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height((20 * scale).dp))
                Text(
                    "Solde coffre: ${formatMontant(soldeCoffre)}",
                    fontSize = (14 * scale).sp,
                    color = Color(0xFF666666)
                )
                Spacer(Modifier.height((16 * scale).dp))
                OutlinedTextField(
                    value = montant,
                    onValueChange = onMontantChange,
                    label = { Text("Montant à retirer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height((24 * scale).dp))
                Row(horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = Color(0xFF666666))
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retirer", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun AjouterArgentDialog(
    objectif: ObjectifEpargne?,
    soldeCoffre: Int,
    montant: String,
    onMontantChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    scale: Float
) {
    if (objectif == null) return

    val montantRestant = objectif.montantCible - objectif.montantActuel

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(modifier = Modifier.padding((24 * scale).dp)) {
                Text(
                    "💰 Ajouter à \"${objectif.nom}\"",
                    fontSize = (18 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height((16 * scale).dp))
                Text(
                    "Solde coffre: ${formatMontant(soldeCoffre)}",
                    fontSize = (14 * scale).sp,
                    color = Color(0xFF666666)
                )
                Spacer(Modifier.height((8 * scale).dp))
                Text(
                    "Montant restant: ${formatMontant(montantRestant)}",
                    fontSize = (14 * scale).sp,
                    color = Color(0xFFFF9800)
                )
                Spacer(Modifier.height((16 * scale).dp))
                OutlinedTextField(
                    value = montant,
                    onValueChange = onMontantChange,
                    label = { Text("Montant à ajouter") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height((24 * scale).dp))
                Row(horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = Color(0xFF666666))
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ajouter", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    objectif: ObjectifEpargne?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    scale: Float
) {
    if (objectif == null) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(
                modifier = Modifier.padding((24 * scale).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "⚠️ Confirmation",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height((16 * scale).dp))
                Text(
                    "Êtes-vous sûr de vouloir supprimer l'objectif \"${objectif.nom}\" ?",
                    fontSize = (16 * scale).sp,
                    textAlign = TextAlign.Center
                )
                if (objectif.montantActuel > 0) {
                    Spacer(Modifier.height((8 * scale).dp))
                    Text(
                        "${formatMontant(objectif.montantActuel)} sera remis dans votre coffre.",
                        fontSize = (14 * scale).sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height((24 * scale).dp))
                Row(horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = Color(0xFF666666))
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Supprimer", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun SuccessRetraitDialog(
    onConfirm: () -> Unit,
    scale: Float
) {
    Dialog(onDismissRequest = onConfirm) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(
                modifier = Modifier.padding((24 * scale).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉", fontSize = (60 * scale).sp)
                Spacer(Modifier.height((16 * scale).dp))
                Text(
                    "Félicitations!",
                    fontSize = (24 * scale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(Modifier.height((8 * scale).dp))
                Text(
                    "Votre objectif a été atteint et l'argent a été transféré dans votre coffre.",
                    fontSize = (16 * scale).sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height((24 * scale).dp))
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Super!", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun NewObjectifDialog(
    nom: String,
    montant: String,
    echeance: String,
    couleur: String,
    icone: String,
    onNomChange: (String) -> Unit,
    onMontantChange: (String) -> Unit,
    onEcheanceChange: (String) -> Unit,
    onCouleurSelect: (String) -> Unit,
    onIconeSelect: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    scale: Float
) {
    val couleursDisponibles = listOf(
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4",
        "#FFEAA7", "#DDA0DD", "#98D8C8"
    )
    val iconesDisponibles = listOf(
        "🎯", "✈️", "🏍️", "🏥", "🏠", "🎓",
        "💍", "🚗", "📱", "🎮", "👔", "🍽️"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape((16 * scale).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding((24 * scale).dp)
            ) {
                Text(
                    "🎯 Nouvel objectif d'épargne",
                    fontSize = (20 * scale).sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height((20 * scale).dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = onNomChange,
                    label = { Text("Nom de l'objectif") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height((16 * scale).dp))

                OutlinedTextField(
                    value = montant,
                    onValueChange = onMontantChange,
                    label = { Text("Montant cible en FCFA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height((16 * scale).dp))

                OutlinedTextField(
                    value = echeance,
                    onValueChange = onEcheanceChange,
                    label = { Text("Date d'échéance (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height((16 * scale).dp))

                Text(
                    "Couleur:",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height((8 * scale).dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)
                ) {
                    couleursDisponibles.forEach { c ->
                        Box(
                            modifier = Modifier
                                .size((40 * scale).dp)
                                .background(
                                    Color(android.graphics.Color.parseColor(c)),
                                    CircleShape
                                )
                                .clickable { onCouleurSelect(c) }
                                .then(
                                    if (couleur == c) Modifier.padding((2 * scale).dp) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (couleur == c) {
                                Text("✓", fontSize = (20 * scale).sp, color = Color.White)
                            }
                        }
                    }
                }
                Spacer(Modifier.height((16 * scale).dp))

                Text(
                    "Icône:",
                    fontSize = (16 * scale).sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height((8 * scale).dp))

                Column {
                    iconesDisponibles.chunked(6).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)
                        ) {
                            row.forEach { i ->
                                Box(
                                    modifier = Modifier
                                        .size((50 * scale).dp)
                                        .background(
                                            if (icone == i) Color(0xFFFFE0B2) else Color(0xFFF5F5F5),
                                            RoundedCornerShape((8 * scale).dp)
                                        )
                                        .clickable { onIconeSelect(i) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(i, fontSize = (24 * scale).sp)
                                }
                            }
                        }
                        Spacer(Modifier.height((8 * scale).dp))
                    }
                }
                Spacer(Modifier.height((24 * scale).dp))

                Row(horizontalArrangement = Arrangement.spacedBy((8 * scale).dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Annuler", color = Color(0xFF666666))
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Créer", color = Color.White)
                    }
                }
            }
        }
    }
}

// -------------------- Page du coffre principale --------------------

@Composable
fun PageCoffre(
    soldeCompteExternal: MutableState<Int>? = null,
    onDepotClick: () -> Unit = {},
    onRetraitClick: () -> Unit = {},
    onNewObjectifClick: () -> Unit = {},
    onAddMoneyClick: (ObjectifEpargne) -> Unit = {},
    onWithdrawClick: (ObjectifEpargne) -> Unit = {},
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // États pour le coffre et le compte
    var soldeCoffre by remember { mutableStateOf(125750) }
    var soldeCompte by remember { mutableStateOf(soldeCompteExternal?.value ?: 258000) }

    // Synchroniser avec le solde externe si fourni
    LaunchedEffect(soldeCompteExternal?.value) {
        soldeCompteExternal?.value?.let { soldeCompte = it }
    }

    // États pour les objectifs
    var objectifs by remember {
        mutableStateOf(
            listOf(
                ObjectifEpargne(
                    id = "1",
                    nom = "Voyage à Dubai",
                    montantCible = 500000,
                    montantActuel = 285000,
                    dateCreation = "2025-09-15",
                    dateEcheance = "2025-12-31",
                    couleur = "#FF6B6B",
                    icone = "✈️"
                ),
                ObjectifEpargne(
                    id = "2",
                    nom = "Nouvelle Moto",
                    montantCible = 800000,
                    montantActuel = 320000,
                    dateCreation = "2025-02-01",
                    dateEcheance = "2025-10-15",
                    couleur = "#4ECDC4",
                    icone = "🏍️"
                ),
                ObjectifEpargne(
                    id = "3",
                    nom = "Urgences Médicales",
                    montantCible = 200000,
                    montantActuel = 150000,
                    dateCreation = "2025-01-10",
                    dateEcheance = "2025-06-30",
                    couleur = "#45B7D1",
                    icone = "🏥"
                )
            )
        )
    }

    // États pour les dialogues
    var showDepotDialog by remember { mutableStateOf(false) }
    var showRetraitDialog by remember { mutableStateOf(false) }
    var showNewObjectifDialog by remember { mutableStateOf(false) }
    var showAjouterArgentDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessRetraitDialog by remember { mutableStateOf(false) }

    // États pour les formulaires
    var montantDepot by remember { mutableStateOf("") }
    var montantRetrait by remember { mutableStateOf("") }
    var montantAjouter by remember { mutableStateOf("") }

    var nouveauObjectifNom by remember { mutableStateOf("") }
    var nouveauObjectifMontant by remember { mutableStateOf("") }
    var nouveauObjectifEcheance by remember { mutableStateOf("") }
    var nouveauObjectifCouleur by remember { mutableStateOf("#FF6B6B") }
    var nouveauObjectifIcone by remember { mutableStateOf("🎯") }

    var selectedObjectif by remember { mutableStateOf<ObjectifEpargne?>(null) }
    var objectifToDelete by remember { mutableStateOf<ObjectifEpargne?>(null) }
    var objectifToWithdraw by remember { mutableStateOf<ObjectifEpargne?>(null) }

    // États pour les alertes
    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }
    var alertType by remember { mutableStateOf("info") }

    val scope = rememberCoroutineScope()

    // Fonction pour afficher une alerte
    fun afficherAlert(message: String, type: String = "info") {
        alertMessage = message
        alertType = type
        showAlert = true
        scope.launch {
            delay(3000)
            showAlert = false
        }
    }

    // Fonction pour déposer au coffre
    fun deposerAuCoffre() {
        val montant = montantDepot.toIntOrNull()

        if (montant == null || montant <= 0) {
            afficherAlert("Veuillez saisir un montant valide", "error")
            return
        }

        if (montant > soldeCompte) {
            afficherAlert("Solde insuffisant sur votre compte principal", "error")
            return
        }

        soldeCompte -= montant
        soldeCoffre += montant
        soldeCompteExternal?.value = soldeCompte

        showDepotDialog = false
        montantDepot = ""
        afficherAlert("${formatMontant(montant)} déposé avec succès dans votre coffre", "success")
    }

    // Fonction pour retirer du coffre
    fun retirerDuCoffre() {
        val montant = montantRetrait.toIntOrNull()

        if (montant == null || montant <= 0) {
            afficherAlert("Veuillez saisir un montant valide", "error")
            return
        }

        if (montant > soldeCoffre) {
            afficherAlert("Solde insuffisant dans votre coffre", "error")
            return
        }

        soldeCoffre -= montant
        soldeCompte += montant
        soldeCompteExternal?.value = soldeCompte

        showRetraitDialog = false
        montantRetrait = ""
        afficherAlert("${formatMontant(montant)} retiré avec succès de votre coffre", "success")
    }

    // Fonction pour créer un nouvel objectif
    fun creerNouvelObjectif() {
        if (nouveauObjectifNom.isBlank() || nouveauObjectifMontant.isBlank() || nouveauObjectifEcheance.isBlank()) {
            afficherAlert("Veuillez remplir tous les champs obligatoires", "error")
            return
        }

        val montant = nouveauObjectifMontant.toIntOrNull()
        if (montant == null || montant <= 0) {
            afficherAlert("Le montant cible doit être positif", "error")
            return
        }

        val nouvelObjectif = ObjectifEpargne(
            id = System.currentTimeMillis().toString(),
            nom = nouveauObjectifNom.trim(),
            montantCible = montant,
            montantActuel = 0,
            dateCreation = java.time.LocalDate.now().toString(),
            dateEcheance = nouveauObjectifEcheance,
            couleur = nouveauObjectifCouleur,
            icone = nouveauObjectifIcone
        )

        objectifs = objectifs + nouvelObjectif

        showNewObjectifDialog = false
        nouveauObjectifNom = ""
        nouveauObjectifMontant = ""
        nouveauObjectifEcheance = ""
        nouveauObjectifCouleur = "#FF6B6B"
        nouveauObjectifIcone = "🎯"

        afficherAlert("Objectif \"${nouvelObjectif.nom}\" créé avec succès!", "success")
    }

    // Fonction pour ajouter de l'argent à un objectif
    fun ajouterArgentObjectif() {
        val objectif = selectedObjectif ?: return
        val montant = montantAjouter.toIntOrNull()

        if (montant == null || montant <= 0) {
            afficherAlert("Veuillez saisir un montant valide", "error")
            return
        }

        if (montant > soldeCoffre) {
            afficherAlert("Solde insuffisant dans votre coffre", "error")
            return
        }

        val montantRestant = objectif.montantCible - objectif.montantActuel
        val montantAAttribuer = minOf(montant, montantRestant)

        objectifs = objectifs.map {
            if (it.id == objectif.id) {
                it.copy(montantActuel = it.montantActuel + montantAAttribuer)
            } else {
                it
            }
        }

        soldeCoffre -= montantAAttribuer

        showAjouterArgentDialog = false
        montantAjouter = ""

        val objectifMisAJour = objectifs.find { it.id == objectif.id }
        if (objectifMisAJour != null && objectifMisAJour.montantActuel >= objectifMisAJour.montantCible) {
            afficherAlert("🎉 Félicitations! Objectif \"${objectifMisAJour.nom}\" atteint!", "success")
        } else {
            afficherAlert("${formatMontant(montantAAttribuer)} ajouté à \"${objectif.nom}\"", "success")
        }

        if (montant > montantRestant) {
            afficherAlert("${formatMontant(montant - montantAAttribuer)} remis dans le coffre (objectif déjà atteint)", "info")
        }
    }

    // Fonction pour retirer l'argent d'un objectif atteint
    fun retirerArgentObjectif(objectif: ObjectifEpargne) {
        if (objectif.montantActuel < objectif.montantCible) {
            afficherAlert("Impossible de retirer: objectif non encore atteint", "error")
            return
        }

        val montantARetirer = objectif.montantActuel
        soldeCoffre += montantARetirer

        objectifs = objectifs.map {
            if (it.id == objectif.id) {
                it.copy(montantActuel = 0)
            } else {
                it
            }
        }

        objectifToWithdraw = objectif
        showSuccessRetraitDialog = true
        afficherAlert("${formatMontant(montantARetirer)} retiré de \"${objectif.nom}\" et ajouté au coffre", "success")
    }

    // Fonction pour supprimer un objectif
    fun supprimerObjectif(objectif: ObjectifEpargne) {
        if (objectif.montantActuel > 0) {
            soldeCoffre += objectif.montantActuel
        }

        objectifs = objectifs.filter { it.id != objectif.id }

        showDeleteConfirmDialog = false
        objectifToDelete = null

        afficherAlert("Objectif \"${objectif.nom}\" supprimé", "success")
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Contenu principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header avec arrière-plan orange et carte
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((220 * scale).dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((150 * scale).dp)
                            .background(
                                Color(0xFFFF9800),
                                shape = RoundedCornerShape(
                                    bottomStart = (30 * scale).dp,
                                    bottomEnd = (30 * scale).dp
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = (80 * scale).dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SoldeCard(
                            soldeTotal = soldeCoffre,
                            objectifsCount = objectifs.size,
                            onDepotClick = { showDepotDialog = true },
                            onRetraitClick = { showRetraitDialog = true },
                            scale = scale
                        )
                    }
                }

                Spacer(Modifier.height((16 * scale).dp))

                // Section des objectifs
                Column(modifier = Modifier.padding(horizontal = (16 * scale).dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🎯 Mes Objectifs d'Épargne",
                            fontSize = (18 * scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Button(
                            onClick = { showNewObjectifDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFF3E0),
                                contentColor = Color(0xFFFF9800)
                            ),
                            shape = RoundedCornerShape((20 * scale).dp),
                            contentPadding = PaddingValues(
                                horizontal = (12 * scale).dp,
                                vertical = (6 * scale).dp
                            )
                        ) {
                            Text("+ Nouvel objectif", fontSize = (12 * scale).sp)
                        }
                    }

                    Spacer(Modifier.height((16 * scale).dp))

                    // Liste des objectifs
                    if (objectifs.isNotEmpty()) {
                        objectifs.forEach { objectif ->
                            ObjectifCard(
                                objectif = objectif,
                                onAddMoneyClick = {
                                    selectedObjectif = it
                                    showAjouterArgentDialog = true
                                },
                                onWithdrawClick = { retirerArgentObjectif(it) },
                                onDeleteClick = {
                                    objectifToDelete = it
                                    showDeleteConfirmDialog = true
                                },
                                scale = scale,
                                modifier = Modifier.padding(bottom = (12 * scale).dp)
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding((32 * scale).dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎯", fontSize = (40 * scale).sp)
                                Spacer(Modifier.height((12 * scale).dp))
                                Text(
                                    "Aucun objectif",
                                    fontSize = (16 * scale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Créez votre premier objectif d'épargne",
                                    fontSize = (13 * scale).sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height((16 * scale).dp))
                    ConseilsSection(scale = scale)
                    Spacer(Modifier.height((32 * scale).dp))
                }
            }
        }

        // Alert message en haut
        AnimatedVisibility(
            visible = showAlert,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = (50 * scale).dp)
                .zIndex(1000f)
        ) {
            AlertMessage(
                message = alertMessage,
                type = alertType,
                onDismiss = { showAlert = false },
                scale = scale
            )
        }

        // Dialogues
        if (showDepotDialog) {
            DepotDialog(
                soldeCompte = soldeCompte,
                montant = montantDepot,
                onMontantChange = { montantDepot = it },
                onConfirm = { deposerAuCoffre() },
                onDismiss = {
                    showDepotDialog = false
                    montantDepot = ""
                },
                scale = scale
            )
        }

        if (showRetraitDialog) {
            RetraitDialog(
                soldeCoffre = soldeCoffre,
                montant = montantRetrait,
                onMontantChange = { montantRetrait = it },
                onConfirm = { retirerDuCoffre() },
                onDismiss = {
                    showRetraitDialog = false
                    montantRetrait = ""
                },
                scale = scale
            )
        }

        if (showAjouterArgentDialog) {
            AjouterArgentDialog(
                objectif = selectedObjectif,
                soldeCoffre = soldeCoffre,
                montant = montantAjouter,
                onMontantChange = { montantAjouter = it },
                onConfirm = { ajouterArgentObjectif() },
                onDismiss = {
                    showAjouterArgentDialog = false
                    montantAjouter = ""
                    selectedObjectif = null
                },
                scale = scale
            )
        }

        if (showNewObjectifDialog) {
            NewObjectifDialog(
                nom = nouveauObjectifNom,
                montant = nouveauObjectifMontant,
                echeance = nouveauObjectifEcheance,
                couleur = nouveauObjectifCouleur,
                icone = nouveauObjectifIcone,
                onNomChange = { nouveauObjectifNom = it },
                onMontantChange = { nouveauObjectifMontant = it },
                onEcheanceChange = { nouveauObjectifEcheance = it },
                onCouleurSelect = { nouveauObjectifCouleur = it },
                onIconeSelect = { nouveauObjectifIcone = it },
                onConfirm = { creerNouvelObjectif() },
                onDismiss = {
                    showNewObjectifDialog = false
                    nouveauObjectifNom = ""
                    nouveauObjectifMontant = ""
                    nouveauObjectifEcheance = ""
                    nouveauObjectifCouleur = "#FF6B6B"
                    nouveauObjectifIcone = "🎯"
                },
                scale = scale
            )
        }

        if (showDeleteConfirmDialog && objectifToDelete != null) {
            DeleteConfirmDialog(
                objectif = objectifToDelete,
                onConfirm = {
                    objectifToDelete?.let { supprimerObjectif(it) }
                },
                onDismiss = {
                    showDeleteConfirmDialog = false
                    objectifToDelete = null
                },
                scale = scale
            )
        }

        if (showSuccessRetraitDialog) {
            SuccessRetraitDialog(
                onConfirm = {
                    showSuccessRetraitDialog = false
                    objectifToWithdraw?.let { obj ->
                        objectifs = objectifs.filter { it.id != obj.id }
                    }
                    objectifToWithdraw = null
                },
                scale = scale
            )
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun PageCoffrePreview() {
    HifadihTheme {
        PageCoffre()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Dialogue Dépôt")
@Composable
fun PageCoffreDepotDialogPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            // Simuler l'ouverture du dialogue de dépôt
            val scale = 1f
            DepotDialog(
                soldeCompte = 258000,
                montant = "50000",
                onMontantChange = {},
                onConfirm = {},
                onDismiss = {},
                scale = scale
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Dialogue Retrait")
@Composable
fun PageCoffreRetraitDialogPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            val scale = 1f
            RetraitDialog(
                soldeCoffre = 125750,
                montant = "30000",
                onMontantChange = {},
                onConfirm = {},
                onDismiss = {},
                scale = scale
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Créer Nouvel Objectif")
@Composable
fun PageCoffreNewObjectifDialogPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            val scale = 1f
            NewObjectifDialog(
                nom = "Vacances à Paris",
                montant = "300000",
                echeance = "2025-12-25",
                couleur = "#FF6B6B",
                icone = "✈️",
                onNomChange = {},
                onMontantChange = {},
                onEcheanceChange = {},
                onCouleurSelect = {},
                onIconeSelect = {},
                onConfirm = {},
                onDismiss = {},
                scale = scale
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Ajouter à un Objectif")
@Composable
fun PageCoffreAjouterArgentDialogPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            val scale = 1f
            val objectifExample = ObjectifEpargne(
                id = "1",
                nom = "Voyage à Dubai",
                montantCible = 500000,
                montantActuel = 285000,
                dateCreation = "2025-09-15",
                dateEcheance = "2025-12-31",
                couleur = "#FF6B6B",
                icone = "✈️"
            )

            AjouterArgentDialog(
                objectif = objectifExample,
                soldeCoffre = 125750,
                montant = "50000",
                onMontantChange = {},
                onConfirm = {},
                onDismiss = {},
                scale = scale
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Confirmation Suppression")
@Composable
fun PageCoffreDeleteConfirmDialogPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            val scale = 1f
            val objectifExample = ObjectifEpargne(
                id = "2",
                nom = "Nouvelle Moto",
                montantCible = 800000,
                montantActuel = 320000,
                dateCreation = "2025-02-01",
                dateEcheance = "2025-10-15",
                couleur = "#4ECDC4",
                icone = "🏍️"
            )

            DeleteConfirmDialog(
                objectif = objectifExample,
                onConfirm = {},
                onDismiss = {},
                scale = scale
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Objectif Atteint - Félicitations")
@Composable
fun PageCoffreSuccessDialogPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            val scale = 1f
            SuccessRetraitDialog(
                onConfirm = {},
                scale = scale
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Avec Alert Message")
@Composable
fun PageCoffreWithAlertPreview() {
    HifadihTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PageCoffre()

            // Simuler une alerte de succès
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                AlertMessage(
                    message = "50 000 FCFA déposé avec succès dans votre coffre",
                    type = "success",
                    onDismiss = {},
                    scale = 1f
                )
            }
        }
    }
}