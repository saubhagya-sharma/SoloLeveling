package com.example.sololeveling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sololeveling.core.BOSS_XP_MULTIPLIER
import com.example.sololeveling.core.DisciplineManager
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.BossEntity
import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.ExercisePrEntity
import com.example.sololeveling.data.local.entity.MuscleStatEntity
import com.example.sololeveling.data.local.entity.StatEntity
import com.example.sololeveling.data.local.entity.TrophyEntity
import com.example.sololeveling.data.local.entity.WorkoutSetEntity
import com.example.sololeveling.domain.StatType
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ExerciseWorkoutActivity : AppCompatActivity() {

    private lateinit var exerciseNameTextView: TextView
    private lateinit var setsRecyclerView: RecyclerView
    private lateinit var currentPrTextView: TextView
    private lateinit var addSetButton: MaterialButton
    private lateinit var finishExerciseButton: MaterialButton

    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val adapter = WorkoutSetAdapter(::onSetTapped)

    private var workoutExerciseId: Int = INVALID_ID
    private var exerciseEntity: ExerciseEntity? = null
    private val weightFormatter = DecimalFormat("0.##")
    private var bossId: Int = INVALID_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_workout)

        workoutExerciseId = intent.getIntExtra(EXTRA_WORKOUT_EXERCISE_ID, INVALID_ID)
        bossId = intent.getIntExtra(EXTRA_BOSS_ID, INVALID_ID)
        if (workoutExerciseId == INVALID_ID) {
            finish()
            return
        }

        exerciseNameTextView = findViewById(R.id.text_exercise_name)
        setsRecyclerView = findViewById(R.id.recycler_sets)
        currentPrTextView = findViewById(R.id.text_current_pr)
        addSetButton = findViewById(R.id.button_add_set)
        finishExerciseButton = findViewById(R.id.button_finish_exercise)

        setsRecyclerView.layoutManager = LinearLayoutManager(this)
        setsRecyclerView.adapter = adapter

        addSetButton.setOnClickListener {
            lifecycleScope.launch {
                val mostRecentSet = database.workoutSetDao()
                    .getMostRecentByWorkoutExerciseId(workoutExerciseId)
                showSetDialog(editSet = null, prefillSet = mostRecentSet)
            }
        }

        finishExerciseButton.setOnClickListener {
            if (adapter.itemCount == 0) {
                Toast.makeText(
                    this,
                    "Add at least one set before finishing.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val exercise = exerciseEntity
                    ?: database.workoutExerciseDao().getExerciseByWorkoutExerciseId(workoutExerciseId)
                    ?: return@launch
                val sets = database.workoutSetDao().getByWorkoutExerciseId(workoutExerciseId)
                applyXpForFinishedExercise(sets, exercise)
            }
        }

        loadExercise()
        loadSets()
    }

    private fun loadExercise() {
        lifecycleScope.launch {
            exerciseEntity = database.workoutExerciseDao().getExerciseByWorkoutExerciseId(workoutExerciseId)
            exerciseNameTextView.text = exerciseEntity?.name ?: getString(R.string.exercise_unknown)

            val exerciseId = exerciseEntity?.id ?: return@launch
            val storedPr = database.exercisePrDao().getPr(exerciseId)
            currentPrTextView.text = formatPrLabel(storedPr)
        }
    }

    private fun loadSets() {
        lifecycleScope.launch {
            val sets = database.workoutSetDao().getByWorkoutExerciseId(workoutExerciseId)
            adapter.submit(sets)
        }
    }

    private fun onSetTapped(workoutSet: WorkoutSetEntity) {
        showSetDialog(editSet = workoutSet, prefillSet = workoutSet)
    }

    private fun showSetDialog(editSet: WorkoutSetEntity?, prefillSet: WorkoutSetEntity?) {
        val isTimeBasedExercise = exerciseEntity?.isTimeBased == true

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 24, 50, 0)
        }

        val minutesInput = EditText(this).apply {
            hint = getString(R.string.minutes_hint)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(prefillSet?.minutes?.toString().orEmpty())
        }

        val weightInput = EditText(this).apply {
            hint = getString(R.string.weight_kg_hint)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(prefillSet?.weight?.toString().orEmpty())
        }

        val repsInput = EditText(this).apply {
            hint = getString(R.string.reps_hint)
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(prefillSet?.reps?.toString().orEmpty())
        }

        if (isTimeBasedExercise) {
            container.addView(
                minutesInput,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        } else {
            container.addView(
                weightInput,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            container.addView(
                repsInput,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        AlertDialog.Builder(this)
            .setTitle(if (editSet == null) R.string.add_set else R.string.edit_set)
            .setView(container)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    var insertedNewSet = false

                    if (isTimeBasedExercise) {
                        val minutes = minutesInput.text.toString().toDoubleOrNull()
                        if (minutes == null) {
                            Toast.makeText(
                                this@ExerciseWorkoutActivity,
                                R.string.minutes_input_error,
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        if (editSet == null) {
                            val maxSetNumber = database.workoutSetDao().getMaxSetNumber(workoutExerciseId)
                            val nextSetNumber = (maxSetNumber ?: 0) + 1
                            database.workoutSetDao().insert(
                                WorkoutSetEntity(
                                    workoutExerciseId = workoutExerciseId,
                                    setNumber = nextSetNumber,
                                    reps = null,
                                    weight = null,
                                    minutes = minutes
                                )
                            )
                            insertedNewSet = true
                            loadSets()
                        } else {
                            database.workoutSetDao().update(
                                editSet.copy(
                                    reps = null,
                                    weight = null,
                                    minutes = minutes
                                )
                            )
                        }
                    } else {
                        val weight = weightInput.text.toString().toDoubleOrNull()
                        val reps = repsInput.text.toString().toIntOrNull()

                        if (weight == null || reps == null) {
                            Toast.makeText(
                                this@ExerciseWorkoutActivity,
                                R.string.set_input_error,
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        if (editSet == null) {
                            val maxSetNumber = database.workoutSetDao().getMaxSetNumber(workoutExerciseId)
                            val nextSetNumber = (maxSetNumber ?: 0) + 1
                            database.workoutSetDao().insert(
                                WorkoutSetEntity(
                                    workoutExerciseId = workoutExerciseId,
                                    setNumber = nextSetNumber,
                                    reps = reps,
                                    weight = weight,
                                    minutes = null
                                )
                            )
                            insertedNewSet = true
                            loadSets()
                        } else {
                            database.workoutSetDao().update(
                                editSet.copy(
                                    reps = reps,
                                    weight = weight,
                                    minutes = null
                                )
                            )
                        }
                    }

                    if (!insertedNewSet) {
                        loadSets()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }


    private data class BestSet(val weight: Double, val reps: Int)

    private data class PrUpdateInfo(
        val exerciseName: String,
        val newWeight: Double,
        val newReps: Int,
        val previousWeight: Double?,
        val previousReps: Int?
    )

    private fun formatPrLabel(pr: ExercisePrEntity?): String {
        return if (pr == null) {
            "PR: None yet"
        } else {
            "PR: ${weightFormatter.format(pr.prWeight)}kg x ${pr.prReps}"
        }
    }

    private fun getBestWeightSet(sets: List<WorkoutSetEntity>): BestSet? {
        var bestSet: BestSet? = null
        sets.forEach { set ->
            val weight = set.weight ?: return@forEach
            val reps = set.reps ?: return@forEach
            if (weight <= 0.0 || reps <= 0) return@forEach

            val currentBest = bestSet
            if (currentBest == null || weight > currentBest.weight || (weight == currentBest.weight && reps > currentBest.reps)) {
                bestSet = BestSet(weight = weight, reps = reps)
            }
        }
        return bestSet
    }

    private suspend fun updatePrIfBroken(exercise: ExerciseEntity, sets: List<WorkoutSetEntity>): PrUpdateInfo? {
        if (exercise.isTimeBased || exercise.primaryMuscleName.equals("Cardio", ignoreCase = true)) {
            return null
        }

        val bestSet = getBestWeightSet(sets) ?: return null
        val prDao = database.exercisePrDao()
        val existingPr = prDao.getPr(exercise.id)

        val isNewPr = existingPr == null ||
            bestSet.weight > existingPr.prWeight ||
            (bestSet.weight == existingPr.prWeight && bestSet.reps > existingPr.prReps)

        if (!isNewPr) {
            return null
        }

        val updatedPr = ExercisePrEntity(
            exerciseId = exercise.id,
            prWeight = bestSet.weight,
            prReps = bestSet.reps,
            dateAchieved = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        if (existingPr == null) {
            prDao.insert(updatedPr)
        } else {
            prDao.update(updatedPr)
        }

        currentPrTextView.text = formatPrLabel(updatedPr)

        return PrUpdateInfo(
            exerciseName = exercise.name,
            newWeight = bestSet.weight,
            newReps = bestSet.reps,
            previousWeight = existingPr?.prWeight,
            previousReps = existingPr?.prReps
        )
    }

    private fun createPrMessage(prUpdateInfo: PrUpdateInfo?): String? {
        if (prUpdateInfo == null) {
            return null
        }

        val previousLine = if (prUpdateInfo.previousWeight != null && prUpdateInfo.previousReps != null) {
            "Previous: ${weightFormatter.format(prUpdateInfo.previousWeight)}kg x ${prUpdateInfo.previousReps}"
        } else {
            "Previous: None"
        }

        return buildString {
            append("NEW PERSONAL RECORD\n")
            append(prUpdateInfo.exerciseName)
            append("\n")
            append("${weightFormatter.format(prUpdateInfo.newWeight)}kg x ${prUpdateInfo.newReps}")
            append("\n")
            append(previousLine)
        }
    }


    private suspend fun applyXpForFinishedExercise(
        sets: List<WorkoutSetEntity>,
        exercise: ExerciseEntity
    ) {
        var totalStrengthXp = 0.0
        var totalEnduranceXp = 0.0
        var totalStaminaXp = 0.0

        sets.forEach { set ->
            val xpBase = if (exercise.isTimeBased) {
                val safeMinutes = set.minutes ?: return@forEach
                if (safeMinutes <= 0.0) return@forEach
                safeMinutes * 10.0
            } else {
                val safeWeight = set.weight ?: return@forEach
                val safeReps = set.reps ?: return@forEach
                val baseWeight = exercise.baseWeight ?: return@forEach
                if (safeReps <= 0 || safeWeight <= 0.0 || baseWeight <= 0.0) return@forEach

                val volume = safeWeight * safeReps
                val weightFactor = safeWeight / baseWeight
                volume * weightFactor * 0.1
            }

            totalStrengthXp += xpBase * exercise.strengthMultiplier
            totalEnduranceXp += xpBase * exercise.enduranceMultiplier
            totalStaminaXp += xpBase * exercise.staminaMultiplier
        }

        if (bossId != INVALID_ID) {
            totalStrengthXp *= BOSS_XP_MULTIPLIER
            totalEnduranceXp *= BOSS_XP_MULTIPLIER
            totalStaminaXp *= BOSS_XP_MULTIPLIER
        }

        val player = GameManager.player
        val strengthBefore = player.getStat(StatType.STRENGTH)?.level ?: 0
        val enduranceBefore = player.getStat(StatType.ENDURANCE)?.level ?: 0
        val staminaBefore = player.getStat(StatType.STAMINA)?.level ?: 0

        player.getStat(StatType.STRENGTH)?.addXp(totalStrengthXp)
        player.getStat(StatType.ENDURANCE)?.addXp(totalEnduranceXp)
        player.getStat(StatType.STAMINA)?.addXp(totalStaminaXp)

        val strengthAfter = player.getStat(StatType.STRENGTH)?.level ?: 0
        val enduranceAfter = player.getStat(StatType.ENDURANCE)?.level ?: 0
        val staminaAfter = player.getStat(StatType.STAMINA)?.level ?: 0

        val levelUps = mutableListOf<String>()
        if (strengthAfter > strengthBefore) {
            levelUps.add("Strength → Lv $strengthAfter")
        }
        if (enduranceAfter > enduranceBefore) {
            levelUps.add("Endurance → Lv $enduranceAfter")
        }
        if (staminaAfter > staminaBefore) {
            levelUps.add("Stamina → Lv $staminaAfter")
        }

        if (player.isMuscleUnlocked()) {
            val muscleXp = totalStrengthXp * 0.5
            val muscle = player.getMuscle(exercise.primaryMuscleName)
            muscle?.addXp(muscleXp)
        }

        val prUpdateInfo = updatePrIfBroken(exercise, sets)

        val queuedMessages = mutableListOf<String>()
        createPrMessage(prUpdateInfo)?.let(queuedMessages::add)
        if (levelUps.isNotEmpty()) {
            queuedMessages.add(
                buildString {
                    append("LEVEL UP\n")
                    append(levelUps.joinToString("\n"))
                }
            )
        }

        if (bossId != INVALID_ID) {
            handleBossOutcome(sets, queuedMessages)
        }

        queuedMessages.forEach { message ->
            SystemMessageManager.show(this, message)
        }

        lifecycleScope.launch {
            persistPlayerProgress()

            val finishDelayMs = if (queuedMessages.isEmpty()) {
                0L
            } else {
                queuedMessages.size * 3_100L
            }

            Handler(Looper.getMainLooper()).postDelayed({ finish() }, finishDelayMs)
        }
    }


    private fun didDefeatBoss(boss: BossEntity, sets: List<WorkoutSetEntity>): Boolean {
        if (sets.isEmpty()) return false
        return if (boss.requiredMinutes != null) {
            sets.all { (it.minutes ?: 0.0) >= boss.requiredMinutes }
        } else {
            sets.all {
                val weight = it.weight ?: 0.0
                val reps = it.reps ?: 0
                weight >= (boss.requiredWeight ?: Double.MAX_VALUE) && reps >= (boss.requiredReps ?: Int.MAX_VALUE)
            }
        }
    }

    private suspend fun handleBossOutcome(sets: List<WorkoutSetEntity>, queuedMessages: MutableList<String>) {
        val boss = database.bossDao().getActiveBoss() ?: return
        val isSuccess = didDefeatBoss(boss, sets)
        if (isSuccess) {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val playerEntity = database.playerDao().getPlayer()
            if (playerEntity != null) {
                database.playerDao().updatePlayer(playerEntity.copy(cheatMeals = playerEntity.cheatMeals + 1))
            }

            database.trophyDao().insert(
                TrophyEntity(
                    bossName = boss.bossName,
                    exerciseName = boss.exerciseName,
                    dateEarned = today
                )
            )

            database.inventoryDao().deleteByType("RUNE_STONE")
            database.bossDao().deleteActiveBoss()

            queuedMessages.add("SYSTEM MESSAGE\nBOSS DEFEATED\n${boss.bossName}\n\n+1 Cheat Meal\n+XP Boost")
        } else {
            val attempts = (boss.attemptsLeft - 1).coerceAtLeast(0)
            if (attempts <= 0) {
                database.inventoryDao().deleteByType("RUNE_STONE")
                database.bossDao().deleteActiveBoss()

                queuedMessages.add(
                    "SYSTEM MESSAGE\nBOSS FAILED\n\nNo attempts remaining.\nThe Rune Stone has shattered."
                )
            } else {
                database.bossDao().update(boss.copy(attemptsLeft = attempts))
                queuedMessages.add("SYSTEM MESSAGE\nBOSS FAILED\n\nAttempts Left: $attempts")
            }
        }
    }

    private suspend fun persistPlayerProgress() {
        val visitResult = DisciplineManager(database).handleVisit()
        if (visitResult.xp > 0) {
            GameManager.player.getStat(StatType.DISCIPLINE)?.addXp(visitResult.xp)

            if (visitResult.goalReached) {
                GameManager.pendingGoalCompletion = true
            } else if (visitResult.extraDay) {
                GameManager.pendingExtraWorkout = true
            }
        }

        val statDao = database.statDao()
        GameManager.player.stats.forEach { stat ->
            val existing = statDao.getByType(stat.type.name)
            if (existing == null) {
                statDao.insertAll(
                    listOf(
                        StatEntity(
                            type = stat.type.name,
                            level = stat.level,
                            currentXp = stat.currentXp
                        )
                    )
                )
            } else {
                statDao.updateStat(
                    type = stat.type.name,
                    level = stat.level,
                    currentXp = stat.currentXp
                )
            }
        }

        val muscleDao = database.muscleStatDao()
        GameManager.player.muscleStats.forEach { muscle ->
            val existing = muscleDao.getByName(muscle.name)
            if (existing == null) {
                muscleDao.insertAll(
                    listOf(
                        MuscleStatEntity(
                            name = muscle.name,
                            level = muscle.level,
                            currentXp = muscle.currentXp
                        )
                    )
                )
            } else {
                muscleDao.updateMuscle(
                    name = muscle.name,
                    level = muscle.level,
                    currentXp = muscle.currentXp
                )
            }
        }
    }

    companion object {
        private const val EXTRA_WORKOUT_EXERCISE_ID = "extra_workout_exercise_id"
        private const val EXTRA_BOSS_ID = "extra_boss_id"
        private const val INVALID_ID = -1

        fun createIntent(context: Context, workoutExerciseId: Int): Intent {
            return Intent(context, ExerciseWorkoutActivity::class.java).apply {
                putExtra(EXTRA_WORKOUT_EXERCISE_ID, workoutExerciseId)
            }
        }

        fun createBossIntent(context: Context, workoutExerciseId: Int, bossId: Int): Intent {
            return createIntent(context, workoutExerciseId).apply {
                putExtra(EXTRA_BOSS_ID, bossId)
            }
        }
    }
}

private class WorkoutSetAdapter(
    private val onSetTapped: (WorkoutSetEntity) -> Unit
) : RecyclerView.Adapter<WorkoutSetAdapter.WorkoutSetViewHolder>() {

    private val items = mutableListOf<WorkoutSetEntity>()

    fun submit(newItems: List<WorkoutSetEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutSetViewHolder {
        val itemView = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return WorkoutSetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WorkoutSetViewHolder, position: Int) {
        holder.bind(items[position], items[position].setNumber, onSetTapped)
    }

    override fun getItemCount(): Int = items.size

    class WorkoutSetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: WorkoutSetEntity, setNumber: Int, onSetTapped: (WorkoutSetEntity) -> Unit) {
            textView.text = if (item.minutes != null) {
                "Set $setNumber   ${item.minutes} min"
            } else {
                val weight = item.weight ?: 0.0
                val reps = item.reps ?: 0
                "Set $setNumber   ${weight}kg x $reps"
            }
            itemView.setOnClickListener { onSetTapped(item) }
        }
    }
}
