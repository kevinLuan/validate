package io.github.validate.utils;

import io.github.validate.param.Param;

public class ErrorUtils {

	public static RuntimeException newParamMissing(Param param) {
		if (param.isRootNode()) {
			throw new IllegalArgumentException("`" + param.getName() + "`参数缺失");
		} else {
			throw new IllegalArgumentException("`" + param.getName() + "`参数->`" + param.getPath() + "`缺失");
		}

	}

	public static IllegalArgumentException newParamError(Param param) {
		if (param.isRootNode()) {
			throw new IllegalArgumentException("`" + param.getName() + "`参数错误");
		} else {
			throw new IllegalArgumentException("`" + param.getName() + "`参数->`" + param.getPath() + "`错误");
		}
	}

	public static IllegalArgumentException newParamError(Param param, String detail) {
		if (detail != null) {
			detail = "(" + detail + ")";
		} else {
			detail = "";
		}
		if (param.isRootNode()) {
			throw new IllegalArgumentException("`" + param.getName() + "`参数错误" + detail);
		} else {
			throw new IllegalArgumentException("`" + param.getName() + "`参数->`" + param.getPath() + "`错误" + detail);
		}
	}

	public static IllegalArgumentException newParamValueError(Param param) {
		if (param.isRootNode()) {
			throw new IllegalArgumentException("`" + param.getName() + "`参数数据元素不能为空");
		} else {
			throw new IllegalArgumentException("`" + param.getName() + "`参数->`" + param.getPath() + "`数据元素不能为空");
		}

	}

	/**
	 * 构造类转换异常
	 * 
	 * @param src
	 * @param dest
	 * @return
	 */
	public static ClassCastException newClassCastException(Class<?> src, Class<?> dest) {
		throw new ClassCastException(src.getName() + " cannot be cast to " + dest.getName());
	}
}
