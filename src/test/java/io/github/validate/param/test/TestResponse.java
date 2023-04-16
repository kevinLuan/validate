package io.github.validate.param.test;

import io.github.validate.param.api.ApiParams;
import io.github.validate.json.api.JsonUtils;
import io.github.validate.param.Param;
import io.github.validate.param.ParamArray;
import io.github.validate.param.api.Validation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.validate.param.DataType;
import io.github.validate.param.ParamObject;
import io.github.validate.param.Primitive;

public class TestResponse {
	@After
	public void after() {
		System.out.println();
	}

	private static ParamObject getResultParam() {
		return ParamObject.required(//
				ParamObject.required("status", "返回", //
						Primitive.required("status_code", DataType.Number, ""), //
						Primitive.required("status_reasion", DataType.String, "")//
				), //
				buildResult()//
		);
	}

	private static Param buildResult() {
		return ParamObject.optional("result", "返回数据", //
				Primitive.required("name", DataType.String, "姓名").setMax(5), //
				Primitive.required("age", DataType.Number, "年龄").setMin(0).setMax(120), //
				ParamArray.required("items", "商品列表", //
						ParamObject.required(//
								Primitive.required("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
								Primitive.required("name", DataType.String, "商品名称").setMax(50)//
						)//
				), //
				ParamArray.required("ids", "id列表", //
						Primitive.required(DataType.Number).setMax(100) //
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
		Map<String, Object> response = Validation.response(param).checkResponse(dataResult).extractResponse(dataResult);
		System.out.println("提取数据：" + JsonUtils.stringify(response));
		String expected = "{'result':{'name':'张三丰','ids':['100'],'items':[{'name':'手机','id':'2'}],'age':'100.11'},'status':{'status_code':100,'status_reasion':'参数错误'}}"
				.replace("'", "\"");
		Assert.assertEquals(expected, JsonUtils.stringify(response));
	}

	public static void main(String[] args) {
		{
			Param param = getResultParam();
			JsonNode dataResult = JsonUtils.parser(getResponseData());
			System.out.println("返回原始数据：" + dataResult.toString());
			Map<String, Object> response = Validation.response(param).checkResponse(dataResult).extractResponse(dataResult);
			System.out.println("提取需要的数据：" + JsonUtils.stringify(response));
			ApiParams.make(param).check(null);
		}
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			Param param = getResultParam();
			JsonNode dataResult = JsonUtils.parser(getResponseData());
			Validation.response(param).checkResponse(dataResult).extractResponse(dataResult);
		}
		System.out.println("use time:" + (System.currentTimeMillis() - start));
	}
}
