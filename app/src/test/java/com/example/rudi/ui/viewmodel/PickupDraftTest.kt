package com.example.rudi.ui.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class PickupDraftTest {
    private val now = LocalDateTime.of(2026, 11, 18, 9, 0)
    private val valid = PickupDraft(address = " Test address ", date = "2026-11-19")

    @Test fun rejectsBlankAddressAndInvalidQuantity() {
        for (quantity in listOf("", "0", "100", "abc")) {
            val errors = valid.copy(address = " ", quantity = quantity).errors(now)
            assertTrue("quantity" in errors)
            assertTrue("address" in errors)
        }
    }

    @Test fun rejectsPastOrInvalidPickupSlot() {
        assertTrue("date" in valid.copy(date = "2026-11-18").errors(now))
        assertTrue("date" in valid.copy(date = "2026-11-17").errors(now))
        assertTrue(valid.copy(date = "2026-11-18", slot = "14:00-16:00").errors(now).isEmpty())
        assertNull(pickupWindow("2026-02-30", "09:00-11:00"))
        assertNull(pickupWindow("2026-11-19", "09:00-08:00"))
    }

    @Test fun editKeepsRoomIdentityAndOriginalTimestamp() {
        val request = valid.copy(quantity = " 2 ", notes = " Old laptop ").toRequest(42L, 100L, now)
        assertEquals(42L, request.id)
        assertEquals(100L, request.createdAt)
        assertEquals(2, request.quantity)
        assertEquals("Test address", request.address)
        assertEquals("Old laptop", request.notes)
        assertFalse("date" in valid.errors(now))
    }
}
