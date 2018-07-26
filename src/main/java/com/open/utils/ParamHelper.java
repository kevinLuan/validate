package com.open.utils;

import com.open.param.Param;
import com.open.param.ParamArray;
import com.open.param.ParamObject;
import com.open.param.api.AdjustParamInstance;
import com.open.param.api.ParentReference;

public class ParamHelper {
	public Param[] params;

	public ParamHelper(Param... params) {
		this.params = params;
		this.init();
	}

	private void init() {
		for (int i = 0; i < this.params.length; i++) {
			Param p = this.params[i];
			this.params[i] = AdjustParamInstance.adjust(p);
			ParentReference.refreshParentReference(this.params);
		}
	}

	public Param[] getParams() {
		return params;
	}

	public void exec(ParamExec exec) {
		for (int i = 0; i < params.length; i++) {
			Param param = params[i];
			if (param.isArray()) {
				array(exec, param.asArray());
			} else if (param.isObject()) {
				object(exec, param.asObject());
			} else {
				exec.execute(param);
			}
		}
	}

	private void object(ParamExec exec, ParamObject obj) {
		exec.execute(obj);
		Param[] params = obj.getChildren();
		for (int i = 0; i < params.length; i++) {
			Param param = params[i];
			if (param.isObject()) {
				object(exec, param.asObject());
			} else if (param.isArray()) {
				array(exec, param.asArray());
			} else {
				exec.execute(param);
			}
		}
	}

	private void array(ParamExec exec, ParamArray array) {
		exec.execute(array);
		Param param = array.getChildrenAsParam();
		if (param.isObject()) {
			object(exec, param.asObject());
		} else {
			exec.execute(param);
		}
	}
}
