package io.github.validate.param.api;

import com.fasterxml.jackson.databind.JsonNode;

public enum ExtendNode {
	remark("remark") {
		@Override
		public boolean matchesType(JsonNode jsonNode) {
			if(jsonNode!=null && jsonNode.isTextual()){
				return jsonNode.textValue().length()<=500;
			}
			return false;
		}
	},
	extendProps("extendProps") {
		@Override
		public boolean matchesType(JsonNode jsonNode) {
			return jsonNode != null && jsonNode.isObject();
		}
	};
	private String name;

	private ExtendNode(String name) {
		this.name = name();
	}

	public String getName() {
		return name;
	}

	public static boolean isDefinition(String name) {
		for (ExtendNode node : values()) {
			if (node.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static ExtendNode getDefinition(String name) {
		for (ExtendNode node : values()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public abstract boolean matchesType(JsonNode jsonNode);
}
