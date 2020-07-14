package com.yunyin.common.util;

import com.alibaba.fastjson.JSON;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author Grayson
 */
public class JsonUtils {
    /**
     * 把string转ListMap
     *
     * @param json
     * @return
     */
    public static List<Map<String, Object>> toListMap(String json) {
        List<Object> list = JSON.parseArray(json);

        List<Map<String, Object>> listm = new ArrayList<Map<String, Object>>();
        for (Object object : list) {
            Map<String, Object> ret = (Map<String, Object>) object; //取出list里面的值转为map
            listm.add(ret);
        }
        return listm;
    }

    /**
     * json里需要加固定列的方法
     *
     * @param map 需要加固定列的方法
     * @return
     */
    public static Map<String, Object> addMapCommonField(Map<String, Object> map) {
        map.put("createBy", "weihan");
        map.put("createTime", DateUtils.now());
        map.put("updateBy", "weihan");
        map.put("updateTime", DateUtils.now());
        map.put("isDelete", 0);
        return map;
    }

    /**
     * 根据用户角色筛选数据
     * @param sysUser 登录用户
     * @param roleList 用户具有的角色
     * @param list 需要筛选的数据
     * @return
     */
    public static List<Map<String, Object>> filterByUserRole(String OrderNo, List<String> roleList, List<Map<String, Object>> list) {
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        // 过滤获取isDelete=0且当前用户所在供应商的数据
        boolean isAdmin = false;
        for(String role : roleList) {
            //admin管理员可以看到全部人员
            if(role.equals("admin")) {
                records = list.stream().filter((Map<String, Object> a) -> "0".equals(a.get("isDelete").toString())
                ).collect(Collectors.toList());
                isAdmin = true;
                break;
            }
        }
        if(isAdmin == false) {
            records = list.stream().filter((Map<String, Object> a) -> "0".equals(a.get("isDelete").toString()) && (a.get("OrderNo").toString().equals(OrderNo))
            ).collect(Collectors.toList());
        }
        return records;
    }


    /**
     * json内容比较方法
     *
     * @param newList 接口返回的json
     * @param oldStr  数据库中的json
     * @param no      字段主键
     * @return
     */
    public static String compareToMap(List<Map<String, Object>> newList, String oldStr, String no) throws ParseException {
        List<Map<String, Object>> oldList = JsonUtils.toListMap(oldStr);
        boolean flag = false;
        for (Map<String, Object> newMap : newList) {
            flag = false;
            for (Map<String, Object> oldMap : oldList) {
                if (oldMap.get(no).toString().equals(newMap.get(no).toString())) {
                    for (String key : newMap.keySet()) {
                        if (oldMap.containsKey(key)) {
                            oldMap.put(key, newMap.get(key));
                        }
                    }
                    flag = true;
                    break;
                }
            }
            //数据库没有进行add
            if (!flag) {
                oldList.add(newMap);
            }
        }
        return JSON.toJSONString(oldList);
    }

    /**
     * json内容比较方法
     *
     * @param newMap 接口返回的json
     * @param oldStr  数据库中的json
     * @param fieldMap  数据库中的默认字段
     * @param headKey      字段主键
     * @return
     */
    public static String compareToHeadMap(Map<String, Object> newMap, String oldStr,Map<String, Object> fieldMap, String headKey[]) throws ParseException {
        List<Map<String, Object>> oldList = JsonUtils.toListMap(oldStr);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        int flag = 0;
        for (Map<String, Object> oldMap : oldList) {
            flag = 0;
            returnMap = fieldMap;
            returnMap.putAll(newMap);
            for(int i = 0; i < headKey.length; i++) {
                if (oldMap.get(headKey[i]).toString().equals(returnMap.get(headKey[i]).toString())) {
                    flag++;
                }else{
                    flag = 0;
                    break;
                }
            }
            if(flag == headKey.length) {
                for (String key : returnMap.keySet()) {
                    if (oldMap.containsKey(key)) {
                        oldMap.put(key, returnMap.get(key));
                    }
                }
                oldMap.put("isDelete", 0);
                oldMap.put("updateBy", "weihan");
                oldMap.put("updateTime", DateUtils.now());
                break;
            }
        }
        if (flag == 0) {
            JsonUtils.addMapCommonField(returnMap);
            oldList.add(returnMap);
        }
        return JSON.toJSONString(oldList);
    }

    /**
     * json内容比较方法
     *
     * @param newList 接口返回的json
     * @param oldStr  数据库中的json
     * @param fieldMap 单表所有字段
     * @param headKey  字段主键
     * @return
     */
    public static String compareToLineMap(List<Map<String, Object>> newList, String oldStr, Map<String,Object> fieldMap, String headKey[]) throws ParseException {
        List<Map<String, Object>> oldList = JsonUtils.toListMap(oldStr);
        List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        int headFlag = 0;
        int lineFlag = 0;
        for(int k = 0; k < newList.size(); k++) {
            newMap = new HashMap<String, Object>();
            newMap.putAll(fieldMap);
            newMap.putAll(newList.get(k));

            if(newMap.containsKey("confirmed")) {
                newMap.put("confirmed", "0");
            }
            String  LineKey[] = newMap.get("formkey").toString().split(",");
            for (int j = 0; j < oldList.size(); j++) {
                headFlag = 0;
                for(int i = 0; i < headKey.length; i++) {
                    if (oldList.get(j).get(headKey[i]).toString().equals(newMap.get(headKey[i]).toString())) {
                        headFlag++;
                    }else{
                        headFlag = 0;
                        break;
                    }
                }
                if (headFlag == headKey.length) {
                    lineFlag = 0;
                    for(int i1 = j; i1 < oldList.size(); i1++) {
                        for(int i = 0;i < LineKey.length; i++){
                            if (oldList.get(i1).get( LineKey[i]).toString().equals(newMap.get( LineKey[i]).toString())) {
                                lineFlag++;
                            }else{
                                lineFlag=0;
                                break;
                            }
                        }
                        if (lineFlag == LineKey.length) {
                            for (String key : newMap.keySet()) {
                                oldList.get(i1).put(key, newMap.get(key));
                            }
                            oldList.get(i1).put("updateBy", "weihan");
                            oldList.get(i1).put("updateTime", DateUtils.now());
                            break;
                        }
                    }
                    if(lineFlag == 0) {
                        JsonUtils.addMapCommonField(newMap);
                        oldList.add(newMap);
                    }
                    break;
                }
            }
            if(headFlag == 0) {
                JsonUtils.addMapCommonField(newMap);
                tempList.add(newMap);
            }
        }
        oldList.addAll(tempList);
        return JSON.toJSONString(oldList);
    }
}
