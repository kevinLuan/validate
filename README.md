#### 根据用户自定义参数做合法性验证

##### 定义请求参数对象结构
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