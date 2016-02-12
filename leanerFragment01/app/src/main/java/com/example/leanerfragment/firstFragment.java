package com.example.leanerfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 李森林 on 2016/2/12.
 */
public class firstFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_first,container,false);
        view.findViewById(R.id.changefragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//       跳转一个新的fragment （可是不能后退到上次界面）
//       getFragmentManager().beginTransaction().replace(R.id.main_fragment, new AnOtherFragment()).commit();
             // 可以显示一个新的fragment并且可以后退到上次状态（加入到了后退栈里）
                getFragmentManager().beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.main_fragment, new AnOtherFragment())
                                    .commit();
            }
        });

        return view;
    }
}
