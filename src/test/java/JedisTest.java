import org.junit.Test;
import redis.clients.jedis.Jedis;
/***
* @Description: 这是一个测试redis的工具
* @Param:
* @return:
* @Author: ChangWei Fu
* @DATE: 2023-07-14   13:19
*/

public class JedisTest {
    @Test
    public void tetsRedis(){
        Jedis jedis = new Jedis("localhost",6379);

        // 输入密码
        jedis.auth("123456");



        jedis.set("username","xiaoming");

        String value=jedis.get("username");
        System.out.println(value);

        jedis.del("username");

        jedis.close();
    }
}

// P152