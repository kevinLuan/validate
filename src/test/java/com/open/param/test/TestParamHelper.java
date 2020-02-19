package com.open.param.test;

import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamNumber;
import com.open.param.ParamObject;
import com.open.param.ParamString;
import com.open.param.api.NumberApi;
import com.open.param.api.ParamApi;
import com.open.param.common.GenerateCode;
import com.open.param.common.ParamSerializable;
import com.open.utils.ParamExec;
import com.open.utils.ParamHelper;
import org.junit.Assert;
import org.junit.Test;

public class TestParamHelper {

  private Param buildResult() {
    return ParamObject.of("result", "返回数据", //
        ParamString.required("name", "姓名").setMax(5), //
        ParamNumber.required("age", "年龄").setMin(0).setMax(120), //
        ParamArray.required("items", "商品列表", //
            ParamObject.required(//
                ParamNumber.required("id", "商品ID").setMin(1).setMax(10), //
                ParamString.required("name", "商品名称").setMax(50)//
            )//
        ), //
        ParamArray.required("ids", "id列表", //
            ParamNumber.required(null).setMax(100) //
        )//
    );
  }

  private Param getParamV2() {
    return ParamApi.object().name("result").description("返回数据").children(
        ParamApi.string(true).name("name").description("姓名").max(5),
        ParamApi.number(true).name("age").description("年龄").min(0).max(120),
        ParamApi.array(true).name("items").description("商品列表")
            .children(ParamApi.object(true).children(
                ParamApi.number(true).name("id").description("商品ID").min(1).max(10),
                ParamApi.string(true).name("name").description("商品名称").max(50))),
        ParamApi.array(true).name("ids").description("id列表").children(
            NumberApi.create().required().max(100)));
  }

  @Test
  public void test() {
    StringBuilder builderA = new StringBuilder();
    {
      Param param = buildResult();
      ParamHelper paramHelper = new ParamHelper(param);
      paramHelper.exec(new ParamExec() {
        @Override
        public void execute(Param param) {
          builderA.append("节点名称:" + param.getName() + "\t是否必须:" + param.isRequired() + "-\t路径："
              + param.getPath()).append("\n");
        }
      });
    }
    StringBuilder builderB = new StringBuilder();
    {
      Param param = getParamV2();
      ParamHelper paramHelper = new ParamHelper(param);
      paramHelper.exec(new ParamExec() {
        @Override
        public void execute(Param param) {
          builderB.append("节点名称:" + param.getName() + "\t是否必须:" + param.isRequired() + "-\t路径："
              + param.getPath()).append("\n");
        }
      });
    }
    System.out.println("------------A--------------");
    System.out.println(builderA.toString());
    System.out.println("------------B--------------");
    System.out.println(builderB.toString());
    Assert.assertEquals(builderA.toString(), builderB.toString());
  }

  @Test
  public void testDecodeToParam() {
    String json =
        "{\"dataType\":\"Object\",\"children\":[{\"name\":\"status\",\"description\":\"状态\",\"dataType\":\"Object\",\"children\":[{\"name\":\"statusCode\",\"description\":\"状态码\",\"exampleValue\":\"1500\",\"dataType\":\"Number\"},{\"name\":\"statusReason\",\"description\":\"状态描述\",\"exampleValue\":\"参数错误\",\"dataType\":\"String\"}]},{\"name\":\"result\",\"description\":\"结果\",\"dataType\":\"Object\",\"children\":[{\"name\":\"id\",\"description\":\"ID\",\"exampleValue\":\"1234\",\"dataType\":\"String\"},{\"name\":\"name\",\"description\":\"名称\",\"exampleValue\":\"xxx\",\"dataType\":\"String\"}]}]}";
    Param param = ParamSerializable.INSTANCE.decode(json);
    System.out.println(param);
    System.out.println(GenerateCode.getJavaCodeV1(param));
  }
}
