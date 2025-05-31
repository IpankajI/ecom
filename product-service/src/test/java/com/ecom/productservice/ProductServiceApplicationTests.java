package com.ecom.productservice;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = com.ecom.productservice.ProductServiceApplication.class)
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {


	@Container
	static MongoDBContainer mongoDBContainer=new MongoDBContainer("mongo:latest");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ProductRepository productRepository;


	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void testAddProduct() throws Exception {
		ProductRequest productRequest=getProductRequest();
		String productRequestContent=objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productRequestContent))
						.andExpect(MockMvcResultMatchers.status().isCreated());

		Assert.assertTrue(productRepository.findAll().size()==1);
	}

	private ProductRequest getProductRequest(){
		return ProductRequest.builder().description("iphone 12").name("iphone 12").price(BigDecimal.valueOf(70000)).build();
	}

}
