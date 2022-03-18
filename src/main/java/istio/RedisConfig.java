package istio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedisConfig {
//
//    @Value("${spring.redis.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
////        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
////        redisStandaloneConfiguration.setHostName(redisHost);
////        redisStandaloneConfiguration.setPort(redisPort);
////        // 패스워드 있으면 설정
////        redisStandaloneConfiguration.setPassword("l0WJZphXJO");
////        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
////        return lettuceConnectionFactory;
//
//        try {
//            List<String> clusterNodes = new ArrayList<String>();
//            InetAddress[] dnsResult = InetAddress.getAllByName("my-release-redis-cluster-headless");
//            for (int i = 0; i < dnsResult.length; i++) {
//                System.out.println("redis ip:" + dnsResult[i].getHostAddress());
//                clusterNodes.add(dnsResult[i].getHostAddress()+":6379");
//            }
//
//            RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(clusterNodes);
//            redisClusterConfiguration.setPassword("redis.123");
//            return new LettuceConnectionFactory(redisClusterConfiguration);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        RedisConnectionFactory redisConnectionFactory = redisConnectionFactory();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return redisTemplate;
//    }
}

