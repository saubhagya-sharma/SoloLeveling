package com.example.sololeveling.domain

data class Exercise(
    val name: String,
    val isTimeBased: Boolean,
    val baseWeight: Double?,
    var strengthMultiplier: Double,
    var enduranceMultiplier: Double,
    var staminaMultiplier: Double
) {
    init {
        strengthMultiplier = strengthMultiplier.coerceIn(0.0, 5.0)
        enduranceMultiplier = enduranceMultiplier.coerceIn(0.0, 5.0)
        staminaMultiplier = staminaMultiplier.coerceIn(0.0, 5.0)

        require((isTimeBased && baseWeight == null) || (!isTimeBased && baseWeight != null)) {
            "baseWeight must be null for time-based exercises and non-null for rep-based exercises."
        }
    }
}
