package com.open.param.test;

import com.open.param.ParamAny;
import com.open.param.ParamBase;
import com.open.param.api.ParamApi;
import com.open.param.common.GenerateCode;
import com.open.param.core.AdjustParamInstance;
import com.open.param.core.ParentReference;
import com.sun.tools.javah.Gen;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.json.api.JsonUtils;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamNumber;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;
import com.open.param.ParamString;
import com.open.param.validate.JsonValidate;
import com.open.validate.impl.ObjectValidate;
import com.open.validate.impl.StringValidate;
import com.open.validate.impl.TestEnum;

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
    {
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
                  ParamNumber.of("num", null).allMatch(TestEnum.values()))));
      // TODO 测试数据类型错误
      JsonNode data = JsonUtils.parser(json.replace("'", "\""));
      try {
        JsonValidate.of(paramObject).check(data).extract(data);
        Assert.fail("没有预期的错误");
      } catch (IllegalArgumentException ex) {
        Assert.assertEquals("`items.num`必须是一个数字", ex.getMessage());
      }
    }
    {// TODO 测试删除无效字段
      String json = "{'items':[{"
          + "'num':'1234'"
          + "}"
          + "]}";
      ParamObject paramObject = ParamObject.of(
          ParamArray.of("items", null,
              ParamObject.of(
                  ParamNumber.of("num", null).allMatch(TestEnum.values()))));
      JsonNode data = JsonUtils.parser(json.replace("'", "\""));
      try {
        JsonValidate.of(paramObject).check(data).extract(data);
        Assert.fail("没有预期的错误");
      } catch (IllegalArgumentException ex) {
        Assert.assertEquals("`items.num`参数无效", ex.getMessage());
      }
    }
    // TODO 测试删除无效字段
    {
      String json = "{'items':[{"
          + "'name':'张三',"
          + "'array':[1,2,3],"
          + "'num':'1'"
          + "}"
          + "]}";
      ParamObject paramObject = ParamObject.of(
          ParamArray.of("items", null,
              ParamObject.of(
                  ParamString.of("name", null).setExampleValue("name")
                      .anyMatch(StringValidate.INSTANCE),
                  ParamArray.of("array", null),
                  ParamNumber.of("num", null).anyMatch(TestEnum.values())//
              ).anyMatch(ObjectValidate.INSTANCE)));
      JsonNode data = JsonUtils.parser(json.replace("'", "\""));
      JsonValidate.of(paramObject).check(data);
      System.out.println("提取有效字段:" + JsonUtils.toString(data));
      ObjectNode objectNode = (ObjectNode) ((ArrayNode) (data.get("items"))).get(0);
      Assert.assertEquals("张三", objectNode.get("name").asText());
      Assert.assertTrue(((ArrayNode) objectNode.get("array")).size() > 0);
      Assert.assertEquals(1, objectNode.get("num").asInt());
      Assert.assertEquals("成功", objectNode.get("check").asText());
    }
  }

  @Test
  public void testApi() {
    String json = ("{'items':[{"
        + "'name':'张三',"
        + "'array':[1,2,3],"
        + "'num':'1'"
        + "}"
        + "]}").replace("'", "\"");
    System.out.println(json);
    {
      String code = GenerateCode.getJavaCodeV2(json);
      System.out.println(code);
      String expected = "ParamApi.object().children(\n"
          + "ParamApi.array().name(\"items\")\n"
          + ".children(\n"
          + "ParamApi.object().children(\n"
          + "ParamApi.string().name(\"name\")\n"
          + ".exampleValue(\"张三\")\n"
          + ",\n"
          + "ParamApi.array().name(\"array\")\n"
          + ".children(\n"
          + "ParamApi.number().exampleValue(1)\n"
          + ")\n"
          + ",\n"
          + "ParamApi.string().name(\"num\")\n"
          + ".exampleValue(\"1\")\n"
          + ")\n"
          + ")\n"
          + ").optimize();";
      Assert.assertEquals(expected, code);
    }
    {
      Param param = ParamApi.object().children(
          ParamApi.array().name("items")
              .children(
                  ParamApi.object().children(
                      ParamApi.string().name("name").exampleValue("张三")
                          .allMatch(StringValidate.INSTANCE),
                      ParamApi.array().name("array").children(
                          ParamApi.number().exampleValue(1)),
                      ParamApi.number().name("num").exampleValue("1").anyMatch(TestEnum.values()))
                      .allMatch(ObjectValidate.INSTANCE)));

      JsonNode data = JsonUtils.parser(json);
      JsonValidate.of(param).check(data);
      System.out.println("提取有效字段:" + JsonUtils.toString(data));
      ObjectNode objectNode = (ObjectNode) ((ArrayNode) (data.get("items"))).get(0);
      Assert.assertEquals("张三", objectNode.get("name").asText());
      Assert.assertTrue(((ArrayNode) objectNode.get("array")).size() > 0);
      Assert.assertEquals(1, objectNode.get("num").asInt());
      Assert.assertEquals("成功", objectNode.get("check").asText());
    }
  }

  @Test
  public void testAnyType() {
    String json =
        "{\"status\":{\"status_code\":0,\"status_reason\":\"\"},\"result\":{\"itemId\":29755640,\"headImg\":\"https://si.geilicdn.com/pcitem173115993-36cc0000016d48822cdd0a20b7b9_500_500.jpg\",\"title\":\"有sku的\",\"hasSku\":true,\"supplierPrice\":20,\"totalStock\":19,\"createTime\":1568182381000,\"editTime\":1568879626000,\"itemDetail\":[{\"type\":1,\"text\":\"商详模块1\",\"link\":null,\"goods\":null,\"diary\":null,\"coupon\":null,\"vgroup\":null,\"url\":null,\"faceUrl\":null,\"videoType\":null,\"data\":null,\"videoId\":null}],\"skus\":[{\"title\":\"sku\",\"itemId\":2975255640,\"skuId\":17189308090,\"supplierPrice\":20,\"suggestPrice\":30,\"stock\":19,\"merchantCode\":\"广东湿\",\"img\":\"\"}],\"expressFee\":{\"bindingTemplate\":{\"expressInfoStr\":\"默认运费:100件内10.0元, 每增加1件, 增加运费0.0元\",\"templateName\":\"默认运费模版\",\"templateId\":5764002},\"bindingRemoteTemplate\":{\"expressInfoStr\":\"\",\"areaNames\":\"海南,西藏,新疆,香港,澳门,内蒙古,台湾,宁夏,甘肃,青海\",\"templateName\":\"包邮(除偏远地区)\",\"templateId\":2},\"freeDelivery\":false,\"remoteExclude\":false}}}";
    String javaCode = GenerateCode.getJavaCodeV2(json);
    System.out.print(javaCode);
    Param param = ParamApi.object().children(
        ParamApi.object().name("status")
            .children(
                ParamApi.number().name("status_code").exampleValue(0),
                ParamApi.string().name("status_reason").exampleValue("")),
        ParamApi.object().name("result")
            .children(
                ParamApi.number().name("itemId").exampleValue(29755640),
                ParamApi.string().name("headImg").exampleValue(
                    "https://si.geilicdn.com/pcitem173115993-36cc0000016d48822cdd0a20b7b9_500_500.jpg"),
                ParamApi.string().name("title").exampleValue("有sku的"),
                ParamApi.bool().name("hasSku").exampleValue(true),
                ParamApi.number().name("supplierPrice").exampleValue(20),
                ParamApi.number().name("totalStock").exampleValue(19),
                ParamApi.number().name("createTime").exampleValue(1568182381000L),
                ParamApi.number().name("editTime").exampleValue(1568879626000L),
                ParamApi.array().name("itemDetail").children(
                    ParamApi.object().children(
                        ParamApi.number().name("type").exampleValue(1),
                        ParamApi.string().name("text").exampleValue("商详模块1"),
                        ParamApi.any().name("link"),
                        ParamApi.any().name("goods"),
                        ParamApi.any().name("diary"),
                        ParamApi.any().name("coupon"),
                        ParamApi.any().name("vgroup"),
                        ParamApi.any().name("url"),
                        ParamApi.any().name("faceUrl"),
                        ParamApi.any().name("videoType"),
                        ParamApi.any().name("data"),
                        ParamApi.any().name("videoId"))),
                ParamApi.array().name("skus")
                    .children(
                        ParamApi.object().children(
                            ParamApi.string().name("title").exampleValue("sku"),
                            ParamApi.number().name("itemId").exampleValue(2975255640L),
                            ParamApi.number().name("skuId").exampleValue(17189308090L),
                            ParamApi.number().name("supplierPrice").exampleValue(20),
                            ParamApi.number().name("suggestPrice").exampleValue(30),
                            ParamApi.number().name("stock").exampleValue(19),
                            ParamApi.string().name("merchantCode").exampleValue("广东湿"),
                            ParamApi.string().name("img")
                                .exampleValue(""))),
                ParamApi.object().name("expressFee")
                    .children(
                        ParamApi.object().name("bindingTemplate")
                            .children(
                                ParamApi.string().name("expressInfoStr")
                                    .exampleValue("默认运费:100件内10.0元, 每增加1件, 增加运费0.0元"),
                                ParamApi.string().name("templateName").exampleValue("默认运费模版"),
                                ParamApi.number().name("templateId").exampleValue(5764002)),
                        ParamApi.object().name("bindingRemoteTemplate")
                            .children(
                                ParamApi.string().name("expressInfoStr").exampleValue(""),
                                ParamApi.string().name("areaNames")
                                    .exampleValue("海南,西藏,新疆,香港,澳门,内蒙古,台湾,宁夏,甘肃,青海"),
                                ParamApi.string().name("templateName").exampleValue("包邮(除偏远地区)"),
                                ParamApi.number().name("templateId").exampleValue(2)),
                        ParamApi.bool().name("freeDelivery").exampleValue(false),
                        ParamApi.bool().name("remoteExclude").exampleValue(false))));
    String code2 = GenerateCode.getJavaCodeV2(param);
    Assert.assertEquals(code2, javaCode);
  }

  @Test
  public void testAnyCheck() {
    String json = ("{"
        + "    'status':{"
        + "        'status_code':0,"
        + "        'status_reason':''"
        + "    },"
        + "    'result':{"
        + "        'attr_list':["
        + "            {"
        + "                'attr_title':'颜色',"
        + "                'attr_values':["
        + "                    {"
        + "                        'attr_id':770182,"
        + "                        'attr_value':'白色'"
        + "                    },"
        + "                    {"
        + "                        'attr_id':770183,"
        + "                        'attr_value':'红色'"
        + "                    }"
        + "                ]"
        + "            }"
        + "        ]"
        + "    }"
        + "}").replace('\'', '"');
    String expected = "ParamApi.object().children(\n"
        + "ParamApi.object().name(\"status\")\n"
        + ".children(\n"
        + "ParamApi.number().name(\"status_code\")\n"
        + ".exampleValue(0)\n"
        + ",\n"
        + "ParamApi.string().name(\"status_reason\")\n"
        + ".exampleValue(\"\")\n"
        + ")\n"
        + ",\n"
        + "ParamApi.object().name(\"result\")\n"
        + ".children(\n"
        + "ParamApi.array().name(\"attr_list\")\n"
        + ".children(\n"
        + "ParamApi.object().children(\n"
        + "ParamApi.string().name(\"attr_title\")\n"
        + ".exampleValue(\"颜色\")\n"
        + ",\n"
        + "ParamApi.array().name(\"attr_values\")\n"
        + ".children(\n"
        + "ParamApi.object().children(\n"
        + "ParamApi.number().name(\"attr_id\")\n"
        + ".exampleValue(770182)\n"
        + ",\n"
        + "ParamApi.string().name(\"attr_value\")\n"
        + ".exampleValue(\"白色\")\n"
        + ")\n"
        + ")\n"
        + ")\n"
        + ")\n"
        + ")\n"
        + ").optimize();";
    Assert.assertEquals(expected, GenerateCode.getJavaCodeV2(json));

    Param param = ParamApi.object().children(
        ParamApi.object().name("status")
            .children(
                ParamApi.number().name("status_code"),
                ParamApi.string().name("status_reason")
            )
        ,
        ParamApi.object().name("result")
            .children(
                ParamApi.array().name("attr_list")
                    .children(
                        ParamApi.object().children(
                            ParamApi.string().name("attr_title").exampleValue("颜色"),
                            ParamApi.array().name("attr_values")
                                .children(
                                    ParamApi.object().children(
                                        ParamApi.number().name("attr_id").exampleValue(770182),
                                        ParamApi.string().name("attr_value").exampleValue("白色")
                                    )
                                )
                        )
                    )
            )
    ).optimize();
    long start=System.currentTimeMillis();
    for(int i=0;i<10000;i++) {
      JsonValidate.of(param).check(json);
    }
    System.out.println("执行1000次耗时:"+(System.currentTimeMillis()-start));
  }


  @Test
  public void testAny() {
    String json = ("{"
        + "    'status':{"
        + "        'status_code':0,"
        + "        'ext':[true,false]"
        + "    },"
        + "    'result':{"
        + "        'attr_list':["
        + "            {"
        + "                'attr_title':'颜色',"
        + "                'attr_values':["
        + "                    {"
        + "                        'attr_id':770182,"
        + "                        'attr_value':'白色',"
        + "                        'status':true"
        + "                    },"
        + "                    {"
        + "                        'attr_id':770183"
        + "                    }"
        + "                ]"
        + "            }"
        + "        ]"
        + "    }"
        + "}").replace('\'', '"');
    {
      Param param = ParamApi.object().children(
          ParamApi.object().name("status")
              .children(
                  ParamApi.any(true).name("ext").description("扩展字段可以时任意类型")
              )
      );
      JsonValidate.of(param).check(json);
    }
    //TODO 测试Any类型为必传值
    {
      Param param = ParamApi.object().children(
          ParamApi.object().name("result")
              .children(
                  ParamApi.array().name("attr_list")
                      .children(
                          ParamApi.object().children(
                              ParamApi.array().name("attr_values")
                                  .children(
                                      ParamApi.object().children(
                                          ParamApi.any(true).name("attr_value")
                                      )
                                  )
                          )
                      )
              )
      );
      try {
        JsonValidate.of(param).check(json);
        Assert.fail("没有出现预期错误");
      } catch (IllegalArgumentException ex) {
        Assert.assertEquals("`result.attr_list.attr_values.attr_value`数据缺失", ex.getMessage());
      }
    }
    //TODO 测试Array的Value为Any类型
    {
      Param param = ParamApi.object().children(
          ParamApi.object().name("result")
              .children(
                  ParamApi.array().name("attr_list")
                      .children(
                          ParamApi.object().children(
                              ParamApi.array().name("attr_values").children(ParamApi.any())
                          )
                      )
              )
      );
      JsonValidate.of(param).check(json);
    }

    //TODO
    {
      Param param = ParamApi.object().children(
          ParamApi.any(true).name("result"),
          ParamApi.any(true).name("status")
      );
      JsonValidate.of(param).check(json);
      JsonValidate.of(ParamApi.any()).check(json);
    }
    //TODO 测试Boolean 必须值
    {
      Param param = ParamApi.object().children(
          ParamApi.object().name("result")
              .children(
                  ParamApi.array().name("attr_list")
                      .children(
                          ParamApi.object().children(
                              ParamApi.array().name("attr_values")
                                  .children(
                                      ParamApi.object().children(
                                          ParamApi.bool(true).name("status")
                                      )
                                  )
                          )
                      )
              )
      );
      try {
        JsonValidate.of(param).check(json);
      } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
        Assert.assertEquals("`result.attr_list.attr_values.status`数据缺失", ex.getMessage());
      }
    }
    {
      Param param = ParamApi.object().children(
          ParamApi.object().name("result")
              .children(
                  ParamApi.array().name("attr_list")
                      .children(
                          ParamApi.object().children(
                              ParamApi.array().name("attr_values")
                                  .children(
                                      ParamApi.object().children(
                                          ParamApi.bool().name("status")
                                      )
                                  )
                          )
                      )
              )
      );
      try {
        JsonValidate.of(param).check(json);
      } catch (IllegalArgumentException ex) {
        Assert.assertEquals("`result.attr_list.attr_values.status`数据缺失", ex.getMessage());
      }
    }
  }
}
