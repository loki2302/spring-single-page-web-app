package com.loki2302.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.AuthenticationResultDTO;
import com.loki2302.dto.BlogServiceErrorCode;
import com.loki2302.dto.PostDTO;
import com.loki2302.dto.ServiceResult;
import com.loki2302.dto.UserDTO;
import com.loki2302.service.implementation.BlogServiceException;
import com.loki2302.service.implementation.UserDetailsRetriever;
import com.loki2302.service.transactionscripts.AuthenticateTransactionScript;
import com.loki2302.service.transactionscripts.CreatePostTransactionScript;
import com.loki2302.service.transactionscripts.CreateUserTransactionScript;
import com.loki2302.service.transactionscripts.DeletePostTransactionScript;
import com.loki2302.service.transactionscripts.GetPostTransactionScript;
import com.loki2302.service.transactionscripts.GetPostsTransactionScript;
import com.loki2302.service.transactionscripts.UpdatePostTransactionScript;

@Service("blogService")
public class BlogServiceImpl implements BlogService {		
	@Autowired CreateUserTransactionScript createUserTransactionScript;	
	@Autowired AuthenticateTransactionScript authenticateTransactionScript;	
	@Autowired UserDetailsRetriever userDetailsRetriever;
	@Autowired CreatePostTransactionScript createPostTransactionScript;	
	@Autowired GetPostTransactionScript getPostTransactionScript;	
	@Autowired GetPostsTransactionScript getPostsTransactionScript;
	@Autowired DeletePostTransactionScript deletePostTransactionScript;	
	@Autowired UpdatePostTransactionScript updatePostTransactionScript;
	
	public ServiceResult<UserDTO> createUser(
			final String userName, 
			final String password) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<UserDTO>() {
			@Override 
			public UserDTO execute() throws BlogServiceException {
				return createUserTransactionScript.createUser(
						userName, 
						password);
			}			
		});
	}
	
	public ServiceResult<AuthenticationResultDTO> authenticate(
			final String userName, 
			final String password) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<AuthenticationResultDTO>() {
			@Override 
			public AuthenticationResultDTO execute() throws BlogServiceException {
				return authenticateTransactionScript.authenticate(
						userName, 
						password);
			}			
		});
	}
	
	public ServiceResult<PostDTO> createPost(
			final String sessionToken, 
			final String text) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<PostDTO>() {
			@Override
			public PostDTO execute() throws BlogServiceException {
				return createPostTransactionScript.createPost(
						sessionToken, 
						text);
			}			
		});		
	}
	
	public ServiceResult<PostDTO> getPost(
			final String sessionToken, 
			final long postId) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<PostDTO>() {
			@Override 
			public PostDTO execute() throws BlogServiceException {
				return getPostTransactionScript.getPost(
						sessionToken, 
						postId);
			}			
		});		
	}
	
	public ServiceResult<List<PostDTO>> getPosts(
			final String sessionToken) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<List<PostDTO>> () {
			@Override
			public List<PostDTO> execute() throws BlogServiceException {
				return getPostsTransactionScript.getPosts(sessionToken);
			}
		}); 
	}
	
	public ServiceResult<Object> deletePost(
			final String sessionToken, 
			final long postId) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<Object>() {
			@Override 
			public Object execute() throws BlogServiceException {
				deletePostTransactionScript.deletePost(
						sessionToken, 
						postId);
				
				return null;
			}			
		});
	}
	
	public ServiceResult<PostDTO> updatePost(
			final String sessionToken, 
			final long postId, 
			final String text) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<PostDTO>() {			
			@Override 
			public PostDTO execute() throws BlogServiceException {				
				return updatePostTransactionScript.updatePost(
						sessionToken, 
						postId, 
						text);
			}			
		});
	}
	
	private static <T> ServiceResult<T> ExecuteWithExceptionHandling(ServiceAction<T> function) {
		try {
			T result = function.execute();			
			return ServiceResult.ok(result);
		} catch(BlogServiceException e) {
			return ServiceResult.error(
					e.getBlogServiceErrorCode(), 
					e.getFieldErrors());
		} catch(RuntimeException e) {
			return ServiceResult.error(BlogServiceErrorCode.InternalError);
		}
	}
	
	public static interface ServiceAction<T> {
		T execute() throws BlogServiceException;
	}
}
