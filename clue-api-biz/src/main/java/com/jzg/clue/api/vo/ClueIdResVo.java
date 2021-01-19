package com.jzg.clue.api.vo;

import com.jzg.framework.core.entity.BaseEntity;

/**
 * @author mush
 * @time 2017/6/7 14:15
 * @describe 成功后返回线索Id
 */
public class ClueIdResVo extends BaseEntity {
    private int clueId;//线索Id

    public int getClueId() {
        return clueId;
    }

    public void setClueId(int clueId) {
        this.clueId = clueId;
    }
}
