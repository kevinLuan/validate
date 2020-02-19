package com.open.param.test;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.GsonSerialize;
import com.open.json.api.JsonUtils;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamNumber;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;
import com.open.param.ParamString;
import com.open.param.parser.GenerateCode;
import com.open.param.parser.GenerateMockSample;
import com.open.param.parser.JsonConverter;
import com.open.param.parser.ParamSerializable;
import com.open.param.validate.JsonValidate;

public class TestParamUtils {

  @Test
  public void testFromJsonConvertParam_and_CheckParam()
      throws JsonProcessingException, IOException {
    String json =
        "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
    Param param = JsonConverter.INSTANCE.convert(json);
    // 修改参数验证范围
    param.asObject().getChildren()[0].asPrimitive().between(10, 20);
    try {
      JsonNode node = JsonUtils.parser(json);
      JsonValidate.of(param).check(node);
      Assert.fail("没有出现预期的错误");
    } catch (Exception e) {
      Assert.assertEquals("`name`长度限制在10~20", e.getMessage());
    }
    // 重置验证逻辑
    param.asObject().getChildren()[0].asPrimitive().between(1, 10);
    // 验证数据范围
    param.asObject().getChildren()[1].asArray().getChildrenAsParam().asPrimitive().between(1, 50);
    try {
      JsonNode node = JsonUtils.parser(json);
      JsonValidate.of(param).check(node);
      Assert.fail("没有出现预期的错误");
    } catch (Exception e) {
      Assert.assertEquals("`ids`限制范围1~50", e.getMessage());
    }
  }

  @Test
  public void test_jsonToParam() {
    String json =
        "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
    Param actual = JsonConverter.INSTANCE.convert(json);
    Param expected = ParamObject.of(//
        ParamPrimitive.of("name", DataType.String, null).setExampleValue("张三丰"), //
        ParamArray.of("ids", null, //
            ParamPrimitive.of(DataType.Number, null).setExampleValue(100)), //
        ParamArray.of("items", null, //
            ParamObject.of(//
                ParamPrimitive.of("name", DataType.String, null).setExampleValue("手机"), //
                ParamPrimitive.of("id", DataType.Number, null).setExampleValue(2)//
            )//
        ), //
        ParamPrimitive.of("age", DataType.Number, null).setExampleValue(100.11)//
    );
    System.out.println(GsonSerialize.INSTANCE.encode(expected));
    System.out.println(GsonSerialize.INSTANCE.encode(actual));
    Assert.assertEquals(GsonSerialize.INSTANCE.encode(actual),
        GsonSerialize.INSTANCE.encode(expected));
  }

  @Test
  public void testGeneratedJavaCode() {
    String json =
        "{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":true,\"address\":{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\",\"arrayBoolean\":[true,false,true,false],\"arrayFloat\":[1.234,2.345],\"arrayString\":[\"篮球\",\"足球\"],\"arrayLong\":[1234234,234234],\"arrayObj\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"},{\"name\":\"Baidu\",\"url\":\"http://www.baidu.com\"},{\"name\":\"SoSo\",\"url\":\"http://www.SoSo.com\"}]}";
    String javaCode = GenerateCode.getJavaCode(json);
    System.out.println("JSON原始数据:" + json);
    System.out.println("生成java代码:" + javaCode);
    Param objParam = ParamObject.of(
        ParamString.of("name", null).setExampleValue("BeJson"),
        ParamString.of("url", null).setExampleValue("http://www.bejson.com"),
        ParamNumber.of("page", null).setExampleValue(88),
        ParamString.of("isNonProfit", null).setExampleValue("true"),
        ParamObject.of("address", null,
            ParamString.of("street", null).setExampleValue("科技园路."),
            ParamString.of("city", null).setExampleValue("江苏苏州"),
            ParamString.of("country", null).setExampleValue("中国"),
            ParamArray.of("arrayBoolean", null,
                ParamString.of(null).setExampleValue("true")),
            ParamArray.of("arrayFloat", null,
                ParamNumber.of(null).setExampleValue(1.234)),
            ParamArray.of("arrayString", null,
                ParamString.of(null).setExampleValue("篮球")),
            ParamArray.of("arrayLong", null,
                ParamNumber.of(null).setExampleValue(1234234)),
            ParamArray.of("arrayObj", null,
                ParamObject.of(
                    ParamString.of("name", null).setExampleValue("Google"),
                    ParamString.of("url", null).setExampleValue("http://www.google.com")))),
        ParamArray.of("links", null,
            ParamObject.of(
                ParamString.of("name", null).setExampleValue("Google"),
                ParamString.of("url", null).setExampleValue("http://www.google.com"))));

    String actual = GenerateMockSample.getMockData(objParam);
    System.out.println(actual);
    String expected =
        "{\"name\":\"BeJson\",\"url\":\"http://www.bejson.com\",\"page\":88,\"isNonProfit\":\"true\",\"address\":{\"street\":\"科技园路.\",\"city\":\"江苏苏州\",\"country\":\"中国\",\"arrayBoolean\":[\"true\"],\"arrayFloat\":[1.234],\"arrayString\":[\"篮球\"],\"arrayLong\":[1234234],\"arrayObj\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"}]},\"links\":[{\"name\":\"Google\",\"url\":\"http://www.google.com\"}]}";
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void test_jsonToCode() {
    Param param = ParamObject.required(//
        ParamPrimitive.of("name", DataType.String, "张三丰"), //
        ParamArray.required("ids", "Array[100]", //
            ParamPrimitive.of(DataType.Number, null)), //
        ParamArray.required("items", "Array[{Object}]", //
            ParamObject.required(//
                ParamString.of("name1", "手机"),
                ParamNumber.of("name2", "手机").between(1.1, 2.1).setExampleValue(11),
                ParamPrimitive.of("name", DataType.String, "手机"), //
                ParamPrimitive.of("id", DataType.Number, "2")//
            )//
        ), //
        ParamPrimitive.of("age", DataType.Number, "100.11")//
    );

    String code = GenerateCode.getJavaCode(param);
    System.out.println("生成Code:");
    System.out.println(code);
    String expected = "ParamObject.required(\n" + //
        "ParamPrimitive.of('name',DataType.String, '张三丰'),\n"//
        + "ParamArray.required('ids','Array[100]',\n"//
        + "ParamPrimitive.of(DataType.Number,null)),\n"//
        + "ParamArray.required('items','Array[{Object}]',\n"//
        + "ParamObject.required(\n" //
        + "ParamString.of(\"name1\",\"手机\"),\n"
        + "ParamNumber.of(\"name2\",\"手机\").between(1.1, 2.1).setExampleValue(11),\n"//
        + "ParamPrimitive.of('name',DataType.String, '手机'),\n"//
        + "ParamPrimitive.of('id',DataType.Number, '2')\n"//
        + ")\n),\n"//
        + "ParamPrimitive.of('age',DataType.Number, '100.11')\n" //
        + ");";
    expected = expected.replace("'", "\"");
    Assert.assertEquals(expected, code);
  }

  @Test
  public void test_fromParamAsJsonData() {
    String json =
        "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
    System.out.println("原始数据JSON:");
    System.out.println(json);
    Param param = JsonConverter.INSTANCE.convert(json);
    String actual = GenerateMockSample.getMockData(param);
    System.out.println("反向解析到JSON:");
    System.out.println(actual);
    Assert.assertEquals(json, actual);
  }

  @Test
  public void test_fromParamAsJsonData1() {
    String json =
        "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
    Param param = ParamSerializable.INSTANCE.decode(json);
    String expected =
        "{\"status\":{\"statusCode\":1500,\"statusReason\":\"参数错误\"},\"result\":{\"id\":\"1234\",\"name\":\"xxx\"}}";
    String actual = GenerateMockSample.getMockData(param);
    System.out.println(actual);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void test_fromParamAsJavaCode() {
    String json =
        "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
    Param param = ParamSerializable.INSTANCE.decode(json);
    String expected =
        "ParamObject.of(ParamObject.of(\"status\",\"状态\",\n"
            + "ParamNumber.of(\"statusCode\",\"状态码\").setExampleValue(1500),\n"
            + "ParamString.of(\"statusReason\",\"状态描述\").setExampleValue(\"参数错误\")\n"
            + ")\n"
            + ",\n"
            + "ParamObject.of(\"result\",\"结果\",\n"
            + "ParamString.of(\"id\",\"ID\").setExampleValue(\"1234\"),\n"
            + "ParamString.of(\"name\",\"名称\").setExampleValue(\"xxx\")\n"
            + ")\n"
            + ");";
    expected = expected.replace("'", "\"");
    String actual = GenerateCode.getJavaCode(param);
    System.out.println(actual);
    Assert.assertEquals(expected, actual);
    {// 生成代码示例
      ParamObject.of(//
          ParamObject.of("status", "状态", //
              ParamPrimitive.of("statusCode", DataType.Number, "状态码").setExampleValue(1500), //
              ParamPrimitive.of("statusReason", DataType.String, "状态描述").setExampleValue("参数错误")//
          ), //
          ParamObject.of("result", "结果", //
              ParamPrimitive.of("id", DataType.String, "ID").setExampleValue("1234"), //
              ParamPrimitive.of("name", DataType.String, "名称").setExampleValue("xxx")//
          )//
      );
    }
  }
}
