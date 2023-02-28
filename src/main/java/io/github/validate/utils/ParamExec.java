package io.github.validate.utils;

import io.github.validate.param.Param;

public interface ParamExec {
	/**
	 * 递归每个参数节点调用该执行器
	 * 
	 * @param param
	 */
	public void execute(Param param);
}