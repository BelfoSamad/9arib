package com.belfoapps.qarib.views.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.databinding.OnBoardingFragmentBinding;
import com.belfoapps.qarib.pojo.Hype;
import com.belfoapps.qarib.ui.adapters.OnBoardingPagerAdapter;
import com.belfoapps.qarib.views.MainActivity;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class OnBoardingFragment extends Fragment {
    private static final String TAG = "OnBoardingFragment";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainListener listener;
    private OnBoardingFragmentBinding mBinding;
    private OnBoardingPagerAdapter mAdapter;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Set ViewBinding
        mBinding = OnBoardingFragmentBinding.inflate(inflater, container, false);

        //Init ViewPager
        initViewPager();

        //Init Listeners
        initListeners();

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void initViewPager() {
        ArrayList<Hype> hypes = new ArrayList<>();

        //Add HypeFrags
        TypedArray imgs = getResources().obtainTypedArray(R.array.boarding_images);
        for (int i = 0; i < getResources().getStringArray(R.array.boarding_titles).length; i++) {
            hypes.add(new Hype(getResources().getStringArray(R.array.boarding_titles)[i],
                    getResources().getStringArray(R.array.boarding_descriptions)[i],
                    imgs.getResourceId(i, -1)));
        }
        imgs.recycle();

        //Set Adapter
        mAdapter = new OnBoardingPagerAdapter(requireActivity(), hypes);

        //Integrate Adapter with ViewPager
        mBinding.viewpager.setAdapter(mAdapter);

        //Integrate Tablayout with ViewPager
        new TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager,
                ((tab, position) -> {
                })).attach();

        //Hide Back
        mBinding.back.setVisibility(View.INVISIBLE);
        mBinding.next.setTag(0);

    }

    public void initListeners() {
        mBinding.back.setOnClickListener(v ->
                mBinding.viewpager.setCurrentItem(mBinding.viewpager.getCurrentItem() - 1, true));

        mBinding.viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    mBinding.back.setVisibility(View.INVISIBLE);
                    mBinding.skip.setVisibility(View.VISIBLE);
                    mBinding.next.setText("Next");
                    mBinding.next.setTag(0);
                } else if (position < mAdapter.getItemCount() - 1) {
                    mBinding.back.setVisibility(View.VISIBLE);
                    mBinding.skip.setVisibility(View.VISIBLE);
                    mBinding.next.setText("Next");
                    mBinding.next.setTag(0);
                } else if (position == mAdapter.getItemCount() - 1) {
                    mBinding.back.setVisibility(View.VISIBLE);
                    mBinding.skip.setVisibility(View.INVISIBLE);
                    mBinding.next.setText("Start");
                    mBinding.next.setTag(1);
                }
            }
        });

        mBinding.next.setOnClickListener(v -> {
            if (v.getTag().equals(0))
                mBinding.viewpager.setCurrentItem(mBinding.viewpager.getCurrentItem() + 1);
            else {
                listener.start();
            }
        });

        mBinding.skip.setOnClickListener(v -> mBinding.viewpager.setCurrentItem(mAdapter.getItemCount() - 1));
    }

}