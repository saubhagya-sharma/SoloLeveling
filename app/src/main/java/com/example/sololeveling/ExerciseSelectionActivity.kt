package com.example.sololeveling

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.ExerciseEntity
import com.example.sololeveling.data.local.entity.WorkoutExerciseEntity
import kotlinx.coroutines.launch

class ExerciseSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var endWorkoutButton: Button
    private var workoutSessionId: Int = INVALID_ID
    private val database by lazy { DatabaseProvider.getDatabase(this) }
    private val adapter = ExerciseSelectionAdapter(::onExerciseTapped, ::onAddNewExerciseTapped)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_selection)

        workoutSessionId = intent.getIntExtra(EXTRA_WORKOUT_SESSION_ID, INVALID_ID)
        if (workoutSessionId == INVALID_ID) {
            finish()
            return
        }

        recyclerView = findViewById(R.id.recycler_exercises)
        endWorkoutButton = findViewById(R.id.button_end_workout)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        endWorkoutButton.setOnClickListener {
            finish()
        }

        loadExercises()
    }

    override fun onResume() {
        super.onResume()
        if (workoutSessionId != INVALID_ID) {
            loadExercises()
        }
    }

    private fun loadExercises() {
        lifecycleScope.launch {
            val exercises = database.exerciseDao().getAll()
            adapter.submitItems(buildListItems(exercises, workoutSessionId))
        }
    }

    private fun buildListItems(
        exercises: List<ExerciseEntity>,
        workoutSessionId: Int
    ): List<ExerciseListItem> {
        val groupedItems = exercises
            .groupBy { it.primaryMuscleName }
            .toSortedMap()
            .flatMap { (muscleName, groupedExercises) ->
                listOf(ExerciseListItem.Header(muscleName)) + groupedExercises
                    .sortedBy { it.name }
                    .map { exercise ->
                        ExerciseListItem.ExerciseRow(exercise, workoutSessionId)
                    }
            }

        return groupedItems + ExerciseListItem.AddNewExerciseRow
    }

    private fun onExerciseTapped(exerciseEntity: ExerciseEntity, workoutSessionId: Int) {
        lifecycleScope.launch {
            val workoutExerciseId = database.workoutExerciseDao().insert(
                WorkoutExerciseEntity(
                    sessionId = workoutSessionId,
                    exerciseId = exerciseEntity.id
                )
            ).toInt()

            startActivity(
                ExerciseWorkoutActivity.createIntent(
                    context = this@ExerciseSelectionActivity,
                    workoutExerciseId = workoutExerciseId
                )
            )
        }
    }

    private fun onAddNewExerciseTapped() {
        startActivity(AddExerciseActivity.createIntent(this))
    }

    companion object {
        const val EXTRA_WORKOUT_SESSION_ID = "extra_workout_session_id"
        private const val INVALID_ID = -1

        fun createIntent(context: Context, workoutSessionId: Int): Intent {
            return Intent(context, ExerciseSelectionActivity::class.java).apply {
                putExtra(EXTRA_WORKOUT_SESSION_ID, workoutSessionId)
            }
        }
    }
}

private sealed class ExerciseListItem {
    data class Header(val title: String) : ExerciseListItem()
    data class ExerciseRow(val exercise: ExerciseEntity, val workoutSessionId: Int) : ExerciseListItem()
    data object AddNewExerciseRow : ExerciseListItem()
}

private class ExerciseSelectionAdapter(
    private val onExerciseTapped: (ExerciseEntity, Int) -> Unit,
    private val onAddNewExerciseTapped: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ExerciseListItem>()

    fun submitItems(newItems: List<ExerciseListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ExerciseListItem.Header -> VIEW_TYPE_HEADER
            is ExerciseListItem.ExerciseRow -> VIEW_TYPE_EXERCISE
            is ExerciseListItem.AddNewExerciseRow -> VIEW_TYPE_ADD_NEW_EXERCISE
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)

        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(view)
            VIEW_TYPE_ADD_NEW_EXERCISE -> AddExerciseViewHolder(view)
            else -> ExerciseViewHolder(view)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ExerciseListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ExerciseListItem.ExerciseRow -> (holder as ExerciseViewHolder).bind(item, onExerciseTapped)
            is ExerciseListItem.AddNewExerciseRow -> (holder as AddExerciseViewHolder).bind(onAddNewExerciseTapped)
        }
    }

    private class HeaderViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val label: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: ExerciseListItem.Header) {
            label.text = item.title
            label.setTypeface(label.typeface, Typeface.BOLD)
            itemView.setOnClickListener(null)
        }
    }

    private class ExerciseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val label: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(
            item: ExerciseListItem.ExerciseRow,
            onExerciseTapped: (ExerciseEntity, Int) -> Unit
        ) {
            label.text = item.exercise.name
            label.setTypeface(label.typeface, Typeface.NORMAL)
            itemView.setOnClickListener {
                onExerciseTapped(item.exercise, item.workoutSessionId)
            }
        }
    }

    private class AddExerciseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val label: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(onAddNewExerciseTapped: () -> Unit) {
            label.text = "➕ Add New Exercise"
            label.setTypeface(label.typeface, Typeface.BOLD)
            itemView.setOnClickListener {
                onAddNewExerciseTapped()
            }
        }
    }

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_EXERCISE = 1
        const val VIEW_TYPE_ADD_NEW_EXERCISE = 2
    }
}
