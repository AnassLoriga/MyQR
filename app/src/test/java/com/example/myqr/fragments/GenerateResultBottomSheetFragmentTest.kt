package com.example.myqr.fragments

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GenerateResultBottomSheetFragmentTest {

    private lateinit var fragment: GenerateResultBottomSheetFragment

    @Before
    fun setUp() {
        // Initialisation de l'instance du fragment pour accéder à la méthode
        fragment = GenerateResultBottomSheetFragment()
    }

    @Test
    fun `parseWiFiDetails with valid Wi-Fi QR code`() {
        val input = "WIFI:S:MyNetwork;P:MyPassword;T:WPA;"
        val expected = mapOf(
            "S" to "MyNetwork",
            "P" to "MyPassword",
            "T" to "WPA"
        )

        val result = fragment.parseWiFiDetails(input)
        assertEquals(expected.size, result.size)
    }

    @Test
    fun `parseWiFiDetails with missing password`() {
        val input = "WIFI:S:NetworkWithoutPassword;T:WPA;"
        val expected = mapOf(
            "S" to "NetworkWithoutPassword",
            "T" to "WPA"
        )

        val result = fragment.parseWiFiDetails(input)
        assertEquals(expected, result)
    }

    @Test
    fun `parseWiFiDetails with invalid format`() {
        val input = "INVALID:DATA:NOT:WIFI;"
        val expected = emptyMap<String, String>()

        val result = fragment.parseWiFiDetails(input)
        assertEquals(expected.size, result.size)
    }

    @Test
    fun `parseWiFiDetails with empty string`() {
        val input = ""
        val expected = emptyMap<String, String>()

        val result = fragment.parseWiFiDetails(input)
        assertEquals(expected.size, result.size)
    }

    @Test
    fun `parseWiFiDetails with no key-value pairs`() {
        val input = "WIFI:ONLYSSID;"
        val expected = emptyMap<String, String>()

        val result = fragment.parseWiFiDetails(input)
        assertEquals(expected, result)
    }
}
