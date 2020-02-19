package com.open.param.api;

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
public final class ArrayApi extends ParamArray {

  public ArrayApi() {
  }

  public static ArrayApi create() {
    ArrayApi array = new ArrayApi();
    array.dataType = DataType.Array;
    array.name = "";
    return array;
  }

  public ArrayApi name(String name) {
    super.setName(name);
    return this;
  }

  public ArrayApi required() {
    super.required = true;
    return this;
  }

  public ArrayApi description(String desc) {
    super.description = desc;
    return this;
  }

  public ArrayApi children(Param children) {
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
