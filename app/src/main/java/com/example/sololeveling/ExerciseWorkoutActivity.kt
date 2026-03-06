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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sololeveling.core.GameManager
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.MuscleStatEntity
import com.example.sololeveling.data.local.entity.StatEntity
import com.example.sololeveling.data.local.entity.WorkoutSetEntity
import com.example.sololeveling.domain.StatType
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ExerciseWorkoutActivity : AppCompatActivity() {

    private lateinit var exerciseNameTextView: TextView
    private lateinit var setsRecyclerView: RecyclerView
    private lateinit var addSetButton: MaterialButton

    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val adapter = WorkoutSetAdapter(::onSetTapped)

    private var workoutExerciseId: Int = INVALID_ID
    private var exerciseEntity: ExerciseEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_workout)

        workoutExerciseId = intent.getIntExtra(EXTRA_WORKOUT_EXERCISE_ID, INVALID_ID)
        if (workoutExerciseId == INVALID_ID) {
            finish()
            return
        }

        exerciseNameTextView = findViewById(R.id.text_exercise_name)
        setsRecyclerView = findViewById(R.id.recycler_sets)
        addSetButton = findViewById(R.id.button_add_set)

        setsRecyclerView.layoutManager = LinearLayoutManager(this)
        setsRecyclerView.adapter = adapter

        addSetButton.setOnClickListener {
            lifecycleScope.launch {
                val mostRecentSet = database.workoutSetDao()
                    .getMostRecentByWorkoutExerciseId(workoutExerciseId)
                showSetDialog(editSet = null, prefillSet = mostRecentSet)
            }
        }

        loadExercise()
        loadSets()
    }

    private fun loadExercise() {
        lifecycleScope.launch {
            exerciseEntity = database.workoutExerciseDao().getExerciseByWorkoutExerciseId(workoutExerciseId)
            exerciseNameTextView.text = exerciseEntity?.name ?: getString(R.string.exercise_unknown)
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
                            val insertedId = database.workoutSetDao().insert(
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
                            applyXpForInsertedSet(
                                insertedId = insertedId,
                                exercise = exerciseEntity,
                                weight = null,
                                reps = null,
                                minutes = minutes
                            )
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
                            val insertedId = database.workoutSetDao().insert(
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
                            applyXpForInsertedSet(
                                insertedId = insertedId,
                                exercise = exerciseEntity,
                                weight = weight,
                                reps = reps,
                                minutes = null
                            )
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


    private suspend fun applyXpForInsertedSet(
        insertedId: Long,
        exercise: ExerciseEntity?,
        weight: Double?,
        reps: Int?,
        minutes: Double?
    ) {
        if (insertedId <= 0L || exercise == null) return

        val xpBase = if (exercise.isTimeBased) {
            val safeMinutes = minutes ?: return
            if (safeMinutes <= 0.0) return
            safeMinutes * 10.0
        } else {
            val safeWeight = weight ?: return
            val safeReps = reps ?: return
            val baseWeight = exercise.baseWeight ?: return
            if (safeReps <= 0 || safeWeight <= 0.0 || baseWeight <= 0.0) return

            val volume = safeWeight * safeReps
            val weightFactor = safeWeight / baseWeight
            volume * weightFactor * 0.1
        }

        val strengthXp = xpBase * exercise.strengthMultiplier
        val enduranceXp = xpBase * exercise.enduranceMultiplier
        val staminaXp = xpBase * exercise.staminaMultiplier

        val player = GameManager.player
        player.getStat(StatType.STRENGTH)?.addXp(strengthXp)
        player.getStat(StatType.ENDURANCE)?.addXp(enduranceXp)
        player.getStat(StatType.STAMINA)?.addXp(staminaXp)

        if (player.isMuscleUnlocked()) {
            val muscleXp = strengthXp * 0.5
            val muscle = player.getMuscle(exercise.primaryMuscleName)
            muscle?.addXp(muscleXp)
        }

        persistPlayerProgress()
    }

    private suspend fun persistPlayerProgress() {
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
        private const val INVALID_ID = -1

        fun createIntent(context: Context, workoutExerciseId: Int): Intent {
            return Intent(context, ExerciseWorkoutActivity::class.java).apply {
                putExtra(EXTRA_WORKOUT_EXERCISE_ID, workoutExerciseId)
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
