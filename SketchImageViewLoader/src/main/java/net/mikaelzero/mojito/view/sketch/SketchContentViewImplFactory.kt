package net.mikaelzero.mojito.view.sketch

import androidx.lifecycle.LifecycleOwner
import net.mikaelzero.mojito.loader.ContentLoader
import net.mikaelzero.mojito.loader.IContentViewImplFactory

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/13 1:12 PM
 * @Description:
 */
class SketchContentViewImplFactory : IContentViewImplFactory {
    override fun newInstance(lifecycleOwner: LifecycleOwner): ContentLoader {
        return SketchImageContentLoaderImpl(lifecycleOwner)
    }
}