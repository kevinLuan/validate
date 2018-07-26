package com.open.datatype;

import com.open.param.ParamPrimitive;

public class Numberx {

	public boolean isDouble;
	public boolean isLong;
	public Number value;

	public boolean isNull() {
		return this.value == null;
	}

	public static Numberx parser(String value, boolean required) {
		Numberx numberx = new Numberx();
		if (value != null) {
			if (value.indexOf(".") != -1) {
				numberx.value = Double.parseDouble(value);
				numberx.isDouble = true;
			} else {
				numberx.value = Long.parseLong(value);
				numberx.isLong = true;
			}
		}
		return numberx;
	}

	public void check(ParamPrimitive primitive) {
		if (primitive.isRequired()) {
			if (value == null) {
				throw new IllegalArgumentException(primitive.getName() + "参数不能为空");
			}
		} else {
			if (value == null) {
				return;
			}
		}
		if (isDouble) {
			if (primitive.getMin() != null) {
				if (primitive.getMin().doubleValue() > value.doubleValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
			if (primitive.getMax() != null) {
				if (primitive.getMax().doubleValue() < value.doubleValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
		} else {
			if (primitive.getMin() != null) {
				if (primitive.getMin().longValue() > value.longValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
			if (primitive.getMax() != null) {
				if (primitive.getMax().longValue() < value.longValue()) {
					throw new IllegalArgumentException(primitive.getTipMsg());
				}
			}
		}
	}

}
