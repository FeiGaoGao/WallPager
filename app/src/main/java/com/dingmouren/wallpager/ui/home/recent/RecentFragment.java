package com.dingmouren.wallpager.ui.home.recent;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dingmouren.wallpager.MyApplication;
import com.dingmouren.wallpager.R;
import com.dingmouren.wallpager.base.BaseFragment;
import com.dingmouren.wallpager.model.bean.UnsplashResult;
import com.dingmouren.wallpager.ui.dagger.DaggerMainActivityComponent;
import com.dingmouren.wallpager.ui.dagger.MainActivityModule;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by dingmouren on 2017/5/2.
 */

public class RecentFragment extends BaseFragment implements RecentContract.View{

    private static final int DELAY_TIME_OUT = 1500;
    private static final String CATEGOTY_NUM = "categoty_id";
    @BindView(R.id.recycler) RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fab) FloatingActionButton mFab;

    private Handler mHandler;
    private DelayRunnable mDelayRunnable;
    @Inject
    RecentAdapter mHomeAdapter;
    @Inject
    RecentPresenter mRecentPresenter;
    private int mCategotyId;
    private LinearLayoutManager mLinearLayoutManager;

    public static RecentFragment newInstance(int category){
        RecentFragment fragment = new RecentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CATEGOTY_NUM,category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void init() {
        if (getArguments() != null){
            mCategotyId = getArguments().getInt(CATEGOTY_NUM);
        }
        mHandler = new Handler();
        mDelayRunnable = new DelayRunnable(this);
    }

    @Override
    public int requestLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView() {
        mLinearLayoutManager = new LinearLayoutManager(MyApplication.sContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mHomeAdapter);
    }

    @Override
    public void initInjector() {
        DaggerMainActivityComponent.builder()
                .mainActivityModule(new MainActivityModule(this))
                .applicationComponent(((MyApplication)this.getActivity().getApplication()).getApplicationComponent())//这里依赖的是ApplicationComponent
                .build().inject(this);
    }

    @Override
    public void initListener() {
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mFab.setOnClickListener(v -> mRecyclerView.smoothScrollToPosition(0));
    }

    @Override
    public void initData() {
        setRefresh(true);
        mRecentPresenter.setCategory(mCategotyId);
        mRecentPresenter.requestData();
    }


    @Override
    public void setRefresh(boolean refresh) {
        if (refresh)
            mSwipeRefreshLayout.setRefreshing(true);
        else
            mHandler.postDelayed(mDelayRunnable,DELAY_TIME_OUT);
    }


    @Override
    public void setData(List<UnsplashResult> data) {
        mHomeAdapter.setList(data);
        mHomeAdapter.notifyDataSetChanged();
        setRefresh(false);
    }

    @Override
    public int getCategoryId() {
        return this.mCategotyId;
    }


    /**
     * 延时任务
     */
    public static class DelayRunnable implements Runnable{
       private WeakReference<RecentFragment> mHomeFragmentWeakReference;
       public DelayRunnable(RecentFragment fragment){
           this.mHomeFragmentWeakReference = new WeakReference<RecentFragment>(fragment);
       }
       @Override
       public void run() {
            RecentFragment homeFragment = mHomeFragmentWeakReference.get();
           if (homeFragment != null){
               homeFragment.mSwipeRefreshLayout.setRefreshing(false);
           }
       }
   }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mLinearLayoutManager.getItemCount() > 1){
                int lastIndex = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastIndex + 1 == mLinearLayoutManager.getItemCount()){
                    mRecentPresenter.requestData();
                }
            }
        }
    };
}
