package com.mds.common.webapp.filter;

import com.google.common.collect.Lists;
import com.mds.sys.model.MenuFunction;
import com.mds.sys.model.MenuTarget;
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.core.ResourceId;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.util.UserAccount;
import com.mds.sys.util.UserUtils;

import net.sf.navigator.displayer.MenuDisplayerMapping;
import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.MenuRepository;

import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.io.IOException;
import java.util.List;

/**
 * Filter to wrap request with a request including user preferred locale.
 */
public class MenuRepositoryFilter extends OncePerRequestFilter {

    /**
     * This method looks for a "locale" request parameter. If it finds one, it sets it as the preferred locale
     * and also configures it to work with JSTL.
     *
     * @param request the current request
     * @param response the current response
     * @param chain the chain
     * @throws IOException when something goes wrong
     * @throws ServletException when a communication failure happens
     */
    @SuppressWarnings("unchecked")
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain chain)
            throws IOException, ServletException {

        try {
			createMenuRepository(request);
		} catch (InvalidMDSRoleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
        chain.doFilter(request, response);
    }
        
    /**  
     * load user menu  
     * @param defaultRepository ：缺省库  
     * @throws InvalidMDSRoleException 
     */  
    private void createMenuRepository(HttpServletRequest request) throws InvalidMDSRoleException    
    {   
	   ServletContext pageContext = this.getServletContext();   
	   MenuRepository defaultRepository = (MenuRepository)pageContext.getAttribute(MenuRepository.MENU_REPOSITORY_KEY);   
	   MenuRepository repository = new MenuRepository();
       repository.setServletContext(this.getServletContext()); 
       //MenuDisplayerMapping displayerMapping = new MenuDisplayerMapping(); 
       //displayerMapping.setName("Velocity"); 
       //displayerMapping.setType("net.sf.navigator.displayer.VelocityMenuDisplayer"); 
       //repository.addMenuDisplayerMapping(displayerMapping); 
	   repository.setDisplayers(defaultRepository.getDisplayers());
	   
	   HttpSession session = request.getSession(false);
	   if (session != null) {
		   SecurityContextImpl sci = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
	
		   if (sci != null && sci.getAuthentication() != null && sci.getAuthentication().isAuthenticated()) {
			   UserAccount cud = (UserAccount) sci.getAuthentication().getPrincipal();
			   if (cud != null) {
				   List list = UserUtils.getMenuFunctionList(cud, true);
				   if (list != null)
				   {
					   List<Long> menuIds = Lists.newArrayList();
					   for (int i=0; i < list.size(); i++)    
					   {
						   MenuFunction menu=(MenuFunction) list.get(i);
						   if (menu.isTop())
						   {
							   repository.addMenu(createMenuComponent(request, cud, menu, null));
							   menuIds.add(menu.getId());
						   }
					   }
					   
					   while(menuIds.size() > 0)
					   {
						   menuIds = createSubMenu(request, cud, repository, list, menuIds);
					   }
				   }
			   }
		   }
	   }
  
	   pageContext.setAttribute("userMenuRepository", repository);   
    } 
    
    private List<Long> createSubMenu(HttpServletRequest request, UserAccount cud, MenuRepository repository, List list, List<Long> parentIds)
    {
       List<Long> subIds = Lists.newArrayList(); 
       for (int i=0; i < list.size(); i++)    
	   {
		   MenuFunction menu=(MenuFunction) list.get(i);
		   
	       if (menu.getParent() != null && parentIds.contains(menu.getParent().getId()))
	       {
	    	   String parentName = (String) menu.getParent().getCode();
	           MenuComponent parentMenu = repository.getMenu(parentName);
	           if (parentMenu != null)    
	           {     
	               repository.addMenu(createMenuComponent(request, cud, menu, parentMenu));
	               subIds.add(menu.getId());
	           }     
	       }
	   }   
       
       return subIds;
    } 
    
    private MenuComponent createMenuComponent(HttpServletRequest request, UserAccount cud, MenuFunction menu, MenuComponent parentMenu)    
    {   
       MenuComponent mc = new MenuComponent();   
       mc.setName(menu.getCode());      
       mc.setTitle(menu.getTitle());
       /*if (!StringUtils.isBlank(menu.getHref())){
	       if (UserUtils.isMobileDevice())
	    	   mc.setLocation(getServletContext().getContextPath() + menu.getHref());
	       else
	    	   mc.setLocation("javascript:top.$.navmenus.NavMenuInTab(this, '" + I18nUtils.getString(menu.getTitle(), request.getLocale())+ "', '"  + getServletContext().getContextPath() + menu.getHref() + "');");
       }*/
       String url = menu.getHref();
       if (menu.getResourceId() != null && menu.getResourceId() != ResourceId.none) {
    	   url = UserUtils.getResourceUrl(menu.getResourceId());
       }
       
       if (cud.isSysUser() && !UserUtils.isMobileDevice(request)) {
    	   mc.setLocation("javascript:top.$.navmenus.NavMenuInTab(this, '" 
    			   + I18nUtils.getString(menu.getTitle(), request.getLocale())+ "', '"  + getServletContext().getContextPath() + url + "');");
       }else{
    	   mc.setLocation(getServletContext().getContextPath() + url);
       }
       mc.setAction(menu.getAction());   
       mc.setTarget(menu.getTargetWindow());               
       mc.setDescription(I18nUtils.getString(menu.getDescription(), request.getLocale()));
       if (!StringUtils.isBlank(menu.getIcon()))
    	   mc.setImage(menu.getIcon());
       //mc.setRoles(menu.getPermission()); 
       if (parentMenu != null)
    	   mc.setParent(parentMenu);
   
       return mc;
    }
}
