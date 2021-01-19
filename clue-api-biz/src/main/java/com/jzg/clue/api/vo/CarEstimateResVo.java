package com.jzg.clue.api.vo;

import com.jzg.framework.core.entity.BaseEntity;

import java.util.List;

/**
 * Created by dougp on 2018/10/24.
 */
public class CarEstimateResVo extends BaseEntity {

    /**
     * result : [{"brandName":"奔驰","FirstRegistrationTimeStr":"2015/5/12 0:00:00","cclid":111,"seriesName":"奔驰C级","expectationPrice":56.3,"guidanceprice":56.2,"systemappraisalprice":56.1,"introduce":"驾驭改变!奔驰长轴距奔驰c，星时享\u201c先享后选-弹性购车新方案\u201d，月付款低至388元.2920mm加长轴距，多功能触摸板，LED智能照明系统，长轴距C级车，享受身与心的非凡之旅!","mobile":"15901073200","cityID":410200,"provinceID":410000,"yearID":2015,"condition":"B","cityName":"开封","specName":"2015款 C 260 L 运动型","createtimeStr":"2016/8/3 15:34:52","name":"周思洋","vin":"201608031533A0000","provinceName":"河南","mileage":11.2}]
     * returncode : 0
     * message : 查询成功
     */
    private List<ResultEntity> result;
    private int returncode;
    private String message;

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public int getReturncode() {
        return returncode;
    }

    public String getMessage() {
        return message;
    }

    public static class ResultEntity  {
        /**
         * brandName : 奔驰
         * FirstRegistrationTimeStr : 2015/5/12 0:00:00
         * cclid : 111
         * seriesName : 奔驰C级
         * expectationPrice : 56.3
         * guidanceprice : 56.2
         * systemappraisalprice : 56.1
         * introduce : 驾驭改变!奔驰长轴距奔驰c，星时享“先享后选-弹性购车新方案”，月付款低至388元.2920mm加长轴距，多功能触摸板，LED智能照明系统，长轴距C级车，享受身与心的非凡之旅!
         * mobile : 15901073200
         * cityID : 410200
         * provinceID : 410000
         * yearID : 2015
         * condition : B
         * cityName : 开封
         * specName : 2015款 C 260 L 运动型
         * createtimeStr : 2016/8/3 15:34:52
         * name : 周思洋
         * vin : 201608031533A0000
         * provinceName : 河南
         * mileage : 11.2
         */
        private String brandName;
        private String FirstRegistrationTimeStr;
        private int cclid;
        private String seriesName;
        private double expectationPrice;
        private double guidanceprice;
        private double systemappraisalprice;
        private String introduce;
        private String mobile;
        private int cityID;
        private int provinceID;
        private int yearID;
        private String condition;
        private String cityName;
        private String specName;
        private String createtimeStr;
        private String name;
        private String vin;
        private String provinceName;
        private String mileage;

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public void setFirstRegistrationTimeStr(String FirstRegistrationTimeStr) {
            this.FirstRegistrationTimeStr = FirstRegistrationTimeStr;
        }

        public void setCclid(int cclid) {
            this.cclid = cclid;
        }

        public void setSeriesName(String seriesName) {
            this.seriesName = seriesName;
        }

        public void setExpectationPrice(double expectationPrice) {
            this.expectationPrice = expectationPrice;
        }

        public void setGuidanceprice(double guidanceprice) {
            this.guidanceprice = guidanceprice;
        }

        public void setSystemappraisalprice(double systemappraisalprice) {
            this.systemappraisalprice = systemappraisalprice;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setCityID(int cityID) {
            this.cityID = cityID;
        }

        public void setProvinceID(int provinceID) {
            this.provinceID = provinceID;
        }

        public void setYearID(int yearID) {
            this.yearID = yearID;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public void setSpecName(String specName) {
            this.specName = specName;
        }

        public void setCreatetimeStr(String createtimeStr) {
            this.createtimeStr = createtimeStr;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setVin(String vin) {
            this.vin = vin;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public void setMileage(String mileage) {
            this.mileage = mileage;
        }

        public String getBrandName() {
            return brandName;
        }

        public String getFirstRegistrationTimeStr() {
            return FirstRegistrationTimeStr;
        }

        public int getCclid() {
            return cclid;
        }

        public String getSeriesName() {
            return seriesName;
        }

        public double getExpectationPrice() {
            return expectationPrice;
        }

        public double getGuidanceprice() {
            return guidanceprice;
        }

        public double getSystemappraisalprice() {
            return systemappraisalprice;
        }

        public String getIntroduce() {
            return introduce;
        }

        public String getMobile() {
            return mobile;
        }

        public int getCityID() {
            return cityID;
        }

        public int getProvinceID() {
            return provinceID;
        }

        public int getYearID() {
            return yearID;
        }

        public String getCondition() {
            return condition;
        }

        public String getCityName() {
            return cityName;
        }

        public String getSpecName() {
            return specName;
        }

        public String getCreatetimeStr() {
            return createtimeStr;
        }

        public String getName() {
            return name;
        }

        public String getVin() {
            return vin;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public String getMileage() {
            return mileage;
        }
    }
}
