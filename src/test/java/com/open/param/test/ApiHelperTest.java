package com.open.param.test;

import com.open.json.api.JsonUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.api.ApiHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.param.DataType;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;

public class ApiHelperTest {

  private static ParamObject getResultParam() {
    return ParamObject.required(//
        ParamObject.required("status", "返回", //
            ParamPrimitive.required("status_code", DataType.Number, ""), //
            ParamPrimitive.required("status_reasion", DataType.String, "")//
        ), //
        buildResult()//
    );
  }

  private static Param buildResult() {
    return ParamObject.of("result", "返回数据", //
        ParamPrimitive.required("name", DataType.String, "姓名").setMax(5), //
        ParamPrimitive.required("age", DataType.Number, "年龄").setMin(0).setMax(120), //
        ParamArray.required("items", "商品列表", //
            ParamObject.required(//
                ParamPrimitive.required("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                ParamPrimitive.required("name", DataType.String, "商品名称").setMax(50)//
            )//
        ), //
        ParamArray.required("ids", "id列表", //
            ParamPrimitive.required(DataType.Number, null).setMax(100) //
        ), //
        ParamObject.of("extendMap", "扩展字段"), //
        ParamArray.of("array_any", "任意数组节点"), //
        ParamArray.of("array_any_simple", "任意数组节点"));
  }

  private static Object getResult() {
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三丰");
    map.put("C_", new Date());
    map.put("age", "100.11");
    List<Object> items = new ArrayList<>();
    Map<String, Object> item = new HashMap<>();
    item.put("id", "2");
    item.put("name", "手机");
    item.put("D_", new Date());
    items.add(item);
    map.put("items", items);
    List<Object> ids = new ArrayList<>();
    ids.add("100");
    map.put("ids", ids);
    map.put("EE__", new HashMap<>());

    Map<String, Object> extendMap = new HashMap<>();
    extendMap.put("a", 10);
    extendMap.put("obj", new HashMap<String, Object>());
    map.put("extendMap", extendMap);
    map.put("array_any", new Object[]{extendMap});
    map.put("array_any_simple", new int[]{1, 2, 3, 4, 5});
    return map;
  }

  private static String getResponseData() {
    Map<String, Object> dataResult = new HashMap<>();
    Map<String, Object> status = new HashMap<>();
    status.put("status_code", 100);
    status.put("A_", new Object());// 协议规范中没有的字段（会自动排除掉）
    status.put("B_", true);//// 协议规范中没有的字段（会自动排除掉）
    status.put("status_reasion", "参数错误");
    dataResult.put("status", status);
    dataResult.put("result", getResult());
    dataResult.put("other", new HashMap<>());
    return JsonUtils.stringify(dataResult);
  }

  @Test
  public void test_response() {
    System.out.println(getResultParam());
    String json = getResponseData();
    System.out.println(json);
    JsonNode jsonNode = JsonUtils.parser(json);
    Map<String, Object> map =
        ApiHelper.response(getResultParam()).checkResponse(jsonNode).extractResponse(jsonNode);
    System.out.println(JsonUtils.stringify(map));
    String expected =
        "{'result':{'array_any':[{'a':10,'obj':{}}],'array_any_simple':[1,2,3,4,5],'extendMap':{'a':10,'obj':{}},'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'},'status':{'status_code':100,'status_reasion':'参数错误'}}";
    expected = expected.replace("'", "\"");
    Assert.assertEquals(expected, JsonUtils.stringify(map));
  }

  private Param buildParam() {
    return ParamObject.required("objParam", "对象参数", //
        ParamPrimitive.required("name", DataType.String, "姓名").setMax(5), //
        ParamPrimitive.required("age", DataType.Number, "年龄").setMin(0).setMax(120), //
        ParamArray.required("items", "商品列表", //
            ParamObject.required(//
                ParamPrimitive.required("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                ParamPrimitive.required("name", DataType.String, "商品名称").setMax(50)//
            )//
        ), //
        ParamArray.required("ids", "id列表", //
            ParamPrimitive.required(DataType.Number,null).setMax(100) //
        ), //
        ParamObject.of("extendMap", "扩展Map(任意子节点)"), ParamArray.of("array_any", "任意数组类型"),
        ParamArray.of("array_any_simple", "任意数组类型"));
  }

  private HttpServletRequest buildHttpRequest() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三丰");
    map.put("age", "100.11");
    List<Object> items = new ArrayList<>();
    Map<String, Object> item = new HashMap<>();
    item.put("id", "2");
    item.put("name", "手机");
    items.add(item);
    map.put("items", items);
    List<Object> ids = new ArrayList<>();
    ids.add("100");
    map.put("ids", ids);
    {
      Map<String, Object> extendMap = new HashMap<>();
      extendMap.put("a", 10);
      extendMap.put("obj", new HashMap<String, Object>());
      map.put("extendMap", extendMap);
      map.put("array_any", new Object[]{extendMap});
      map.put("array_any_simple", new int[]{1, 2, 3, 4, 5});
    }
    String json = JsonUtils.stringify(map);
    request.addParameter("objParam", json);
    return request;
  }

  @Test
  public void test_param() {
    Map<String, Object> map = ApiHelper.request(buildParam()).checkRequest(buildHttpRequest())
        .extractRequest(buildHttpRequest());
    System.out.println("提取数据：" + JsonUtils.stringify(map));
    String expected =
        "{'objParam':{'array_any':[{'a':10,'obj':{}}],'array_any_simple':[1,2,3,4,5],'extendMap':{'a':10,'obj':{}},'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'}}";
    String actual = JsonUtils.stringify(map);
    Assert.assertEquals(expected.replace("'", "\""), actual);
  }
}
