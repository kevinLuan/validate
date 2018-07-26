package com.open.param;

import org.apache.commons.lang3.StringUtils;

/**
 * 解析参数节点Path工具类
 * 
 * @author KEVIN LUAN
 */
class NodeHelper {
	// 指定从root节点到当前节点的path (root->current_node)
	private String path;
	private String rootName;
	private String currentNode;

	public String getCurrentName() {
		return this.currentNode;
	}

	public String getRootName() {
		return this.rootName;
	}

	public String getPath() {
		return this.path;
	}

	public String toString() {
		return "rootName:" + rootName + "\tcurrent_node:" + currentNode + "\tpath:" + path;
	}

	public static NodeHelper parser(Param pm) {
		NodeHelper helper = new NodeHelper();
		helper.init(pm);
		return helper;
	}

	private void init(Param param) {
		if (param.isArray()) {
			if (param.getParentNode() != null) {
				init(param.getParentNode());
			}
		} else if (param.isObjectValue()) {
			if (param.getParentNode() != null) {
				init(param.getParentNode());
			}
		} else {
			this.currentNode = param.getName();
		}

		if (param.isRootNode()) {
			this.path = param.getName();
			this.rootName = param.getName();
		} else {
			StringBuilder nodeBuild = new StringBuilder();
			this.rootName = parserPathAndRootNode(param, nodeBuild);
			this.path = nodeBuild.toString();
		}
	}

	private String parserPathAndRootNode(Param param, StringBuilder nodeBuild) {
		if (param.isRootNode() && nodeBuild.length() == 0) {
			nodeBuild.append(param.getName());
			return param.getName();
		}
		if (StringUtils.isNotBlank(param.getName())) {
			if (nodeBuild.length() == 0) {
				nodeBuild.append(param.getName());
			} else {
				nodeBuild.insert(0, param.getName() + ".");
			}
		}
		if (param.isRootNode()) {
			return param.getName();
		} else {
			return parserPathAndRootNode(param.getParentNode(), nodeBuild);
		}
	}

	public boolean isRootNode() {
		return this.rootName.equals(this.path);
	}
}
