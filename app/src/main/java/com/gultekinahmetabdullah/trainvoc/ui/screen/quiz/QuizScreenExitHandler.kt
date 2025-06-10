package com.gultekinahmetabdullah.trainvoc.ui.screen.quiz

object QuizScreenExitHandler {
    private val callbacks = mutableSetOf<() -> Unit>()

    fun register(callback: () -> Unit) {
        callbacks.add(callback)
    }

    fun unregister(callback: () -> Unit) {
        callbacks.remove(callback)
    }

    fun triggerExit() {
        callbacks.forEach { it() }
    }
}

