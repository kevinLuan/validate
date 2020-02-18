package com.open.param.api;

import java.util.List;
import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;

/**
 * 调整参数实例对象，在通过反序列化框架序列化出来的对象统一是ParamBase类型，调整后会改为原始类型
 * 
 * @author KEVIN LUAN
 */
public class AdjustParamInstance {

	public static void adjust(List<Param> paramList) {
		for (int i = 0; i < paramList.size(); i++) {
			Param param = paramList.get(i);
			if (param.isArray()) {
				paramList.set(i, param.asArray());
				param = paramList.get(i);
				refreshChildrens(param.asArray().getChildren());
			} else if (param.isObject()) {
				paramList.set(i, param.asObject());
				param = paramList.get(i);
				refreshChildrens(param.asObject().getChildren());
			} else if (param.isPrimitive()) {
				paramList.set(i, param.asPrimitive());
			} else {
				throw new IllegalArgumentException("不支持的类型:" + param);
			}
		}
	}

	public static Param adjust(Param param) {
		Param refParam = param;
		if (param.isArray()) {
			refParam = param.asArray();
			refreshChildrens(param.asArray().getChildren());
		} else if (param.isObject()) {
			refParam = param.asObject();
			refreshChildrens(param.asObject().getChildren());
		} else if (param.isPrimitive()) {
			refParam = param.asPrimitive();
		} else {
			throw new IllegalArgumentException("不支持的类型:" + param);
		}
		return refParam;
	}

	private static void refreshChildrens(Param children) {
		if (children.isArray()) {
			ParamArray array = children.asArray();
			refreshChildrens(array.getChildren());
		} else if (children.isObject()) {
			ParamObject object = children.asObject();
			refreshChildrens(object.getChildren());
		}
	}

	private static void refreshChildrens(Param[] childrens) {
		if (childrens != null) {
			for (int i = 0; i < childrens.length; i++) {
				if (childrens[i].isArray()) {
					childrens[i] = childrens[i].asArray();
					refreshChildrens(childrens[i]);
				} else if (childrens[i].isObject()) {
					childrens[i] = childrens[i].asObject();
					refreshChildrens(childrens[i]);
				} else if (childrens[i].isPrimitive()) {
					childrens[i] = childrens[i].asPrimitive();
				} else {
					throw new IllegalArgumentException("不支持的类型:" + childrens[i]);
				}
			}
		}
	}
}