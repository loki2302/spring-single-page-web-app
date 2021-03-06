package com.loki2302;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.loki2302.entities.Post;
import com.loki2302.entities.User;
import com.loki2302.repositories.PostRepository;
import com.loki2302.repositories.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class AppTest {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;
				
	@Test
	public void thereAreNoUsersByDefault() {
		long count = userRepository.count();
		assertEquals(0, count);
	}
	
	@Test
	public void canCreateUser() {
		User user = new User();
		user.setUserName("loki2302");
		user.setPassword("qwerty");
		
		assertNull(user.getId());
		user = userRepository.save(user);
		assertNotNull(user.getId());
		assertEquals(1, userRepository.count());
		
		assertEquals("loki2302", user.getUserName());
		assertEquals("qwerty", user.getPassword());
		
		User retrievedUser = userRepository.findOne(user.getId());
		assertEquals("loki2302", retrievedUser.getUserName());
		assertEquals("qwerty", retrievedUser.getPassword());
		
		retrievedUser = userRepository.findUserByName("loki2302");
		assertEquals("loki2302", retrievedUser.getUserName());
		assertEquals("qwerty", retrievedUser.getPassword());
		
		retrievedUser.setUserName("2302loki");
		retrievedUser = userRepository.findOne(user.getId());
		assertEquals("2302loki", retrievedUser.getUserName());
		
		userRepository.delete(retrievedUser.getId());
		assertEquals(0, userRepository.count());
	}
	
	@Test
	public void cantRetrieveUserForBadId() {
		User user = userRepository.findOne(123L);
		assertNull(user);
	}
	
	@Test
	public void cantRetrieveUserForBadUserName() {
		User user = userRepository.findUserByName("qwerty");
		assertNull(user);
	}
	
	@Test
	public void canPageUsers() {
		int totalUsers = 100;
		
		for(int i = 0; i < totalUsers; ++i) {
			User user = new User();
			userRepository.save(user);
		}
		assertEquals(100, userRepository.count());
		
		int pageNumber = 0;
		Page<User> page = null;
		do {
			PageRequest pageRequest = new PageRequest(pageNumber, 3);
			page = userRepository.findAll(pageRequest);
			assertEquals(pageNumber, page.getNumber());
			assertTrue(page.getNumberOfElements() <= 3);
			assertEquals(100, page.getTotalElements());
			
			++pageNumber;
		} while(page.hasNextPage());
		
		assertEquals(33, page.getNumber());
	}
	
	@Test
	public void canCreateUserAndPost() {
		User user = new User();
		user.setUserName("loki2302");
		user.setPassword("qwerty");
		user = userRepository.save(user);
		
		Post post = new Post();
		post.setAuthor(user);
		post.setText("post #1");
		post = postRepository.save(post);
		
		List<Post> userPosts = postRepository.findAllByAuthor(user);
		assertEquals(1, userPosts.size());
		
		long userPostsCount = postRepository.countByAuthor(user);
		assertEquals(1, userPostsCount);
	}
}
