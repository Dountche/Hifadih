@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hifadih

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Taille du code PIN (5 chiffres)
 */
const val PIN_SIZE = 5

/**
 * Code PIN de démonstration (à remplacer par la logique d'authentification réelle)
 */
const val DEMO_PASSWORD = "12345"

// -------------------- États de l'authentification --------------------

/**
 * Énumération des états possibles de l'authentification PIN
 */
enum class PinAuthState {
    IDLE,           // État initial, en attente de saisie
    VERIFYING,      // Vérification en cours
    SUCCESS,        // Authentification réussie
    ERROR,          // Erreur d'authentification
    FINGERPRINT     // Authentification par empreinte en cours
}

// -------------------- Générateur de clavier aléatoire --------------------

/**
 * Génère une disposition aléatoire des chiffres 0-9 pour le clavier
 * Retourne une liste de 3 lignes avec 3 chiffres chacune, plus la ligne spéciale
 */
@Composable
fun rememberRandomKeypadLayout(): List<List<Int?>> {
    return remember {
        // Mélanger les chiffres 0-9
        val digits = (0..9).toList().shuffled()

        // Créer la disposition : 3 lignes de 3 chiffres + 1 ligne spéciale
        listOf(
            listOf(digits[0], digits[1], digits[2]),           // Ligne 1
            listOf(digits[3], digits[4], digits[5]),           // Ligne 2
            listOf(digits[6], digits[7], digits[8]),           // Ligne 3
            listOf(null, digits[9], null)                       // Ligne 4 (null = empreinte/effacer)
        )
    }
}

// -------------------- Composables utilitaires (responsive) --------------------

/**
 * Renvoie la configuration responsive (scale basé sur largeur)
 */
@Composable
private fun rememberScreenScale(): Pair<Float, Float> {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()
    val scale = (screenWidthDp / 390f).coerceIn(0.7f, 2.0f)
    return Pair(scale, screenWidthDp)
}

/**
 * Bouton personnalisé pour le clavier numérique PIN (taille responsive interne)
 */
@Composable
fun PinKeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable BoxScope.() -> Unit
) {
    val (scale, screenWidth) = rememberScreenScale()
    val sizeDp = ((screenWidth * 0.18f).coerceIn(48f, 88f)).dp

    var isPressed by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "Button scale animation"
    )

    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        modifier = modifier
            .size(sizeDp)
            .scale(scaleAnim)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    isPressed = true
                    onClick()
                    isPressed = false
                }
            ),
        shape = CircleShape,
        color = backgroundColor,
        contentColor = contentColor,
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center, content = content)
    }
}

/**
 * Indicateur visuel pour chaque chiffre du PIN (responsive)
 */
@Composable
fun PinIndicator(
    isFilled: Boolean,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (scale, screenWidth) = rememberScreenScale()
    val iconSizeDp = ((screenWidth * 0.05f).coerceIn(12f, 28f)).dp

    val scaleAnim by animateFloatAsState(
        targetValue = if (isFilled) 1.1f else 1f,
        animationSpec = tween(200),
        label = "Pin indicator scale"
    )

    val color = when {
        isError -> MaterialTheme.colorScheme.error
        isFilled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    Icon(
        imageVector = if (isFilled) Icons.Filled.Circle else Icons.Outlined.Circle,
        contentDescription = if (isFilled) "Chiffre saisi" else "Chiffre manquant",
        modifier = modifier
            .size(iconSizeDp)
            .scale(scaleAnim),
        tint = color
    )
}

/**
 * Bouton d'authentification par empreinte digitale (responsive)
 */
@Composable
fun FingerprintButton(
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (scale, screenWidth) = rememberScreenScale()
    val btnSize = ((screenWidth * 0.16f).coerceIn(48f, 100f)).dp
    val iconSize = ((screenWidth * 0.07f).coerceIn(22f, 40f)).dp

    var isPulsing by remember { mutableStateOf(false) }
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPulsing) 1.1f else 1f,
        animationSpec = tween(1000),
        label = "Fingerprint pulse"
    )

    LaunchedEffect(isEnabled) {
        if (isEnabled) {
            while (true) {
                isPulsing = true
                delay(1000)
                isPulsing = false
                delay(1000)
            }
        }
    }

    Surface(
        modifier = modifier
            .size(btnSize)
            .scale(scaleAnim)
            .clickable(
                enabled = isEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = CircleShape,
        color = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (isEnabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (isEnabled) 4.dp else 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Authentification par empreinte",
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

// -------------------- Écran PIN avec clavier aléatoire --------------------

/**
 * Écran de saisie du code PIN avec clavier aléatoire
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinScreen(
    phoneNumber: String = "+225 XX XX XX XX",
    onPinSuccess: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    previewState: PinAuthState? = null,
    previewErrorMessage: String = "",
    previewPinDigits: Int = 0
) {
    // Disposition aléatoire du clavier (mémorisée pour cette instance)
    val keypadLayout = rememberRandomKeypadLayout()

    // Mesures responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    val outerPadding = (screenWidth * 0.04f).dp
    val cardPadding = (screenWidth * 0.06f).dp
    val logoSize = ((screenWidth * 0.2f).coerceIn(56f, 120f)).dp
    val headlineFont = (20 * scale).sp
    val smallFont = (12 * scale).sp
    val mediumGap = (screenHeight * 0.02f).dp
    val largeGap = (screenHeight * 0.035f).dp
    val keyGap = ((screenWidth * 0.06f).coerceIn(12f, 28f)).dp
    val keypadRowSpacing = keyGap
    val keypadButtonSize = ((screenWidth * 0.18f).coerceIn(48f, 88f)).dp

    // États
    val inputPin = remember { mutableStateListOf<Int>() }
    var authState by remember { mutableStateOf(previewState ?: PinAuthState.IDLE) }
    var errorMessage by remember { mutableStateOf(previewErrorMessage) }

    // Pré-remplissage pour preview
    LaunchedEffect(previewPinDigits) {
        if (previewPinDigits > 0 && inputPin.isEmpty()) {
            repeat(previewPinDigits.coerceAtMost(PIN_SIZE)) {
                inputPin.add((it + 1) % 10)
            }
        }
    }

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    // Logique de vérification du PIN
    LaunchedEffect(inputPin.size) {
        if (inputPin.size == PIN_SIZE && previewState == null) {
            authState = PinAuthState.VERIFYING
            delay(500)

            val enteredPin = inputPin.joinToString("")
            if (enteredPin == DEMO_PASSWORD) {
                authState = PinAuthState.SUCCESS
                delay(1200)
                onPinSuccess()
            } else {
                authState = PinAuthState.ERROR
                errorMessage = "Code PIN incorrect. Veuillez réessayer."
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(1000)
                inputPin.clear()
                authState = PinAuthState.IDLE
                errorMessage = ""
            }
        }
    }

    // Helpers
    val addDigit: (Int) -> Unit = { digit ->
        if (inputPin.size < PIN_SIZE && authState == PinAuthState.IDLE) {
            inputPin.add(digit)
        }
    }
    val removeLastDigit: () -> Unit = {
        if (inputPin.isNotEmpty() && authState == PinAuthState.IDLE) {
            inputPin.removeAt(inputPin.size - 1)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val authenticateWithFingerprint: () -> Unit = {
        coroutineScope.launch {
            if (previewState == null) {
                authState = PinAuthState.FINGERPRINT
                delay(1800)
                authState = PinAuthState.SUCCESS
                delay(1000)
                onPinSuccess()
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
            .padding(outerPadding),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (screenWidth * 0.02f).dp),
            elevation = CardDefaults.cardElevation(defaultElevation = (6f * scale).dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape((screenWidth * 0.04f).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = largeGap)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(logoSize)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(mediumGap))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Code PIN de ") }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                append("sécurité")
                            }
                        },
                        fontSize = headlineFont,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height((screenHeight * 0.008f).dp))
                    Text(
                        "Saisissez votre code PIN à $PIN_SIZE chiffres",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = smallFont,
                        textAlign = TextAlign.Center
                    )
                }

                // Indicateurs PIN
                when (authState) {
                    PinAuthState.SUCCESS -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(((screenWidth * 0.2f).coerceIn(48f, 100f)).dp)
                            )
                            Spacer(Modifier.height((screenHeight * 0.01f).dp))
                            Text(
                                "Authentification réussie !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16 * scale).sp
                            )
                        }
                    }
                    PinAuthState.FINGERPRINT -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Fingerprint",
                                modifier = Modifier.size(((screenWidth * 0.14f).coerceIn(36f, 80f)).dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height((screenHeight * 0.008f).dp))
                            Text(
                                "Authentification en cours...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = smallFont
                            )
                        }
                    }
                    else -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((screenWidth * 0.03f).dp),
                            modifier = Modifier.padding(vertical = (screenHeight * 0.02f).dp)
                        ) {
                            repeat(PIN_SIZE) { index ->
                                PinIndicator(
                                    isFilled = inputPin.size > index,
                                    isError = authState == PinAuthState.ERROR
                                )
                            }
                        }
                    }
                }

                // Erreur
                AnimatedVisibility(
                    visible = errorMessage.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = smallFont,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = (screenHeight * 0.01f).dp)
                    )
                }

                if (authState == PinAuthState.VERIFYING) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(((screenWidth * 0.08f).coerceIn(24f, 48f)).dp),
                            strokeWidth = ((2.5f * scale)).dp
                        )
                        Spacer(Modifier.height((screenHeight * 0.01f).dp))
                        Text(
                            "Vérification...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = smallFont
                        )
                    }
                }

                Spacer(Modifier.height(largeGap))

                // 🎲 CLAVIER NUMÉRIQUE ALÉATOIRE
                Column(
                    verticalArrangement = Arrangement.spacedBy(keypadRowSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Lignes 1-3 : chiffres normaux
                    keypadLayout.take(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(keypadRowSpacing)) {
                            row.forEach { digit ->
                                digit?.let {
                                    PinKeyButton(
                                        onClick = { addDigit(it) },
                                        modifier = Modifier.size(keypadButtonSize)
                                    ) {
                                        Text(
                                            it.toString(),
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = (18 * scale).sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Ligne 4 : Empreinte + dernier chiffre + Effacer
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(keypadRowSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Empreinte digitale
                        FingerprintButton(
                            onClick = authenticateWithFingerprint,
                            isEnabled = authState == PinAuthState.IDLE,
                            modifier = Modifier.size(((screenWidth * 0.16f).coerceIn(48f, 100f)).dp)
                        )

                        // Dernier chiffre (celui qui reste)
                        keypadLayout[3][1]?.let { digit ->
                            PinKeyButton(
                                onClick = { addDigit(digit) },
                                modifier = Modifier.size(keypadButtonSize)
                            ) {
                                Text(
                                    digit.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = (18 * scale).sp
                                )
                            }
                        }

                        // Bouton Effacer
                        PinKeyButton(
                            onClick = removeLastDigit,
                            modifier = Modifier.size(keypadButtonSize),
                            backgroundColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Backspace,
                                contentDescription = "Effacer",
                                modifier = Modifier.size(((screenWidth * 0.06f).coerceIn(20f, 32f)).dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(mediumGap))

                // Footer
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy((screenHeight * 0.01f).dp)
                ) {
                    Text(
                        "Code PIN oublié ?",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = (14 * scale).sp,
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                    Text(
                        "← Retour à la connexion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (12 * scale).sp,
                        modifier = Modifier.clickable(onClick = onBackPressed)
                    )
                }
            }
        }
    }
}

// -------------------- Gestionnaire biométrique --------------------

class BiometricAuthManager(private val context: android.content.Context) {
    fun isBiometricAvailable(): Boolean {
        return true
    }

    fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        onSuccess()
    }
}

// -------------------- PREVIEWS --------------------

@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun PinScreenPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PinScreen(phoneNumber = "+225 01 23 45 67", paddingValues = innerPadding)
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Avec chiffres saisis")
@Composable
fun PinScreenWithDigitsPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PinScreen(paddingValues = innerPadding, previewPinDigits = 3)
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Vérification")
@Composable
fun PinScreenVerifyingPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PinScreen(
                paddingValues = innerPadding,
                previewState = PinAuthState.VERIFYING,
                previewPinDigits = 5
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Succès")
@Composable
fun PinScreenSuccessPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PinScreen(paddingValues = innerPadding, previewState = PinAuthState.SUCCESS)
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Erreur")
@Composable
fun PinScreenErrorPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PinScreen(
                paddingValues = innerPadding,
                previewState = PinAuthState.ERROR,
                previewErrorMessage = "Code PIN incorrect. Veuillez réessayer.",
                previewPinDigits = 2
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Empreinte")
@Composable
fun PinScreenFingerprintPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            PinScreen(paddingValues = innerPadding, previewState = PinAuthState.FINGERPRINT)
        }
    }
}

// -------------------- Activity --------------------

class PinActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HifadihTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PinScreen(
                        phoneNumber = intent.getStringExtra("phone_number") ?: "+225 XX XX XX XX",
                        onPinSuccess = {},
                        onBackPressed = {},
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}