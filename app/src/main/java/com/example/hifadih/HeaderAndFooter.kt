package com.example.hifadih

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hifadih.ui.theme.HifadihTheme

// -------------------- Header Composant --------------------

// Composant Header avec couleur dynamique selon la page

@Composable
fun AppHeader(
    currentPage: PageType = PageType.ACCUEIL,
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // Couleur du header selon la page
    val headerColor = when (currentPage) {
        PageType.ACCUEIL -> Color(0xFF2196F3)
        PageType.PROFILE -> Color(0xFF9C27B0)
        PageType.STATISTIQUES -> Color(0xFF4CAF50)
        PageType.COFFRE -> Color(0xFFFF9800)
        else -> Color.Gray
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height((56 * scale).dp),
        color = headerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = (16 * scale).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size((48 * scale).dp)
            ) {
                Text(
                    "☰",
                    fontSize = (20 * scale).sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.weight(1f))

            // Titre de l'app
            Text(
                "Hifadih",
                fontSize = (20 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.weight(1f))


            Spacer(Modifier.width((48 * scale).dp))
        }
    }
}

// -------------------- Footer Composant --------------------

//Item du footer de navigation

@Composable
private fun FooterItem(
    icon: String,
    label: String,
    page: PageType,
    currentPage: PageType,
    onPageSelected: (PageType) -> Unit,
    footerColor: Color,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val isSelected = currentPage == page

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = false,
                    radius = (24 * scale).dp,
                    color = Color.White
                ),
                onClick = { onPageSelected(page) }
            )
            .padding((8 * scale).dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = (20 * scale).sp,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
        )

        Text(
            text = label,
            fontSize = (11 * scale).sp,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}


//Composant Footer avec couleur dynamique et navigation

@Composable
fun AppFooter(
    currentPage: PageType = PageType.ACCUEIL,
    onPageSelected: (PageType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Responsive
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val scale = (screenWidth / 390f).coerceIn(0.7f, 2.0f)

    // Couleur du footer selon la page
    val footerColor = when (currentPage) {
        PageType.ACCUEIL -> Color(0xFF2196F3)
        PageType.PROFILE -> Color(0xFF9C27B0)
        PageType.STATISTIQUES -> Color(0xFF4CAF50)
        PageType.COFFRE -> Color(0xFFFF9800)
        else -> Color.Gray
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = footerColor,
        shape = RoundedCornerShape(
            topStart = (25 * scale).dp,
            topEnd = (25 * scale).dp
        ),
        tonalElevation = (8 * scale).dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((68 * scale).dp)
                .padding(horizontal = (8 * scale).dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterItem(
                icon = "🏠",
                label = "Accueil",
                page = PageType.ACCUEIL,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                footerColor = footerColor,
                scale = scale,
                modifier = Modifier.weight(1f)
            )

            FooterItem(
                icon = "📊",
                label = "Stats",
                page = PageType.STATISTIQUES,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                footerColor = footerColor,
                scale = scale,
                modifier = Modifier.weight(1f)
            )

            FooterItem(
                icon = "💰",
                label = "Coffre",
                page = PageType.COFFRE,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                footerColor = footerColor,
                scale = scale,
                modifier = Modifier.weight(1f)
            )

            FooterItem(
                icon = "👤",
                label = "Profil",
                page = PageType.PROFILE,
                currentPage = currentPage,
                onPageSelected = onPageSelected,
                footerColor = footerColor,
                scale = scale,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// -------------------- Previews --------------------

@Preview(showBackground = true)
@Composable
fun AppHeaderAccueilPreview() {
    HifadihTheme {
        AppHeader(currentPage = PageType.ACCUEIL)
    }
}

@Preview(showBackground = true)
@Composable
fun AppHeaderProfilePreview() {
    HifadihTheme {
        AppHeader(currentPage = PageType.PROFILE)
    }
}

@Preview(showBackground = true)
@Composable
fun AppHeaderStatistiquesPreview() {
    HifadihTheme {
        AppHeader(currentPage = PageType.STATISTIQUES)
    }
}

@Preview(showBackground = true)
@Composable
fun AppHeaderCoffrePreview() {
    HifadihTheme {
        AppHeader(currentPage = PageType.COFFRE)
    }
}

@Preview(showBackground = true)
@Composable
fun AppFooterAccueilPreview() {
    HifadihTheme {
        AppFooter(currentPage = PageType.ACCUEIL)
    }
}

@Preview(showBackground = true)
@Composable
fun AppFooterProfilePreview() {
    HifadihTheme {
        AppFooter(currentPage = PageType.PROFILE)
    }
}

@Preview(showBackground = true)
@Composable
fun AppFooterStatistiquesPreview() {
    HifadihTheme {
        AppFooter(currentPage = PageType.STATISTIQUES)
    }
}

@Preview(showBackground = true)
@Composable
fun AppFooterCoffrePreview() {
    HifadihTheme {
        AppFooter(currentPage = PageType.COFFRE)
    }
}
