package com.gultekinahmetabdullah.trainvoc.constants

/**
 * Avatar list for player profiles.
 * Synchronized with TrainvocWeb avatars for cross-platform consistency.
 */
object Avatars {
    val AVATAR_LIST = listOf(
        "\uD83E\uDD8A", // 🦊
        "\uD83D\uDC31", // 🐱
        "\uD83D\uDC36", // 🐶
        "\uD83D\uDC35", // 🐵
        "\uD83D\uDC38", // 🐸
        "\uD83D\uDC3C", // 🐼
        "\uD83D\uDC27", // 🐧
        "\uD83D\uDC2F", // 🐯
        "\uD83E\uDD81", // 🦁
        "\uD83D\uDC2E", // 🐮
        "\uD83D\uDC28", // 🐨
        "\uD83D\uDC30", // 🐰
        "\uD83D\uDC3B", // 🐻
        "\uD83D\uDC37", // 🐷
        "\uD83D\uDC14", // 🐔
        "\uD83E\uDD84", // 🦄
        "\uD83D\uDC19", // 🐙
        "\uD83D\uDC22", // 🐢
        "\uD83D\uDC33", // 🐳
        "\uD83D\uDC1D"  // 🐝
    )

    /**
     * Gets an avatar by index, wrapping around if index exceeds list length.
     */
    fun getAvatarByIndex(index: Int): String {
        return AVATAR_LIST[index % AVATAR_LIST.size]
    }

    /**
     * Gets a random avatar from the list.
     */
    fun getRandomAvatar(): String {
        return AVATAR_LIST.random()
    }

    /**
     * Gets the index of an avatar, or -1 if not found.
     */
    fun getAvatarIndex(avatar: String): Int {
        return AVATAR_LIST.indexOf(avatar)
    }
}
