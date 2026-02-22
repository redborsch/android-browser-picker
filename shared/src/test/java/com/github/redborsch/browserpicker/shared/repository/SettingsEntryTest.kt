package com.github.redborsch.browserpicker.shared.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.*
import org.junit.Test

class SettingsEntryTest {

    @Test
    fun `serialized data can be deserialized`() {
        val testEntry = createTestEntry()
        val deserialized = SettingsEntry.deserialize(testEntry.serialize())
        assertEquals(testEntry, deserialized)
    }

    @Test
    fun `2 serialized entries with the same data are equal`() {
        val testEntry = createTestEntry()
        val testEntry2 = createTestEntry()
        assertEquals(
            testEntry.serialize(),
            testEntry2.serialize()
        )
    }

    @Test
    fun `deserialization is future proof`() {
        // Add some extra data which is not used currently to the serialized entry and make sure it
        // still can be deserialized to the same entry
        val testEntry = createTestEntry()
        val serialized = testEntry.serialize() + ",should,be,ignored"
        val deserialized = SettingsEntry.deserialize(serialized)
        assertEquals(testEntry, deserialized)
    }

    /**
     * Make sure that we don't miss tests for any additional data.
     */
    @Test
    fun `serialization format is correct`() {
        val testEntry = createTestEntry()
        assertEquals(
            "some.test.package|1,1234",
            testEntry.serialize(),
        )
    }

    @Test
    fun `handles gracefully incorrect visibility`() {
        val expected = SettingsEntry(
            "test.package",
            false,
            1234,
        )
        val deserialized = SettingsEntry.deserialize("test.package|some-incorrect-visibility-value,1234")
        assertEquals(expected, deserialized)
    }

    private fun createTestEntry() = SettingsEntry(
        "some.test.package",
        true,
        1234,
    )

    private val gson = Gson()

    /**
     * We could also change [SettingsEntry] to a data class, but for production it has no benefit.
     */
    fun assertEquals(expected: SettingsEntry, actual: SettingsEntry) {
        val jsonExpected = gson.toJson(expected)
        val jsonActual = gson.toJson(actual)
        assertEquals(jsonExpected, jsonActual)
    }
}
