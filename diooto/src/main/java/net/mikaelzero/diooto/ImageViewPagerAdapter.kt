package net.mikaelzero.diooto

import androidx.collection.LongSparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlin.reflect.full.superclasses

class ImageViewPagerAdapter constructor(
    val viewpagerBeanList: List<ViewPagerBean>,
    fragmentManager: FragmentActivity
) : FragmentStateAdapter(fragmentManager) {


    override fun getItemCount(): Int = viewpagerBeanList.size
    override fun getItemId(position: Int): Long = viewpagerBeanList[position].hashCode().toLong()
    override fun containsItem(itemId: Long): Boolean =
        viewpagerBeanList.any { it.hashCode().toLong() == itemId }

    override fun createFragment(position: Int): Fragment =
        ImageFragment.newInstance(
            viewpagerBeanList[position].url,
            position,
            viewpagerBeanList[position].showImmediately,
            viewpagerBeanList[position].contentViewOriginModel
        )

    fun getFragment(position: Int): ImageFragment? {
        return this::class.superclasses.find { it == FragmentStateAdapter::class }
            ?.java?.getDeclaredField("mFragments")
            ?.let { field ->
                field.isAccessible = true
                val mFragments = field.get(this) as LongSparseArray<Fragment>
                return@let mFragments[getItemId(position)] as ImageFragment
            }
    }
}