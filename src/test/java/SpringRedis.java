import com.itheima.reggie.ReggieApplication;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConstants;
import java.sql.Time;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

// 你告诉测试框架要加载ReggieApplication类及其相关的配置和组件，以便在测试中使用它们。
@SpringBootTest(classes = ReggieApplication.class)
@RunWith(SpringRunner.class)
public class SpringRedis {

    //    @Qualifier("redisTemplate")
    //    @Autowired

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void testString() {
        redisTemplate.opsForValue().set("city","beijing");
        String value = (String) redisTemplate.opsForValue().get("city");
        System.out.println(value);

        redisTemplate.opsForValue().set("key1","value1", 10L, TimeUnit.SECONDS);

        Boolean Flag=redisTemplate.opsForValue().setIfAbsent("city","name");
        System.out.println(Flag);
    }

    // 操作hash 数据类型
    @Test
    public void testHash(){
        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("008","name","xiaoming");
        hashOperations.put("008","age","20");
        hashOperations.put("008","address","beijing");

        String age = (String) hashOperations.get("008","age");
        System.out.println(age);

        // 获取hash中的所有字段

        Set keys = hashOperations.keys("008");
        for(Object key:keys){
            System.out.println(key);
        }

        List values = hashOperations.values("008");
        for(Object value:keys){
            System.out.println(value);
        }
    }
    // 操作List类型的数据
    @Test
    public void tetsList(){
        ListOperations listOperations = redisTemplate.opsForList();

        // 存值
        listOperations.leftPush("myJlist","a");
        listOperations.leftPushAll("myJlist","b","c","d");

        //取值

        List<String> myList=listOperations.range("myJlist",0,-1);
        for (String value:myList){
            System.out.println(value);
        }

        //出队列
        Object element = listOperations.rightPop("myJlist");

        // 获得列表长度
        Long size =(Long) listOperations.size("myJlist");
        int lSize = size.intValue();
        for(int i=0;i<lSize;i++){
            String ele = (String) listOperations.rightPop("myJlist");

            System.out.println(ele);
        }
    }

    // 操作set类型的数据
    @Test
    public void testSet(){
        SetOperations setOperations= redisTemplate.opsForSet();
        setOperations.add("myJset","a","b","c");
        Set<String> myset=setOperations.members("myJset");
        for(String o:myset){
            System.out.println(o);
        }
    }

    // 操作zset

    @Test
    public void tetsZset(){
        ZSetOperations zSetOperations=redisTemplate.opsForZSet();

        zSetOperations.add("myJzset","a",10.0);
        zSetOperations.add("myJzset","b",11.0);
        zSetOperations.add("myJzset","c",15.0);
        zSetOperations.add("myJzset","d",18.0);

        Set<String> myZset =zSetOperations.range("myJzset",0,-1);

        for (String s:myZset){
            System.out.println(s);
        }

        //修改分数
        zSetOperations.incrementScore("myJzset","b",200);
        myZset =zSetOperations.range("myJzset",0,-1);

        for (String s:myZset){
            System.out.println(s);
        }

        // 删除成员
        zSetOperations.remove("myJzset","a","b");
        myZset =zSetOperations.range("myJzset",0,-1);

        for (String s:myZset){
            System.out.println(s);
        }
    }

    @Test
    public void testCommon(){
        //获取key
        Set<String> keys=redisTemplate.keys("*");
        for(String k:keys){
            System.out.println(k);
        }
        //判断存在
        boolean Flag=redisTemplate.hasKey("itcast");
        System.out.println(Flag);
        //删除key
        redisTemplate.delete("myJzset");
        //获取key的数据类型
        DataType datatype=redisTemplate.type("myJset");
        System.out.println(datatype);
    }

}

















