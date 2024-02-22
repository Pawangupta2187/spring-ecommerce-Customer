//package com.customer.customer.config;
//
//import com.hazelcast.config.Config;
//import com.hazelcast.config.EvictionConfig;
//import com.hazelcast.config.EvictionPolicy;
//import com.hazelcast.config.MapConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
//@Configuration
//public class HazelcastConfiguration {
//    @Bean
//    public Config hazelCastConfig(){
//        return new Config()
//                .setInstanceName("hazelcast-instance")
//                .addMapConfig(
//                        new MapConfig()
//                                .setName("Customer")
//                                .setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU))
////                                .(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
////                                .setEvictionPolicy(EvictionPolicy.LRU)
//                                .setTimeToLiveSeconds(20));
//    }
//}
