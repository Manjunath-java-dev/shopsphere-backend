package com.shopsphere.controller;

import com.shopsphere.dto.request.ProductRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.ProductResponse;
import com.shopsphere.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public ApiResponse<ProductResponse> addProduct(@Valid @RequestBody ProductRequest productRequest){
      ProductResponse productResponse =   productService.addProduct(productRequest);
      return ApiResponse.<ProductResponse>builder()
              .success(true)
              .message("Product added successfully")
              .data(productResponse)
              .build();

    }

//    @GetMapping
//    public ApiResponse<List<ProductResponse>>  getAllProducts(){
//       List<ProductResponse> produccts = productService.getAllProducts();
//       return ApiResponse.<List<ProductResponse>>builder()
//               .success(true)
//               .message("Products fetched successfully")
//               .data(produccts)
//               .build();
//    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
       ProductResponse product = productService.getProductById(id);
        return ApiResponse.<ProductResponse>
                builder()
                .success(true)
                .message("Product fetched successfully")
                .data(product)
                .build();

    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@Valid @PathVariable Long id , @RequestBody ProductRequest productRequest){
      ProductResponse productResponse =  productService.updateProduct(id,productRequest);
      return ApiResponse.<ProductResponse>
              builder()
              .success(true)
              .message("Product updated successfully")
              .data(productResponse)
              .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id){
           String message =   productService.deleteProduct(id);
           return ApiResponse.<String>builder()
                   .success(true)
                   .message(message)
                   .data(null)
                   .build();

    }

    @GetMapping("/search")
    public ApiResponse<List<ProductResponse>> searchProducts(@RequestParam String name){
       List<ProductResponse> productResponse = productService.searchProducts(name);
       return ApiResponse.<List<ProductResponse>>builder()
               .success(true)
               .message("Products fetched successfully")
               .data(productResponse)
               .build();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId){
      List<ProductResponse> response =  productService.getProductsByCategory(categoryId);
      return ApiResponse.<List<ProductResponse>>builder()
              .success(true)
              .message("Products by category fetched successfully")
              .data(response)
              .build();
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAllProducts(Pageable pageable){
     Page<ProductResponse> products =  productService.getAllProducts(pageable);
     return ApiResponse.<Page<ProductResponse>>builder()
             .success(true)
             .message("Products fetched successfully")
             .data(products)
             .build();
    }
}
