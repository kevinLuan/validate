package com.open.param.test;

import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.Primitive;
import com.open.utils.ParamExec;
import com.open.utils.ParamHelper;

public class TestParamHelper {
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

	public static void main(String[] args) {
		Param param = buildResult();
		ParamHelper paramHelper = new ParamHelper(param);
		paramHelper.exec(new ParamExec() {
			@Override
			public void execute(Param param) {
				System.out.println("节点名称:" + param.getName() + "\t是否必须:" + param.isRequire() + "-\t路径：" + param.getPath());
			}
		});
	}
}
