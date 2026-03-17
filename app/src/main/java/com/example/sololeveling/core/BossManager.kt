package com.example.sololeveling.core

import com.example.sololeveling.data.local.AppDatabase
import com.example.sololeveling.data.local.entity.BossEntity
import com.example.sololeveling.data.local.entity.InventoryEntity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

const val WORKOUTS_PER_BOSS = 1
const val BOSS_XP_MULTIPLIER = 1.5
private const val RUNE_TYPE = "RUNE_STONE"

object BossManager {

    suspend fun deleteExpiredItems(database: AppDatabase, today: String = LocalDate.now().toString()) {
        database.inventoryDao().deleteExpired(today)
        database.bossDao().deleteExpired(today)
    }

    suspend fun maybeGiveRuneStone(database: AppDatabase, totalWorkouts: Int) {
        if (totalWorkouts <= 0) return

        val playerDao = database.playerDao()
        val player = playerDao.getPlayer() ?: return

        val currentMilestone = totalWorkouts / WORKOUTS_PER_BOSS
        val lastMilestone = player.lastBossRewardWorkoutCount / WORKOUTS_PER_BOSS

        if (currentMilestone > lastMilestone) {
            val inventoryDao = database.inventoryDao()

            if (inventoryDao.getItem(RUNE_TYPE) == null) {
                val today = currentDate()
                val expiry = addDays(today, 4)

                inventoryDao.insert(
                    InventoryEntity(
                        type = RUNE_TYPE,
                        createdDate = today,
                        expiryDate = expiry
                    )
                )
            }

            playerDao.updatePlayer(
                player.copy(lastBossRewardWorkoutCount = totalWorkouts)
            )
        }
    }

    fun currentDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun addDays(date: String, days: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(date)!!
        cal.add(Calendar.DAY_OF_YEAR, days)
        return sdf.format(cal.time)
    }

    suspend fun generateBoss(database: AppDatabase): BossEntity? {
        val today = LocalDate.now()
        val active = database.bossDao().getActiveBoss()
        if (active != null) return active

        val prExercises = database.exercisePrDao().getAll()
        val candidates = mutableListOf<BossCandidate>()

        prExercises.forEach { pr ->
            val exercise = database.exerciseDao().getById(pr.exerciseId) ?: return@forEach
            candidates.add(
                BossCandidate(
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    type = resolveType(exercise),
                    requiredWeight = pr.prWeight,
                    requiredReps = pr.prReps,
                    requiredMinutes = null
                )
            )
        }

        val allExercises = database.exerciseDao().getAll().filter { it.isTimeBased }
        allExercises.forEach { exercise ->
            val bestMinutes = database.workoutSetDao().getBestMinutesForExercise(exercise.id) ?: return@forEach
            if (bestMinutes > 0) {
                candidates.add(
                    BossCandidate(
                        exerciseId = exercise.id,
                        exerciseName = exercise.name,
                        type = resolveType(exercise),
                        requiredWeight = null,
                        requiredReps = null,
                        requiredMinutes = bestMinutes
                    )
                )
            }
        }

        if (candidates.isEmpty()) return null

        val level = GameManager.player.overallLevel()
        val weightMultiplier = when {
            level < 15 -> 0.75
            level < 30 -> 0.80
            else -> 0.85
        }

        val extraReps = when {
            level < 15 -> 2
            level < 30 -> 3
            else -> 4
        }

        val candidate = candidates.random()
        val requiredMinutes = candidate.requiredMinutes?.let {
            val extra = when {
                level < 15 -> Random.nextInt(2, 4)
                level < 30 -> Random.nextInt(3, 6)
                else -> Random.nextInt(4, 7)
            }
            it + extra
        }

        val boss = BossEntity(
            exerciseId = candidate.exerciseId,
            exerciseName = candidate.exerciseName,
            bossName = generateBossName(candidate.type),
            requiredWeight = candidate.requiredWeight?.times(weightMultiplier),
            requiredReps = candidate.requiredReps?.plus(extraReps),
            requiredMinutes = requiredMinutes,
            createdDate = today.toString(),
            expiryDate = today.plusDays(4).toString()
        )

        val id = database.bossDao().insert(boss).toInt()
        return boss.copy(id = id)
    }

    fun generateBossName(type: String): String {
        val options = when (type) {
            "Strength" -> listOf("Iron Sentinel", "Steel Titan")
            "Endurance" -> listOf("Endless Guardian", "Rep Devourer")
            else -> listOf("Shadow Runner", "Wind Reaper")
        }
        return options.random()
    }

    private fun resolveType(exercise: com.example.sololeveling.data.local.entity.ExerciseEntity): String {
        if (exercise.isTimeBased) return "Stamina"
        return when (maxOf(exercise.strengthMultiplier, exercise.enduranceMultiplier, exercise.staminaMultiplier)) {
            exercise.strengthMultiplier -> "Strength"
            exercise.enduranceMultiplier -> "Endurance"
            else -> "Stamina"
        }
    }

    private data class BossCandidate(
        val exerciseId: Int,
        val exerciseName: String,
        val type: String,
        val requiredWeight: Double?,
        val requiredReps: Int?,
        val requiredMinutes: Double?
    )
}
