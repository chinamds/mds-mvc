/*
 * MenuRepository.java
 *
 * Created on January 29, 2001, 9:51 AM
 */
package com.mds.aiotplayer.sys.util;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Holder of Menus and their items. Can be populated programmatically.
 *
 * @author  ssayles, mraible
 * Repository:�ֿ�
 */
public class MenuRepository implements Serializable {
    //~ Static fields/initializers =============================================

    private static Log log = LogFactory.getLog(MenuRepository.class);

    //~ Instance fields ========================================================

    protected String name = null;
    protected LinkedMap menus = new LinkedMap();
    private String breadCrumbDelimiter;

    //~ Methods ================================================================
    public Set getMenuNames() {
        return menus.keySet();
    }
    
    /**
     * Convenience method for dynamic menus - returns the top-level menus
     * only.
     */
    public List getTopMenus() {
        List topMenus = new ArrayList();
        if (menus == null) {
            log.warn("No menus found in repository!");
            return topMenus;
        }

        for (Iterator it = menus.keySet().iterator(); it.hasNext();) {
            String name = (String) it.next();
            MenuComponent menu = getMenu(name);
            if (menu.getParent() == null) {
                topMenus.add(menu);
            }
        }
        return topMenus;
    }

    public MenuComponent getMenu(String menuName) {
        return (MenuComponent) menus.get(menuName);
    }


    /**
     * Adds a new menu.  This is called when parsing the menu xml definition.
     * @param menu The menu component to add.
     */
    public void addMenu(MenuComponent menu) {
        if (menus.containsKey(menu.getName())) {            
            if (log.isDebugEnabled()) {
                log.warn("Menu '" + menu.getName() + "' already exists in repository");
            }
            List children = (getMenu(menu.getName())).getComponents();
            if (children != null && menu.getComponents() != null) {
                for (Iterator it = children.iterator(); it.hasNext();) {
                    MenuComponent child = (MenuComponent) it.next();
                    menu.addMenuComponent(child);
                }
            }
        }
        
        menus.put(menu.getName(), menu);
    }

    /**
     * Allows easy removal of a menu by its name.
     * @param name
     */
    public void removeMenu(String name) {
        menus.remove(name);
    }

    /**
     * Allows easy removal of all menus, suggested use for users wanting to reload menus without having to perform
     * a complete reload of the MenuRepository
     */
    public void removeAllMenus() {
        menus.clear();
    }

    public void clear() {
        menus.clear();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Method getMenu.  Get a subMenu beneath a root or parent menu.  Will drill-down as deep as requested
     * @param menuName - e.g grandParent.parent.menu
     * @param delimiter - e.g. '.'
     * @return MenuComponent
     */
    public MenuComponent getMenu(String menuName, String delimiter) {
        MenuComponent parent = null;
        StringTokenizer st = new StringTokenizer(menuName, delimiter);
        boolean firstMenu = true;

        while (st.hasMoreTokens()) {
            if (firstMenu) {
                parent = this.getMenu(st.nextToken());
                firstMenu = false;
            } else {
                MenuComponent child = null;
                String name = st.nextToken();
                for (int a = 0; a < parent.getComponents().size(); a++) {
                    if (name.equals(((MenuComponent) parent.getComponents().get(a)).getName())) {
                        child = (MenuComponent) parent.getComponents().get(a);
                        a = parent.getComponents().size();
                    }
                }
                if (child != null) {
                    parent = child;
                } else {
                    parent = null;
                    break;
                }
            }
        }

        return parent;
    }

    /**
     * Method getMenuDepth.
     * Get the depth of the deepest sub-menu within the requested top menu
     * @param menuName - name of the top menu to check the menu depth 
     * @return int.  If no menuName found return -1
     */
    public int getMenuDepth(String menuName) {

        MenuComponent menu = this.getMenu(menuName);
        if (menu == null)
            return -1;
        if (menu.getMenuComponents() == null)
            return 1;
        
        return menu.getMenuDepth();
    }

    /**
     * Method getMenuDepth.
     * Get the depth of the deepest sub-menu throughout all menus held in the repository
     * @return int.  If no menus return -1.
     */
    public int getMenuDepth() {
        int currentDepth = 0;

        List topMenus = this.getTopMenus();

        if (topMenus == null)
            return -1;
        for (Iterator menu = topMenus.iterator(); menu.hasNext();) {
            int depth = ((MenuComponent) menu.next()).getMenuDepth();
            if (currentDepth < depth)
                currentDepth = depth;
        }
        return currentDepth;
    }

    /**
     * Method getTopMenusAsArray.  Get menus as array rather than a List
     * @return MenuComponent[]
     */
    public MenuComponent[] getTopMenusAsArray() {
        List menuList = this.getTopMenus();
        MenuComponent[] menus = new MenuComponent[menuList.size()];
        for (int a = 0; a < menuList.size(); a++) {
            menus[a] = (MenuComponent) menuList.get(a);
        }

        return menus;
    }

    /**
     * Get a List of all the top menus' names
     * @return List
     */
    public List getTopMenusNames() {
        List menus = this.getTopMenus();
        ArrayList names = new ArrayList();
        for (Iterator iterator = menus.iterator(); iterator.hasNext();) {
            MenuComponent menu = (MenuComponent) iterator.next();
            names.add(menu.getName());
        }
        return names;
    }

    public void setBreadCrumbDelimiter(String string) {
        breadCrumbDelimiter = string;
    }

    public void buildBreadCrumbs() {
        if (breadCrumbDelimiter == null) {
            throw new NullPointerException("No breadCrumbDelimiter present");
        }
        ArrayList menus = (ArrayList)this.getTopMenus();
        for (Iterator iterator = menus.iterator(); iterator.hasNext();) {
            MenuComponent menu = (MenuComponent)iterator.next();
            menu.setBreadCrumb(breadCrumbDelimiter);
        }
    }

    public void buildBreadCrumbs(String delimiter) {
        this.breadCrumbDelimiter = delimiter;
        buildBreadCrumbs();
    }
}
