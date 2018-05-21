package com.zaaach.citypicker.sfp;

import android.content.Context;
import android.content.SharedPreferences;

import com.zaaach.citypicker.db.DBManager;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by copo on 18-5-18.
 */

public class SpfManager {
    //北京
    public static final String BEIJING = "11000000";
    //天津
    public static final String TIANJIN = "12000000";
    //上海
    public static final String SHANGHAI = "31000000";
    //重庆
    public static final String CHONGQING = "50000000";

    private static final String EKK_CONFIG = "ekk_config";
    private static final String INDEX = "index";
    private Context mContext;
    private final SharedPreferences.Editor editor;
    private final DBManager dbManager;
    private final SharedPreferences spf;


    public SpfManager(Context mContext){
        this.mContext = mContext;
        spf = mContext.getSharedPreferences(EKK_CONFIG, Context.MODE_PRIVATE);
        editor = spf.edit();
        dbManager = new DBManager(mContext);
    }

    //需要外部App手动调用
    public void insertCity(String id){
        List<City> allCities = getAllCities();
//        for(City city:allCities){
//            //已存在城市
//            if(id.equals(city.getCode())) {
//                deleteCityByid(city.getCode());//配置文件中删除
//                allCities.remove(city);//内存中删除
//            }
//        }

        for(int i=0;i<allCities.size();i++){
            City city = allCities.get(i);
            if(city.getCode().equals(id)){
                deleteCityByid(city.getCode());//配置文件中删除
                allCities.remove(city);
            }
        }

        if(allCities.size()<4){
            editor.putString(getCurrIndex(),id);
        }else {
            deleteFirstCity();
            editor.putString(getCurrIndex(),id);
        }
        editor.commit();
    }

    //删除id
    private void deleteFirstCity(){
        editor.remove(getMinIndex());
        editor.commit();
    }


    private void deleteCityByid(String id){
        Map<String, ?> all = spf.getAll();
        Iterator<String> keyIterator = all.keySet().iterator();
        while (keyIterator.hasNext()){
            String keyId = keyIterator.next();
            if(!keyId.equals("index")){
                if(spf.getString(keyId,"").equals(id)){
                    editor.remove(keyId);
                    editor.commit();
                }
            }
        }
    }

    //当前留空下标
    private String getCurrIndex(){
        int index = spf.getInt(INDEX, 0);
        editor.putInt(INDEX,index+1);
        return index+"";
    }

    //查询所有
    public List<City> getAllCities(){
        List<City> allCities;
        Map<Integer,City> indexCitiesMap = new HashMap<>();
        Map<String, ?> all = spf.getAll();
        Iterator<String> keyIterator = all.keySet().iterator();
        while(keyIterator.hasNext()){
            String keyId = keyIterator.next();
            if(!keyId.equals("index")){
                String cityId = spf.getString(keyId, "");
                City city = getCity(cityId);
                if(city!=null){
                    indexCitiesMap.put(Integer.valueOf(keyId),city);
                }
            }
        }
        allCities = sortCities(indexCitiesMap);
        return allCities;
    }

    private List<City> sortCities(Map<Integer,City> cityMap){
        Iterator<Integer> keyIterator = cityMap.keySet().iterator();
        List<Integer> integers = new ArrayList<>();
        List<City> sortedCities = new ArrayList<>();
        while (keyIterator.hasNext()){
            integers.add(keyIterator.next());
        }
        Collections.sort(integers,new CompareInteger());
        for(int i:integers){
            sortedCities.add(cityMap.get(i));
        }
        return sortedCities;
    }

    //获取当前已存城市下标的最小值
    private String getMinIndex(){
        int min = 0;
        List<Integer> indexs = new ArrayList<>();
        Map<String, ?> all = spf.getAll();
        Iterator<String> keyIterator = all.keySet().iterator();
        while (keyIterator.hasNext()){
            String keyId = keyIterator.next();
            if(!keyId.equals("index")){
                indexs.add(Integer.valueOf(keyId));
            }
        }
        if(indexs.size()>0){
            min = indexs.get(0);
            for(int i:indexs){
                if(i<min) min = i;
            }
        }
        return String.valueOf(min);
    }

    private City getCity(String id){
        City city = null;
        switch (id){
            case BEIJING:
                city = new City("北京市", "北京", "beijng","11000000","11000000");
                break;
            case TIANJIN:
                city = new City("天津市", "天津", "tianjin","12000000","12000000");
                break;
            case SHANGHAI:
                city = new City("上海市", "上海", "shanghai","31000000","31000000");
                break;
            case CHONGQING:
                city = new City("重庆市","重庆","chongqing","50000000","50000000");
                break;
            default:
                city = dbManager.searchCityById(id);
        }
        return city;
    }

    private class CompareInteger implements Comparator<Integer>{

        @Override
        public int compare(Integer integer, Integer t1) {
            return t1.compareTo(integer);
        }
    }

}
