package com.example.hifadih

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.hifadih.profile.PageAPropos
import com.example.hifadih.profile.PageAide
import com.example.hifadih.profile.PageInformationsPersonnelles
import com.example.hifadih.profile.PageParrainage
import com.example.hifadih.profile.PageSecurite
import com.example.hifadih.settings.PageLangue
import com.example.hifadih.settings.PageParametres
import com.example.hifadih.settings.PageTheme
import com.example.hifadih.ui.theme.HifadihTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.activity.compose.BackHandler

// -------------------- Utilitaires pour la navigation par gestes --------------------

fun getPageOrder(): List<PageType> {
    return listOf(PageType.ACCUEIL, PageType.STATISTIQUES, PageType.COFFRE, PageType.PROFILE)
}

fun getNextPage(currentPage: PageType): PageType {
    val pages = getPageOrder()
    val currentIndex = pages.indexOf(currentPage)
    return if (currentIndex >= 0 && currentIndex < pages.size - 1) {
        pages[currentIndex + 1]
    } else {
        currentPage
    }
}

fun getPreviousPage(currentPage: PageType): PageType {
    val pages = getPageOrder()
    val currentIndex = pages.indexOf(currentPage)
    return if (currentIndex > 0) {
        pages[currentIndex - 1]
    } else {
        currentPage
    }
}

// -------------------- Sidebar Component --------------------

@Composable
private fun SidebarItem(
    icon: String,
    label: String,
    isDanger: Boolean = false,
    onClick: () -> Unit,
    scale: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(
                horizontal = (16 * scale).dp,
                vertical = (12 * scale).dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size((36 * scale).dp)
                .background(
                    if (isDanger) Color(0xFFFFEBEE) else Color(0xFFF8F9FA),
                    RoundedCornerShape((10 * scale).dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                icon,
                fontSize = (16 * scale).sp
            )
        }

        Spacer(Modifier.width((12 * scale).dp))

        Text(
            label,
            fontSize = (14 * scale).sp,
            fontWeight = FontWeight.Medium,
            color = if (isDanger) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            "›",
            fontSize = (16 * scale).sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun SectionDivider(
    title: String,
    scale: Float
) {
    Text(
        title.uppercase(),
        fontSize = (10 * scale).sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        letterSpacing = (0.5 * scale).sp,
        modifier = Modifier.padding(
            horizontal = (16 * scale).dp,
            vertical = (12 * scale).dp
        )
    )
}

// Sidebar composable with real-time reveal (offsetPx in [-sidebarWidthPx, 0]).

@Composable
private fun Sidebar(
    isOpen: Boolean,
    offsetPx: Float,
    onDismiss: () -> Unit,
    onNavigateToPage: (PageType) -> Unit,
    onLogout: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val sidebarWidthDp = (280 * scale).dp
    val sidebarWidthPx = with(density) { sidebarWidthDp.toPx() }

    // Smooth animation of the offset
    val animatedOffsetPx by animateFloatAsState(
        targetValue = offsetPx.coerceIn(-sidebarWidthPx, 0f),
        animationSpec = tween(durationMillis = 220)
    )

    // Overlay alpha depends on how visible the sidebar is (0..0.5)
    val overlayAlpha = ((sidebarWidthPx + animatedOffsetPx) / sidebarWidthPx).coerceIn(0f, 1f) * 0.5f

    // If fully hidden & offset is exactly hidden, we don't display the overlay/card to avoid unnecessary composition
    val fullyHidden = animatedOffsetPx <= -sidebarWidthPx + 1f && !isOpen

    if (!fullyHidden) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            // Background overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )

            // Sidebar card positioned by offset (animatedOffsetPx)
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(sidebarWidthDp)
                    .offset { IntOffset(animatedOffsetPx.roundToInt(), 0) }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* empêche propagation */ }
                    ),
                shape = RoundedCornerShape(topEnd = (16 * scale).dp, bottomEnd = (16 * scale).dp),
                elevation = CardDefaults.cardElevation(defaultElevation = (8 * scale).dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                                )
                            )
                            .padding((20 * scale).dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier
                                        .size((32 * scale).dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Fermer",
                                        tint = Color.White,
                                        modifier = Modifier.size((16 * scale).dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height((8 * scale).dp))

                            Box(
                                modifier = Modifier
                                    .size((60 * scale).dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = (24 * scale).sp)
                            }

                            Spacer(Modifier.height((12 * scale).dp))

                            Text(
                                "Dountchè KONE",
                                fontSize = (18 * scale).sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                "kidt@email.com",
                                fontSize = (12 * scale).sp,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(top = (8 * scale).dp),
                        contentPadding = PaddingValues(bottom = (16 * scale).dp)
                    ) {
                        item { SectionDivider("Navigation", scale) }

                        item {
                            SidebarItem(
                                icon = "🏠",
                                label = "Accueil",
                                onClick = { onNavigateToPage(PageType.ACCUEIL) },
                                scale = scale
                            )
                        }

                        item {
                            SidebarItem(
                                icon = "📊",
                                label = "Tableau de bord",
                                onClick = { onNavigateToPage(PageType.STATISTIQUES) },
                                scale = scale
                            )
                        }

                        item { SectionDivider("Compte", scale) }

                        item {
                            SidebarItem(
                                icon = "👤",
                                label = "Mon Profil",
                                onClick = { onNavigateToPage(PageType.PROFILE) },
                                scale = scale
                            )
                        }

                        item {
                            SidebarItem(
                                icon = "⚙️",
                                label = "Paramètres",
                                onClick = {onNavigateToPage(PageType.PARAMETRE)},
                                scale = scale
                            )
                        }

                        item { SectionDivider("Support", scale) }

                        item {
                            SidebarItem(
                                icon = "📞",
                                label = "Aide & Support",
                                onClick = {onNavigateToPage(PageType.AIDE)},
                                scale = scale
                            )
                        }

                        item {
                            SidebarItem(
                                icon = "ℹ️",
                                label = "À propos",
                                onClick = {onNavigateToPage(PageType.APROPOS)},
                                scale = scale
                            )
                        }

                        item { Spacer(Modifier.height((24 * scale).dp)) }

                        item {
                            SidebarItem(
                                icon = "🚪",
                                label = "Déconnexion",
                                isDanger = true,
                                onClick = onLogout,
                                scale = scale
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------- Main App Screen --------------------

@Composable
fun MainAppScreen(
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()
    val scale = (screenWidthDp / 390f).coerceIn(0.7f, 2.0f)

    // States
    var currentPage by remember { mutableStateOf(PageType.ACCUEIL) }
    var sidebarOpen by remember { mutableStateOf(false) }
    var previousPage by remember { mutableStateOf<PageType?>(null) }

    //etas partagées entres homescreen et coffrescreen
    var soldeCompte by remember { mutableStateOf(125750) }
    val soldeCompteState = remember { mutableStateOf(soldeCompte) }

    // Synchronisation
    LaunchedEffect(soldeCompteState.value) {
        soldeCompte = soldeCompteState.value
    }

    val density = LocalDensity.current
    val screenWidthPx = with(density) { screenWidthDp.dp.toPx() }
    val sidebarWidthPx = with(density) { (280 * scale).dp.toPx() }

    // Slider/gesture thresholds (en px)
    val swipeThresholdPx = screenWidthPx * 0.18f // sensibilité pour changer de page (≈18% de l'écran)
    val sidebarEdgePx = with(density) { 28.dp.toPx() } // zone de départ au bord gauche pour ouvrir sidebar
    val sidebarOpenThresholdPx = screenWidthPx * 0.12f // distance à glisser pour ouvrir la sidebar

    // Sidebar offset state (0 = ouvert, -sidebarWidthPx = caché)
    var sidebarOffsetPx by remember { mutableStateOf(-sidebarWidthPx) }

    // Make sure initial offset is hidden
    LaunchedEffect(sidebarWidthPx) {
        sidebarOffsetPx = -sidebarWidthPx
    }

    //gestion du retour systeme
    BackHandler(enabled = currentPage != PageType.ACCUEIL) {
        when (currentPage) {

            // Pages principales - retour à l'accueil
            PageType.PROFILE, PageType.STATISTIQUES, PageType.COFFRE -> {
                currentPage = PageType.ACCUEIL
            }

            // Pages de profil - retour au profil
            PageType.INFOPERSO, PageType.SECURITE, PageType.PARRAINAGE,
            PageType.AIDE, PageType.APROPOS -> {
                if (previousPage != null) {
                    currentPage = previousPage!!
                    sidebarOpen = true
                    sidebarOffsetPx = 0f
                    previousPage = null
                } else {
                    currentPage = PageType.PROFILE
                }
            }

            // Pages paramètres
            PageType.THEME, PageType.LANGUAGE -> {
                currentPage = PageType.PARAMETRE
            }
            PageType.PARAMETRE -> {
                if (previousPage != null) {
                    currentPage = previousPage!!
                    sidebarOpen = true
                    sidebarOffsetPx = 0f
                    previousPage = null
                } else {
                    currentPage = PageType.ACCUEIL
                }
            }

            // Services - retour à l'accueil
            else -> {
                currentPage = PageType.ACCUEIL
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(currentPage, sidebarOpen, sidebarWidthPx) {
                // variables pour accumuler le drag
                var startX = 0f
                var totalDragX = 0f
                var totalDragY = 0f

                detectDragGestures(
                    onDragStart = { offset ->
                        startX = offset.x
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDrag = { change: PointerInputChange, dragAmount ->
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        // Si on est en train de commencer depuis le bord gauche (ou si la sidebar est déjà ouverte),
                        // on met à jour l'offset pour révéler la sidebar en temps réel
                        if (!sidebarOpen) {
                            if (startX < sidebarEdgePx && totalDragX > 0f) {
                                // révéler progressivement la sidebar
                                val newOffset = (-sidebarWidthPx + totalDragX).coerceIn(-sidebarWidthPx, 0f)
                                sidebarOffsetPx = newOffset
                            }
                        } else {
                            // Si la sidebar est ouverte et on drag vers la gauche, on suit le mouvement pour refermer progressivement
                            if (totalDragX < 0f) {
                                val newOffset = (totalDragX).coerceIn(-sidebarWidthPx, 0f)
                                sidebarOffsetPx = newOffset
                            }
                        }
                    },
                    onDragEnd = {
                        // Décision finale : navigation ou toggle sidebar
                        if (abs(totalDragX) > abs(totalDragY) && abs(totalDragX) > swipeThresholdPx) {
                            // geste horizontal significatif
                            if (!sidebarOpen) {
                                // navigation entre pages
                                if (totalDragX > 0f) {
                                    val previousPage = getPreviousPage(currentPage)
                                    if (previousPage != currentPage) currentPage = previousPage
                                } else {
                                    val nextPage = getNextPage(currentPage)
                                    if (nextPage != currentPage) currentPage = nextPage
                                }
                                // reset sidebar hidden
                                sidebarOpen = false
                                sidebarOffsetPx = -sidebarWidthPx
                            } else {
                                // sidebar ouverte : swipe vers la gauche ferme
                                if (totalDragX < -swipeThresholdPx) {
                                    sidebarOpen = false
                                    sidebarOffsetPx = -sidebarWidthPx
                                } else {
                                    // trop petit, remettre ouverte
                                    sidebarOpen = true
                                    sidebarOffsetPx = 0f
                                }
                            }
                        } else {
                            // Pas une navigation : gérer la sidebar selon la distance drag
                            if (!sidebarOpen) {
                                if (startX < sidebarEdgePx && totalDragX > sidebarOpenThresholdPx) {
                                    // ouvrir la sidebar
                                    sidebarOpen = true
                                    sidebarOffsetPx = 0f
                                } else {
                                    // remettre cachée
                                    sidebarOpen = false
                                    sidebarOffsetPx = -sidebarWidthPx
                                }
                            } else {
                                // Sidebar était ouverte ; si on a dragé suffisamment vers la gauche => fermer
                                if (totalDragX < -sidebarOpenThresholdPx) {
                                    sidebarOpen = false
                                    sidebarOffsetPx = -sidebarWidthPx
                                } else {
                                    sidebarOpen = true
                                    sidebarOffsetPx = 0f
                                }
                            }
                        }

                        // reset totals
                        totalDragX = 0f
                        totalDragY = 0f
                    },
                    onDragCancel = {
                        // revenir à l'état logique
                        if (sidebarOpen) sidebarOffsetPx = 0f else sidebarOffsetPx = -sidebarWidthPx
                    }
                )
            }
    ) {
        // Application principale
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            if (currentPage in setOf( PageType.ACCUEIL, PageType.COFFRE, PageType.STATISTIQUES, PageType.PROFILE)) {
                AppHeader(
                    currentPage = currentPage,
                    onMenuClick = {
                        sidebarOpen = true
                        sidebarOffsetPx = 0f
                    }
                )
            }

            // Contenu principal
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        val pages = getPageOrder()
                        val targetIndex = pages.indexOf(targetState)
                        val initialIndex = pages.indexOf(initialState)

                        if (targetIndex > initialIndex) {
                            slideInHorizontally(
                                initialOffsetX = { width -> width },
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { width -> -width },
                                        animationSpec = tween(400)
                                    ) + fadeOut(animationSpec = tween(400))
                        } else {
                            slideInHorizontally(
                                initialOffsetX = { width -> -width },
                                animationSpec = tween(400)
                            ) + fadeIn(animationSpec = tween(400)) togetherWith
                                    slideOutHorizontally(
                                        targetOffsetX = { width -> width },
                                        animationSpec = tween(400)
                                    ) + fadeOut(animationSpec = tween(400))
                        }
                    },
                    label = "Page transition with swipe direction"
                ) { page ->
                    when (page) {
                        PageType.ACCUEIL -> PageAccueil(
                            soldeCompteState = soldeCompteState,
                            onServiceClick = { service -> service.page?.let { page -> currentPage = page } },
                            onProfileClick = { currentPage = PageType.PROFILE },
                            onSeeAllTransactionsClick = { currentPage = PageType.HISTORIQUE },
                            onInviteClick = {currentPage = PageType.PARRAINAGE},
                        )

                        PageType.PROFILE -> PageProfile(
                            onStatistiquesClick = { currentPage = PageType.STATISTIQUES},
                            onRechargesClick = { currentPage = PageType.RECHARGE},
                            onSupportClick = {currentPage = PageType.AIDE},
                            onLogoutClick = onLogout,
                            onBackPressed = {currentPage = PageType.ACCUEIL},
                            onMenuOptionClick = { option ->
                                option.page?.let { page -> currentPage = page }
                            }
                        )
                        PageType.STATISTIQUES -> PageStatistiques(
                            onBackPressed = {currentPage = PageType.ACCUEIL}
                        )
                        PageType.COFFRE -> PageCoffre(
                            soldeCompteExternal = soldeCompteState,
                            onBackPressed = {currentPage = PageType.ACCUEIL}
                        )
                        PageType.RECHARGE -> RechargeScreen(
                            onBackPressed = {currentPage = PageType.ACCUEIL}
                        )
                        PageType.TRANSFERT -> TransferScreen(
                            onBackPressed = { currentPage = PageType.ACCUEIL }
                        )
                        PageType.HISTORIQUE -> TransactionsScreen(
                            onBackPressed = {currentPage = PageType.ACCUEIL}
                        )
                        PageType.ABONNEMENT -> SubscribeScreen(
                            onBackPressed = {currentPage = PageType.ACCUEIL }
                        )

                        PageType.FACTURE -> BillsScreen(
                            onBackPressed = {currentPage = PageType.ACCUEIL }
                        )

                        PageType.INFOPERSO -> PageInformationsPersonnelles(
                            onBackClick  = {currentPage = PageType.PROFILE }
                        )
                        PageType.SECURITE -> PageSecurite(
                            onBackClick  = {currentPage = PageType.PROFILE }
                        )
                        PageType.PARRAINAGE -> PageParrainage(
                            onBackClick  = {currentPage = PageType.PROFILE }
                        )
                        PageType.AIDE -> PageAide(
                            onBackClick  = {
                                if (previousPage != null) {
                                    currentPage = previousPage!!
                                    sidebarOpen = true
                                    sidebarOffsetPx = 0f
                                    previousPage = null
                                } else {
                                    currentPage = PageType.PROFILE
                                }
                            }
                        )
                        PageType.APROPOS -> PageAPropos(
                            onBackClick  = {
                                if (previousPage != null) {
                                    currentPage = previousPage!!
                                    sidebarOpen = true
                                    sidebarOffsetPx = 0f
                                    previousPage = null
                                } else {
                                    currentPage = PageType.PROFILE
                                }
                            }
                        )

                        PageType.PARAMETRE ->  PageParametres(
                            onBackClick = {
                                if (previousPage != null) {
                                    currentPage = previousPage!!
                                    sidebarOpen = true
                                    sidebarOffsetPx = 0f
                                    previousPage = null
                                } else {
                                    currentPage = PageType.ACCUEIL
                                }
                            },
                            onThemeClick = {currentPage = PageType.THEME},
                            onLanguageClick = {currentPage = PageType.LANGUAGE}
                        )

                        PageType.THEME -> PageTheme(
                            onBackClick = {currentPage = PageType.PARAMETRE}
                        )

                        PageType.LANGUAGE -> PageLangue(
                            onBackClick = {currentPage = PageType.PARAMETRE}
                        )

                    }
                }
            }

            // Footer
            if (currentPage in setOf( PageType.ACCUEIL, PageType.COFFRE, PageType.STATISTIQUES, PageType.PROFILE)) {
                AppFooter(
                    currentPage = currentPage,
                    onPageSelected = { page -> currentPage = page }
                )
            }
        }

        // Sidebar (au-dessus de everything)
        Sidebar(
            isOpen = sidebarOpen,
            offsetPx = sidebarOffsetPx,
            onDismiss = {
                sidebarOpen = false
                sidebarOffsetPx = -sidebarWidthPx
            },
            onNavigateToPage = { page ->
                //sauver la page prinipale actuelle
                if (currentPage in setOf(PageType.ACCUEIL, PageType.STATISTIQUES, PageType.COFFRE, PageType.PROFILE)) {
                    previousPage = currentPage
                }
                currentPage = page
                sidebarOpen = false
                sidebarOffsetPx = -sidebarWidthPx
            },
            onLogout = {
                sidebarOpen = false
                sidebarOffsetPx = -sidebarWidthPx
                onLogout()
            },
            scale = scale,
            modifier = Modifier.zIndex(10f)
        )
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true, heightDp = 800)
@Composable
fun MainAppScreenPreview() {
    HifadihTheme {
        MainAppScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Avec Sidebar")
@Composable
fun MainAppScreenWithSidebarPreview() {
    HifadihTheme {
        var sidebarVisible by remember { mutableStateOf(true) }

        Box(modifier = Modifier.fillMaxSize()) {
            MainAppScreen()

            if (sidebarVisible) {
                Sidebar(
                    isOpen = true,
                    offsetPx = 0f,
                    onDismiss = { sidebarVisible = false },
                    onNavigateToPage = { },
                    onLogout = { },
                    scale = 1f,
                    modifier = Modifier.zIndex(20f)
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 800, name = "Navigation par gestes")
@Composable
fun MainAppScreenSwipePreview() {
    HifadihTheme {
        var currentPage by remember { mutableStateOf(PageType.STATISTIQUES) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF2196F3).copy(alpha = 0.1f)
            ) {
                Text(
                    "👈 Glissez pour naviguer 👉 | Bord gauche pour menu ☰",
                    fontSize = 12.sp,
                    color = Color(0xFF2196F3),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }

            MainAppScreen()
        }
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 320)
@Composable
fun MainAppScreenSmallScreenPreview() {
    HifadihTheme {
        MainAppScreen()
    }
}

@Preview(showBackground = true, heightDp = 800, widthDp = 480)
@Composable
fun MainAppScreenLargeScreenPreview() {
    HifadihTheme {
        MainAppScreen()
    }
}

