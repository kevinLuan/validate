package com.open.param;

/**
 * 对象ObjectNode参数
 *
 * @author KEVIN LUAN
 */
public class ParamObject extends ParamBase {

  protected ParamObject() {
    super();
  }

  public ParamObject(String name, boolean required, String description, Param[] childrens) {
    super(name, required, DataType.Object, description);
    if (childrens != null) {
      this.children = new ParamBase[childrens.length];
      for (int i = 0; i < childrens.length; i++) {
        Param param = childrens[i];
        this.children[i] = (ParamBase) param;
        // if (param.isObjectValue()) {
        // throw new IllegalArgumentException("ParamObject子节点Name不能为空");
        // }
      }
    }
  }

  public static ParamObject required(String name, String description, Param... childrens) {
    return new ParamObject(name, true, description, childrens);
  }

  public static ParamObject required(Param... childrens) {
    return new ParamObject("", true, null, childrens);
  }

  public static ParamObject of(Param... childrens) {
    return new ParamObject("", false, null, childrens);
  }

  public static ParamObject of(String name, String description, Param... childrens) {
    return new ParamObject(name, false, description, childrens);
  }

  @Override
  public final boolean isObject() {
    return true;
  }

  @Override
  public final ParamObject asObject() {
    return this;
  }

  public final boolean existsChildrens() {
    return this.children != null && children.length > 0;
  }

  public Param[] getChildren() {
    return children;
  }

}
