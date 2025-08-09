package com.ecom.apigateway.appconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;

import com.ecom.apigateway.utils.JwtUtil;


@Configuration
public class AppConfig {

	@Value("${spring.appconfig.otp}")
	public String otpClient;
	public static final String OTP_CLIENT_AUTH0="auth0";
	@Value("${TWILIO_USERNAME}")
	public String twilioUsername;
	@Value("${TWILIO_PASSWORD}")
	public String twilioPassword;

	// github oauth 2.0
	@Value("${GITHUB_CLIENT_ID}")
	public String githubClientId;
	@Value("${GITHUB_CLIENT_SECRET}")
	public String githubClientSecret;


	@Bean
	public WebClient webClient(){
		return WebClient.builder().build();
	}

	@Bean
	public JwtUtil jwtUtil(){
		return new JwtUtil(System.getenv("PRIVATE_KEY"), System.getenv("PUBLIC_KEY"));
	}

	@Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
		redisConfig.setHostName("redis");
        redisConfig.setPort(6379);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder().build();

        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisConfig, clientConfig);

        jedisConnectionFactory.afterPropertiesSet();
        jedisConnectionFactory.start();

		return jedisConnectionFactory;
    }

	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setEnableTransactionSupport(true);
		template.afterPropertiesSet();
		return template;
	}
}
