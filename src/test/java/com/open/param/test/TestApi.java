package com.open.param.test;

import com.open.param.DataType;
import com.open.param.JsonToParamUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamBase;
import com.open.param.ParamObject;
import com.open.param.Primitive;
import com.open.param.ParamUtils;
import com.open.json.api.GsonSerialize;
import com.open.utils.TestHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestApi {
    private static Param product = ParamObject.require("product", "商品对象", //
            Primitive.require("name", DataType.String, "商品名称").setExampleValue("IPhone7"),
            Primitive.require("price", DataType.Number, "商品价格").setExampleValue(99.98),
            ParamArray.require("skus", "商品SKU属性列表", //
                    ParamObject.require(//
                            Primitive.require("id", DataType.Number, "参数描述").setExampleValue(100), //
                            Primitive.require("name", DataType.String, "参数描述").setExampleValue("移动版"), //
                            ParamArray.require("code", "参数描述", //
                                    ParamObject.optional(//
                                            Primitive.optional("id", DataType.Number, "id").setExampleValue(12345), //
                                            Primitive.optional("title", DataType.String, "标题").setExampleValue("土黄金色")//
                                    )//
                            )//
                    )//
            )//
    );

    @Test
    public void test() {
        String json = ParamUtils.toJsonDataExample(product);
        String expected = "{\"name\":\"IPhone7\",\"price\":99.98,\"skus\":[{\"id\":100,\"name\":\"移动版\",\"code\":[{\"id\":12345,\"title\":\"土黄金色\"}]}]}";
        Assert.assertEquals(expected, json);
        String javaCode = ParamUtils.generateCode(json);
        System.out.println("根据json数据生成验证参数代码:" + javaCode);
        Param param = ParamUtils.fromJsonToParam(json);
        Param generateParam = ParamObject.optional(//
                Primitive.optional("name", DataType.String, "参数描述").setExampleValue("IPhone7"),//
                Primitive.optional("price", DataType.Number, "参数描述").setExampleValue(99.98),//
                ParamArray.optional("skus", "参数描述",//
                        ParamObject.optional(//
                                Primitive.optional("id", DataType.Number, "参数描述").setExampleValue(100),//
                                Primitive.optional("name", DataType.String, "参数描述").setExampleValue("移动版"),//
                                ParamArray.optional("code", "参数描述",//
                                        ParamObject.optional(//
                                                Primitive.optional("id", DataType.Number, "参数描述").setExampleValue(12345),//
                                                Primitive.optional("title", DataType.String, "参数描述").setExampleValue("土黄金色")//
                                        )//
                                )//
                        )//
                )//
        );
        Assert.assertEquals(ParamUtils.generateCode(param), ParamUtils.generateCode(generateParam));
    }

    @Test
    public void serializationTest() throws IOException {
        JsonToParamUtils.DESCRIPTION = "参数描述";
        String paramDefine = ParamUtils.serialization(product);
        String fileData = TestHelper.readFile("product_param_define.json");
        Assert.assertEquals(ParamUtils.serialization(ParamUtils.deserialization(fileData)), paramDefine);
        Param param = ParamUtils.deserialization(paramDefine);
        System.out.println("生成数据示例:" + ParamUtils.toJsonDataExample(param));
        String json = "{\"name\":\"IPhone7\",\"price\":99.98,\"skus\":[{\"id\":100,\"name\":\"移动版\",\"code\":[{\"id\":12345,\"title\":\"土黄金色\"}]}]}";
        Assert.assertEquals(json, ParamUtils.toJsonDataExample(ParamUtils.fromJsonToParam(json)));


    }
}
