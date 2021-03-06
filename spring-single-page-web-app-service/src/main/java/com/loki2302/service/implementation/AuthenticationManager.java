package com.loki2302.service.implementation;

import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.BlogServiceErrorCode;
import com.loki2302.entities.Session;
import com.loki2302.entities.User;
import com.loki2302.repositories.SessionRepository;
import com.loki2302.repositories.UserRepository;

@Service
public class AuthenticationManager {
	@Autowired UserRepository userRepository;	
	@Autowired SessionRepository sessionRepository;
	int sessionPeriod;
	
	public void setSessionPeriod(int sessionPeriod) {
		this.sessionPeriod = sessionPeriod;
	}
	
	public int getSessionPeriod() {
		return sessionPeriod;
	}
		
	public Session authenticate(
			String userName, 
			String password) throws BlogServiceException {
		
		User user = userRepository.findUserByName(userName);
		if(user == null) {
			throw new BlogServiceException(BlogServiceErrorCode.BadUserNameOrPassword);
		}
		
		if(!user.getPassword().equals(password)) {
			throw new BlogServiceException(BlogServiceErrorCode.BadUserNameOrPassword);
		}
		
		Session session = new Session();
		session.setUser(user);
		session.setSessionToken(UUID.randomUUID().toString());
		session.setLastActivity(new Date());
		session = sessionRepository.save(session);
		
		return session;			
	}
	
	public User getUser(String sessionToken) throws BlogServiceException {
		Session session = sessionRepository.findBySessionToken(sessionToken);
		if(session == null) {
			throw new BlogServiceException(BlogServiceErrorCode.NoSuchSession);
		}
		
		DateTime lastActivity = new DateTime(session.getLastActivity());
		DateTime currentTime = new DateTime(new Date());
		Seconds sessionSeconds = Seconds.secondsBetween(lastActivity, currentTime);		
		if(sessionSeconds.getSeconds() >= sessionPeriod) {
			sessionRepository.delete(session);			
			throw new BlogServiceException(BlogServiceErrorCode.SessionExpired); 
		}
		
		session.setLastActivity(currentTime.toDate());
		session = sessionRepository.save(session);
		
		User user = session.getUser();
		return user;
	}
}