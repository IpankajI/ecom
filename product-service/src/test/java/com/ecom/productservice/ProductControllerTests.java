package com.ecom.productservice;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.productservice.controller.ProductController;
import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.model.Product;
import com.ecom.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
@ExtendWith(MockitoExtension.class)
class ProductControllerTests {

    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String name="Iphone15";
    private final String description="Iphone15 for India";
    private final BigDecimal price=BigDecimal.valueOf(65000+1);
    private final Long productId=11L;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp(){
        product=new Product(productId, name, description, price);
        productRequest=new ProductRequest(name, description, price.toString());
    }


    @Test
    void testAddProduct() throws Exception{
        when(productService.addProduct(productRequest)).thenReturn(product);
        mockMvc.perform(post("/api/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(productRequest)))
        .andExpect(status().isCreated());
    }

    @Test
    void testGetProduct() throws Exception{
        when(productService.getProduct(productId)).thenReturn(product);

        mockMvc.perform(get("/api/products/{productId}", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(name))
        .andExpect(jsonPath("$.description").value(description))
        .andExpect(jsonPath("$.price").value(price.toString()))
        .andExpect(jsonPath("$.id").value(productId.toString()));
    }
    
    @Test
    void testGetAllProducts() throws Exception{
        List<Product> products=List.of(product);
        when(productService.getProducts(anyLong(), anyLong(), anyInt())).thenReturn(products);

        mockMvc.perform(get("/api/products", productId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data", hasSize(1)))
        .andExpect(jsonPath("$.data[0].name").value(name))
        .andExpect(jsonPath("$.data[0].description").value(description))
        .andExpect(jsonPath("$.data[0].price").value(price.toString()))
        .andExpect(jsonPath("$.data[0].id").value(productId.toString()))
        .andDo((data)->System.out.println(data.getResponse().getContentAsString()));
    }
}
