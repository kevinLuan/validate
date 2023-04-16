package io.github.validate.param;

public enum DataType {
	String {
		@Override
		public boolean isString() {
			return true;
		}

		@Override
		public void check(Primitive primitive, String value) {
			if (primitive.isRequired()) {
				if (value == null) {
					throw new IllegalArgumentException(primitive.getName() + "参数不能为空");
				}
			} else {
				if (value == null) {
					return;
				}
			}
			if (primitive.getMin() != null) {
				if (primitive.getMin().intValue() > value.length()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
			if (primitive.getMax() != null) {
				if (primitive.getMax().intValue() < value.length()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
		}
	},
	Number {
		@Override
		public boolean isNumber() {
			return true;
		}
	},
	Array, Object;
	/**
	 * 断言基本数据类型 (String,Number)
	 * 
	 * @param dataType
	 */
	public static boolean isPrimitive(DataType dataType) {
		if (dataType != null && (DataType.String == dataType || DataType.Number == dataType)) {
			return true;
		}
		return false;
	}

	public boolean isNumber() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public void check(Primitive p, String value) {
		// TODO 子类实现
	}

	public static DataType parser(String dataType) {
		for (DataType type : values()) {
			if (type.name().equals(dataType)) {
				return type;
			}
		}
		throw new IllegalArgumentException("不支持的dataType:" + dataType);
	}
}
