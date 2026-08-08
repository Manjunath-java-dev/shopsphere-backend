package com.shopsphere.service;

import com.shopsphere.dto.request.ProductRequest;
import com.shopsphere.dto.response.ProductResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.entity.Product;
import com.shopsphere.exception.CategoryNotFoundException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.repositoy.CategoryRepository;
import com.shopsphere.repositoy.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository){
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse addProduct(ProductRequest productRequest){
      Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(()->new CategoryNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setBrand(productRequest.getBrand());
        product.setColor(productRequest.getColor());
        product.setVariant(productRequest.getVariant());
        product.setCategory(category);

        Product  savedProduct =  productRepository.save(product);

        ProductResponse productResponse = new ProductResponse();

        productResponse.setId(savedProduct.getId());
        productResponse.setName(savedProduct.getName());
        productResponse.setDescription(savedProduct.getDescription());
        productResponse.setPrice(savedProduct.getPrice());
        productResponse.setStock(savedProduct.getStock());
        productResponse.setBrand(savedProduct.getBrand());
        productResponse.setColor(savedProduct.getColor());
        productResponse.setVariant(savedProduct.getVariant());
        productResponse.setRating(savedProduct.getRating());
        productResponse.setCategoryName(savedProduct.getCategory().getName());

        return productResponse;

    }

//
//    public List<ProductResponse> getAllProducts(){
//      List<Product> productList =  productRepository.findAll();
//
//      return productList.stream()
//              .map(product -> {
//                  ProductResponse response= new ProductResponse();
//                  response.setId(product.getId());
//                  response.setName(product.getName());
//                  response.setDescription(product.getDescription());
//                  response.setPrice(product.getPrice());
//                  response.setStock(product.getStock());
//                  response.setBrand(product.getBrand());
//                  response.setColor(product.getColor());
//                  response.setVariant(product.getVariant());
//                  response.setRating(product.getRating());
//                  response.setCategoryName(product.getCategory().getName());
//
//                  return response;
//              })
//              .toList();
//    }

    public ProductResponse getProductById(Long id){
       Product product = productRepository.findById(id)
               .orElseThrow(()->new ProductNotFoundException("Product not found with id: "+id));
       ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setBrand(product.getBrand());
        response.setColor(product.getColor());
        response.setVariant(product.getVariant());
        response.setRating(product.getRating());
        response.setCategoryName(product.getCategory().getName());
        return response;

    }

    public ProductResponse updateProduct(Long id,ProductRequest productRequest){
      Product existingProduct =  productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("Product not found"));

        Category category =  categoryRepository.findById(productRequest.getCategoryId())
                      .orElseThrow(()->new CategoryNotFoundException("Category not found"));

      existingProduct.setName(productRequest.getName());
      existingProduct.setDescription(productRequest.getDescription());
      existingProduct.setPrice(productRequest.getPrice());
      existingProduct.setStock(productRequest.getStock());
      existingProduct.setBrand(productRequest.getBrand());
      existingProduct.setColor(productRequest.getColor());
      existingProduct.setVariant(productRequest.getVariant());
      existingProduct.setCategory(category);

     Product savedProduct = productRepository.save(existingProduct);
     ProductResponse response = new ProductResponse();
     response.setName(savedProduct.getName());
        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setPrice(savedProduct.getPrice());
        response.setStock(savedProduct.getStock());
        response.setBrand(savedProduct.getBrand());
        response.setColor(savedProduct.getColor());
        response.setVariant(savedProduct.getVariant());
        response.setRating(savedProduct.getRating());
        response.setCategoryName(savedProduct.getCategory().getName());

        return response;

    }

    public String deleteProduct(Long id){
      Product product =  productRepository.findById(id)
              .orElseThrow(()->new ProductNotFoundException("product not found"));
      productRepository.delete(product);
      return "Product deleted successfully";
    }

    public List<ProductResponse> searchProducts(String name){
       List<Product> productsList = productRepository.findByNameContainingIgnoreCase(name);
      return productsList.stream()
              .map(product -> {
                  ProductResponse response= new ProductResponse();
                  response.setId(product.getId());
                  response.setName(product.getName());
                  response.setDescription(product.getDescription());
                  response.setPrice(product.getPrice());
                  response.setStock(product.getStock());
                  response.setBrand(product.getBrand());
                  response.setColor(product.getColor());
                  response.setVariant(product.getVariant());
                  response.setRating(product.getRating());
                  response.setCategoryName(product.getCategory().getName());

                  return response;
              })
              .toList();
    }

    public List<ProductResponse> getProductsByCategory(Long categoryId){
        categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));
       List<Product> productsList = productRepository.findByCategoryId(categoryId);
        return productsList.stream()
                .map(product -> {
                    ProductResponse response= new ProductResponse();
                    response.setId(product.getId());
                    response.setName(product.getName());
                    response.setDescription(product.getDescription());
                    response.setPrice(product.getPrice());
                    response.setStock(product.getStock());
                    response.setBrand(product.getBrand());
                    response.setColor(product.getColor());
                    response.setVariant(product.getVariant());
                    response.setRating(product.getRating());
                    response.setCategoryName(product.getCategory().getName());

                    return response;
                })
                .toList();

    }

    public Page<ProductResponse> getAllProducts(Pageable pageable){
      Page<Product> products =  productRepository.findAll(pageable);
      return products.map(product->{
          ProductResponse response= new ProductResponse();
          response.setId(product.getId());
          response.setName(product.getName());
          response.setDescription(product.getDescription());
          response.setPrice(product.getPrice());
          response.setStock(product.getStock());
          response.setBrand(product.getBrand());
          response.setColor(product.getColor());
          response.setVariant(product.getVariant());
          response.setRating(product.getRating());
          response.setCategoryName(product.getCategory().getName());

          return response;
      });
    }
}
