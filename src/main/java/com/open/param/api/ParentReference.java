package com.open.param.api;

import com.open.param.ParamBase;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;

/**
 * 对象父级节点引用设置
 * 
 * @author KEVIN LUAN
 */
public class ParentReference {
  private static final Logger LOGGER = LoggerFactory.getLogger(ParentReference.class);

  public static void refreshParentReference(List<Param> params) {
    for (Param pm : params) {
      if (pm.isArray()) {
        arrayParam(pm.asArray(), null);
      } else if (pm.isObject()) {
        objectParam(pm.asObject(), null);
      }
    }
  }

  /**
   * 设置Param父级节点引用
   */
  public static void refreshParentReference(Param... params) {
    for (Param pm : params) {
      if (pm.isArray()) {
        arrayParam(pm.asArray(), null);
      } else if (pm.isObject()) {
        objectParam(pm.asObject(), null);
      }
    }
  }

  private static void arrayParam(ParamArray array, Param parent) {
    array.setParentNode((ParamBase) parent);
    if (array.existsChildrens()) {
      Param pm = array.getChildrenAsParam();
      if (pm.isObject()) {
        objectParam(pm.asObject(), array);
      } else if (pm.isPrimitive()) {
        pm.setParentNode(array);
      } else if (pm.isArray()) {
        arrayParam(pm.asArray(), array);
      } else {
        LOGGER.warn("没有父级引用类型->" + pm);
      }
    }
  }

  private static void objectParam(ParamObject object, Param parent) {
    object.setParentNode((ParamBase) parent);
    if (object.existsChildrens()) {
      for (Param pm : object.getChildren()) {
        if (pm.isObject()) {
          objectParam(pm.asObject(), object);
        } else if (pm.isPrimitive()) {
          pm.setParentNode(object);
        } else if (pm.isArray()) {
          arrayParam(pm.asArray(), object);
        } else {
          LOGGER.warn("没有父级引用类型->" + pm);
        }
      }
    }
  }
}
