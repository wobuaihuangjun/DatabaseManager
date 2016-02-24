package com.huangzj.databaseupgrade.dao.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 市信息数据表
 * <p/>
 * Created by lhd on 2015/10/9.
 */
@DatabaseTable(tableName = "city")
public class City {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(unique = true)
    private String cityNo;

    @DatabaseField
    private String cityName;

    @DatabaseField
    private String provinceName;

    @DatabaseField
    private String addColumn1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", cityNo='" + cityNo + '\'' +
                ", cityName='" + cityName + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", addColumn1='" + addColumn1 + '\'' +
                '}';
    }
}
