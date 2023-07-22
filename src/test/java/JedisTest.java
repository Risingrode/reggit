import org.junit.Test;
import redis.clients.jedis.Jedis;


public class JedisTest {
    @Test
    public void tetsRedis(){
        Jedis jedis = new Jedis("localhost",6379);

        // 输入密码
        jedis.auth("123456");
        jedis.select(0);
        jedis.set("username","xiaoming");

        String value=jedis.get("username");
        System.out.println(value);

        jedis.del("username");

        jedis.close();
    }
}

// P152