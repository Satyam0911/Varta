package Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.myapplication.fragment.Calls_frag;
import com.example.myapplication.fragment.Status_frag;
import com.example.myapplication.fragment.chats_frag;

public class FragmentAdapter extends FragmentPagerAdapter {
    private static final FragmentManager fm = null;

    public FragmentAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:return new chats_frag();
            case 1:return new Status_frag();
            case 2:return new Calls_frag();
            default:return new chats_frag();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0){
            title = "Chats";
        }
        if (position == 1){
            title = "Status";
        }
        if (position == 2){
            title = "Calls";
        }

        return title;
    }
}
