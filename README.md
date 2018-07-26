#### 根据用户自定义参数做合法性验证

#### 项目介绍
  
    1. 根据自定义的结构化参数模型进行合法性验证验证，参数定义格式参考1.0.0,验证示例参考1.1.1；
    2. 根据定义参数结构提取有效数据结构体，忽略未定义的参数；

#####1.0.0 定义请求参数对象结构
```Java
Param param= Param buildParam() {
		return ParamObject.required("objParam", "对象参数", //
				ParamPrimitive.required("name", DataType.String, "姓名").setMax(5), //
				ParamPrimitive.required("age", DataType.Number, "年龄").setMin(0).setMax(120), //
				ParamArray.required("items", "商品列表", //
						ParamObject.required(//
								ParamPrimitive.required("id", DataType.Number, "商品ID").setMin(1).setMax(10), //
								ParamPrimitive.required("name", DataType.String, "商品名称").setMax(50)//
						)//
				), //
				ParamArray.required("ids", "id列表", //
						ParamPrimitive.required(DataType.Number).setMax(100) //
				)//
		);
	}
```

#####1.0.1 参数合法性验证 
  
    待补充...

#####1.0.2 提取合法性数据

    待补充...
  

###### 问题反馈
  email: kevin_Luan@126.com