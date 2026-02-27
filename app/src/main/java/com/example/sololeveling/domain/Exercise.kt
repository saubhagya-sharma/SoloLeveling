package com.example.sololeveling.domain

data class Exercise(
    val name: String,
    val isTimeBased: Boolean,
    val baseWeight: Double?,
    strengthMultiplier: Double,
    enduranceMultiplier: Double,
    staminaMultiplier: Double
) {
    val strengthMultiplier: Double = strengthMultiplier.coerceIn(0.0, 5.0)
    val enduranceMultiplier: Double = enduranceMultiplier.coerceIn(0.0, 5.0)
    val staminaMultiplier: Double = staminaMultiplier.coerceIn(0.0, 5.0)

    init {
        require((isTimeBased && baseWeight == null) || (!isTimeBased && baseWeight != null)) {
            "baseWeight must be null for time-based exercises and non-null for rep-based exercises."
        }
    }
}
