package com.seclab.nmaping.adapter;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.seclab.nmaping.R;
import com.seclab.nmaping.bean.ScanBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScanResAdapter extends BaseQuickAdapter<ScanBean, BaseViewHolder> {


    public ScanResAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ScanBean scanBean) {

        baseViewHolder.setText(R.id.tv_scanTitle,scanBean.getIpName());

    }
}
