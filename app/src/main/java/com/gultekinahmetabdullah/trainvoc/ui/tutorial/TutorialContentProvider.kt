package com.gultekinahmetabdullah.trainvoc.ui.tutorial

import com.gultekinahmetabdullah.trainvoc.ui.games.GameType

/**
 * Provides tutorial content for each game type.
 * Uses Strategy pattern - each game has its own tutorial steps.
 */
object TutorialContentProvider {

    fun getTutorialSteps(gameType: GameType): List<TutorialStep> {
        return when (gameType) {
            GameType.MULTIPLE_CHOICE -> multipleChoiceTutorial()
            GameType.FLIP_CARDS -> flipCardsTutorial()
            GameType.SPEED_MATCH -> speedMatchTutorial()
            GameType.FILL_IN_BLANK -> fillInBlankTutorial()
            GameType.WORD_SCRAMBLE -> wordScrambleTutorial()
            GameType.LISTENING_QUIZ -> listeningQuizTutorial()
            GameType.PICTURE_MATCH -> pictureMatchTutorial()
            GameType.SPELLING_CHALLENGE -> spellingChallengeTutorial()
            GameType.TRANSLATION_RACE -> translationRaceTutorial()
            GameType.CONTEXT_CLUES -> contextCluesTutorial()
        }
    }

    private fun multipleChoiceTutorial() = listOf(
        TutorialStep(
            id = "mc_welcome",
            title = "Welcome to Multiple Choice!",
            description = "Test your vocabulary by selecting the correct translation from four options.",
            lottieAnimation = "animations/anime_book.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "mc_question",
            title = "Read the Question",
            description = "You'll see a word to translate. It could be English to meaning or meaning to English.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "mc_options",
            title = "Select Your Answer",
            description = "Tap one of the four options. Green means correct, red means wrong. Learn from your mistakes!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "mc_scoring",
            title = "Track Your Progress",
            description = "Watch your score and progress bar. Try to answer all 10 questions correctly!",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "mc_complete",
            title = "You're Ready!",
            description = "Good luck! Tap the help button anytime to see this tutorial again.",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun flipCardsTutorial() = listOf(
        TutorialStep(
            id = "fc_welcome",
            title = "Welcome to Flip Cards!",
            description = "A memory matching game! Find pairs of words and their meanings.",
            lottieAnimation = "animations/anime_book.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "fc_flip",
            title = "Flip the Cards",
            description = "Tap any card to flip it and reveal what's hidden underneath.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "fc_match",
            title = "Find Matching Pairs",
            description = "Flip two cards. If they match (word + meaning), they stay revealed. If not, they flip back.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "fc_strategy",
            title = "Minimize Your Moves",
            description = "Remember card positions! Try to match all pairs in as few moves as possible.",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "fc_complete",
            title = "Ready to Play!",
            description = "Good luck! Focus and remember where each card is.",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun speedMatchTutorial() = listOf(
        TutorialStep(
            id = "sm_welcome",
            title = "Speed Match Challenge!",
            description = "Race against time! Match words with their meanings as fast as you can.",
            lottieAnimation = "animations/anime_typing.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "sm_columns",
            title = "Two Columns",
            description = "Words are on the left, meanings on the right. Tap a word, then tap its matching meaning.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "sm_timer",
            title = "Beat the Clock",
            description = "Watch the timer! You have limited time to match all pairs before it runs out.",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "sm_combo",
            title = "Build Combos",
            description = "Match quickly and correctly to build combos. Combos multiply your score!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "sm_complete",
            title = "Ready, Set, Match!",
            description = "Stay focused and match fast. You've got this!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun fillInBlankTutorial() = listOf(
        TutorialStep(
            id = "fib_welcome",
            title = "Fill in the Blank",
            description = "Complete sentences by choosing the missing word from context.",
            lottieAnimation = "animations/anime_typing.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "fib_read",
            title = "Read the Sentence",
            description = "A word is missing from the sentence. Use the surrounding context to understand what fits.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "fib_select",
            title = "Choose the Answer",
            description = "Select the word that best completes the sentence. Think about grammar and meaning!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "fib_complete",
            title = "You're All Set!",
            description = "Context is key! Pay attention to the words around the blank.",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun wordScrambleTutorial() = listOf(
        TutorialStep(
            id = "ws_welcome",
            title = "Word Scramble",
            description = "Unscramble the jumbled letters to form the correct word!",
            lottieAnimation = "animations/anime_book.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "ws_letters",
            title = "Scrambled Letters",
            description = "You'll see jumbled letters and a hint about the word's meaning. Rearrange them!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "ws_type",
            title = "Type Your Answer",
            description = "Type the unscrambled word in the text field and submit. Spelling matters!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "ws_hints",
            title = "Use Hints Wisely",
            description = "Stuck? Use hints to reveal letters, but try to solve it yourself first!",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "ws_complete",
            title = "Start Unscrambling!",
            description = "Look for common letter patterns. Good luck!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun listeningQuizTutorial() = listOf(
        TutorialStep(
            id = "lq_welcome",
            title = "Listening Quiz",
            description = "Train your ears! Listen to words and select the correct answer.",
            lottieAnimation = "animations/anime_typing.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "lq_listen",
            title = "Listen Carefully",
            description = "Tap the speaker icon to hear the word. You can replay it a limited number of times.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "lq_answer",
            title = "Select the Match",
            description = "Choose the option that matches what you heard - the meaning, translation, or spelling.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "lq_complete",
            title = "Ready to Listen!",
            description = "Listening helps you remember words better. Enjoy!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun pictureMatchTutorial() = listOf(
        TutorialStep(
            id = "pm_welcome",
            title = "Picture Match",
            description = "Visual learning! Match words to their corresponding images.",
            lottieAnimation = "animations/anime_book.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "pm_look",
            title = "Look at the Image",
            description = "An image represents a concept or object. Study it carefully.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "pm_select",
            title = "Choose the Word",
            description = "Select the word that best describes what you see in the image.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "pm_complete",
            title = "Visual Learning!",
            description = "Associating words with images improves memory. Have fun!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun spellingChallengeTutorial() = listOf(
        TutorialStep(
            id = "sc_welcome",
            title = "Spelling Challenge",
            description = "Test your spelling! Type the correct spelling of each word.",
            lottieAnimation = "animations/anime_typing.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "sc_clue",
            title = "Read the Clue",
            description = "You'll see the word's meaning or hear it spoken. Your job is to spell it correctly.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "sc_type",
            title = "Type Carefully",
            description = "Enter your spelling in the text field. Every letter counts!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "sc_reveal",
            title = "Reveal Letters",
            description = "Stuck? Use the reveal button to show letters one at a time as hints.",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "sc_complete",
            title = "Spell Away!",
            description = "Practice makes perfect. Double-check before submitting!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun translationRaceTutorial() = listOf(
        TutorialStep(
            id = "tr_welcome",
            title = "Translation Race",
            description = "Speed translation challenge! Translate as many words as possible before time runs out.",
            lottieAnimation = "animations/anime_typing.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "tr_word",
            title = "See the Word",
            description = "A word appears that needs translation. Think fast!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "tr_translate",
            title = "Type the Translation",
            description = "Quickly type the translation and submit. Speed and accuracy both matter!",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "tr_timer",
            title = "Beat the Clock",
            description = "The timer is always ticking! Stay calm and translate as many as you can.",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "tr_complete",
            title = "Ready to Race!",
            description = "Trust your knowledge and type fast. Good luck!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )

    private fun contextCluesTutorial() = listOf(
        TutorialStep(
            id = "cc_welcome",
            title = "Context Clues",
            description = "Learn words naturally! Understand new words from their context in sentences.",
            lottieAnimation = "animations/anime_book.json",
            pointerDirection = PointerDirection.BOTTOM
        ),
        TutorialStep(
            id = "cc_read",
            title = "Read the Passage",
            description = "A highlighted word appears in a sentence. Read the whole sentence carefully.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "cc_infer",
            title = "Infer the Meaning",
            description = "Use surrounding words and context to figure out what the highlighted word means.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "cc_select",
            title = "Select the Definition",
            description = "Choose the meaning that best fits based on the context clues you found.",
            pointerDirection = PointerDirection.TOP
        ),
        TutorialStep(
            id = "cc_complete",
            title = "Context is King!",
            description = "This is how native speakers learn new words. Enjoy learning naturally!",
            action = TutorialAction.COMPLETE,
            lottieAnimation = "animations/anime_verified.json"
        )
    )
}
