package app.dev.nick.music;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import dev.nick.logger.LoggerManager;

public class FragmentController {

    List<Fragment> mPages;
    FragmentManager mFragmentManager;

    Fragment mCurrent;

    int mDefIndex = 0;

    public FragmentController(FragmentManager mFragmentManager, List<Fragment> mPages) {
        this.mFragmentManager = mFragmentManager;
        this.mPages = mPages;
        init();
    }

    private void init() {
        FragmentManager fragmentManager = mFragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        for (Fragment fragment : mPages) {
            transaction.add(R.id.container, fragment, fragment.getClass().getSimpleName());
            transaction.hide(fragment);
        }

        transaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }

    public void setDefaultIndex(int index) {
        mDefIndex = index;
    }

    public Fragment getCurrent() {
        return mCurrent == null ? mPages.get(mDefIndex) : mCurrent;
    }

    public void setCurrent(int index) {
        LoggerManager.getLogger(getClass()).funcEnter();
        FragmentManager fragmentManager = mFragmentManager;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(getCurrent());
        transaction.show(mPages.get(index));
        transaction.commitAllowingStateLoss();
        mCurrent = mPages.get(index);
    }
}
