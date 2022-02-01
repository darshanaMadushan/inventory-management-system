package com.rcs2.inventory.config;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class InventoryAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	public RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return authentication.getName();
			}
		};
		
		Logger LOGGER = LoggerFactory.getLogger(InventoryAuthenticationSuccessHandler.class);
		
		boolean hasUserRole = false;
		boolean hasWorkshopAdminRole = false; 
		boolean hasKeeperRole = false;
		boolean hasAdminRole = false; 
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		
		for(GrantedAuthority grantedAuthority : authorities) {
			if(grantedAuthority.getAuthority().equals("WORKSHOP_USER")) {
				hasUserRole = true;
				break;
			}else if(grantedAuthority.getAuthority().equals("WORKSHOP_ADMIN")) {
				hasWorkshopAdminRole = true;
				break;
			}else if (grantedAuthority.getAuthority().equals("KEEPER")) {
				hasKeeperRole = true;
				break;
			} else if(grantedAuthority.getAuthority().equals("ADMIN")) {
				hasAdminRole = true; 
				break;
			}
		}
		
		if(hasUserRole) {
			LOGGER.info("Logged in User: "  + principal.getName());
			redirectStrategy.sendRedirect(request, response, "/user-home");
		} else if(hasWorkshopAdminRole) {
			LOGGER.info("Logged In Workshop Admin: " + principal.getName());
			redirectStrategy.sendRedirect(request, response, "/workshop-admin-home");
		}else if(hasKeeperRole) {
			LOGGER.info("Logged in Keeper: "  + principal.getName());
			redirectStrategy.sendRedirect(request, response, "/keeper-home");
		} else if (hasAdminRole) {
			LOGGER.info("Logged in Admin: "  + principal.getName());
			redirectStrategy.sendRedirect(request, response, "/admin-home");
		} else {
			LOGGER.error("Error in Log in : " + IllegalStateException.class.toString());
		}
	}

	
}
