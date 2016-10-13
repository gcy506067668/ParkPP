package com.park.letmesleep.vo;

/**
 * 结束停车Vo
 * Created by ansore on 16-9-12.
 */
public class EndParkVo extends CommonVo{

    //停车场Id
    private int parkId;
    //车位Id
    private int spaceId;
    //
    private String telephone;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getParkId() {
        return parkId;
    }

    public void setParkId(int parkId) {
        this.parkId = parkId;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }
}
