package com.open.param.test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.param.ParamNumber;
import com.open.param.ParamString;
import com.open.param.parser.GenerateCode;
import com.sun.javafx.tools.packager.MakeAllParams;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.JsonUtils;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;
import com.open.param.api.ApiParams;
import com.open.param.validate.JsonValidate;

public class TestResponse {

  @After
  public void after() {
    System.out.println();
  }

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
        )//
    );
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
  public void test_ok() {
    Param param = getResultParam();
    JsonNode dataResult = JsonUtils.parser(getResponseData());
    Map<String, Object> response =
        JsonValidate.of(param).check(dataResult).extract(dataResult);
    System.out.println("提取数据：" + JsonUtils.stringify(response));
    String expected =
        "{'result':{'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'},'status':{'status_code':100,'status_reasion':'参数错误'}}"
            .replace("'", "\"");
    Assert.assertEquals(expected, JsonUtils.stringify(response));
  }

  @Test
  public void test() {
    {
      Param param = getResultParam();
      JsonNode dataResult = JsonUtils.parser(getResponseData());
      System.out.println("原始数据：" + dataResult.toString());
      Map<String, Object> response =
          JsonValidate.of(param).check(dataResult).extract(dataResult);
      System.out.println("提取有效数据：" + JsonUtils.stringify(response));
    }
    long start = System.currentTimeMillis();
    for (int i = 0; i < 10000; i++) {
      Param param = getResultParam();
      JsonNode dataResult = JsonUtils.parser(getResponseData());
      JsonValidate.of(param).check(dataResult).extract(dataResult);
    }
    System.out.println("use time:" + (System.currentTimeMillis() - start));
  }

  @Test
  public void testCheck() {
    String json = "{'items':[{"
        + "'name':'张三',"
        + "'array':[1,2,3],"
        + "'num':'xxx'"
        + "}"
        + "]}";
    ParamObject paramObject = ParamObject.of(
        ParamArray.of("items", null,
            ParamObject.of(
                ParamString.of("name", null).setExampleValue("name"),
                ParamArray.of("array", null),
                ParamNumber.of("num", null)
            )
        )
    );
    {
      //TODO 测试数据类型错误
      JsonNode data = JsonUtils.parser(json.replace("'", "\""));
      try {
        JsonValidate.of(paramObject).check(data).extract(data);
        Assert.fail("没有预期的错误");
      } catch (IllegalArgumentException ex) {
        Assert.assertEquals("`items.num`必须是一个数字", ex.getMessage());
      }
    }
    {//TODO 测试删除无效字段
      json = "{'items':[{"
          + "'name_':'张三',"
          + "'array':[1,2,3],"
          + "'num':'12312'"
          + "}"
          + "]}";
      JsonNode data = JsonUtils.parser(json.replace("'", "\""));
      Map<String, Object> map = JsonValidate.of(paramObject).check(data).extract(data);
      System.out.println("提取有效字段:" + JsonUtils.toString(map));
      ObjectNode objectNode = (ObjectNode) ((ArrayNode) (map.get("items"))).get(0);
      Assert.assertNull(objectNode.get("name_"));
      Assert.assertEquals(3, ((ArrayNode) objectNode.get("array")).size());
      Assert.assertEquals(12312, objectNode.get("num").asInt());
    }
  }

}
