package com.ergin.ghclient.feature.issues.impl.data.mapper

import com.ergin.ghclient.core.database.entity.IssueDraftEntity
import com.ergin.ghclient.feature.issues.domain.model.DraftPriority
import com.ergin.ghclient.feature.issues.domain.model.IssueDraft
import org.junit.Assert.assertEquals
import org.junit.Test

class IssueDraftEntityMapperTest {

    @Test
    fun toEntity_mapsAllPrioritiesToIntegersCorrectly() {
        val low = IssueDraft("1", "o", "r", "T", "B", DraftPriority.LOW, 10L).toEntity()
        val normal = IssueDraft("2", "o", "r", "T", "B", DraftPriority.NORMAL, 20L).toEntity()
        val critical = IssueDraft("3", "o", "r", "T", "B", DraftPriority.CRITICAL, 30L).toEntity()

        assertEquals(0, low.priority)
        assertEquals(1, normal.priority)
        assertEquals(2, critical.priority)
    }

    @Test
    fun toDomain_mapsIntegersAndFallbackCorrectly() {
        val low = IssueDraftEntity("1", "o", "r", "T", "B", 0, 10L).toDomain()
        val normal = IssueDraftEntity("2", "o", "r", "T", "B", 1, 20L).toDomain()
        val critical = IssueDraftEntity("3", "o", "r", "T", "B", 2, 30L).toDomain()
        val unknown = IssueDraftEntity("4", "o", "r", "T", "B", 99, 40L).toDomain()

        assertEquals(DraftPriority.LOW, low.priority)
        assertEquals(DraftPriority.NORMAL, normal.priority)
        assertEquals(DraftPriority.CRITICAL, critical.priority)
        assertEquals(DraftPriority.NORMAL, unknown.priority)
    }
}
