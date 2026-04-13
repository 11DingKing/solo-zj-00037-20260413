package com.in28minutes.fullstack.springboot.maven.crud.springbootcrudfullstackwithmaven.course;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.in28minutes.fullstack.springboot.maven.crud.springbootcrudfullstackwithmaven.config.AppConfig;
import com.in28minutes.fullstack.springboot.maven.crud.springbootcrudfullstackwithmaven.exception.InvalidSortFieldException;

@RestController
public class CourseResource {

	@Autowired
	private CoursesHardcodedService courseManagementService;

	@GetMapping("/instructors/{username}/courses")
	public Map<String, Object> getAllCourses(
			@PathVariable String username,
			@PageableDefault(size = 10) Pageable pageable) {
		
		try {
			AppConfig.validateSortFields(pageable.getSort());
		} catch (IllegalArgumentException e) {
			throw new InvalidSortFieldException(e.getMessage());
		}
		
		Page<Course> page = courseManagementService.findAll(pageable);
		
		Map<String, Object> response = new HashMap<>();
		response.put("content", page.getContent());
		response.put("totalElements", page.getTotalElements());
		response.put("totalPages", page.getTotalPages());
		response.put("number", page.getNumber());
		
		return response;
	}

	@GetMapping("/instructors/{username}/courses/{id}")
	public Course getCourse(@PathVariable String username, @PathVariable long id) {
		return courseManagementService.findById(id);
	}

	@DeleteMapping("/instructors/{username}/courses/{id}")
	public ResponseEntity<Void> deleteCourse(@PathVariable String username, @PathVariable long id) {

		Course course = courseManagementService.deleteById(id);

		if (course != null) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.notFound().build();
	}

	@PutMapping("/instructors/{username}/courses/{id}")
	public ResponseEntity<Course> updateCourse(@PathVariable String username, @PathVariable long id,
			@RequestBody Course course) {

		Course courseUpdated = courseManagementService.save(course);

		return new ResponseEntity<Course>(course, HttpStatus.OK);
	}

	@PostMapping("/instructors/{username}/courses")
	public ResponseEntity<Void> createCourse(@PathVariable String username, @RequestBody Course course) {

		Course createdCourse = courseManagementService.save(course);

		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdCourse.getId())
				.toUri();

		return ResponseEntity.created(uri).build();
	}

}
