@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hifadih

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme

// -------------------- Registration Data Model --------------------
data class RegistrationData(
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var pinCode: String = "",
    var selectedCountry: Country = Countries.list.find { it.code == "CI" } ?: Countries.list.first(),
    var acceptTerms: Boolean = false
)

enum class RegistrationStep { PERSONAL_INFO, LOGIN_INFO, PASSWORD, VERIFICATION }

// -------------------- Helpers responsive --------------------
@Composable
private fun rememberResponsive(): ResponsiveValues {
    val configuration = LocalConfiguration.current
    val screenW = configuration.screenWidthDp.toFloat()
    val screenH = configuration.screenHeightDp.toFloat()
    // scale baseline 390dp width, clamp between 0.7 and 2.0
    val scale = (screenW / 390f).coerceIn(0.7f, 2.0f)
    return ResponsiveValues(screenWidth = screenW, screenHeight = screenH, scale = scale)
}

data class ResponsiveValues(
    val screenWidth: Float,
    val screenHeight: Float,
    val scale: Float
)

// -------------------- Progress indicator (responsive) --------------------
@Composable
fun ProgressIndicator(currentStep: RegistrationStep, modifier: Modifier = Modifier) {
    val res = rememberResponsive()
    val dotSize = ((res.screenWidth * 0.03f).coerceIn(8f, 16f)).dp
    val lineWidth = ((res.screenWidth * 0.06f).coerceIn(24f, 48f)).dp
    val gap = ((res.screenWidth * 0.02f).coerceIn(8f, 16f)).dp

    val steps = listOf(
        RegistrationStep.PERSONAL_INFO,
        RegistrationStep.LOGIN_INFO,
        RegistrationStep.PASSWORD,
        RegistrationStep.VERIFICATION
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = steps.indexOf(currentStep) > index
            val isCurrent = currentStep == step

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(
                        color = when {
                            isCompleted -> MaterialTheme.colorScheme.primary
                            isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.outline
                        },
                        shape = RoundedCornerShape(50)
                    )
            )

            if (index < steps.size - 1) {
                Spacer(modifier = Modifier.width(gap))
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .height((dotSize / 4))
                        .background(
                            color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(gap))
            }
        }
    }
}

// -------------------- étape 1 : info perso (responsive) --------------------
@Composable
fun PersonalInfoStep(
    registrationData: RegistrationData,
    onDataChange: (RegistrationData) -> Unit,
    onNext: () -> Unit
) {
    val res = rememberResponsive()
    val spacerSmall = ((res.screenHeight * 0.015f).coerceIn(8f, 24f)).dp
    val btnHeight = ((res.screenHeight * 0.07f).coerceIn(44f, 64f)).dp

    Column {
        Text(
            "Informations personnelles",
            fontSize = (18 * res.scale).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = spacerSmall)
        )
        OutlinedTextField(
            value = registrationData.firstName,
            onValueChange = { onDataChange(registrationData.copy(firstName = it)) },
            label = { Text(text = "Prénoms", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Prénom") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(spacerSmall))
        OutlinedTextField(
            value = registrationData.lastName,
            onValueChange = { onDataChange(registrationData.copy(lastName = it)) },
            label = { Text(text = "Nom", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Nom") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(spacerSmall * 2))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(btnHeight),
            enabled = registrationData.firstName.length >= 2 && registrationData.lastName.length >= 2,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continuer", fontSize = (16 * res.scale).sp)
        }
    }
}

// -------------------- étape 2 : Login Info (USERNAME / EMAIL / PHONE) --------------------
@Composable
fun LoginInfoStep(
    registrationData: RegistrationData,
    onDataChange: (RegistrationData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val res = rememberResponsive()
    val spacerSmall = ((res.screenHeight * 0.015f).coerceIn(8f, 24f)).dp
    val btnHeight = ((res.screenHeight * 0.07f).coerceIn(44f, 64f)).dp

    Column {
        Text(
            "Informations de connexion",
            fontSize = (18 * res.scale).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = spacerSmall)
        )

        // Username
        OutlinedTextField(
            value = registrationData.username,
            onValueChange = { onDataChange(registrationData.copy(username = it)) },
            label = { Text("Nom d'utilisateur", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Nom d'utilisateur (optionnel)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacerSmall))

        // Email
        OutlinedTextField(
            value = registrationData.email,
            onValueChange = { onDataChange(registrationData.copy(email = it)) },
            label = { Text("Email", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("email@example.com") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(spacerSmall))

        // Phone (avec le drapeau du pays)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            // CountryDropdown est defin dans le LoginScreen (ne pas redéfinir ici)
            CountryDropdown(
                selectedCountry = registrationData.selectedCountry,
                onCountrySelected = { onDataChange(registrationData.copy(selectedCountry = it)) },
                isVisible = true,
                modifier = Modifier.padding(end = ((res.screenWidth * 0.02f).coerceIn(6f, 12f)).dp)
            )
            Spacer(modifier = Modifier.width(((res.screenWidth * 0.02f).coerceIn(6f, 12f)).dp))
            OutlinedTextField(
                value = registrationData.phoneNumber,
                onValueChange = { onDataChange(registrationData.copy(phoneNumber = it.filter { ch -> ch.isDigit() })) },
                label = { Text(text = "Téléphone", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                placeholder = { Text("XXXXXXXXXX") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
            )
        }

        Spacer(modifier = Modifier.height(spacerSmall * 2))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(0.45f).height(btnHeight),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retour", fontSize = (16 * res.scale).sp)
            }

            Spacer(modifier = Modifier.width(((res.screenWidth * 0.02f).coerceIn(6f, 12f)).dp))

            Button(
                onClick = onNext,
                modifier = Modifier.weight(0.45f).height(btnHeight),
                enabled = (registrationData.email.isNotBlank() || registrationData.username.isNotBlank() || registrationData.phoneNumber.length >= 8),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continuer", fontSize = (16 * res.scale).sp)
            }
        }
    }
}

// -------------------- fun for la conformité du mot de passe --------------------
fun isPasswordValid(password: String): Boolean {
    // >=8, 1 uppercase, 1 lowercase, 1 digit, 1 symbol
    val regex = Regex("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}\$")
    return regex.matches(password)
}

// -------------------- etape 3 : Password + PIN --------------------
@Composable
fun PasswordStep(
    registrationData: RegistrationData,
    onDataChange: (RegistrationData) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val res = rememberResponsive()
    val spacerSmall = ((res.screenHeight * 0.015f).coerceIn(8f, 24f)).dp
    val btnHeight = ((res.screenHeight * 0.07f).coerceIn(44f, 64f)).dp

    val pw = registrationData.password
    val confirm = registrationData.confirmPassword
    val pin = registrationData.pinCode
    val passwordOk = isPasswordValid(pw)
    val pinOk = pin.length == 5 && pin.all { it.isDigit() }
    val canProceed = passwordOk && (pw == confirm) && pinOk

    Column {
        Text("Créer un mot de passe", fontSize = (18 * res.scale).sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = spacerSmall))

        OutlinedTextField(
            value = pw,
            onValueChange = { onDataChange(registrationData.copy(password = it)) },
            label = { Text("Mot de passe", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Mot de passe") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(spacerSmall))

        OutlinedTextField(
            value = confirm,
            onValueChange = { onDataChange(registrationData.copy(confirmPassword = it)) },
            label = { Text(text = "Confirmer le mot de passe", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Confirmer le mot de passe") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = confirm.isNotEmpty() && pw != confirm
        )
        Spacer(modifier = Modifier.height(spacerSmall))

        // critère du Password
        Column(modifier = Modifier.padding(8.dp)) {
            Text("Mot de passe doit contenir :", fontWeight = FontWeight.Medium, fontSize = (14 * res.scale).sp)
            Spacer(modifier = Modifier.height(((res.screenHeight * 0.01f).coerceIn(6f, 12f)).dp))
            Text("• Au moins 8 caractères", color = if (pw.length >= 8) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Une lettre majuscule", color = if (pw.any { it.isUpperCase() }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Une lettre minuscule", color = if (pw.any { it.isLowerCase() }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Un chiffre", color = if (pw.any { it.isDigit() }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Text("• Un symbole", color = if (pw.any { !it.isLetterOrDigit() }) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.height(spacerSmall))

        // PIN
        OutlinedTextField(
            value = pin,
            onValueChange = {
                if (it.length <= 5 && it.all { ch -> ch.isDigit() }) onDataChange(registrationData.copy(pinCode = it))
            },
            label = { Text(text = "Code PIN de sécurité (5 chiffres)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            placeholder = { Text("Code PIN de sécurité Ex : 12345", fontSize = 13.sp,)},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (confirm.isNotEmpty() && pw != confirm) {
            Text("Les mots de passe ne correspondent pas", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(spacerSmall * 2))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = onBack,
                modifier = Modifier.weight(0.45f).height(btnHeight),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Retour", fontSize = (16 * res.scale).sp) }

            Spacer(modifier = Modifier.width(((res.screenWidth * 0.02f).coerceIn(6f, 12f)).dp))

            Button(
                onClick = onNext,
                modifier = Modifier.weight(0.45f).height(btnHeight),
                enabled = canProceed,
                shape = RoundedCornerShape(12.dp)
            ) { Text("Continuer", fontSize = (16 * res.scale).sp) }
        }
    }
}

// -------------------- étapes 4 : Verification --------------------
@Composable
fun VerificationStep(
    registrationData: RegistrationData,
    onDataChange: (RegistrationData) -> Unit,
    onFinalize: () -> Unit,
) {
    val res = rememberResponsive()
    val spacerSmall = ((res.screenHeight * 0.015f).coerceIn(8f, 24f)).dp
    Column {
        Text("Finaliser l'inscription", fontSize = (18 * res.scale).sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = spacerSmall))

        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(((res.screenWidth * 0.04f).coerceIn(8f, 16f)).dp)
        ) {
            Text("Vérifiez vos informations :", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = spacerSmall))
            Text("Nom complet: ${registrationData.firstName} ${registrationData.lastName}")
            Text("Username: ${registrationData.username}")
            Text("Email: ${registrationData.email}")
            Text("Téléphone: ${registrationData.selectedCountry.phoneCode} ${registrationData.phoneNumber}")
        }

        Spacer(modifier = Modifier.height(spacerSmall))

        Row(verticalAlignment = Alignment.Top) {
            Checkbox(checked = registrationData.acceptTerms, onCheckedChange = { onDataChange(registrationData.copy(acceptTerms = it)) })
            Spacer(modifier = Modifier.width(((res.screenWidth * 0.02f).coerceIn(6f, 12f)).dp))
            Text("J'accepte les conditions d'utilisation et la politique de confidentialité", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(spacerSmall))

        Button(
            onClick = {
                // ici la logique d'inscription -> appel API, etc.
                onFinalize()
            },
            modifier = Modifier.fillMaxWidth().height(((res.screenHeight * 0.07f).coerceIn(44f, 64f)).dp),
            enabled = registrationData.acceptTerms,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Créer mon compte", fontSize = (16 * res.scale).sp)
        }
    }
}

// -------------------- RegistrationScreen (root responsive) --------------------
@Composable
fun RegistrationScreen(
    onRegistered: () -> Unit,
    onBackPressed: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues()
) {
    val res = rememberResponsive()
    val outerPadding = ((res.screenWidth * 0.04f).coerceIn(8f, 24f)).dp
    val cardPadding = ((res.screenWidth * 0.06f).coerceIn(12f, 32f)).dp
    val logoSize = ((res.screenWidth * 0.18f).coerceIn(56f, 120f)).dp
    var currentStep by remember { mutableStateOf(RegistrationStep.PERSONAL_INFO) }
    var registrationData by remember { mutableStateOf(RegistrationData()) }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val scale = screenWidthDp / 390f

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
                .padding(horizontal = ((res.screenWidth * 0.02f).coerceIn(6f, 12f)).dp),
            elevation = CardDefaults.cardElevation(defaultElevation = (6f * res.scale).dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(((res.screenWidth * 0.04f).coerceIn(8f, 20f)).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(
                        bottom = ((res.screenHeight * 0.02f).coerceIn(
                            12f,
                            32f
                        )).dp
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo",
                        modifier = Modifier.size(logoSize)
                    )
                    Spacer(
                        modifier = Modifier.height(
                            ((res.screenHeight * 0.01f).coerceIn(
                                8f,
                                20f
                            )).dp
                        )
                    )
                    Row {
                        Text(
                            "Rejoignez ",
                            fontSize = (20 * res.scale).sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Hifadih",
                            fontSize = (20 * res.scale).sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        "Créez votre compte en quelques étapes",
                        fontSize = (14 * res.scale).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            top = ((res.screenHeight * 0.008f).coerceIn(
                                6f,
                                16f
                            )).dp, bottom = ((res.screenHeight * 0.01f).coerceIn(8f, 20f)).dp
                        )
                    )
                }

                ProgressIndicator(
                    currentStep = currentStep,
                    modifier = Modifier.padding(
                        bottom = ((res.screenHeight * 0.02f).coerceIn(
                            12f,
                            24f
                        )).dp
                    )
                )

                when (currentStep) {
                    RegistrationStep.PERSONAL_INFO -> PersonalInfoStep(
                        registrationData = registrationData,
                        onDataChange = { registrationData = it },
                        onNext = { currentStep = RegistrationStep.LOGIN_INFO }
                    )

                    RegistrationStep.LOGIN_INFO -> LoginInfoStep(
                        registrationData = registrationData,
                        onDataChange = { registrationData = it },
                        onNext = { currentStep = RegistrationStep.PASSWORD },
                        onBack = { currentStep = RegistrationStep.PERSONAL_INFO }
                    )

                    RegistrationStep.PASSWORD -> PasswordStep(
                        registrationData = registrationData,
                        onDataChange = { registrationData = it },
                        onNext = { currentStep = RegistrationStep.VERIFICATION },
                        onBack = { currentStep = RegistrationStep.LOGIN_INFO }
                    )

                    RegistrationStep.VERIFICATION -> VerificationStep(
                        registrationData = registrationData,
                        onDataChange = { registrationData = it },
                        onFinalize = onRegistered,
                    )
                }

                Spacer(
                    modifier = Modifier.height(
                        ((res.screenHeight * 0.02f).coerceIn(
                            12f,
                            24f
                        )).dp
                    )
                )

                // lien de connexion (comme login screen: "Connectez-vous" en bleu et souligné)
                Row(
                    modifier = Modifier
                        .padding((screenWidthDp * 0.02f).dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vous avez déja un compte ? ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = (13 * scale).sp,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = "Connectez-vous",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            textDecoration = TextDecoration.Underline
                        ),
                        fontSize = (13 * scale).sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable {
                            onBackPressed()
                        }
                    )
                }
            }
        }
    }
}

// -------------------- Activity--------------------
class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HifadihTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                    RegistrationScreen(
                        onRegistered = { finish() },
                        onBackPressed = { finish() },
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}

// -------------------- Previews--------------------
@Preview(showBackground = true, heightDp = 800, widthDp = 400)
@Composable
fun RegistrationScreenPreview() {
    HifadihTheme {
        RegistrationScreen(onRegistered = {}, onBackPressed = {})
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 300)
@Composable
fun PersonalInfoStepPreview() {
    HifadihTheme {
        PersonalInfoStep(registrationData = RegistrationData(), onDataChange = {}, onNext = {})
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 400)
@Composable
fun LoginInfoStepPreview() {
    HifadihTheme {
        val data = RegistrationData()
        LoginInfoStep(registrationData = data, onDataChange = {}, onNext = {}, onBack = {})
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 420)
@Composable
fun PasswordStepPreview() {
    HifadihTheme {
        PasswordStep(registrationData = RegistrationData(), onDataChange = {}, onNext = {}, onBack = {})
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 420)
@Composable
fun VerificationStepPreview() {
    HifadihTheme {
        VerificationStep(registrationData = RegistrationData(firstName = "Jean", lastName = "Dupont", username = "jdupont", email = "jd@mail.com", phoneNumber = "01234567"), onDataChange = {}, onFinalize = {})
    }
}
