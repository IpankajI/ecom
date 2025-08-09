package com.ecom.productservice.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ecom.productservice.dto.PageCursor;
import com.ecom.productservice.dto.PageResponse;
import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.model.Product;
import com.ecom.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "product apis")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody ProductRequest productRequest){
        productService.addProduct(productRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageResponse<List<ProductResponse>> getAllProducts(@QueryParam("after") String after,
             @QueryParam("before") String before, @QueryParam("limit") Integer limit){

        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Long afterId=0l;
        Long beforeId=0l;
        if(limit==null){
            limit=1;
        }
        if(after!=null){
            try {
                PageCursor pageCursor=objectMapper.readValue(Base64.getUrlDecoder().decode(after), PageCursor.class);
                afterId=Long.valueOf(pageCursor.getAfter());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if(before!=null){
            try {
                byte[] data=Base64.getUrlDecoder().decode(before);
                PageCursor pageCursor=objectMapper.readValue(data, PageCursor.class);
                beforeId=Long.valueOf(pageCursor.getBefore());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        List<Product> products=productService.getProducts(beforeId, afterId, limit);
        if(products.isEmpty()){
            return new PageResponse<List<ProductResponse>>(new ArrayList<ProductResponse>(), "", "");
        }

        PageCursor pageCursorAfter=new PageCursor();
        pageCursorAfter.setAfter(products.get(products.size()-1).getId().toString());
        pageCursorAfter.setTime(LocalDateTime.now());

        PageCursor pageCursorBefore=new PageCursor();
        pageCursorBefore.setBefore(products.get(0).getId().toString());
        pageCursorBefore.setTime(LocalDateTime.now());


        String nextAfter="";
        String nextBefore="";

        try {

            nextAfter=UriComponentsBuilder.fromUriString("http://localhost:8080/api/products")
                        .queryParam("after",  Base64.getUrlEncoder().encodeToString(objectMapper.writeValueAsBytes(pageCursorAfter)))
                        .queryParam("limit", limit)
                        .toUriString();
            nextBefore=UriComponentsBuilder.fromUriString("http://localhost:8080/api/products")
                        .queryParam("before",  Base64.getUrlEncoder().encodeToString(objectMapper.writeValueAsBytes(pageCursorBefore)))
                        .queryParam("limit", limit)
                        .toUriString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



        return new PageResponse<List<ProductResponse>>(products
            .stream().map(this::productResponseFrom).toList(), nextBefore, nextAfter);

    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProduct(@PathVariable("productId") Long productId){
        return productResponseFrom(productService.getProduct(productId));
    }

    private ProductResponse productResponseFrom(Product product){
        return ProductResponse.builder()
            .description(product.getDescription())
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice().toString())
            .build();
    }
}
