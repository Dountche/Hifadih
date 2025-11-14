@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hifadih

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// -------------------- Modèles de données existants --------------------

data class Country(
    val name: String,
    val code: String,
    val phoneCode: String,
    @DrawableRes val flag: Int
)

enum class LoginType { PHONE, EMAIL, USERNAME }

object Countries {
    val list = listOf(
        Country("Bénin", "BJ", "+229", R.drawable.flag_bj),
        Country("Burkina Faso", "BF", "+226", R.drawable.flag_bf),
        Country("Cap-Vert", "CV", "+238", R.drawable.flag_cv),
        Country("Côte d'Ivoire", "CI", "+225", R.drawable.flag_ci),
        Country("Gambie", "GM", "+220", R.drawable.flag_gm),
        Country("Ghana", "GH", "+233", R.drawable.flag_gh),
        Country("Guinée", "GN", "+224", R.drawable.flag_gn),
        Country("Guinée-Bissau", "GW", "+245", R.drawable.flag_gw),
        Country("Libéria", "LR", "+231", R.drawable.flag_lr),
        Country("Mali", "ML", "+223", R.drawable.flag_ml),
        Country("Mauritanie", "MR", "+222", R.drawable.flag_mr),
        Country("Niger", "NE", "+227", R.drawable.flag_ne),
        Country("Nigeria", "NG", "+234", R.drawable.flag_ng),
        Country("Sénégal", "SN", "+221", R.drawable.flag_sn),
        Country("Sierra Leone", "SL", "+232", R.drawable.flag_sl),
        Country("Togo", "TG", "+228", R.drawable.flag_tg)
    )
}

// -------------------- Utilitaires existants --------------------

fun detectLoginType(input: String): LoginType {
    return when {
        input.contains("@") && input.contains(".") -> LoginType.EMAIL
        input.startsWith("+") || input.all { it.isDigit() || it == ' ' } && input.replace(" ", "").length >= 8 -> LoginType.PHONE
        else -> LoginType.USERNAME
    }
}

fun getlabelForLoginType(loginValue: String): String {
    val LoginValue = loginValue.trim()
    if (LoginValue.isEmpty()) {
        return "Téléphone, Email ou Nom d'utilisateur"
    } else {
        return when (detectLoginType(LoginValue)) {
            LoginType.PHONE -> "Téléphone"
            LoginType.EMAIL -> "Email"
            LoginType.USERNAME -> "Nom d'utilisateur"
        }
    }
}

// -------------------- CountryDropdown responsive --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDropdown(
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    isVisible: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val countryFieldWidth = (screenWidthDp * 0.22f).dp
    val dropdownWidth = (screenWidthDp * 0.8f).dp

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier) {
        TextField(
            readOnly = true,
            value = selectedCountry.phoneCode,
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .width(countryFieldWidth),
            leadingIcon = {
                Image(
                    painter = painterResource(selectedCountry.flag),
                    contentDescription = "Flag ${selectedCountry.code}",
                    modifier = Modifier.size((screenWidthDp * 0.05f).dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.width(dropdownWidth)) {
            Countries.list.forEach { country ->
                DropdownMenuItem(text = {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding((screenWidthDp * 0.02f).dp)) {
                        Image(
                            painter = painterResource(country.flag),
                            contentDescription = "Drapeau ${country.name}",
                            modifier = Modifier.size((screenWidthDp * 0.06f).dp)
                        )
                        Spacer(Modifier.width((screenWidthDp * 0.03f).dp))
                        Column {
                            Text(text = country.name, style = MaterialTheme.typography.bodyMedium)
                            Text(text = country.phoneCode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }, onClick = {
                    onCountrySelected(country)
                    expanded = false
                })
            }
        }
    }
}

// -------------------- UniversalLoginField responsive --------------------

@Composable
fun UniversalLoginField(
    value: String,
    onValueChange: (String) -> Unit,
    selectedCountry: Country,
    onCountrySelected: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()
    val scale = screenWidthDp / 390f

    val countryFieldWidth = (screenWidthDp * 0.22f).dp
    val labelPaddingStart = if (detectLoginType(value) == LoginType.PHONE) countryFieldWidth + (screenWidthDp * 0.02f).dp else 0.dp

    val loginType = detectLoginType(value)
    val showCountrySelector = loginType == LoginType.PHONE && (value.isEmpty() || value.all { it.isDigit() || it == ' ' })

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (showCountrySelector) {
                CountryDropdown(
                    selectedCountry = selectedCountry,
                    onCountrySelected = onCountrySelected,
                    isVisible = true,
                    modifier = Modifier.padding(end = (screenWidthDp * 0.02f).dp)
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape((screenWidthDp * 0.02f).dp)),
                label = {
                    Text(
                        text = getlabelForLoginType(value),
                        modifier = Modifier.padding(bottom = (screenHeightDp * 0.005f).dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = (14 * scale).sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                placeholder = {
                    Text(
                        text = "Tél, mail or uname",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (12 * scale).sp
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        Spacer(Modifier.height((screenHeightDp * 0.01f).dp))

        Text(
            text = when (loginType) {
                LoginType.PHONE -> "Connexion via 2FA (code PIN) activée. Un code de vérification sera envoyé par SMS."
                LoginType.EMAIL -> "Connexion via 2DA (password) Un lien de connexion sera envoyé à cette adresse"
                LoginType.USERNAME -> "\n"
            },
            style = MaterialTheme.typography.bodySmall,
            fontSize = (12 * scale).sp,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = labelPaddingStart)
        )
    }
}

// -------------------- LoginScreen responsive --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()
    val scale = screenWidthDp / 390f

    val outerPadding = (screenWidthDp * 0.04f).dp
    val cardInnerPadding = (screenWidthDp * 0.06f).dp
    val logoSize = (screenWidthDp * 0.25f).dp
    val smallSpacer = (screenHeightDp * 0.012f).dp
    val mediumSpacer = (screenHeightDp * 0.02f).dp
    val largeSpacer = (screenHeightDp * 0.04f).dp
    val buttonHeight = (screenHeightDp * 0.07f).dp

    var loginValue by remember { mutableStateOf("") }
    var passwdValue by remember { mutableStateOf("") }
    var showPasswdField by remember { mutableStateOf(false) }
    val shouldShowPasswordField = showPasswdField && loginValue.length >= 3

    LaunchedEffect(loginValue) {
        if (loginValue.length < 3) {
            showPasswdField = false
        }
    }

    var selectedCountry by remember {
        mutableStateOf(
            Countries.list.find { it.code == "CI" } ?: Countries.list.first()
        )
    }

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    DisposableEffect(Unit) {
        focusManager.clearFocus()
        onDispose { }
    }

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
                .padding(horizontal = (screenWidthDp * 0.02f).dp),
            elevation = CardDefaults.cardElevation(defaultElevation = (4 * scale).dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape((screenWidthDp * 0.04f).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardInnerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = largeSpacer)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Logo Hifadih",
                        modifier = Modifier.size(logoSize)
                    )

                    Spacer(Modifier.height(mediumSpacer))

                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Bienvenue sur ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                append("Hifadih")
                            }
                        },
                        fontSize = (22 * scale).sp,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(Modifier.height(smallSpacer))

                    Text(
                        "Connectez-vous avec votre téléphone, email ou nom d'utilisateur",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = (13 * scale).sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                // Universal field
                UniversalLoginField(
                    value = loginValue,
                    onValueChange = { loginValue = it },
                    selectedCountry = selectedCountry,
                    onCountrySelected = { selectedCountry = it },
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(
                    visible = shouldShowPasswordField,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Spacer(Modifier.height(mediumSpacer))
                        OutlinedTextField(
                            value = passwdValue,
                            onValueChange = { passwdValue = it },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Mot de passe", fontSize = (14 * scale).sp) },
                            placeholder = { Text("Entrez votre mot de passe", fontSize = (13 * scale).sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                AnimatedVisibility(
                    visible = shouldShowPasswordField,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        text = "Mot de passe oublié ?",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            textDecoration = TextDecoration.Underline,
                            color = Color.Blue
                        ),
                        fontSize = (13 * scale).sp,
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(top = (screenHeightDp * 0.01f).dp)
                            .clickable {
                                // TODO : Navigation vers l'écran de récupération de mot de passe
                            }
                    )
                }

                Spacer(Modifier.height(largeSpacer))

                Button(
                    onClick = {
                        when (detectLoginType(loginValue)) {
                            LoginType.PHONE -> {
                                val rawPhone = "${selectedCountry.phoneCode}${loginValue.trim()}"
                                val encodedPhone = Uri.encode(rawPhone)
                                navController.navigate("pin_screen/$encodedPhone")
                            }
                            LoginType.EMAIL, LoginType.USERNAME -> {
                                if (loginValue.length >= 3) {
                                    if (passwdValue.isEmpty()) {
                                        showPasswdField = true
                                    } else {
                                        if (detectLoginType(loginValue) == LoginType.USERNAME) {
                                            val phonenumberlinkedtousername : String = "+2250103*****9"
                                            navController.navigate("otp_screen/$phonenumberlinkedtousername")
                                        } else {
                                            val email = loginValue.trim()
                                            navController.navigate("otp_screen/$email")
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(buttonHeight),
                    enabled = loginValue.isNotEmpty() && loginValue.length >= 3,
                    shape = RoundedCornerShape((screenWidthDp * 0.02f).dp)
                ) {
                    Text(
                        "Continuer",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = (15 * scale).sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(mediumSpacer))

                Row(
                    modifier = Modifier
                        .padding((screenWidthDp * 0.02f).dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vous n'avez pas de compte ? ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = (13 * scale).sp,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = "Inscrivez-vous",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            textDecoration = TextDecoration.Underline
                        ),
                        fontSize = (13 * scale).sp,
                        color = Color.Blue,
                        modifier = Modifier.clickable {
                            navController.navigate("register_screen")
                        }
                    )
                }

                Spacer(Modifier.height((screenHeightDp * 0.01f).dp))

                Text(
                    "En continuant, vous acceptez nos conditions d'utilisation",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = (11 * scale).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// -------------------- MainActivity mise à jour --------------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HifadihTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login_screen") {
                            LoginScreen(paddingValues = PaddingValues(), navController = navController)
                        }

                        composable("pin_screen/{phoneNumber}") { backStackEntry ->
                            val encoded = backStackEntry.arguments?.getString("phoneNumber") ?: ""
                            val phoneNumber = Uri.decode(encoded)
                            PinScreen(
                                phoneNumber = phoneNumber,
                                onPinSuccess = {
                                    navController.navigate("otp_screen/$phoneNumber") {
                                        popUpTo("login_screen") { inclusive = false }
                                    }
                                },
                                onBackPressed = { navController.popBackStack() },
                                paddingValues = PaddingValues()
                            )
                        }

                        composable("register_screen") {
                            RegistrationScreen(
                                paddingValues = PaddingValues(),
                                onRegistered = {},
                                onBackPressed = { navController.popBackStack() }
                            )
                        }

                        composable("otp_screen/{loginvalue}") { backStackEntry ->
                            val encoded = backStackEntry.arguments?.getString("loginvalue") ?: ""
                            val logval = Uri.decode(encoded)
                            OtpScreen(
                                loginValue = logval,
                                paddingValues = PaddingValues(),
                                onOtpSuccess = {
                                    // Navigation vers l'application principale après succès OTP
                                    navController.navigate("main_app") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                },
                                onResendOtp = {},
                                onBackPressed = {
                                    navController.navigate("login_screen") {
                                        popUpTo("login_screen") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ★ NOUVELLE ROUTE - Application principale
                        composable("main_app") {
                            MainAppScreen(
                                onLogout = {
                                    // Retour à l'écran de connexion lors de la déconnexion
                                    navController.navigate("login_screen") {
                                        popUpTo("main_app") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    HifadihTheme {
        val navController = rememberNavController()
        LoginScreen(paddingValues = PaddingValues(), navController = navController)
    }
}