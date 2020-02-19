package com.open.param.v2;

import org.apache.commons.lang3.StringUtils;
import com.open.param.DataType;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamBase;

/**
 * 数组参数类型
 *
 * @author KEVIN LUAN
 */
public class ArrayType extends ParamArray {

  public ArrayType() {
  }

  public static ArrayType create() {
    ArrayType array = new ArrayType();
    array.dataType = DataType.Array;
    array.name = "";
    return array;
  }

  public ArrayType name(String name) {
    super.setName(name);
    return this;
  }

  public ArrayType required() {
    super.required = true;
    return this;
  }

  public ArrayType description(String desc) {
    super.description = desc;
    return this;
  }

  public ArrayType children(Param children) {
    super.children = new ParamBase[]{(ParamBase) children};
    check(children);
    return this;
  }

  private void check(Param childrens) {
    if (childrens != null) {
      if (childrens.getDataType() == DataType.Array) {
        throw new IllegalArgumentException("无效的数据格式(数组不应该直接嵌套数组)");
      } else {
        if (StringUtils.isNotBlank(childrens.getName())) {
          throw new IllegalArgumentException("ParamArray节点的子节点不应该存在节点名称");
        }
      }
    }
  }

  public Param[] getChildren() {
    return children;
  }
}
