package com.shopsphere.controller;

import com.shopsphere.dto.request.CategoryRequest;
import com.shopsphere.dto.response.ApiResponse;
import com.shopsphere.dto.response.CategoryResponse;
import com.shopsphere.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ApiResponse<CategoryResponse> addCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.addCategory(request);

        return ApiResponse.<CategoryResponse>builder()
                .success(true)
                .message("Category created successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories(){
       List<CategoryResponse> response = categoryService.getAllCategories();
       return ApiResponse.<List<CategoryResponse>>builder()
               .success(true)
               .message("Categories fetched successfully")
               .data(response)
               .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id){
      CategoryResponse categoryResponse =  categoryService.getCategoryById(id);
      return ApiResponse.<CategoryResponse>builder()
              .success(true)
              .message("Category fetched successfully")
              .data(categoryResponse)
              .build();

    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id , @RequestBody CategoryRequest categoryRequest){
      CategoryResponse categoryResponse =  categoryService.updateCategory(id,categoryRequest);
      return  ApiResponse.<CategoryResponse>builder()
              .success(true)
              .message("Category updated successfully")
              .data(categoryResponse)
              .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id){
   String message =  categoryService.deleteCategory(id);
   return ApiResponse.<String>builder()
           .success(true)
           .message(message)
           .data(null)
           .build();

    }
}
