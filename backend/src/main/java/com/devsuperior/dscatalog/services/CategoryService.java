package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
		
	@Autowired // Spring will inject a valid dependency from this repository
	private CategoryRepository repository;
	
	//how to make this method to access the Repository and call categories in the database? As we can see above, we created a dependency with CategoryRepository.
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {		
		List<Category> list = repository.findAll();		
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());				
	}

}
