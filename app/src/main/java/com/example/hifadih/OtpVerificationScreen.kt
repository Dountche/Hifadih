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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.outlined.Backspace
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.util.Base64

/**
 * Taille du code OTP (4 chiffres)
 */
const val OTP_SIZE = 4

/**
 * Code OTP de démonstration
 */
val code_Otp = OtpService.generateOtpCode()

// -------------------- États de l'authentification OTP --------------------

/**
 * Énumération des états possibles de l'authentification OTP
 */
enum class OtpAuthState {
    IDLE,           // État initial, en attente de saisie
    VERIFYING,      // Vérification en cours
    SUCCESS,        // Authentification réussie
    ERROR,          // Erreur d'authentification
    AUTO_FILLING    // Remplissage automatique en cours
}

// -------------------- Service OTP --------------------

object OtpService {
    private const val TWILIO_ACCOUNT_SID = "ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    private const val TWILIO_AUTH_TOKEN = "your_auth_token_here"
    private const val TWILIO_PHONE_NUMBER = "+1234567890"

    fun generateOtpCode(): String {
        return (1000..9999).random().toString()
    }

    suspend fun sendOtpBySms(phoneNumber: String, otpCode: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.twilio.com/2010-04-01/Accounts/$TWILIO_ACCOUNT_SID/Messages.json")
                val connection = url.openConnection() as HttpURLConnection

                val credentials = "$TWILIO_ACCOUNT_SID:$TWILIO_AUTH_TOKEN"
                val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())

                connection.requestMethod = "POST"
                connection.setRequestProperty("Authorization", "Basic $encodedCredentials")
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val message = "Votre code de vérification Hifadih: $otpCode. Ce code expire dans 5 minutes."

                val postData = buildString {
                    append("From=").append(URLEncoder.encode(TWILIO_PHONE_NUMBER, "UTF-8"))
                    append("&To=").append(URLEncoder.encode(phoneNumber, "UTF-8"))
                    append("&Body=").append(URLEncoder.encode(message, "UTF-8"))
                }

                connection.outputStream.use { output ->
                    output.write(postData.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode in 200..299) {
                    connection.inputStream.use { it.bufferedReader().readText() }
                } else {
                    connection.errorStream?.use { it.bufferedReader().readText() } ?: "Erreur inconnue"
                }

                when (responseCode) {
                    HttpURLConnection.HTTP_CREATED -> Result.success("SMS envoyé avec succès via Twilio")
                    HttpURLConnection.HTTP_BAD_REQUEST -> Result.failure(Exception("Paramètres invalides: $responseBody"))
                    HttpURLConnection.HTTP_UNAUTHORIZED -> Result.failure(Exception("Authentification Twilio échouée. Vérifiez vos identifiants."))
                    HttpURLConnection.HTTP_FORBIDDEN -> Result.failure(Exception("Numéro non vérifié ou quota dépassé: $responseBody"))
                    else -> Result.failure(Exception("Erreur Twilio ($responseCode): $responseBody"))
                }
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("Timeout: Vérifiez votre connexion internet"))
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("Pas de connexion internet"))
            } catch (e: Exception) {
                Result.failure(Exception("Erreur réseau SMS: ${e.message}", e))
            }
        }
    }

    suspend fun sendOtpByEmail(email: String, otpCode: String): Result<String> {
        val year = LocalDate.now().year
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.emailjs.com/api/v1.0/email/send")
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val jsonData = """
                    {
                        "service_id": "service_hifadih_gamail",
                        "template_id": "template_4v9b49e",
                        "user_id": "ps4BSGUNSS1U5goTV",
                        "accessToken": "TC4rcvWtcqlz1t2l-Q895",
                        "template_params": {
                            "to_email": "$email",
                            "otp_code": "$otpCode",
                            "app_name": "Hifadih",
                            "year": "$year",
                            "user_name": "${email.substringBefore("@")}"
                        }
                    }
                """.trimIndent()

                connection.outputStream.use { output ->
                    output.write(jsonData.toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Result.success("Email OTP envoyé avec succès")
                } else {
                    val error = connection.errorStream?.use { it.bufferedReader().readText() }
                    Result.failure(Exception("Erreur EmailJS ($responseCode): $error"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Erreur email: ${e.message}", e))
            }
        }
    }
}

// -------------------- Générateur de clavier aléatoire OTP --------------------

/**
 * Génère une disposition aléatoire des chiffres 0-9 pour le clavier OTP
 */
@Composable
fun rememberRandomOtpKeypadLayout(): List<List<Int?>> {
    return remember {
        val digits = (0..9).toList().shuffled()

        listOf(
            listOf(digits[0], digits[1], digits[2]),           // Ligne 1
            listOf(digits[3], digits[4], digits[5]),           // Ligne 2
            listOf(digits[6], digits[7], digits[8]),           // Ligne 3
            listOf(null, digits[9], null)                      // Ligne 4 (null = autofill/effacer)
        )
    }
}

// -------------------- Composables utilitaires --------------------

@Composable
private fun rememberScreenScale(): Pair<Float, Float> {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()
    val scale = (screenWidthDp / 390f).coerceIn(0.7f, 2.0f)
    return Pair(scale, screenWidthDp)
}

@Composable
fun OtpKeyButton(
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

@Composable
fun OtpIndicator(
    digit: String = "",
    isFilled: Boolean = false,
    isError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val (scale, screenWidth) = rememberScreenScale()
    val boxSize = ((screenWidth * 0.12f).coerceIn(40f, 60f)).dp

    val scaleAnim by animateFloatAsState(
        targetValue = if (isFilled) 1.05f else 1f,
        animationSpec = tween(200),
        label = "Otp indicator scale"
    )

    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFilled -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

    val backgroundColor = when {
        isError -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        isFilled -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surface
    }

    Box(
        modifier = modifier
            .size(boxSize)
            .scale(scaleAnim)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isFilled) {
            Text(
                text = digit,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = (20 * scale).sp,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AutoFillButton(
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
        label = "AutoFill pulse"
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
        color = if (isEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        contentColor = if (isEnabled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        tonalElevation = if (isEnabled) 4.dp else 1.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Remplissage automatique OTP",
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

// -------------------- Écran OTP avec clavier aléatoire --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    loginValue: String = "+225 XX XX XX XX",
    onOtpSuccess: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onResendOtp: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(),
    previewState: OtpAuthState? = null,
    previewErrorMessage: String = "",
    previewOtpDigits: List<String> = emptyList()
) {
    // Disposition aléatoire du clavier (mémorisée pour cette instance)
    val keypadLayout = rememberRandomOtpKeypadLayout()

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
    val inputOtp = remember { mutableStateListOf<String>() }
    var authState by remember { mutableStateOf(previewState ?: OtpAuthState.IDLE) }
    var errorMessage by remember { mutableStateOf(previewErrorMessage) }

    var timeLeft by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    // Pré-remplissage pour preview
    LaunchedEffect(previewOtpDigits) {
        if (previewOtpDigits.isNotEmpty() && inputOtp.isEmpty()) {
            previewOtpDigits.forEach { digit ->
                if (inputOtp.size < OTP_SIZE) {
                    inputOtp.add(digit)
                }
            }
        }
    }

    // Timer countdown
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        canResend = true
    }

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    // Envoi OTP via SMS ou Email
    when (detectLoginType(loginValue)) {
        LoginType.PHONE -> {
            LaunchedEffect(Unit) {
                val result = OtpService.sendOtpBySms(loginValue, code_Otp)
                if (result.isFailure) {
                    errorMessage = "Erreur lors de l'envoi du SMS: ${result.exceptionOrNull()?.message}"
                    authState = OtpAuthState.ERROR
                } else {
                    errorMessage = ""
                }
            }
        }
        LoginType.EMAIL -> {
            LaunchedEffect(Unit) {
                val result = OtpService.sendOtpByEmail(loginValue, code_Otp)
                if (result.isFailure) {
                    errorMessage = "Erreur lors de l'envoi de l'email: ${result.exceptionOrNull()?.message}"
                    authState = OtpAuthState.ERROR
                } else {
                    errorMessage = ""
                }
            }
        }
        LoginType.USERNAME -> {}
    }

    // Logique de vérification du OTP
    LaunchedEffect(inputOtp.size) {
        if (inputOtp.size == OTP_SIZE && previewState == null) {
            authState = OtpAuthState.VERIFYING
            delay(500)

            val enteredOtp = inputOtp.joinToString("")
            if (enteredOtp == code_Otp) {
                authState = OtpAuthState.SUCCESS
                delay(1200)
                onOtpSuccess()
            } else {
                authState = OtpAuthState.ERROR
                errorMessage = "Code OTP incorrect. Veuillez réessayer."
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                delay(1000)
                inputOtp.clear()
                authState = OtpAuthState.IDLE
                errorMessage = ""
            }
        }
    }

    // Helpers
    val addDigit: (String) -> Unit = { digit ->
        if (inputOtp.size < OTP_SIZE && authState == OtpAuthState.IDLE) {
            inputOtp.add(digit)
        }
    }
    val removeLastDigit: () -> Unit = {
        if (inputOtp.isNotEmpty() && authState == OtpAuthState.IDLE) {
            inputOtp.removeAt(inputOtp.size - 1)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val autoFillOtp: () -> Unit = {
        coroutineScope.launch {
            if (previewState == null && authState == OtpAuthState.IDLE) {
                authState = OtpAuthState.AUTO_FILLING
                inputOtp.clear()

                code_Otp.forEach { char ->
                    delay(200)
                    inputOtp.add(char.toString())
                }

                delay(300)
                authState = OtpAuthState.IDLE
            }
        }
    }

    val resendCode: () -> Unit = {
        if (canResend) {
            timeLeft = 60
            canResend = false
            inputOtp.clear()
            authState = OtpAuthState.IDLE
            errorMessage = ""
            onResendOtp()
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
                        modifier = Modifier.size(logoSize).clip(CircleShape)
                    )
                    Spacer(Modifier.height(mediumGap))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Code de ") }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                append("vérification")
                            }
                        },
                        fontSize = headlineFont,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height((screenHeight * 0.008f).dp))
                    Text(
                        "Saisissez le code à $OTP_SIZE chiffres envoyé au",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = smallFont,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        loginValue,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        fontSize = smallFont,
                        textAlign = TextAlign.Center
                    )
                }

                // Indicateurs OTP
                when (authState) {
                    OtpAuthState.SUCCESS -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Success",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(((screenWidth * 0.2f).coerceIn(48f, 100f)).dp)
                            )
                            Spacer(Modifier.height((screenHeight * 0.01f).dp))
                            Text(
                                "Code vérifié avec succès !",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = (16 * scale).sp
                            )
                        }
                    }
                    OtpAuthState.AUTO_FILLING -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((screenWidth * 0.03f).dp),
                                modifier = Modifier.padding(vertical = (screenHeight * 0.02f).dp)
                            ) {
                                repeat(OTP_SIZE) { index ->
                                    OtpIndicator(
                                        digit = inputOtp.getOrNull(index) ?: "",
                                        isFilled = inputOtp.size > index,
                                        isError = authState == OtpAuthState.ERROR
                                    )
                                }
                            }
                            Spacer(Modifier.height((screenHeight * 0.008f).dp))
                            Text(
                                "Remplissage automatique...",
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
                            repeat(OTP_SIZE) { index ->
                                OtpIndicator(
                                    digit = inputOtp.getOrNull(index) ?: "",
                                    isFilled = inputOtp.size > index,
                                    isError = authState == OtpAuthState.ERROR
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

                if (authState == OtpAuthState.VERIFYING) {
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
                                    OtpKeyButton(
                                        onClick = { addDigit(it.toString()) },
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

                    // Ligne 4 : AutoFill + dernier chiffre + Effacer
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(keypadRowSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Remplissage automatique
                        AutoFillButton(
                            onClick = autoFillOtp,
                            isEnabled = authState == OtpAuthState.IDLE && inputOtp.isEmpty(),
                            modifier = Modifier.size(((screenWidth * 0.16f).coerceIn(48f, 100f)).dp)
                        )

                        // Dernier chiffre
                        keypadLayout[3][1]?.let { digit ->
                            OtpKeyButton(
                                onClick = { addDigit(digit.toString()) },
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
                        OtpKeyButton(
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
                    if (canResend) {
                        Text(
                            "Renvoyer le code",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = (14 * scale).sp,
                            modifier = Modifier.clickable { resendCode() }
                        )
                    } else {
                        Text(
                            "Renvoyer le code dans ${timeLeft}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = smallFont
                        )
                    }

                    Text(
                        "← Retour",
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

// -------------------- PREVIEWS --------------------

@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun OtpScreenPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OtpScreen(loginValue = "+225 01 23 45 67", paddingValues = innerPadding)
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Avec chiffres")
@Composable
fun OtpScreenWithDigitsPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OtpScreen(
                loginValue = "+225 01 23 45 67",
                paddingValues = innerPadding,
                previewOtpDigits = listOf("1", "2")
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Vérification")
@Composable
fun OtpScreenVerifyingPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OtpScreen(
                loginValue = "+225 01 23 45 67",
                paddingValues = innerPadding,
                previewState = OtpAuthState.VERIFYING,
                previewOtpDigits = listOf("1", "2", "3", "4")
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Succès")
@Composable
fun OtpScreenSuccessPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OtpScreen(
                loginValue = "+225 01 23 45 67",
                paddingValues = innerPadding,
                previewState = OtpAuthState.SUCCESS
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "Erreur")
@Composable
fun OtpScreenErrorPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OtpScreen(
                loginValue = "+225 01 23 45 67",
                paddingValues = innerPadding,
                previewState = OtpAuthState.ERROR,
                previewErrorMessage = "Code OTP incorrect. Veuillez réessayer.",
                previewOtpDigits = listOf("1", "2")
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 400, name = "AutoFill")
@Composable
fun OtpScreenAutoFillPreview() {
    HifadihTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            OtpScreen(
                loginValue = "+225 01 23 45 67",
                paddingValues = innerPadding,
                previewState = OtpAuthState.AUTO_FILLING,
                previewOtpDigits = listOf("1", "2")
            )
        }
    }
}

// -------------------- Activity --------------------

class OtpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HifadihTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OtpScreen(
                        loginValue = intent.getStringExtra("phone_number") ?: "+225 XX XX XX XX",
                        onOtpSuccess = {},
                        onBackPressed = {},
                        onResendOtp = {},
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}