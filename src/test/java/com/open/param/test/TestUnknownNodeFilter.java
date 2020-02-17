package com.open.param.test;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.json.api.JsonUtils;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;
import com.open.param.api.ApiUnknownNodeFilter;
import com.open.param.validate.JsonValidate;
import com.open.param.validate.RequestValidate;

/**
 * 测试未知字段过滤器处理
 * 
 * @author KEVIN LUAN
 */
public class TestUnknownNodeFilter {
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

  @Test
  public void test_Filter() {
    Param param = buildResult();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("result",
        "{\"name\":true,\"error\":true,\"remark\":\"remark\",\"extendProps\":[\"不符合邀请类型的属性\"],\"age\":1,\"items\":[{\"id\":1,\"name\":true,\"remark\":true,\"ERROR\":true,\"extendProps\":{\"abc\":12.2423}}],\"ids\":[1]}");
    Map<String, Object> map =
        RequestValidate.of(param).setUnknownNodeFilter(ApiUnknownNodeFilter.INSTANCE)
            .check(request).extract(request);
    System.out.println(map);
    String expected =
        "{result={\"name\":true,\"remark\":\"remark\",\"age\":1,\"items\":[{\"id\":1,\"name\":true,\"extendProps\":{\"abc\":12.2423}}],\"ids\":[1]}}";
    Assert.assertEquals(expected, map.toString());
    JsonNode jsonNode = JsonUtils.parser(request.getParameter("result"));
    map = JsonValidate.of(param).setUnknownNodeFilter(ApiUnknownNodeFilter.INSTANCE)
        .check(jsonNode)
        .extract(jsonNode);
    System.out.println(map);
    expected =
        "{name=true, ids=[1], remark=\"remark\", items=[{\"id\":1,\"name\":true,\"extendProps\":{\"abc\":12.2423}}], age=1}";
    Assert.assertEquals(expected, map.toString());

  }
}
