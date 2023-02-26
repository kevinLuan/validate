#### 复杂参数验证中间件

#### 项目介绍
  
    1. 根据自定义的结构化参数模型进行合法性验证验证，参数定义格式参考1.0.0,验证示例参考1.1.1；
    2. 根据定义参数结构提取有效数据结构体，忽略未定义的参数；

##### 1.0.0 定义请求参数对象结构
```Java
    Param param = ParamObject.optional("userInfo", "用户信息",
        Primitive.require("name", DataType.String, "姓名").setMin(2).setMax(10),//姓名必填,格式: 2~10字符
        Primitive.optional("age", DataType.Number, "年龄").between(18, 65)//年龄字段选填，格式: 18岁~65岁
    );
```

##### 1.0.1 参数合法性验证 
客户端请求代码示例：
```java
    MockHttpServletRequest request = new MockHttpServletRequest();
    Map<String, Object> map = new HashMap<>();
    map.put("name", "张三丰");
    map.put("age", "60");
    map.put("sql", "CSRF漏洞");
    request.addParameter("userInfo", JsonUtils.stringify(map));
```
客户端请求数据示例：
```json
  {
    "name":"张三",
    "age":30
    //    ...
  }
```
##### 1.0.2 服务端数据验证
代码示例：
```java
Param param = ParamObject.require("userInfo", "用户信息",
    Primitive.require("name", DataType.String, "姓名").setMin(2).setMax(32),
    Primitive.optional("age", DataType.Number, null).between(3, 18)
);
try {
    Validation validation = Validation.request(param);
    //验证请求参数
    validation.checkRequest(request);
    //根据验证参数要求格式，提取数据
    Map<String, Object> extractData = validation.extractRequest(request);
} catch (IllegalArgumentException e) {
    Assert.assertEquals("`paramInfo.age`限制范围3(含)~18(含)", e.getMessage());
}
```
##### 1.0.3 验证完成并提取合法性数据
代码示例：
```java
Param param = ParamObject.require("userInfo", "用户信息",
    Primitive.require("name", DataType.String, "姓名").setMin(2).setMax(32),
    Primitive.optional("age", DataType.Number, null).between(3, 18)
);
Map<String, Object> extractData = Validation.request(param).checkRequest(request).extractRequest(request);
```

###### 问题反馈
  email: kevin_Luan@126.com
