package com.github.redborsch.browserpicker.shared.repository

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsEntryTest {

    @Test(expected = IllegalArgumentException::class)
    fun `cannot supply negative order`() {
        createTestEntry(order = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `cannot use order bigger than MAX_INT - 1`() {
        createTestEntry(order = Int.MAX_VALUE)
    }

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

    /**
     * Make sure that we don't miss tests for any additional data.
     */
    @Test
    fun `serialization format is correct`() {
        val visibleEntry = createTestEntry(visible = true)
        val invisibleEntry = createTestEntry(visible = false)
        val visibleEntryWith0Order = createTestEntry(visible = true, order = 0)
        val invisibleEntryWith0Order = createTestEntry(visible = false, order = 0)
        val maxOrder = createTestEntry(visible = false, order = Int.MAX_VALUE - 1)
        assertEquals(
            "some.test.package|1235",
            visibleEntry.serialize(),
        )
        assertEquals(
            "some.test.package|-1235",
            invisibleEntry.serialize(),
        )
        assertEquals(
            "some.test.package|1",
            visibleEntryWith0Order.serialize(),
        )
        assertEquals(
            "some.test.package|-1",
            invisibleEntryWith0Order.serialize(),
        )
        assertEquals(
            "some.test.package|-2147483647",
            maxOrder.serialize(),
        )
    }

    @Test
    fun `deserialization is correct`() {
        val visibleEntry = createTestEntry(visible = true)
        val invisibleEntry = createTestEntry(visible = false)
        val visibleEntryWith0Order = createTestEntry(visible = true, order = 0)
        val invisibleEntryWith0Order = createTestEntry(visible = false, order = 0)
        val maxOrder = createTestEntry(visible = false, order = Int.MAX_VALUE - 1)
        val minimalPackage = SettingsEntry("a", true, 0)
        assertEquals(
            visibleEntry,
            SettingsEntry.deserialize("some.test.package|1235"),
        )
        assertEquals(
            invisibleEntry,
            SettingsEntry.deserialize("some.test.package|-1235"),
        )
        assertEquals(
            visibleEntryWith0Order,
            SettingsEntry.deserialize("some.test.package|1"),
        )
        assertEquals(
            invisibleEntryWith0Order,
            SettingsEntry.deserialize("some.test.package|-1"),
        )
        assertEquals(
            maxOrder,
            SettingsEntry.deserialize("some.test.package|-2147483647"),
        )
        assertEquals(
            minimalPackage,
            SettingsEntry.deserialize("a|1"),
        )
    }

    @Test
    fun `handles gracefully incorrect formats`() {
        // No package separator
        assertNull(SettingsEntry.deserialize("test.package"))
        // Empty package
        assertNull(SettingsEntry.deserialize("|1234"))
        // Blank package
        assertNull(SettingsEntry.deserialize("   |1234"))
        // Serialized order is messed up
        assertEquals(
            SettingsEntry("dummy", true, 0),
            SettingsEntry.deserialize("dummy|some-broken-number")
        )
        // Multiple package separators - essentially the same as above
        assertEquals(
            SettingsEntry("dummy", true, 0),
            SettingsEntry.deserialize("dummy|another|1234")
        )
    }

    private fun createTestEntry(visible: Boolean = true, order: Int = 1234) = SettingsEntry(
        "some.test.package",
        visible,
        order,
    )

    private val gson = Gson()

    /**
     * We could also change [SettingsEntry] to a data class, but for production it has no benefit.
     */
    fun assertEquals(expected: SettingsEntry?, actual: SettingsEntry?) {
        val jsonExpected = gson.toJson(expected)
        val jsonActual = gson.toJson(actual)
        assertEquals(jsonExpected, jsonActual)
    }
}
