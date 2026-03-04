package com.example.sololeveling.domain

data class Exercise(
    val name: String,
    val isTimeBased: Boolean,
    val baseWeight: Double?,
    val strengthMultiplier: Double,
    val enduranceMultiplier: Double,
    val staminaMultiplier: Double,
    val primaryMuscleName: String
) {
    init {
        require((isTimeBased && baseWeight == null) || (!isTimeBased && baseWeight != null)) {
            "baseWeight must be null for time-based exercises and non-null for rep-based exercises."
        }
    }
}
