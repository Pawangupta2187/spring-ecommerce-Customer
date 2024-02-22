package com.customer.customer;

import com.customer.customer.Auditing.AuditorAwareImpl;
import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

//@EnableCaching
@SpringBootApplication
public class CustomerApplication {
	@Value("${hazlecast.server.host}")
	private String hazlecastHost;

	@Value("${hazlecast.server.port}")
	private int hazlecastPort;


	@Value("${hazlecast.server.cluster.name}")
	private String hazlecastClusterName;

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}
	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}

	@Bean
	public HazelcastInstance hazelcastInstance()
	{
		NetworkConfig networkConfig = new NetworkConfig();
		networkConfig.setPublicAddress(hazlecastHost).addOutboundPort(hazlecastPort);
		Config config = new Config();
		config.setNetworkConfig(networkConfig);
		config.setClusterName(hazlecastClusterName);
		return Hazelcast.newHazelcastInstance(config);
	}
}
