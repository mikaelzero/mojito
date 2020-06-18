package net.mikaelzero.mojito.loader

import android.view.View
import net.mikaelzero.mojito.interfaces.IMojitoFragment

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/18 10:54 AM
 * @Description:
 */
interface ImageCoverLoader {
    fun attach(iMojitoFragment: IMojitoFragment):View?
}