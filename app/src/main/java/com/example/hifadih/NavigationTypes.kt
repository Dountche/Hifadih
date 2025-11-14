package com.example.hifadih

// -------------------- Types de navigation --------------------

enum class PageType {
    ACCUEIL,
    PROFILE,
    STATISTIQUES,
    COFFRE,
    TRANSFERT,
    RECHARGE,
    ABONNEMENT,
    FACTURE,
    HISTORIQUE,
    INFOPERSO,
    SECURITE,
    PARRAINAGE,
    AIDE,
    APROPOS,
    PARAMETRE,
    THEME,
    LANGUAGE

}


data class NavigationState(
    val currentPage: PageType = PageType.ACCUEIL,
    val sidebarVisible: Boolean = false
)


data class Transaction(
    val id: String,
    val type: TransactionType,
    val nom: String,
    val montant: Int, // en FCFA
    val date: String,
    val statut: StatutTransaction,
    val icone: String,
    val couleur: String
)

enum class TransactionType {
    ENVOI, RECEPTION, RETRAIT, DEPOT, ACHAT
}

enum class StatutTransaction {
    REUSSIE, EN_COURS, ECHOUEE
}


data class ObjectifEpargne(
    val id: String,
    val nom: String,
    val montantCible: Int,
    val montantActuel: Int,
    val dateCreation: String,
    val dateEcheance: String,
    val couleur: String,
    val icone: String
)

data class UserInfo(
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val numeroClient: String,
    val niveauVip: NiveauVip,
    val photoUrl: String? = null
)

enum class NiveauVip {
    Bronze, Silver, Gold
}

data class MenuOption(
    val id: String,
    val icone: String,
    val titre: String,
    val subtitle: String? = null,
    val couleur: String,
    val badge: String? = null,
    val hasArrow: Boolean = true,
    val page: PageType? = null
)

data class BankService(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val page: PageType? = null
)

data class StatCard(
    val titre: String,
    val valeur: String,
    val evolution: String,
    val couleur: String,
    val icone: String,
    val pourcentage: Int
)

data class DonneesMensuelles(
    val mois: String,
    val revenus: Int,
    val depenses: Int,
    val epargne: Int
)

data class CategorieDepense(
    val nom: String,
    val montant: Int,
    val couleur: String,
    val icone: String
)