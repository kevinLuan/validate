package io.github.validate.param;

/**
 * 原子参数（参数的最小单位）
 *
 * @author KEVIN LUAN
 */
public class Primitive extends ParamBase {

    public Primitive(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        super(name, require, dataType, description);
        if (!DataType.isPrimitive(dataType)) {
            throw new IllegalArgumentException("无效的数据类型:" + dataType);
        }
        this.min = min;
        this.max = max;
    }

    public static Primitive mark(String name, boolean require, DataType dataType, String description, Number min, Number max) {
        return new Primitive(name, require, dataType, description, min, max);
    }

    /**
     * 创建一个必须参数
     *
     * @param name
     * @return
     */
    public static Primitive require(String name, DataType dataType, String description) {
        return Primitive.mark(name, true, dataType, description, null, null);
    }

    /**
     * 创建一个必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @param dataType
     * @return
     */
    public static Primitive require(DataType dataType) {
        return Primitive.mark("", true, dataType, null, null, null);
    }

    /**
     * 创建一个非必须参数
     *
     * @param name
     * @return
     */
    public static Primitive optional(String name, DataType dataType, String description) {
        return Primitive.mark(name, false, dataType, description, null, null);
    }

    /**
     * 创建一个非必须参数
     * <p>
     * 当前基本类型只能用在父节点是Array的情况例如：array[0,1,2]
     *
     * @param name
     * @return
     */
    public static Primitive optional(DataType dataType) {
        return Primitive.mark("", false, dataType, null, null, null);
    }

    @Override
    public Primitive asPrimitive() {
        return this;
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }

    public Primitive setMin(Number min) {
        this.min = min;
        this.check();
        return this;
    }

    private void check() {
        if (min != null && max != null) {
            if (String.valueOf(min).indexOf(".") != -1 || String.valueOf(max).indexOf(".") != -1) {
                if (min.doubleValue() > max.doubleValue()) {
                    throw new IllegalArgumentException("`" + max + "`必须大于`" + min + "`");
                }
            } else {
                if (min.longValue() > max.longValue()) {
                    throw new IllegalArgumentException("`" + max + "`必须大于`" + min + "`");
                }
            }
        }
    }

    public Primitive between(Number min, Number max) {
        this.setMin(min);
        this.setMax(max);
        return this;
    }

    public Primitive setMax(Number max) {
        this.max = max;
        this.check();
        return this;
    }

    public String getTipMsg() {
        return this.getTipMsg(getPath());
    }

    public String getTipMsg(String path) {
        if (getDataType().isNumber()) {
            if (this.min != null && this.max != null) {
                return "`" + path + "`限制范围" + min + "(含)~" + max + "(含)";
            } else if (this.min != null) {
                return "`" + path + "`必须大于等于" + min;
            } else if (this.max != null) {
                return "`" + path + "`必须小于等于" + this.max;
            } else {
                return "`" + path + "`必须是一个数字";
            }
        } else if (getDataType().isString()) {
            if (this.min != null && this.max != null) {
                return "`" + path + "`长度限制在" + min + "(含)~" + max + "(含)";
            } else if (this.min != null) {
                return "`" + path + "`长度必须大于等于" + min;
            } else if (this.max != null) {
                return "`" + path + "`长度必须小于等于" + this.max;
            }
        }
        return "`" + getPath() + "`参数错误";
    }

    /**
     * 存在范围验证
     *
     * @return
     */
    public boolean existBetweenCheck() {
        return max != null || min != null;
    }

    /**
     * 获取示例原始值
     *
     * @return
     */
    public String getExampleValue() {
        return exampleValue;
    }

    /**
     * 设置示例值(可以用户mock数据)
     *
     * @param exampleValue
     * @return
     */
    public Primitive setExampleValue(Object exampleValue) {
        super.exampleValue = String.valueOf(exampleValue);
        return this;
    }
}
