package cn.flyfun.gamesdk.core.impl.login

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import cn.flyfun.gamesdk.core.impl.login.fragment.ChooseFragment
import cn.flyfun.gamesdk.core.impl.login.fragment.RegisterFragment

/**
 * @author #Suyghur.
 * Created on 2020/12/10
 */
class LoginFragmentPagerAdapter constructor(private val titles: Array<String>, var fm: FragmentManager) : FragmentPagerAdapter(fm) {


    var currentFragment: Fragment? = null
        private set

    override fun getCount(): Int {
        return titles.size
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 1) {
            RegisterFragment()
        } else {
            ChooseFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        currentFragment = `object` as Fragment
        super.setPrimaryItem(container, position, `object`)
    }

}