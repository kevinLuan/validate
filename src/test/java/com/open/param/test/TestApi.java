package com.open.param.test;

import com.open.param.DataType;
import com.open.param.JsonToParamUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamBase;
import com.open.param.ParamObject;
import com.open.param.ParamPrimitive;
import com.open.param.ParamUtils;
import com.open.json.api.GsonSerialize;

public class TestApi {
	public static void main(String[] args) {
		JsonToParamUtils.DESCRIPTION = "参数描述";
		Param product = ParamObject.required("product", "商品对象", //
				ParamPrimitive.required("name", DataType.String, "商品名称").setExampleValue("IPhone7"),
				ParamPrimitive.required("price", DataType.Number, "商品价格").setExampleValue(99.98),
				ParamArray.required("skus", "商品SKU属性列表", //
						ParamObject.required(//
								ParamPrimitive.required("id", DataType.Number, "参数描述").setExampleValue(100), //
								ParamPrimitive.required("name", DataType.String, "参数描述").setExampleValue("移动版"), //
								ParamArray.required("code", "参数描述", //
										ParamObject.noRequired(//
												ParamPrimitive.noRequired("id", DataType.Number, "id").setExampleValue(12345), //
												ParamPrimitive.noRequired("title", DataType.String, "标题").setExampleValue("土黄金色")//
										)//
								)//
						)//
				)//
		);
		String string= GsonSerialize.INSTANCE.encode(product);
		System.out.println(string);
		Param param1= GsonSerialize.INSTANCE.decode(string, ParamBase.class);
		string=GsonSerialize.INSTANCE.encode(param1);
		System.out.println(string);
		System.out.println(ParamUtils.fromParamAsJsonData(product));
		String json = "{\"name\":\"IPhone\",\"price\":5800.00,\"skus\":[{\"id\":123,\"name\":\"移动版\",\"code\":[{\"title\":\"土黄金\",\"id\":1000},{\"title\":\"黑色\",\"id\":1001}]}]}";
		Param param = ParamUtils.fromJsonAsParam(json);
		System.out.println(ParamUtils.fromParamAsJavaCode(param));
		System.out.println(ParamUtils.fromParamAsJsonData(param));

		

	}
}
