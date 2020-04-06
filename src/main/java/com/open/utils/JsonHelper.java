package com.open.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Objects;
import java.util.function.Consumer;

public class JsonHelper {

  private String json = null;
  private JsonNode _jsonNode;
  private boolean failover = true;
  private static ObjectMapper mapper = new ObjectMapper();

  private JsonHelper(String json) throws IOException {
    this(mapper.readTree(json));
  }

  private JsonHelper(JsonNode jsonNode) {
    Objects.requireNonNull(jsonNode, "`jsonNode`参数不能为空");
    this._jsonNode = jsonNode;
    this.json = jsonNode.toString();
  }

  public static JsonHelper of(String json) throws IOException {
    return new JsonHelper(json);
  }

  public static JsonHelper of(JsonNode node) {
    return new JsonHelper(node);
  }

  /**
   * 深度遍历,如果path叶子节点为array的话，consumer消费传入的为数组中的每个原数
   *
   * @param path
   */
  public int deepTraversal(String path, Consumer<JsonNode> func) {
    String[] nodes = parsePath(path);
    if (this._jsonNode.isObject()) {
      return doDeepSearch(path, (ObjectNode) this._jsonNode, nodes, null, null, Option.traversal,
          func);
    } else if (this._jsonNode.isArray()) {
      return doDeepSearch(path, (ArrayNode) this._jsonNode, nodes, null, null, Option.traversal,
          func);
    } else {
      throw new IllegalArgumentException("不支持的:" + path + "节点");
    }
  }

  /**
   * 如果path叶子节点缺失则添加叶子节点
   *
   * @param path
   * @param value
   */
  public int missingAndSet(String path, String value) {
    String[] nodes = parsePath(path);
    if (this._jsonNode.isObject()) {
      return deepSearch(path, (ObjectNode) this._jsonNode, nodes, null, value,
          Option.missingAndAdd);
    } else if (this._jsonNode.isArray()) {
      return deepSearch(path, (ArrayNode) this._jsonNode, nodes, null, value, Option.missingAndAdd);
    } else {
      throw throwError(path, "", this._jsonNode);
    }
  }

  private IllegalArgumentException throwError(String path, String node, JsonNode jsonNode) {
    throw new IllegalArgumentException(
        "path:`" + path + "`中存在不支持的操作(node:`" + node + "` dataType:`" + jsonNode
            .getNodeType() + "`)");
  }

  /**
   * 根据Path节点获取Value
   *
   * @param path
   * @return
   */
  public JsonNode get(String path) {
    String[] nodes = parsePath(path);
    JsonNode root = this._jsonNode;
    if (root.isObject()) {
      for (int i = 0; i < nodes.length - 1; i++) {
        root = root.get(nodes[i]);
        if (root == null) {
          if (failover) {
            return null;
          }
          throw throwError(path, nodes[i], root);
        } else if (!root.isObject()) {
          throw throwError(path, nodes[i], root);
        }
      }
      if (root.isObject()) {
        return root.get(nodes[nodes.length - 1]);
      } else {
        throw throwError(path, nodes[nodes.length - 1], root);
      }
    }
    throw new IllegalArgumentException(
        "不支持的node操作:`" + root.getNodeType() + "` path:`" + path + "`");
  }

  /**
   * 根据json node path删除
   *
   * @param path
   * @return
   */
  public int delete(String path) {
    String[] nodes = parsePath(path);
    if (this._jsonNode.isObject()) {
      return deepSearch(path, (ObjectNode) this._jsonNode, nodes, null, null, Option.delete);
    } else if (this._jsonNode.isArray()) {
      return deepSearch(path, (ArrayNode) this._jsonNode, nodes, null, null, Option.delete);
    } else {
      throw new IllegalArgumentException("不支持的:" + path + "节点");
    }
  }

  /**
   * 比较并删除
   *
   * @param path
   * @param expect
   */
  public int compareAndDelete(String path, String expect) {
    String[] nodes = parsePath(path);
    if (this._jsonNode.isObject()) {
      return deepSearch(path, (ObjectNode) this._jsonNode, nodes, expect, null,
          Option.compareAndDel);
    } else if (this._jsonNode.isArray()) {
      return deepSearch(path, (ArrayNode) this._jsonNode, nodes, expect, null,
          Option.compareAndDel);
    } else {
      throw new IllegalArgumentException("不支持的:" + path + "节点");
    }
  }

  /**
   * 验证path节点value是否与期望值相等
   *
   * @param path
   * @param expect
   * @return
   */
  public boolean checkValue(String path, String expect) {
    return checkValue(_jsonNode, path, expect);
  }

  private boolean checkValue(JsonNode root, String path, String expect) {
    String[] nodes = parsePath(path);
    for (int i = 0; i < nodes.length; i++) {
      root = root.get(nodes[i]);
      if (root == null && i != nodes.length - 1) {
        return false;
      }
    }
    if (isNull(root) || expect == null) {
      return isNull(root) && expect == null;
    } else {
      return root.textValue().equals(expect);
    }
  }

  private boolean isNull(JsonNode value) {
    return value == null || value.isNull() || value.isMissingNode();
  }

  /**
   * 进入指定path节点,返回path节点下的对象引用
   *
   * @param path
   * @return
   */
  public JsonHelper cd(String path) {
    String[] nodes = parsePath(path);
    JsonNode root = _jsonNode;
    if (!root.isObject()) {
      throw new IllegalArgumentException("无效的数据类型:" + root.getNodeType());
    }
    for (int i = 0; i < nodes.length - 1; i++) {
      root = root.get(nodes[i]);
      if (root == null || !root.isObject()) {
        throw new IllegalArgumentException("无效的path:`" + path + "`");
      }
    }
    root = root.get(nodes[nodes.length - 1]);
    if (root == null) {
      throw new IllegalArgumentException("无效的path:`" + path + "`");
    }
    return JsonHelper.of(root);
  }

  /**
   * 根据path节点比较期望目标值比较，如果相等，则使用新的值覆盖
   *
   * @param path
   * @param expect
   * @param update
   * @return
   */
  public int compareAndSet(String path, String expect, String update) {
    String[] nodes = parsePath(path);
    if (this._jsonNode.isObject()) {
      return deepSearch(path, (ObjectNode) this._jsonNode, nodes, expect, update,
          Option.compareAndSet);
    } else if (this._jsonNode.isArray()) {
      return deepSearch(path, (ArrayNode) this._jsonNode, nodes, expect, update,
          Option.compareAndSet);
    } else {
      throw throwError(path, "", this._jsonNode);
    }
  }

  private int deepSearch(String path, ObjectNode objectNode, String[] nodes, String expect,
      String update, Option option) {
    return doDeepSearch(path, objectNode, nodes, expect, update, option, null);
  }

  private int doDeepSearch(String path, ObjectNode objectNode, String[] nodes, String expect,
      String update, Option option, Consumer<JsonNode> func) {
    JsonNode root = objectNode;
    for (int i = 0; i < nodes.length - 1; i++) {
      root = root.get(nodes[i]);
      if (root == null) {
        if (failover) {
          return 0;
        } else {
          throw throwError(path, nodes[i], root);
        }
      } else if (root.isObject()) {
        continue;
      } else if (root.isArray()) {
        String array[] = new String[nodes.length - i - 1];
        System.arraycopy(nodes, i + 1, array, 0, array.length);
        return doDeepSearch(path, (ArrayNode) root, array, expect, update, option, func);
      } else {
        throw throwError(path, nodes[i], root);
      }
    }
    String name = nodes[nodes.length - 1];
    return nodeOperator(path, name, expect, update, root, option, func);
  }

  private int nodeOperator(String path, String name, String expect, String update, JsonNode root,
      Option option, Consumer<JsonNode> func) {
    if (root.isObject()) {
      ObjectNode objNode = (ObjectNode) root;
      return process(name, expect, update, option, func, objNode);
    } else if (root.isArray()) {
      int count = 0;
      for (int i = 0; i < root.size(); i++) {
        if (root.get(i).isObject()) {
          ObjectNode objNode = (ObjectNode) root.get(i);
          return process(name, expect, update, option, func, objNode);
        } else {
          throw throwError(path, name, root.get(i));
        }
      }
      return count;
    } else {
      throw throwError(path, name, root);
    }
  }

  private int process(String name, String expect, String update, Option option,
      Consumer<JsonNode> func,
      ObjectNode objNode) {
    if (option == Option.compareAndSet) {
      if (checkValueEq(objNode, name, expect)) {
        objNode.put(name, update);
        return 1;
      }
    } else if (option == Option.set) {
      objNode.put(name, update);
      return 1;
    } else if (option == Option.delete) {
      objNode.remove(name);
      return 1;
    } else if (option == Option.compareAndDel) {
      if (checkValueEq(objNode, name, expect)) {
        objNode.remove(name);
        return 1;
      }
    } else if (option == Option.missingAndAdd) {
      JsonNode value = objNode.get(name);
      if (value == null || value.isMissingNode()) {
        objNode.put(name, update);
        return 1;
      }
    } else if (option == Option.traversal) {
      JsonNode value = objNode.get(name);
      if (value != null) {
        if (value.isArray()) {
          value.forEach(func);
          return value.size();
        } else {
          func.accept(value);
          return 1;
        }
      }
    } else {
      throw new IllegalArgumentException("不支持的option:" + option);
    }
    return 0;
  }

  /**
   * 验证节点.name 到值与期望值相等
   *
   * @param objectNode
   * @param name
   * @param expect
   * @return
   */
  private boolean checkValueEq(ObjectNode objectNode, String name, String expect) {
    JsonNode value = objectNode.get(name);
    if (isNull(value) || expect == null) {
      return isNull(value) && expect == null;
    } else if (value.isValueNode()) {
      return value.textValue().equals(expect);
    } else {
      return false;
    }
  }

  private int deepSearch(String path, ArrayNode arrayNode, String[] nodes, String expect,
      String update, Option option) {
    return doDeepSearch(path, arrayNode, nodes, expect, update, option, null);
  }

  private int doDeepSearch(String path, ArrayNode arrayNode, String[] nodes, String expect,
      String update, Option option, Consumer<JsonNode> func) {
    int count = 0;
    for (int i = 0; i < arrayNode.size(); i++) {
      if (arrayNode.get(i).isObject()) {
        count += doDeepSearch(path, (ObjectNode) arrayNode.get(i), nodes, expect, update, option,
            func);
      } else {
        throw throwError(path, nodes[0], arrayNode);
      }
    }
    return count;
  }

  /**
   * 遍历ArrayNode节点
   */
  public void forEach(Consumer<JsonNode> func) {
    if (this._jsonNode != null) {
      this._jsonNode.forEach(func);
    }
  }

  /**
   * 根据path设置value
   *
   * @param path
   * @param value
   */
  public int set(String path, String value) {
    String[] nodes = parsePath(path);
    if (this._jsonNode.isObject()) {
      return deepSearch(path, (ObjectNode) this._jsonNode, nodes, null, value, Option.set);
    } else if (this._jsonNode.isArray()) {
      return deepSearch(path, (ArrayNode) this._jsonNode, nodes, null, value, Option.set);
    } else {
      throw throwError(path, "", this._jsonNode);
    }
  }

  private String[] parsePath(String path) {
    if (path.indexOf(".") != -1) {
      return path.split("\\.");
    } else {
      return new String[]{path};
    }
  }

  public JsonNode getJsonNode() {
    return this._jsonNode;
  }

  public enum Option {
    compareAndSet,
    set,
    delete,
    compareAndDel,
    missingAndAdd,
    traversal
  }

}
