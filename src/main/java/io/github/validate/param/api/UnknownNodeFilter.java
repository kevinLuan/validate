package io.github.validate.param.api;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface UnknownNodeFilter {
	/**
	 * 处理未知的Node节点
	 * 
	 * @param name 未知的node name
	 * @param parent
	 * @return
	 */
	public void process(String name, ObjectNode parent);
}
