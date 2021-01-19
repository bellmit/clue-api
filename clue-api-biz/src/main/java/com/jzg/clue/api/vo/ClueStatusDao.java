package com.jzg.clue.api.vo;

import com.jzg.framework.core.vo.BaseVo;

/**
 * Created by JZG on 2017/5/24.
 */
public class ClueStatusDao implements BaseVo {

    private String Grounding;
    private int Groundingnum;
    private String Deal;

    public String getGrounding() {
        return Grounding;
    }

    public void setGrounding(String grounding) {
        Grounding = grounding;
    }

    public int getGroundingnum() {
        return Groundingnum;
    }

    public void setGroundingnum(int groundingnum) {
        Groundingnum = groundingnum;
    }

    public String getDeal() {
        return Deal;
    }

    public void setDeal(String deal) {
        Deal = deal;
    }
}
