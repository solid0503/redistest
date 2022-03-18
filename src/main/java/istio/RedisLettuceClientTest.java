package istio;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class RedisLettuceClientTest extends Thread {

//    @Autowired
//    RedisTemplate<String, Object> redisTemplate;

    public RedisLettuceClientTest() {

    }

    RedisClusterClient redisCluster = null;
    StatefulRedisClusterConnection<String,String> connection = null;
    RedisAdvancedClusterCommands  cluster = null;

    private void connectToRedis() {
        try {
            ArrayList<RedisURI> nodeList = new ArrayList<RedisURI>();

            InetAddress[] dnsResult = InetAddress.getAllByName("my-release-redis-cluster-headless");
            for (int i=0; i<dnsResult.length; i++) {
                System.out.println("redis ip:" + dnsResult[i].getHostAddress());
                RedisURI node = RedisURI.create(dnsResult[i].getHostAddress(), 6379);
                node.setTimeout(Duration.ofMillis(1000));
                node.setPassword("redis.123");
                nodeList.add(node);
            }

            try {
                if (redisCluster != null ) {
                    redisCluster.shutdownAsync();
                    redisCluster = null;
                }
            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                redisCluster = RedisClusterClient.create(nodeList);
            } catch (Exception e) {
                System.out.println(e);
                return;
            }

            ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                                                                                                .enablePeriodicRefresh(Duration.ofMillis(30000))
                                                                                                .enableAllAdaptiveRefreshTriggers()
                                                                                                .build();

            redisCluster.setOptions(ClusterClientOptions.builder()
                                                        .topologyRefreshOptions(topologyRefreshOptions)
                                                        .autoReconnect(false)
                                                        .build());

            connection = redisCluster.connect();
            connection.setReadFrom(ReadFrom.SLAVE);    // -> Readonly
            cluster = connection.sync();
            String ret = cluster.ping();
            System.out.println( "6379 -cluster] ping -> " + ret);
            String ret2 = cluster.info("server");
            System.out.println("info server -> " + ret2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @PostConstruct
    public void post() {
        try {
            connectToRedis();

            this.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int resTime10msCount = 0;
        int resTime50msCount = 0;
        int resTime100msCount = 0;
        int resTime500msCount = 0;
        int resTime1000msCount = 0;
        int resTimeOverTime = 0;
        long maxResTime = 0;
        int procCount=0;
        int i = 0;
        long totalDiff = 0;
        long beforePrint = System.currentTimeMillis();
        while (true) {
            try {
                long before = System.currentTimeMillis();
//                redisTemplate.opsForValue().set("key" + (i%1000), "value" + i);
                cluster.set("key" + (i % 1000), "value" + i);
                long after = System.currentTimeMillis();
                long diff = after - before;
                if (diff <= 10) {
                    resTime10msCount++;
                } else if (diff <= 50) {
                    resTime50msCount++;
                } else if (diff <= 100) {
                    resTime100msCount++;
                } else if (diff <= 500) {
                    resTime500msCount++;
                } else if (diff <= 1000) {
                    resTime1000msCount++;
                } else {
                    resTimeOverTime++;
                }
                procCount++;
                totalDiff += diff;
                if (maxResTime < diff) {
                    maxResTime = diff;
                }
                if ( after - beforePrint >= 1000) {
                    System.out.println(String.format("10ms=%d, 50ms=%d, 100ms=%d, 500ms=%d, 1000ms=%d, 1000msOver=%d, procCount=%d, maxResTime=%d",
                                                     resTime10msCount, resTime50msCount, resTime100msCount, resTime500msCount,
                                                     resTime1000msCount, resTimeOverTime, procCount, maxResTime));
                    beforePrint = after;
                    resTime10msCount = 0;
                    resTime50msCount = 0;
                    resTime100msCount = 0;
                    resTime500msCount = 0;
                    resTime1000msCount = 0;
                    resTimeOverTime = 0;
                    totalDiff = 0;
                    procCount = 0;
                    maxResTime = 0;
                }

                i++;
                if (i == 10000000) {
                    i = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
                connectToRedis();
            }
        }
    }
}
