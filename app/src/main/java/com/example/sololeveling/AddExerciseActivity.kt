package com.example.sololeveling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sololeveling.data.local.DatabaseProvider
import com.example.sololeveling.data.local.entity.ExerciseEntity
import kotlinx.coroutines.launch

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var primaryMuscleGroup: RadioGroup
    private lateinit var exerciseTypeGroup: RadioGroup
    private lateinit var baseWeightInput: EditText
    private lateinit var strengthMultiplierInput: EditText
    private lateinit var enduranceMultiplierInput: EditText
    private lateinit var staminaMultiplierInput: EditText

    private val database by lazy { DatabaseProvider.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        nameInput = findViewById(R.id.input_exercise_name)
        primaryMuscleGroup = findViewById(R.id.group_primary_muscle)
        exerciseTypeGroup = findViewById(R.id.group_exercise_type)
        baseWeightInput = findViewById(R.id.input_base_weight)
        strengthMultiplierInput = findViewById(R.id.input_strength_multiplier)
        enduranceMultiplierInput = findViewById(R.id.input_endurance_multiplier)
        staminaMultiplierInput = findViewById(R.id.input_stamina_multiplier)

        findViewById<Button>(R.id.button_save_exercise).setOnClickListener {
            saveExercise()
        }

        exerciseTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_weight_based) {
                baseWeightInput.visibility = View.VISIBLE
            } else {
                baseWeightInput.visibility = View.GONE
                baseWeightInput.setText("")
            }
        }
    }

    private fun saveExercise() {
        val name = nameInput.text.toString().trim()
        if (name.isEmpty()) {
            showToast("Exercise name is required")
            return
        }

        val selectedPrimaryMuscleId = primaryMuscleGroup.checkedRadioButtonId
        if (selectedPrimaryMuscleId == View.NO_ID) {
            showToast("Please select a primary muscle")
            return
        }

        val selectedTypeId = exerciseTypeGroup.checkedRadioButtonId
        if (selectedTypeId == View.NO_ID) {
            showToast("Please select an exercise type")
            return
        }

        val strengthMultiplier = strengthMultiplierInput.text.toString().toDoubleOrNull()
        if (strengthMultiplier == null || strengthMultiplier <= 0.0) {
            showToast("Strength multiplier must be greater than 0")
            return
        }

        val enduranceMultiplier = enduranceMultiplierInput.text.toString().toDoubleOrNull()
        if (enduranceMultiplier == null || enduranceMultiplier <= 0.0) {
            showToast("Endurance multiplier must be greater than 0")
            return
        }

        val staminaMultiplier = staminaMultiplierInput.text.toString().toDoubleOrNull()
        if (staminaMultiplier == null || staminaMultiplier <= 0.0) {
            showToast("Stamina multiplier must be greater than 0")
            return
        }

        val isTimeBased = selectedTypeId == R.id.radio_time_based
        val baseWeight: Double? = if (isTimeBased) {
            null
        } else {
            val parsedWeight = baseWeightInput.text.toString().toDoubleOrNull()
            if (parsedWeight == null || parsedWeight <= 0.0) {
                showToast("Base weight must be greater than 0 for weight based exercises")
                return
            }
            parsedWeight
        }

        val primaryMuscleName = when (selectedPrimaryMuscleId) {
            R.id.radio_chest -> "Chest"
            R.id.radio_back -> "Back"
            R.id.radio_legs -> "Legs"
            R.id.radio_shoulders -> "Shoulders"
            R.id.radio_arms -> "Arms"
            R.id.radio_core -> "Core"
            R.id.radio_cardio -> "Cardio"
            else -> {
                showToast("Please select a primary muscle")
                return
            }
        }

        lifecycleScope.launch {
            database.exerciseDao().insert(
                ExerciseEntity(
                    name = name,
                    primaryMuscleName = primaryMuscleName,
                    isTimeBased = isTimeBased,
                    baseWeight = baseWeight,
                    strengthMultiplier = strengthMultiplier,
                    enduranceMultiplier = enduranceMultiplier,
                    staminaMultiplier = staminaMultiplier
                )
            )
            showToast("Exercise added")
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AddExerciseActivity::class.java)
        }
    }
}
