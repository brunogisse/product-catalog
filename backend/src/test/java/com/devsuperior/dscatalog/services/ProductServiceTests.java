package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private PageImpl<Product> page;  
	private Product product;
	private Category category;
	private ProductDTO productDto;
	
	@BeforeEach
	void setUp () throws Exception {
		existingId = 1L;
		nonExistingId = 0L;
		dependentId = 4L;
		product = Factory.createProduct();
		productDto = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		
		Mockito.when(repository.findAll((Pageable)any())).thenReturn(page);
		Mockito.when(repository.save(any())).thenReturn(product);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());	
		
		Mockito.when(repository.find(any(),any(),any())).thenReturn(page);
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);
	}
	
	@Test
	public void updateIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.update(existingId, productDto);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void updatedShouldThrowResourceNotFoundExceptionhenIdDoesNotExist() {

		Assertions.assertThrows(ResourceNotFoundException.class , () -> {			
			service.update(nonExistingId, productDto);
		});
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionhenIdDoesNotExist() {
				
		Assertions.assertThrows(ResourceNotFoundException.class , () -> {
			service.findById(nonExistingId);
		});
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existingId);
		Assertions.assertNotNull(result);
	}
	
	
	@Test
	public void findAllPageShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(0L, "",pageable);
		Assertions.assertNotNull(result);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowEmptyResourceNotFoundExceptionIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

}
