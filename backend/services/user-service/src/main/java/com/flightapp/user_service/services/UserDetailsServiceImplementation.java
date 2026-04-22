package com.flightapp.user_service.services;
import org.springframework.beans.factory.annotation.Autowired;
import com.flightapp.user_service.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
public class UserDetailsServiceImplementation implements UserDetailsService {
	@Autowired
	UserRepository userRepository;
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException
	{
		User user=userRepository.findByUsername(username)
				.orElseThrow(()->new UsernameNotFoundException("User Not Founf with username: "+username));
		if(user.isBlocked())
		{
			throw new org.springframework.security.authentication.LockedException("User account is blocked!");
		}
		return UserDetailsServiceImplementation.build(user);
	}

}
