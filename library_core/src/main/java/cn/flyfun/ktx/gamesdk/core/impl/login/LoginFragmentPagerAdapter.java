package cn.flyfun.ktx.gamesdk.core.impl.login;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import cn.flyfun.ktx.gamesdk.core.impl.login.fragment.ChooseFragment;
import cn.flyfun.ktx.gamesdk.core.impl.login.fragment.RegisterFragment;

/**
 * @author #Suyghur.
 * Created on 10/30/20
 */
public class LoginFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;

    private Fragment mCurrentFragment;

    public LoginFragmentPagerAdapter(String[] titles, FragmentManager fm) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new RegisterFragment();
        }
        return new ChooseFragment();
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        mCurrentFragment = (Fragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }
}
