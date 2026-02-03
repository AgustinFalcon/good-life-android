package com.agusstkd.goodlife.presentation.components.modal.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Modelo para un item de acción rápida en el modal.
 *
 * @property type Tipo de acción [QuickActionType]
 * @property label Texto visible del item
 * @property icon Icono del item
 * @property backgroundColor Color de fondo del icono
 * @property contentDescription Descripción para accesibilidad
 */
data class QuickActionItem(
    val type: QuickActionType,
    val label: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val contentDescription: String = label
)
