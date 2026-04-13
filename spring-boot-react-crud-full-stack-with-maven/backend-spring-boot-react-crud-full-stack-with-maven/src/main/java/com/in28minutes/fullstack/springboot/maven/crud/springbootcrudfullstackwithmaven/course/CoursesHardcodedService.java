package com.in28minutes.fullstack.springboot.maven.crud.springbootcrudfullstackwithmaven.course;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CoursesHardcodedService {

	private static List<Course> courses = new ArrayList<>();
	private static long idCounter = 0;

	static {
		courses.add(new Course(++idCounter, "in28minutes", "Learn Full stack with Spring Boot and Angular"));
		courses.add(new Course(++idCounter, "in28minutes", "Learn Full stack with Spring Boot and React"));
		courses.add(new Course(++idCounter, "in28minutes", "Master Microservices with Spring Boot and Spring Cloud"));
		courses.add(new Course(++idCounter, "in28minutes",
				"Deploy Spring Boot Microservices to Cloud with Docker and Kubernetes"));
	}

	public List<Course> findAll() {
		return courses;
	}

	public Page<Course> findAll(Pageable pageable) {
		List<Course> sortedCourses = new ArrayList<>(courses);
		
		if (pageable.getSort().isSorted()) {
			sortedCourses.sort(getComparator(pageable.getSort()));
		}
		
		int totalElements = sortedCourses.size();
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), totalElements);
		
		List<Course> pageContent;
		if (start > totalElements) {
			pageContent = new ArrayList<>();
		} else {
			pageContent = sortedCourses.subList(start, end);
		}
		
		return new PageImpl<>(pageContent, pageable, totalElements);
	}

	private Comparator<Course> getComparator(Sort sort) {
		Comparator<Course> comparator = null;
		
		for (Sort.Order order : sort) {
			Comparator<Course> orderComparator = getOrderComparator(order);
			if (comparator == null) {
				comparator = orderComparator;
			} else {
				comparator = comparator.thenComparing(orderComparator);
			}
		}
		
		return comparator != null ? comparator : Comparator.comparing(Course::getId);
	}

	private Comparator<Course> getOrderComparator(Sort.Order order) {
		Comparator<Course> comparator;
		String property = order.getProperty();
		
		switch (property) {
			case "id":
				comparator = Comparator.comparing(Course::getId);
				break;
			case "username":
				comparator = Comparator.comparing(Course::getUsername, Comparator.nullsFirst(String::compareTo));
				break;
			case "description":
				comparator = Comparator.comparing(Course::getDescription, Comparator.nullsFirst(String::compareTo));
				break;
			default:
				comparator = Comparator.comparing(Course::getId);
		}
		
		if (order.isDescending()) {
			comparator = comparator.reversed();
		}
		
		return comparator;
	}

	public Course save(Course course) {
		if (course.getId() == -1 || course.getId() == 0) {
			course.setId(++idCounter);
			courses.add(course);
		} else {
			deleteById(course.getId());
			courses.add(course);
		}
		return course;
	}

	public Course deleteById(long id) {
		Course course = findById(id);

		if (course == null)
			return null;

		if (courses.remove(course)) {
			return course;
		}

		return null;
	}

	public Course findById(long id) {
		for (Course course : courses) {
			if (course.getId() == id) {
				return course;
			}
		}

		return null;
	}
}
