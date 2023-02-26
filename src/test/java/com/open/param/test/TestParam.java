package com.open.param.test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.open.domain.api.DataResult;
import com.open.json.api.GsonSerialize;
import com.open.json.api.JsonUtils;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.api.Validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.open.param.DataType;
import com.open.param.ParamBase;
import com.open.param.ParamObject;
import com.open.param.Primitive;

public class TestParam {
    private Param buildParam() {
        return ParamObject.require("objParam", "对象参数", //
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
        String json = JsonUtils.stringify(map);
        request.addParameter("objParam", json);
        return request;
    }

    @Before
    public void before() {
    }

    @Test
    public void test_filter_object() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", "100.11");
        map.put("A1", "没有定义的参数参数");
        map.put("A2", "没有定义的参数参数");
        List<Object> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "2");
        item.put("A3", "没有定义的参数参数");
        item.put("name", "手机");
        items.add(item);
        map.put("items", items);
        List<Object> ids = new ArrayList<>();
        ids.add("100");
        map.put("ids", ids);
        String json = JsonUtils.stringify(map);
        request.addParameter("objParam", json);
        Param param = buildParam();
        Map<String, Object> data = Validation.request(param).checkRequest(request).extractRequest(request);
        String actual = JsonUtils.stringify(data);
        HttpServletRequest myRequest = buildHttpRequest();
        data = Validation.request(param).checkRequest(myRequest).extractRequest(myRequest);
        String expected = JsonUtils.stringify(data);
        Assert.assertEquals(expected, actual);
        System.out.println(actual);
    }

    @Test
    public void testValidateFail() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("age", "160");
        map.put("sql", "CSRF漏洞");
        request.addParameter("userInfo", JsonUtils.stringify(map));
        Param param = ParamObject.optional("userInfo", "用户信息",
                Primitive.require("name", DataType.String, "姓名").setMin(2).setMax(32),
                Primitive.optional("age", DataType.Number, null).between(18, 65)
        );
        try {
            Map<String, Object> extractData = Validation.request(param).checkRequest(request).extractRequest(request);
            System.out.println(extractData);
            Assert.fail("未出现逾期结果");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("`userInfo.age`限制范围18(含)~65(含)", e.getMessage());
        }
    }

    @Test
    public void testDataExtract() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "张三丰");
        map.put("sql", "CSRF漏洞");//各位传递的参数，在经过提取提取时，将会自动忽略
        request.addParameter("userInfo", JsonUtils.stringify(map));
        Param param = ParamObject.optional("userInfo", null,
                Primitive.require("name", DataType.String, null),
                Primitive.optional("age", DataType.Number, null)
        );
        Map<String, Object> extractData = Validation.request(param).checkRequest(request).extractRequest(request);
        ObjectNode userInfo = (ObjectNode) extractData.get("userInfo");
        Assert.assertEquals(userInfo.get("name").textValue(), "张三丰");
        Assert.assertFalse(userInfo.has("sql"));//字段被忽略
    }

    @Test
    public void test_filter_object_max() {
        try {
            String a1 = DataResult
                    .make("T",
                            new Object[]{DataResult.success(new Object[]{DataResult.make("S", "OK").addResult("city", "北京")})})
                    .toJSON();
            String b1 = DataResult.make("B", DataResult.success(new Object[]{DataResult.success("OK")})).toJSON();
            System.out.println(a1);
            System.out.println(b1);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("A1", a1);
            request.addParameter("B1", b1);
            Param A1 = ParamObject.require("A1", "参数描述", //
                    ParamObject.require("result", "参数描述", //
                            ParamArray.require("T", "参数描述", //
                                    ParamObject.require(//
                                            ParamObject.require("status", "参数描述", //
                                                    Primitive.require("statusCode", DataType.Number, "参数描述")//
                                            ), //
                                            ParamArray.require("result", "参数描述", //
                                                    ParamObject.require(//
                                                            Primitive.require("result", DataType.String, "参数描述")//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );
            Param B1 = ParamObject.require("B1", "X", ParamObject.require("result", "X"));
            Map<String, Object> map = Validation.request(A1, B1).checkRequest(request).extractRequest(request);
            Assert.fail("没有出现预期错误");
            System.out.println(map);
        } catch (Exception e) {
            Assert.assertEquals("`A1.result.T.result.result`参数错误", e.getMessage());
        }
        System.out.println("------------");
        {
            DataResult<?> data = DataResult.make("T",
                    new Object[]{DataResult.success(new Object[]{DataResult.make("S", "OK").addResult("city", "北京")})});
            String a1 = data.toJSON();
            String b1 = DataResult.make("B", DataResult.success(new Object[]{DataResult.success("OK")})).toJSON();
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("A1", a1);
            request.addParameter("B1", b1);
            request.addParameter("list", JsonUtils.stringify(new Object[]{data, data}));
            System.out.println(a1);
            System.out.println(b1);
            System.out.println(request.getParameter("list"));
            Param A1 = ParamObject.require("A1", "参数描述", //
                    ParamObject.require("result", "参数描述", //
                            ParamArray.require("T", "参数描述", //
                                    ParamObject.require(//
                                            ParamObject.require("status", "参数描述", //
                                                    Primitive.require("statusCode", DataType.Number, "参数描述")//
                                            ), //
                                            ParamArray.require("result", "参数描述", //
                                                    ParamObject.require(//
                                                            ParamObject.require("status", "参数描述", //
                                                                    Primitive.require("statusReason", DataType.String, "参数描述")//
                                                            ), //
                                                            ParamObject.require("result", "参数描述", //
                                                                    Primitive.require("city", DataType.String, "参数描述")//
                                                            )//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );
            Param list = ParamArray.require("list", "参数描述", //
                    ParamObject.require(//
                            ParamObject.require("result", "参数描述", //
                                    ParamArray.require("T", "参数描述", //
                                            ParamObject.require(//
                                                    ParamObject.require("status", "参数描述", //
                                                            Primitive.require("statusCode", DataType.Number, "参数描述")//
                                                    ), //
                                                    ParamArray.require("result", "参数描述", //
                                                            ParamObject.require(//
                                                                    ParamObject.require("status", "参数描述", //
                                                                            Primitive.require("statusReason", DataType.String, "参数描述")//
                                                                    ), //
                                                                    ParamObject.require("result", "参数描述", //
                                                                            Primitive.require("city", DataType.String, "参数描述")//
                                                                    )//
                                                            )//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );

            Param B1 = ParamObject.require("B1", "参数描述", //
                    ParamObject.require("result", "参数描述", //
                            ParamObject.require("B", "参数描述", //
                                    ParamObject.require("status", "参数描述", //
                                            Primitive.optional("statusCode", DataType.Number, "参数描述")//
                                    ), //
                                    ParamArray.optional("result", "参数描述", //
                                            ParamObject.require(//
                                                    Primitive.optional("result", DataType.String, "参数描述"), //
                                                    ParamObject.optional("status", "", //
                                                            Primitive.require("statusCode", DataType.Number, "参数描述")//
                                                    )//
                                            )//
                                    )//
                            )//
                    ), //
                    ParamObject.require("status", "参数描述", //
                            Primitive.require("statusReason", DataType.String, "statusReason")//
                    )//
            );
            Map<String, Object> map = Validation.request(A1, B1).checkRequest(request).extractRequest(request);
            System.out.println(map);
            String string = "{'result':{'T':[{'status':{'statusCode':0},'result':[{'status':{'statusReason':''},'result':{'city':'北京'}}]}]}}"
                    .replace("'", "\"");
            Assert.assertEquals(string, JsonUtils.stringify(map.get("A1")));
            String expected = "{'status':{'statusReason':''},'result':{'B':{'status':{'statusCode':0},'result':[{'status':{'statusCode':0},'result':'OK'}]}}}"
                    .replace("'", "\"");
            Assert.assertEquals(expected, JsonUtils.stringify(map.get("B1")));

            map = Validation.request(list).checkRequest(request).extractRequest(request);
            ArrayNode arrayNode = (ArrayNode) map.get("list");
            Assert.assertEquals(JsonUtils.stringify(arrayNode.get(0)), JsonUtils.stringify(arrayNode.get(1)));
            Assert.assertEquals(JsonUtils.stringify(arrayNode.get(0)), string);
            System.out.println("List-->>>" + JsonUtils.stringify(map.get("list")));
            long start = System.currentTimeMillis();
            for (int i = 0; i < 50000; i++) {
                Validation.request(A1, B1, list).checkRequest(request).extractRequest(request);
            }
            System.out.println("使用耗时:" + (System.currentTimeMillis() - start));
        }
    }

    @Test
    public void test_checkParams() {
        Param param = buildParam();
        // 输出参数定义
        Gson gson = new Gson();
        System.out.println(gson.toJson(param));
        HttpServletRequest request = buildHttpRequest();
        System.out.println("objParam:" + request.getParameter("objParam"));
        Validation.request(param).checkRequest(request);
    }

    @Test
    public void test_deserialize() {
        Param param = buildParam();
        // 输出参数定义
        Gson gson = new Gson();
        String json = gson.toJson(param);
        System.out.println(json);
        MockHttpServletRequest mock_request = new MockHttpServletRequest();
        param = gson.fromJson(json, ParamBase.class);
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
        ids.add("10x0");
        map.put("ids", ids);
        String json1 = JsonUtils.stringify(map);
        mock_request.addParameter("objParam", json1);
        try {
            Validation.request(param).checkRequest(mock_request);
        } catch (Exception e) {
            Assert.assertEquals("`objParam.ids[]`必须小于等于100", e.getMessage());
        }
    }

    @Test
    public void test_string_length() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("password", "zhangsanfeng---------");
        Primitive param = Primitive.require("password", DataType.String, "密码").setMin(8).setMax(20);
        try {
            Validation.request(param).checkRequest(request);
            Assert.fail("没有出现预期错误");
        } catch (Exception e) {
            Assert.assertEquals(param.getTipMsg(), e.getMessage());
        }
    }

    @Test
    public void test_number_length() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("price", "7.99999999");
        request.addParameter("price_min", "7.19");
        request.addParameter("price_max", "20.00001");
        {
            Primitive param = Primitive.require("price", DataType.Number, "价格").setMin(8).setMax(20);
            try {
                Validation.request(param).checkRequest(request);
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals(param.getTipMsg(), e.getMessage());
            }
        }
        {
            Primitive param = Primitive.require("price_min", DataType.Number, "价格").setMin(7.18).setMax(20);
            Validation.request(param).checkRequest(request);
        }

        {
            Primitive param = Primitive.require("price_max", DataType.Number, "价格").setMin(7.18).setMax(20);
            try {
                Validation.request(param).checkRequest(request);
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals(param.getTipMsg(), e.getMessage());
            }
        }
    }

    @Test
    public void test_err_param_definds() {
        try {
            Primitive.require("", DataType.Array, "");
            Assert.fail("没有出现预期错误");
        } catch (Exception e) {
            Assert.assertEquals("无效的数据类型:Array", e.getMessage());
        }
        try {
            ParamObject.require("o", "描述", //
                    ParamArray.optional("a", "描述", //
                            ParamArray.require("a1", "描述", //
                                    ParamObject.require(//
                                            Primitive.require("", DataType.String, "")//
                                    )//
                            )//
                    )//
            );
            Assert.fail("没有出现预期错误");
        } catch (Exception e) {
            Assert.assertEquals("无效的数据格式(数组不应该直接嵌套数组)", e.getMessage());
        }
    }

    @Test
    public void test_error() {
        {
            ParamObject param = ParamObject.require("objParam", "对象参数", //
                    ParamObject.require("obj1", "对象", Primitive.require("name", DataType.String, "姓名")));
            MockHttpServletRequest request = new MockHttpServletRequest();
            {
                try {
                    request.addParameter("objParam", "{\"obj1\":{}}");
                    Validation.request(param).checkRequest(request);
                    Assert.fail("没有出现预期错误");
                } catch (Exception e) {
                    Assert.assertEquals("`objParam.obj1.name`参数缺失", e.getMessage());
                }
            }
        }

        {
            try {
                ParamArray.require("ids", "id列表", //
                        Primitive.require("id", DataType.Number, "商品ID").setMax(100) //
                );
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals("ParamArray节点的子节点不应该存在节点名称", e.getMessage());
            }
        }
        {
            ParamObject param = ParamObject.require("objParam", "对象参数", //
                    ParamObject.require("obj1", "对象", //
                            Primitive.require("name", DataType.String, "姓名")//
                    ), //
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
            MockHttpServletRequest request = new MockHttpServletRequest();
            {
                try {
                    request.addParameter("objParam", "{\"obj1\":{\"name\":\"张三\"}}");
                    Validation.request(param).checkRequest(request);
                    Assert.fail("没有出现预期错误");
                } catch (Exception e) {
                    Assert.assertEquals("`objParam.items`参数缺失", e.getMessage());
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
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
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":{\"name\":\"张三\"}}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items`参数错误", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"name\":\"张三\"}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.id`参数缺失", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":null}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.ids`参数缺失", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[]}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("objParam.items.ids[]不能为空", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMin(10).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[10000]}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.ids`限制范围10(含)~100(含)", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMin(10).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[true,false]}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.ids[]`限制范围10(含)~100(含)", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMin(10).setMax(100) //
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam", "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[{}]}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.ids`参数错误", e.getMessage());
                    }
                }
            }
        }

        {
            {
                ParamObject param = ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        Primitive.require("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
                                        Primitive.require("name", DataType.String, "商品名称").setMax(50), //
                                        ParamArray.require("ids", "id列表", //
                                                Primitive.require(DataType.Number).setMin(10).setMax(100) //
                                        ), //
                                        ParamArray.require("array", "x", //
                                                ParamObject.require( //
                                                        Primitive.optional("test", DataType.Number, "")//
                                                )//
                                        )//
                                )//
                        ) //
                );
                MockHttpServletRequest request = new MockHttpServletRequest();
                {
                    try {
                        request.addParameter("objParam",
                                "{\"items\":[{\"id\":1,\"name\":\"张三\",\"ids\":[100],\"array\":[{\"test\":\"x\"}]}]}");
                        Validation.request(param).checkRequest(request);
                        Assert.fail("没有出现预期错误");
                    } catch (Exception e) {
                        Assert.assertEquals("`objParam.items.array.test`必须是一个数字", e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    public void test_init_param_error() {
        {
            try {
                ParamObject.require("objParam", "对象参数", //
                        ParamArray.require("items", "商品列表", //
                                ParamObject.require(//
                                        ParamArray.require("array", "x", //
                                                ParamObject.require("x", "x", //
                                                        Primitive.optional("test", DataType.Number, "")//
                                                )//
                                        )//
                                )//
                        ) //
                );
                Assert.fail("没有出现预期错误");
            } catch (Exception e) {
                Assert.assertEquals("ParamArray节点的子节点不应该存在节点名称", e.getMessage());
            }
        }
    }

    @Test
    public void test_seriable() {
        try {
            String a1 = DataResult
                    .make("T",
                            new Object[]{DataResult.success(new Object[]{DataResult.make("S", "OK").addResult("city", "北京")})})
                    .toJSON();
            String b1 = DataResult.make("B", DataResult.success(new Object[]{DataResult.success("OK")})).toJSON();
            System.out.println(a1);
            System.out.println(b1);
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("A1", a1);
            request.addParameter("B1", b1);
            Param A1 = ParamObject.require("A1", "参数描述", //
                    ParamObject.require("result", "参数描述", //
                            ParamArray.require("T", "参数描述", //
                                    ParamObject.require(//
                                            ParamObject.require("status", "参数描述", //
                                                    Primitive.require("statusCode", DataType.Number, "参数描述")//
                                            ), //
                                            ParamArray.require("result", "参数描述", //
                                                    ParamObject.require(//
                                                            Primitive.require("result", DataType.String, "参数描述")//
                                                    )//
                                            )//
                                    )//
                            )//
                    )//
            );
            Param B1 = ParamObject.require("B1", "X", ParamObject.require("result", "X"));
            {// 经过一次序列化在反序列化处理
                A1 = GsonSerialize.INSTANCE.decode(GsonSerialize.INSTANCE.encode(A1), ParamBase.class);
                B1 = GsonSerialize.INSTANCE.decode(GsonSerialize.INSTANCE.encode(B1), ParamBase.class);
            }
            Map<String, Object> map = Validation.request(A1, B1).checkRequest(request).extractRequest(request);
            Assert.fail("没有出现预期错误");
            System.out.println(map);
        } catch (Exception e) {
            Assert.assertEquals("`A1.result.T.result.result`参数错误", e.getMessage());
        }
    }

    @Test
    public void test_json() {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("obj", "");
            Param param = ParamObject.require("obj", "参数描述");
            Validation.request(param).checkRequest(request);
            Assert.fail("没有出现预期错误");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("`obj`参数错误", e.getMessage());
        }
    }
}
