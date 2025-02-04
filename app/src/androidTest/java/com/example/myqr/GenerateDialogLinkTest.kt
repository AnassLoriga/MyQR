package com.example.myqr

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myqr.Activitys.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenerateDialogLinkTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testGenerateQRCode() {
        // Ouvrir le DialogFragment (adapte selon ton implémentation)
        onView(withId(R.id.link)).perform(click()) // Bouton pour ouvrir le dialog

        // Vérifier que le champ de texte est visible
        onView(withId(R.id.editText)).check(matches(isDisplayed()))

        // Entrer un lien dans le champ de texte
        onView(withId(R.id.editText)).perform(typeText("https://example.com"), closeSoftKeyboard())

        // Cliquer sur le bouton de génération du QR Code
        onView(withId(R.id.btnGenerateQR)).perform(click())

        // Vérifier que l'image du QR Code est visible
        onView(withId(R.id.imageViewQR)).check(matches(isDisplayed()))

        // Vérifier que les boutons Enregistrer et Partager sont visibles
        onView(withId(R.id.btnEnregistrer)).check(matches(isDisplayed()))
        onView(withId(R.id.btnPartager)).check(matches(isDisplayed()))
    }
}
