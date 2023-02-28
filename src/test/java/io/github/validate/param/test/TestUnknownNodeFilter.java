package io.github.validate.param.test;

import io.github.validate.json.api.JsonUtils;
import io.github.validate.param.Param;
import io.github.validate.param.ParamArray;
import io.github.validate.param.api.Validation;
import io.github.validate.param.api.ApiUnknownNodeFilter;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.validate.param.DataType;
import io.github.validate.param.ParamObject;
import io.github.validate.param.Primitive;

/**
 * 测试未知字段过滤器处理
 * 
 * @author KEVIN LUAN
 */
public class TestUnknownNodeFilter {
	private static Param buildResult() {
		return ParamObject.optional("result", "返回数据", //
				Primitive.require("name", DataType.String, "姓名").setMax(5), //
				Primitive.require("age", DataType.Number, "年龄").setMin(0).setMax(120), //
				ParamArray.require("items", "商品列表", //
						ParamObject.require(//
								Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
								Primitive.require("name", DataType.String, "商品名称").setMax(50)//
						)//
				), //
				ParamArray.require("ids", "id列表", //
						Primitive.require(DataType.Number).setMax(100) //
				)//
		);
	}

	@Test
	public void test_Filter() {
		Param param = buildResult();
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("result",
				"{\"name\":true,\"error\":true,\"remark\":\"remark\",\"extendProps\":[\"不符合邀请类型的属性\"],\"age\":1,\"items\":[{\"id\":1,\"name\":true,\"remark\":true,\"ERROR\":true,\"extendProps\":{\"abc\":12.2423}}],\"ids\":[1]}");
		Map<String, Object> map = Validation.request(param).setUnknownNodeFilter(ApiUnknownNodeFilter.INSTANCE)
				.checkRequest(request).extractRequest(request);
		System.out.println(map);
		String expected = "{result={\"name\":true,\"remark\":\"remark\",\"age\":1,\"items\":[{\"id\":1,\"name\":true,\"extendProps\":{\"abc\":12.2423}}],\"ids\":[1]}}";
		Assert.assertEquals(expected, map.toString());
		JsonNode jsonNode = JsonUtils.parser(request.getParameter("result"));
		map = Validation.response(param).setUnknownNodeFilter(ApiUnknownNodeFilter.INSTANCE).checkResponse(jsonNode)
				.extractResponse(jsonNode);
		System.out.println(map);
		expected = "{name=true, ids=[1], remark=\"remark\", items=[{\"id\":1,\"name\":true,\"extendProps\":{\"abc\":12.2423}}], age=1}";
		Assert.assertEquals(expected, map.toString());

	}
}
