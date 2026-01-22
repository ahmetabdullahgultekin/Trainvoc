package com.gultekinahmetabdullah.trainvoc.viewmodel

import androidx.lifecycle.ViewModel
import com.gultekinahmetabdullah.trainvoc.repository.IPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for managing accessibility settings
 *
 * Features:
 * - High contrast mode
 * - Color blind friendly modes (Deuteranopia, Protanopia, Tritanopia)
 * - Text size adjustment (0.8x - 1.5x)
 * - Haptic feedback toggle
 * - Reduce motion toggle
 *
 * All settings are persisted via PreferencesRepository with encrypted storage.
 */
@HiltViewModel
class AccessibilityViewModel @Inject constructor(
    private val preferencesRepository: IPreferencesRepository
) : ViewModel() {

    private val _highContrastEnabled = MutableStateFlow(preferencesRepository.isHighContrastEnabled())
    val highContrastEnabled: StateFlow<Boolean> = _highContrastEnabled.asStateFlow()

    private val _colorBlindModeEnabled = MutableStateFlow(preferencesRepository.getColorBlindMode() != null)
    val colorBlindModeEnabled: StateFlow<Boolean> = _colorBlindModeEnabled.asStateFlow()

    private val _colorBlindMode = MutableStateFlow(preferencesRepository.getColorBlindMode() ?: "deuteranopia")
    val colorBlindMode: StateFlow<String> = _colorBlindMode.asStateFlow()

    private val _textSizeScale = MutableStateFlow(preferencesRepository.getTextSizeScale())
    val textSizeScale: StateFlow<Float> = _textSizeScale.asStateFlow()

    private val _hapticFeedbackEnabled = MutableStateFlow(preferencesRepository.isHapticFeedbackEnabled())
    val hapticFeedbackEnabled: StateFlow<Boolean> = _hapticFeedbackEnabled.asStateFlow()

    private val _reduceMotionEnabled = MutableStateFlow(preferencesRepository.isReduceMotionEnabled())
    val reduceMotionEnabled: StateFlow<Boolean> = _reduceMotionEnabled.asStateFlow()

    /**
     * Enable or disable high contrast mode
     */
    fun setHighContrastEnabled(enabled: Boolean) {
        preferencesRepository.setHighContrastEnabled(enabled)
        _highContrastEnabled.value = enabled
    }

    /**
     * Enable or disable color blind mode
     * When disabled, sets colorBlindMode to null in preferences
     */
    fun setColorBlindModeEnabled(enabled: Boolean) {
        _colorBlindModeEnabled.value = enabled
        if (enabled) {
            // Enable with current mode
            preferencesRepository.setColorBlindMode(_colorBlindMode.value)
        } else {
            // Disable by setting to null
            preferencesRepository.setColorBlindMode(null)
        }
    }

    /**
     * Set the specific color blind mode type
     * @param mode One of: "deuteranopia", "protanopia", "tritanopia"
     */
    fun setColorBlindMode(mode: String) {
        require(mode in listOf("deuteranopia", "protanopia", "tritanopia")) {
            "Invalid color blind mode: $mode"
        }
        _colorBlindMode.value = mode
        if (_colorBlindModeEnabled.value) {
            preferencesRepository.setColorBlindMode(mode)
        }
    }

    /**
     * Set text size scale
     * @param scale Multiplier from 0.8f to 1.5f (80% to 150%)
     */
    fun setTextSizeScale(scale: Float) {
        val clampedScale = scale.coerceIn(0.8f, 1.5f)
        preferencesRepository.setTextSizeScale(clampedScale)
        _textSizeScale.value = clampedScale
    }

    /**
     * Enable or disable haptic feedback
     */
    fun setHapticFeedbackEnabled(enabled: Boolean) {
        preferencesRepository.setHapticFeedbackEnabled(enabled)
        _hapticFeedbackEnabled.value = enabled
    }

    /**
     * Enable or disable reduce motion
     */
    fun setReduceMotionEnabled(enabled: Boolean) {
        preferencesRepository.setReduceMotionEnabled(enabled)
        _reduceMotionEnabled.value = enabled
    }

    /**
     * Reset all accessibility settings to defaults
     */
    fun resetToDefaults() {
        setHighContrastEnabled(false)
        setColorBlindModeEnabled(false)
        setTextSizeScale(1.0f)
        setHapticFeedbackEnabled(true)
        setReduceMotionEnabled(false)
    }
}
