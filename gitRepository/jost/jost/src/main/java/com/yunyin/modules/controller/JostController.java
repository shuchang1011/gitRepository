package com.yunyin.modules.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunyin.common.api.vo.Result;
import com.yunyin.common.aspect.annotation.AutoLog;
import com.yunyin.common.util.DateUtils;
import com.yunyin.common.util.HttpRequestUtil;
import com.yunyin.common.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 条码枪请求nav
 * @Author: marco
 * @Date: 2019-06-12
 * @Version: V1.0
 */
@RestController
@RequestMapping("/jost")
public class JostController {

    @Value("${JOST_HTTP_PORT}")
    private int JOST_HTTP_PORT;
    @Value("${JOST_HTTP_PATH}")
    private String JOST_HTTP_PATH;
    @Value("${JOST_HTTP_USERNAME}")
    private String JOST_HTTP_USERNAME;
    @Value("${JOST_HTTP_PASSWORD}")
    private String JOST_HTTP_PASSWORD;

    /**
     * 通过No查询nav发货行数据
     * @param No
     * @return
     */
    @AutoLog(value = "通过No查询nav发货行数据")
    @GetMapping(value = "/shipment/shipmentList")
    public Result<Map<String, Object>> shipmentList(@RequestParam(name = "No", required = true) String No,
                                                    HttpServletRequest req) throws ParseException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();

        if (StringUtils.isEmpty(String.valueOf(No))) {
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("records", null);
            res.put("total", 0);
            result.setSuccess(true);
            result.setResult(res);
            return result;
        }

        List<Map<String, Object>> shipmentValues = new ArrayList<Map<String, Object>>();
        shipmentValues = getReturnValues("WhseShipmentList", "No%20eq%20'" + No + "'");
        shipmentValues = shipmentValues.stream().filter((Map<String, Object> a) -> "0".equals(a.get("Shipped").toString())
        ).collect(Collectors.toList());

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("records", shipmentValues);
        res.put("total", shipmentValues.size());
        result.setResult(res);
        result.setSuccess(true);

        return result;
    }

    /**
     * 通过No查询nav发货行数据
     * @param No
     * @return
     */
    @AutoLog(value = "查询nav发货SN行数据")
    @GetMapping(value = "/shipment/shipSNList")
    public Result<Map<String, Object>> shipSNList(@RequestParam(name = "No", required = true) String No,
                                                  @RequestParam(name = "Line_No", required = true) int Line_No,
                                                  HttpServletRequest req) throws ParseException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();

        if (StringUtils.isEmpty(String.valueOf(No))) {
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("records", null);
            res.put("total", 0);
            result.setSuccess(true);
            result.setResult(res);
            return result;
        }

        List<Map<String, Object>> shipmentValues = new ArrayList<Map<String, Object>>();
        shipmentValues = getReturnValues("WhseShipSNList", "Whse_Shipment_No%20eq%20'" + No + "'%20and%20Whse_Shipment_Line_No%20eq%20" + Line_No);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("records", shipmentValues);
        res.put("total", shipmentValues.size());
        result.setResult(res);
        result.setSuccess(true);

        return result;
    }

    /**
     * 通过No查询nav发货行数据
     * @param No
     * @return
     */
    @AutoLog(value = "通过No查询nav发货行数据")
    @GetMapping(value = "/output/outputList")
    public Result<Map<String, Object>> outputList(@RequestParam(name = "No", required = true) String No,
                                                    HttpServletRequest req) throws ParseException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();

        if (StringUtils.isEmpty(String.valueOf(No))) {
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("records", null);
            res.put("total", 0);
            result.setSuccess(true);
            result.setResult(res);
            return result;
        }

        List<Map<String, Object>> shipmentValues = new ArrayList<Map<String, Object>>();
        shipmentValues = getReturnValues("OutputList", "Prod_Order_No%20eq%20'" + No + "'");
        shipmentValues = shipmentValues.stream().filter((Map<String, Object> a) -> Integer.parseInt(a.get("Quantity").toString()) > 0
        ).collect(Collectors.toList());

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("records", shipmentValues);
        res.put("total", shipmentValues.size());
        result.setResult(res);
        result.setSuccess(true);

        return result;
    }

    /**
     * 校验SN码是否有效
     * @param No
     * @return
     */
    @AutoLog(value = "校验SN码是否有效")
    @GetMapping(value = "/checkSNCode")
    public Result<Map<String, Object>> checkSNCode(@RequestParam(name = "No", required = true) String No,
                                                   @RequestParam(name = "ItemNo", required = true) String ItemNo,
                                                   HttpServletRequest req) throws ParseException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();

        List<Map<String, Object>> shipmentValues = new ArrayList<Map<String, Object>>();
        shipmentValues = getReturnValues("SNEntry", "SN_No%20eq%20'" + No + "'%20and%20Item_No%20eq%20'" + ItemNo + "'");
        if(shipmentValues.size() == 0) {
            result.error500("未找到对应SN编号");
            return result;
        } else if(Boolean.parseBoolean(shipmentValues.get(0).get("Shipped").toString())){
            result.error500("物料已发货");
            return result;
        }

        result.success("校验成功");
        return result;
    }

    /**
     * 提交物料至nav保存
     * @param type
     * @param map
     * @return
     */
    @AutoLog(value = "提交物料至nav保存")
    @PostMapping(value = "/{type}/commitItems")
    public Result<Map<String, Object>> commitItems(@PathVariable(name = "type", required = true) String type,
                                                   @RequestBody Map<String, Object> map,
                                                   HttpServletRequest req) throws ParseException {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Map<String, Object>> shipmentList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> shipmentSNList = new ArrayList<Map<String, Object>>();
        String[] SNList = {};
        String localTime = DateUtils.getDataString(DateUtils.datetimeFormat);
        String utcTime = DateUtils.localToUTC(localTime);
        StringBuilder localSb = new StringBuilder(localTime);
        StringBuilder utcSb = new StringBuilder(utcTime);

        shipmentList = (List<Map<String, Object>>)map.get("shipmentList");

       /* paramMap.put("Type", type);
        paramMap.put("DocNo", "SH000003");//单号
        paramMap.put("DocLineNo", 30000);//行号
        paramMap.put("SourceNo", "104019");//源单号
        paramMap.put("SourceLineNo", "30000");//源行号
        paramMap.put("ItemNo", "LS-120");//物料编号
        paramMap.put("ItemDescription", "");//物料说明
        paramMap.put("Quantity", "6");
        paramMap.put("ITEMSNNo", "LS-100-001");
        paramMap.put("DocDate", "2019-07-18T00:00:00");
        paramMap.put("CreationDate", "2019-07-18T00:00:00");
        paramMap.put("CreationTime", "2019-07-18T08:00:00Z");
        paramMap.put("CreationID", "TU001");
        paramMap.put("TerminalID", "T002");*/


        paramMap.put("Type", type);
        for(Map<String, Object> shipment : shipmentList) {
            paramMap.put("DocNo", shipment.get("No"));//单号
            paramMap.put("DocLineNo", Integer.parseInt(shipment.get("Line_No").toString()));//行号
            paramMap.put("SourceNo", shipment.get("Source_No"));//源单号
            paramMap.put("SourceLineNo", shipment.get("Source_Line_No").toString());//源行号
            paramMap.put("ItemNo", shipment.get("Item_No"));//物料编号
            paramMap.put("ItemDescription", "");//物料说明
            shipmentSNList = (List<Map<String, Object>>)shipment.get("shipmentSNList");
            for(Map<String, Object> shipmentSN : shipmentSNList) {
                paramMap.put("Quantity", shipmentSN.get("Qty_to_Ship").toString());
                SNList = (String[])shipmentSN.get("SNList");
                for(String SN : SNList) {
                    localSb = new StringBuilder(DateUtils.getDataString(DateUtils.datetimeFormat));
                    utcSb = new StringBuilder(DateUtils.localToUTC(DateUtils.getDataString(DateUtils.datetimeFormat)));
                    localTime = localSb.replace(9,10,"T").replace(localSb.length() - 2,localSb.length() - 1,"Z").toString();
                    utcTime = utcSb.replace(9,10,"T").toString();
                    paramMap.put("ITEMSNNo", SN);
                    paramMap.put("DocDate", utcTime);
                    paramMap.put("CreationDate", utcTime);
                    paramMap.put("CreationTime", localTime);
                    paramMap.put("CreationID", "TU001");
                    paramMap.put("TerminalID", "T002");
                    JSONObject jsonBody = HttpRequestUtil.httpRequestPost(JOST_HTTP_PORT, JOST_HTTP_PATH, JSONObject.toJSONString(paramMap) ,"WhseRS_Buffer", JOST_HTTP_USERNAME, JOST_HTTP_PASSWORD);
                }
            }
        }

        return result;
    }

    public Map<String, Object> JsonObjectToMap(JSONObject jsonObject) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Iterator it = jsonObject.keySet().iterator();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            String value = jsonObject.get(key).toString();
            returnMap.put(key, value);
        }
        return returnMap;
    }

    public List<Map<String, Object>> getReturnValues(String apiUrl, String filter){
        Map<String, Object> returnMap = new HashMap<>();
        JSONObject jsonBody = new JSONObject();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("$filter", filter);
        jsonBody = HttpRequestUtil.httpRequestGet(JOST_HTTP_PORT, JOST_HTTP_PATH, paramsMap, apiUrl, JOST_HTTP_USERNAME, JOST_HTTP_PASSWORD);

        returnMap = JsonObjectToMap(jsonBody);
        List<Map<String, Object>> returnValues = new ArrayList<Map<String, Object>>();
        returnValues = JsonUtils.toListMap(returnMap.get("value").toString());
        return returnValues;
    }

}

