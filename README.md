
# Hifadih Mobile Banking

**Application mobile de bancarisation digitale – Frontend Android (Kotlin & Jetpack Compose)**

---

## Description du projet

Hifadih Mobile Banking est une application Android visant à faciliter la bancarisation digitale en Côte d’Ivoire.
Elle offre aux utilisateurs une interface simple, intuitive et sécurisée pour :

* gérer un compte digital,
* effectuer des transferts,
* consulter l’historique,
* gérer une épargne (coffre),
* utiliser l’authentification biométrique.

Ce dépôt contient **la partie frontend**, développée en **Kotlin** avec **Jetpack Compose**.

## Objectifs du projet

* Proposer une solution locale de bancarisation digitale.
* Offrir une interface moderne, fluide et accessible.
* Intégrer la sécurité biométrique native.
* Faciliter l’intégration future d’un backend bancaire ou d’API financières.

## Fonctionnalités principales

**Connexion & Authentification biométrique** (Fingerprint / Face Unlock).
* **Tableau de bord financier**.
* **Transfert local / Simulation de transaction**.
* **Module d’épargne (coffre)**.
* **Historique des opérations**.
* **Gestion du profil**.
* **UI moderne Jetpack Compose + Material3**.

> **Note :**
> Cette version est une **simulation fonctionnelle (sans backend)**.
> Les données affichées sont mockées en attendant l’intégration des API réelles.

## Stack Technique

### **Langage**

* **Kotlin** (100% natif Android)

### **Framework UI**

* **Jetpack Compose**
* **Material 3 (M3)**

### **Outils Android**

* AndroidX
* BiometricPrompt API
* Navigation Compose

### **Architecture**

* MVVM (simple)
* State management Jetpack Compose
* ViewModel + MutableState

## Installation & Lancement

### **1. Cloner le projet**

git clone https://github.com/Dountche/Hifadih.git

### **2. Ouvrir avec Android Studio**

* File → Open → sélectionner le dossier du projet

### **3. Lancer l'application**

* Choisir un émulateur
* Cliquer sur **Run ▶️**

## Tests / Validation

* Tests UI manuels réalisés via Android Studio Emulator
* Scénarios testés : connexion, épargne, transferts, navigation

## Limitations actuelles

* Aucun backend connecté (simulation des données)
* Quelques dépendances Material3 encore instables
* Aucune maquette UI fournie, design construit manuellement
* Responsive géré entièrement à la main

## Perspectives d’évolution

* Intégration complet du backend (API REST sécurisée)
* Ajout d’un espace marchand (paiement dans les boutiques)
* Recharge de crédit téléphonique
* Portage multiplateforme (iOS / Web)
* Intégration Open Banking
* Tableau de bord intelligent (IA / scoring simple)

##Auteur

**KONE Dountche Issa**
Développeur fullstack web et mobile
