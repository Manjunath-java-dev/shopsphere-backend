package com.shopsphere.service;

import com.shopsphere.dto.request.CategoryRequest;
import com.shopsphere.dto.response.CategoryResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.exception.CategoryAlreadyExistsException;
import com.shopsphere.exception.CategoryNotFoundException;
import com.shopsphere.repositoy.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;

    }

    public CategoryResponse addCategory(CategoryRequest categoryRequest) {
        //check duplicate category
        categoryRepository.findByName(categoryRequest.getName()).ifPresent(category -> {
            throw new CategoryAlreadyExistsException("Category already exists");
        });

        //convert dto to entity
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        //save to databse
       Category savedCategory =  categoryRepository.save(category);

       //convert entity to response dto
        return new CategoryResponse(savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getDescription());


    }

   public List<CategoryResponse> getAllCategories(){
      List<Category> categories =   categoryRepository.findAll();
      return categories.stream()
              .map(category -> new CategoryResponse(category.getId()
              ,category.getName()
              ,category.getDescription()))
              .toList();
    }

    public CategoryResponse getCategoryById(Long id){
       Category category =  categoryRepository.findById(id)
               .orElseThrow(()->new CategoryNotFoundException("Category not found with id: " + id));
       return new CategoryResponse(
               category.getId(),
               category.getName(),
               category.getDescription());
    }

    public CategoryResponse updateCategory(Long id,CategoryRequest categoryRequest){
                 Category existingCategory =   categoryRepository.findById(id)
                            .orElseThrow(()->new CategoryNotFoundException("Category not found with id: "+id
                    ));
                 existingCategory.setName(categoryRequest.getName());
                 existingCategory.setDescription(categoryRequest.getDescription());

            Category savedCategory = categoryRepository.save(existingCategory);

                 CategoryResponse categoryResponse = new CategoryResponse();
                 categoryResponse.setId(savedCategory.getId());
                 categoryResponse.setName(savedCategory.getName());
                 categoryResponse.setDescription(savedCategory.getDescription());
                 return categoryResponse;

    }

    public String deleteCategory(Long id){
      Category category =  categoryRepository.findById(id)
                .orElseThrow(()->new CategoryNotFoundException("Category not found with id: "+id));

      categoryRepository.delete(category);
      return "Category deleted successfully";
    }



}
