package com.shopsphere.controller;


import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.ProductResponse;
import com.shopsphere.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/customer/products")
public class CustomerProductController {

    Logger log = LoggerFactory.getLogger(CustomerProductController.class);

    private final ProductService productService;
    public CustomerProductController(ProductService productService){


        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable){
       Page<ProductResponse> response = productService.getAllProducts(pageable);
       return ApiResponse.<Page<ProductResponse>>builder()
               .success(true)
               .message("Products fetched successfully")
               .data(response)
               .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id){
      ProductResponse response =  productService.getProductById(id);
      return ApiResponse.<ProductResponse>builder()
              .success(true)
              .message("Product fetched successfully")
              .data(response)
              .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProduct(@RequestParam String name){
        log.info("Customer searching products with name: {}", name);

        List<ProductResponse>  response =  productService.searchProducts(name);
        return ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("Products fetched successfully")
                .data(response)
                .build();
    }

    @GetMapping("/category/{categoryId}")
    public  ApiResponse<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId){
        log.info("Customer searching products by category id: {}", categoryId);
       List<ProductResponse> response = productService.getProductsByCategory(categoryId);
       return ApiResponse.<List<ProductResponse>>builder()
               .success(true)
               .message("Products by category fetched successfully")
               .data(response)
               .build();
    }

}



