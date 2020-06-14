package net.mikaelzero.mojito.loader

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/14 8:54 PM
 * @Description:
 */
interface IMojitoConfig {
    companion object {
        const val DRAG_ONLY_BOTTOM = 1
        const val DRAG_BOTH_BOTTOM_TOP = 2
    }

    /**
     * DRAG_ONLY_BOTTOM
     * DRAG_BOTH_BOTTOM_TOP
     */
    fun dragMode(): Int
}