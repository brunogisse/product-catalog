package com.devsuperior.dscatalog.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.services.CategoryService;


//This is a Rest Controller Class

@RestController // telling to the spring that this class will be a Rest Controller to answer requests on this resource
@RequestMapping(value = "/categories") //for mapping URLs as www.DNS_da_aplicação/categories 
public class CategoryResource {
	
	@Autowired // Spring will inject a valid dependency from this repository
	private CategoryService service;
	
	@GetMapping //setting that the method will be a web service (an end point from the resource from Category). 
	public ResponseEntity<List<Category>> findAll() {
		List<Category> list = service.findAll();		
		return ResponseEntity.ok().body(list);
	}

}
