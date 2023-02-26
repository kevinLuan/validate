package com.open.param.test;

import com.open.json.api.GsonSerialize;
import com.open.json.api.JsonUtils;
import com.open.param.JsonToParamUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.api.Validation;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.open.param.DataType;
import com.open.param.ParamBase;
import com.open.param.ParamObject;
import com.open.param.Primitive;
import com.open.param.ParamUtils;

public class TestParamUtils {

	@Test
	public void testFromJsonConvertParam_and_CheckParam() throws JsonProcessingException, IOException {
		String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
		Param param = ParamUtils.fromJsonToParam(json);
		// 修改参数验证范围
		param.asObject().getChildren()[0].asPrimitive().between(10, 20);
		try {
			JsonNode node = JsonUtils.parser(json);
			Validation.response(param).checkResponse(node);
			Assert.fail("没有出现预期的错误");
		} catch (Exception e) {
			Assert.assertEquals("`name`长度限制在10(含)~20(含)", e.getMessage());
		}
		// 重置验证逻辑
		param.asObject().getChildren()[0].asPrimitive().between(1, 10);
		// 验证数据范围
		param.asObject().getChildren()[1].asArray().getChildrenAsParam().asPrimitive().between(1, 50);
		try {
			JsonNode node = JsonUtils.parser(json);
			Validation.response(param).checkResponse(node);
			Assert.fail("没有出现预期的错误");
		} catch (Exception e) {
			Assert.assertEquals("`ids`限制范围1(含)~50(含)", e.getMessage());
		}
	}

	@Test
	public void test_jsonToParam() {
		String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
		String desc = (JsonToParamUtils.DESCRIPTION = "参数描述");
		Param actual = ParamUtils.fromJsonToParam(json);
		Param expected = ParamObject.optional(//
				Primitive.optional("name", DataType.String, desc).setExampleValue("张三丰"), //
				ParamArray.optional("ids", desc, //
						Primitive.optional(DataType.Number).setExampleValue(100)), //
				ParamArray.optional("items", desc, //
						ParamObject.optional(//
								Primitive.optional("name", DataType.String, desc).setExampleValue("手机"), //
								Primitive.optional("id", DataType.Number, desc).setExampleValue(2)//
						)//
				), //
				Primitive.optional("age", DataType.Number, desc).setExampleValue(100.11)//
		);
		System.out.println(GsonSerialize.INSTANCE.encode(expected));
		System.out.println(GsonSerialize.INSTANCE.encode(actual));
		Assert.assertTrue(expected.equals(actual));
	}

	@Test
	public void test_jsonToCode() {
		Param param = ParamObject.require(//
				Primitive.optional("name", DataType.String, "张三丰"), //
				ParamArray.require("ids", "Array[100]", //
						Primitive.optional(DataType.Number)), //
				ParamArray.require("items", "Array[{Object}]", //
						ParamObject.require(//
								Primitive.optional("name", DataType.String, "手机"), //
								Primitive.optional("id", DataType.Number, "2")//
						)//
				), //
				Primitive.optional("age", DataType.Number, "100.11")//
		);

		String code = ParamUtils.generateCode(param);
		System.out.println("生成Code:");
		System.out.println(code);
		String expected = "ParamObject.require(//\n" +
				"Primitive.optional(\"name\",DataType.String,\"张三丰\"),//\n" +
				"ParamArray.require(\"ids\",\"Array[100]\",//\n" +
				"Primitive.optional(DataType.Number)),//\n" +
				"ParamArray.require(\"items\",\"Array[{Object}]\",//\n" +
				"ParamObject.require(//\n" +
				"Primitive.optional(\"name\",DataType.String,\"手机\"),//\n" +
				"Primitive.optional(\"id\",DataType.Number,\"2\")//\n" +
				")//\n" +
				"),//\n" +
				"Primitive.optional(\"age\",DataType.Number,\"100.11\")//\n" +
				");";
		expected = expected.replace("'", "\"");
		Assert.assertEquals(expected, code);
	}

	@Test
	public void test_fromParamAsJsonData() {
		String json = "{\"name\":\"张三丰\",\"ids\":[100],\"items\":[{\"name\":\"手机\",\"id\":2}],\"age\":100.11}";
		System.out.println("原始数据JSON:");
		System.out.println(json);
		Param param = ParamUtils.fromJsonToParam(json);
		String actual = ParamUtils.toJsonDataExample(param);
		System.out.println("反向解析到JSON:");
		System.out.println(actual);
		Assert.assertEquals(json, actual);
	}

	@Test
	public void test_fromParamAsJsonData1() {
		String json = "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
		Param param = GsonSerialize.INSTANCE.decode(json, ParamBase.class);
		String expected = "{\"status\":{\"statusCode\":1500,\"statusReason\":\"参数错误\"},\"result\":{\"id\":\"1234\",\"name\":\"xxx\"}}";
		String actual = ParamUtils.toJsonDataExample(param);
		System.out.println(actual);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test_fromParamAsJavaCode() {
		String json = "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
		Param param = GsonSerialize.INSTANCE.decode(json, ParamBase.class);
		String expected = "ParamObject.optional(ParamObject.optional(\"status\",\"状态\",//\n" +
				"Primitive.optional(\"statusCode\",DataType.Number,\"状态码\").setExampleValue(1500),//\n" +
				"Primitive.optional(\"statusReason\",DataType.String,\"状态描述\").setExampleValue(\"参数错误\")//\n" +
				")//\n" +
				",//\n" +
				"ParamObject.optional(\"result\",\"结果\",//\n" +
				"Primitive.optional(\"id\",DataType.String,\"ID\").setExampleValue(\"1234\"),//\n" +
				"Primitive.optional(\"name\",DataType.String,\"名称\").setExampleValue(\"xxx\")//\n" +
				")//\n" +
				"//\n" +
				");";
		String actual = ParamUtils.generateCode(param);
		System.out.println(actual);
		Assert.assertEquals(expected, actual);
		
		{// 生成代码示例
			ParamObject.optional(//
					ParamObject.optional("status", "状态", //
							Primitive.optional("statusCode", DataType.Number, "状态码").setExampleValue(1500), //
							Primitive.optional("statusReason", DataType.String, "状态描述").setExampleValue("参数错误")//
					), //
					ParamObject.optional("result", "结果", //
							Primitive.optional("id", DataType.String, "ID").setExampleValue("1234"), //
							Primitive.optional("name", DataType.String, "名称").setExampleValue("xxx")//
					)//
			);
		}
	}
}
