package com.example.sololeveling.core

import com.example.sololeveling.domain.PlayerProfile
import com.example.sololeveling.domain.Stat
import com.example.sololeveling.domain.StatType

object GameManager {
    lateinit var player: PlayerProfile
    var hasMuscleUnlockBeenAnnounced: Boolean = false

    fun initializePlayer(name: String) {
        val baseStats = listOf(
            Stat(type = StatType.STRENGTH),
            Stat(type = StatType.ENDURANCE),
            Stat(type = StatType.STAMINA),
            Stat(type = StatType.DISCIPLINE)
        )

        player = PlayerProfile(name = name, stats = baseStats)
        hasMuscleUnlockBeenAnnounced = false
    }
}
