package com.agusstkd.goodlife.domain.model.training

/**
 * Esfuerzo percibido de un set (RPE).
 *
 * Tipo cerrado: el rango 1–10 vive aquí. UI y UseCase no copian `1..10`.
 * El JSON/texto entra solo por [fromWire].
 */
sealed interface PerceivedExertion {

    data object Absent : PerceivedExertion

    class Score private constructor(val value: Int) : PerceivedExertion {
        companion object {
            fun fromValid(value: Int): Score {
                require(value in 1..10)
                return Score(value)
            }
        }
    }

    data class Unknown(val raw: String?) : PerceivedExertion

    fun toDraftRpe(): Int? = when (this) {
        Absent -> null
        is Unknown -> null
        is Score -> value
    }

    companion object {
        fun fromWire(raw: String?): PerceivedExertion {
            val trimmed = raw?.trim().orEmpty()
            if (trimmed.isEmpty()) return Absent
            val parsed = trimmed.toIntOrNull() ?: return Unknown(raw)
            return if (parsed in 1..10) Score.fromValid(parsed) else Unknown(raw)
        }
    }
}
