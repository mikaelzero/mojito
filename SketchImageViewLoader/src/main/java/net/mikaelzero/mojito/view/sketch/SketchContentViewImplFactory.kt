package net.mikaelzero.mojito.view.sketch

import net.mikaelzero.mojito.loader.ContentLoader
import net.mikaelzero.mojito.loader.IContentViewImplFactory

/**
 * @Author:         MikaelZero
 * @CreateDate:     2020/6/13 1:12 PM
 * @Description:
 */
class SketchContentViewImplFactory : IContentViewImplFactory {
    override fun newInstance(): ContentLoader {
        return SketchImageContentLoaderImpl()
    }
}