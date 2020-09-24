package net.mikaelzero.mojito.tools

import net.mikaelzero.mojito.bean.ActivityConfig

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/9/24 1:22 PM
 * @Description:
 */
class DataWrapUtil {
    companion object {
        var config: ActivityConfig? = null
        fun put(config: ActivityConfig) {
            this.config = config
        }

        fun get(): ActivityConfig? = config

        fun remove() {
            config = null
        }
    }
}