package com.open.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.open.json.api.JsonUtils;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

public class TestJsonHelper {

  String json = ("{"
      + "    'id':100,"
      + "    'name':'张三',"
      + "    'level1':{"
      + "        'productName':'iphone',"
      + "        'other':'5G',"
      + "        'level2':{"
      + "            'name':'测试',"
      + "            'kevin':'hello',"
      + "            'x':'xx1111111',"
      + "            'items':["
      + "                {"
      + "                    'a1':'yes',"
      + "                    'a2':'xx',"
      + "                    'abc':'OK'"
      + "                },"
      + "                {"
      + "                    'a1':'no',"
      + "                    'a2':'xx11',"
      + "                    'abc':'OK'"
      + "                },"
      + "                {"
      + "                    'a1':'OK',"
      + "                    'a2':'xx1',"
      + "                    'abc':'OK','objs':[{'name':'中国'}]"
      + "                }"
      + "            ]"
      + "        }"
      + "    }"
      + "}").replace("'", "\"");

  @Test
  public void test() throws IOException {
    JsonHelper root = JsonHelper.of(json);
    {
      Assert.assertTrue(root.checkValue("level1.level2.name", "测试"));
      if (root.checkValue("level1.level2.name", "测试")) {
        String value = root.get("level1.productName").textValue();
        Assert.assertEquals("iphone", value);
        Assert.assertEquals(1, root.set("level1.type", value));
        Assert.assertEquals(value, root.get("level1.type").textValue());
      }
    }
    {
      root.compareAndSet("level1.level2.items.a1", "yes", "成功");
      AtomicInteger counter = new AtomicInteger(0);
      root.cd("level1.level2.items").forEach((node) -> {
        counter.incrementAndGet();
        String[] values = new String[]{"成功", "no", "OK"};
        String value = node.get("a1").textValue();
        Assert.assertEquals(values[counter.get() - 1], value);
      });
      Assert.assertEquals(3, counter.get());
    }
    {
      int res = root.compareAndSet("level1.level2.items.objs.name", "中国", "😁");
      Assert.assertEquals(1, res);
      ArrayNode arrayNode = (ArrayNode) root.get("level1.level2.items");
      Assert.assertNull(JsonHelper.of(arrayNode.get(0)).get("objs.name"));
      Assert.assertNull(JsonHelper.of(arrayNode.get(1)).get("objs.name"));
      try {
        Assert.assertEquals("😁", JsonHelper.of(arrayNode.get(2)).get("objs.name"));
        Assert.fail("没有出现预期错误");
      } catch (IllegalArgumentException ex) {
        Assert.assertEquals("path:`objs.name`中存在不支持的操作(node:`objs` dataType:`ARRAY`)",
            ex.getMessage());
      }
      JsonHelper.of(arrayNode.get(2)).cd("objs").forEach((node) -> {
        Assert.assertEquals("😁", node.get("name").textValue());
      });
    }
    {
      Assert.assertEquals(1, root.compareAndSet("level1.level2.items.objs.x", null, "^v^"));
      AtomicInteger counter = new AtomicInteger(0);
      root.cd("level1.level2.items").forEach((node) -> {
        counter.incrementAndGet();
        if (counter.get() == 1 || counter.get() == 2) {
          Assert.assertNull(node.get("objs"));
        } else if (counter.get() == 3) {
          Assert.assertEquals("^v^", node.get("objs").get(0).get("x").textValue());
        } else {
          Assert.fail("操作预期范围错误");
        }
      });
    }
    {
      Assert.assertEquals(1, root.compareAndSet("level1.level2.items.a2", "xx11", "😭"));
      AtomicInteger counter = new AtomicInteger(0);
      root.cd("level1.level2.items").forEach((node) -> {
        counter.incrementAndGet();
        if (counter.get() == 1) {
          Assert.assertEquals("xx", node.get("a2").textValue());
        } else if (counter.get() == 2) {
          Assert.assertEquals("😭", node.get("a2").textValue());
        } else if (counter.get() == 3) {
          Assert.assertEquals("xx1", node.get("a2").textValue());
        } else {
          Assert.fail("操作预期范围错误");
        }
      });
    }
    {
      Assert.assertEquals(3, root.set("level1.level2.items.def", "🦊"));
      root.cd("level1.level2.items").forEach((node) -> {
        Assert.assertEquals("🦊", node.get("def").textValue());
      });
    }
    {
      Assert.assertEquals(1, root.set("level1.level2.items.objs.code", "😊"));
      AtomicInteger counter = new AtomicInteger(0);
      root.cd("level1.level2.items").forEach((node) -> {
        counter.incrementAndGet();
        if (counter.get() < 3) {
          Assert.assertNull(node.get("objs"));
        } else {
          JsonHelper.of(node.get("objs")).forEach((node1) -> {
            Assert.assertEquals("😊", node1.get("code").textValue());
          });
        }
      });
    }
    //遍历
    JsonHelper.of(root.getJsonNode()).cd("level1.level2.items").forEach((node) -> {
      //验证path Value
      if (JsonHelper.of(node).checkValue("a2", "xx")) {
        //设置$path $value
        Assert.assertEquals(1, JsonHelper.of(node).set("status", "👌"));
        Assert.assertEquals("👌", JsonHelper.of(node).get("status").textValue());
      }
    });
    {
      Assert.assertEquals(3, root.delete("level1.level2.items.a1"));
      root.cd("level1.level2.items").forEach((node) -> {
        Assert.assertNull(node.get("a1"));
      });
    }

    {
      Assert.assertEquals(1, root.compareAndDelete("level1.level2.items.objs.x", "^v^"));
      Assert.assertNull(
          root.cd("level1.level2.items").getJsonNode().get(2).get("objs").get(0).get("x"));
    }
    {
      Assert.assertEquals(1, root.missingAndSet("level1.level2.items.objs.1111", "OK"));
      Assert.assertEquals("OK",
          root.cd("level1.level2.items").getJsonNode().get(2).get("objs").get(0).get("1111")
              .textValue());
      System.out.println(root.getJsonNode());
    }

    {
      System.out.println(root.getJsonNode());
      Assert.assertEquals(1, root.deepTraversal("level1.level2", ((node) -> {
        Assert.assertEquals("测试", node.get("name").textValue());
        Assert.assertEquals("hello", node.get("kevin").textValue());
      })));
    }
    {
      System.out.println(root.getJsonNode());
      Assert.assertEquals(3, root.deepTraversal("level1.level2.items", ((node) -> {
      })));
      Assert.assertEquals(1, root.deepTraversal("level1.level2.items.objs", ((node) -> {
        Assert.assertEquals("\uD83D\uDE01", node.get("name").textValue());
        Assert.assertEquals("\uD83D\uDE0A", node.get("code").textValue());
      })));
    }
    try {
      root.get("level1.level2.items.objs");
      Assert.fail("没有出现预期出错");
    } catch (IllegalArgumentException ex) {
      Assert.assertEquals("path:`level1.level2.items.objs`中存在不支持的操作(node:`items` dataType:`ARRAY`)", ex.getMessage());
    }
    System.out.println(
        JsonUtils.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root.getJsonNode()));
  }
}
