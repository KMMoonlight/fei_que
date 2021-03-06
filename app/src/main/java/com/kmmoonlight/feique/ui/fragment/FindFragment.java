package com.kmmoonlight.feique.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.kmmoonlight.entity.BannerRepo;
import com.kmmoonlight.entity.HotRepo;
import com.kmmoonlight.entity.RecommendRepo;
import com.kmmoonlight.feique.R;
import com.kmmoonlight.feique.databinding.FragmentFindLayoutBinding;
import com.kmmoonlight.feique.ui.FindDocActivity;
import com.kmmoonlight.feique.ui.adapter.DocAdapter;
import com.kmmoonlight.feique.ui.adapter.MyViewPagerAdapter;
import com.kmmoonlight.feique.ui.base.BaseFragment;
import com.kmmoonlight.feique.view_model.BannerViewModel;
import com.kmmoonlight.feique.view_model.HotViewModel;
import com.kmmoonlight.feique.view_model.RecommendViewModel;
import com.stx.xhb.androidx.XBanner;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class FindFragment extends BaseFragment {

    private FragmentFindLayoutBinding binding;
    private DocAdapter docAdapter;
    private DocAdapter hotAdapter;
    private List<RecommendRepo.DataBean.DocsBean> recommendRepoList;
    private List<RecommendRepo.DataBean.DocsBean> hotRepoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindLayoutBinding.inflate(inflater, container, false);
        initView(inflater);
        return binding.getRoot();
    }

    private void initView(LayoutInflater layoutInflater) {
        //-----------Data Init---------------------

        List<View> viewList = new ArrayList<>();
        recommendRepoList = new ArrayList<>();
        hotRepoList = new ArrayList<>();
        docAdapter = new DocAdapter(recommendRepoList, getActivity());
        hotAdapter = new DocAdapter(hotRepoList, getActivity());

        //----------View Init----------------------


        View view_hot = layoutInflater.inflate(R.layout.doc_list_layout, null);
        View view_recommend = layoutInflater.inflate(R.layout.doc_list_layout, null);

        ListView lv_recommend = view_recommend.findViewById(R.id.lv_doc);
        lv_recommend.setAdapter(docAdapter);

        ListView lv_hot = view_hot.findViewById(R.id.lv_doc);
        lv_hot.setAdapter(hotAdapter);

        viewList.add(view_hot);
        viewList.add(view_recommend);

        //------------View Action-------------------

        BannerViewModel bannerViewModel = ViewModelProviders.of(this).get(BannerViewModel.class);
        RecommendViewModel recommendViewModel = ViewModelProviders.of(this).get(RecommendViewModel.class);
        HotViewModel hotViewModel= ViewModelProviders.of(this).get(HotViewModel.class);

        bannerViewModel.getViewModel().observe(this, new Observer<BannerRepo>() {
            @Override
            public void onChanged(BannerRepo bannerRepo) {
                binding.xBanner.setBannerData(bannerRepo.getData());
                binding.xBanner.loadImage(new XBanner.XBannerAdapter() {
                    @Override
                    public void loadBanner(XBanner banner, Object model, View view, int position) {
                        Glide.with(getActivity())
                                .load(bannerRepo.getData().get(position).getXBannerUrl())
                                .into((ImageView) view);
                    }
                });
                binding.xBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
                    @Override
                    public void onItemClick(XBanner banner, Object model, View view, int position) {
                        //跳转
                        Intent intent = new Intent(getActivity(), FindDocActivity.class);
                        intent.putExtra("id", bannerRepo.getData().get(position).getId());
                        intent.putExtra("book_id", bannerRepo.getData().get(position).getBook().getId());
                        startActivity(intent);
                    }
                });
            }
        });

        recommendViewModel.getViewModel().observe(this, new Observer<RecommendRepo>() {
            @Override
            public void onChanged(RecommendRepo recommendRepo) {
                recommendRepoList.clear();
                recommendRepoList.addAll(recommendRepo.getData().getDocs());
                docAdapter.notifyDataSetChanged();
            }
        });
        hotViewModel.getViewModel().observe(this, new Observer<HotRepo>() {
            @Override
            public void onChanged(HotRepo hotRepo) {
                hotRepoList.clear();
                hotRepoList.addAll(hotRepo.getData());
                hotAdapter.notifyDataSetChanged();
            }
        });

        //ViewPager 和 TabLayout绑定
        binding.findVp.setAdapter(new MyViewPagerAdapter(viewList));
        binding.findTab.setupWithViewPager(binding.findVp);
        binding.findTab.getTabAt(0).setText(getResources().getString(R.string.hot));
        binding.findTab.getTabAt(1).setText(getResources().getString(R.string.recommend));


        lv_hot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FindDocActivity.class);
                intent.putExtra("id", hotRepoList.get(position).getId());
                intent.putExtra("book_id", hotRepoList.get(position).getBook().getId());
                startActivity(intent);
            }
        });

        lv_recommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), FindDocActivity.class);
                intent.putExtra("id", recommendRepoList.get(position).getId());
                intent.putExtra("book_id", recommendRepoList.get(position).getBook().getId());
                startActivity(intent);
            }
        });

    }

}
