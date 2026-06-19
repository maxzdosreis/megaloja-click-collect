package br.com.megaloja.services;

import br.com.megaloja.dtos.CreateProductRequest;
import br.com.megaloja.dtos.ProductResponse;
import br.com.megaloja.dtos.UpdateProductRequest;
import br.com.megaloja.exceptions.BusinessException;
import br.com.megaloja.exceptions.ResourceNotFoundException;
import br.com.megaloja.filters.ProductFilter;
import br.com.megaloja.mappers.ProductMapper;
import br.com.megaloja.models.Product;
import br.com.megaloja.repositories.ProductRepository;
import br.com.megaloja.specifications.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        if (productRepository.existsByName(request.name())) {
            throw new BusinessException("Já existe um produto cadastrado com o nome: " + request.name());
        }
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = findEntityById(id);
        productMapper.updateFromDto(request, product);
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    public ProductResponse findById(Long id) {
        Product product = findEntityById(id);
        return productMapper.toResponse(product);
    }

    public Page<ProductResponse> findAll(ProductFilter filter, Pageable pageable) {
        return productRepository.findAll(ProductSpecification.withFilters(filter), pageable)
                .map(productMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findEntityById(id);
        if (!product.getActive()) {
            throw new ResourceNotFoundException("Produto não encontrado com id: " + id);
        }
        productRepository.disableProduct(id);
    }

    protected Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
    }
}
