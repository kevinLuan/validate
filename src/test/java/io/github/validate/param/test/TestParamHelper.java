package io.github.validate.param.test;

import io.github.validate.param.DataType;
import io.github.validate.param.Param;
import io.github.validate.param.ParamArray;
import io.github.validate.param.ParamObject;
import io.github.validate.param.Primitive;
import io.github.validate.utils.ParamExec;
import io.github.validate.utils.ParamHelper;

public class TestParamHelper {
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

	public static void main(String[] args) {
		Param param = buildResult();
		ParamHelper paramHelper = new ParamHelper(param);
		paramHelper.exec(new ParamExec() {
			@Override
			public void execute(Param param) {
				System.out.println("节点名称:" + param.getName() + "\t是否必须:" + param.isRequired() + "-\t路径：" + param.getPath());
			}
		});
	}
}
