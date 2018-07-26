package com.open.param.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ApiUnknownNodeFilter implements UnknownNodeFilter {
	public static final ApiUnknownNodeFilter INSTANCE = new ApiUnknownNodeFilter();

	/**
	 * 所有对象节点均支持extendProps及remark属性，只要符是合要求格式的均跳过处理，否则删除该节点
	 */
	@Override
	public void process(String name, ObjectNode parent) {
		if (parent != null && name != null) {
			if (ExtendNode.isDefinition(name)) {
				if (ExtendNode.getDefinition(name).matchesType(parent.get(name))) {
					return;
				}
			}
			parent.remove(name);
		}
	}

}
