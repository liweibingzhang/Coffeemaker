package edu.ncsu.csc.CoffeeMaker.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import edu.ncsu.csc.CoffeeMaker.models.CoffeemakerUser;
import edu.ncsu.csc.CoffeeMaker.models.enums.CoffeemakerUserType;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService service;

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param request
     *            request that we recieved from the client
     * @param model
     *            underlying UI model
     * @param username
     *            Username
     * @param sessionid
     *            Session ID for user
     * @param type
     *            User type
     * @return contents of the page
     */
    @GetMapping ( { "/", "/login.html" } )
    public String login ( final Model model, final HttpServletRequest request,
            @CookieValue ( name = "username", required = false ) final String username,
            @CookieValue ( name = "sessionid", required = false ) final String sessionid,
            @CookieValue ( name = "type", required = false ) final String type ) {
        if ( sessionid != null && type != null && username != null ) {
            final CoffeemakerUser user = service.findByName( username );
            if ( user != null && user.compareSessionId( sessionid ) ) {
                return user.getUserType() == CoffeemakerUserType.Staff ? "staff" : "customer";
            }
        }
        return "login";
    }

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/index", "/index.html" } )
    public String index ( final Model model ) {
        return "index";
    }

    /**
     * On a GET request to /recipe, the RecipeController will return
     * /src/main/resources/templates/recipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/recipe", "/recipe.html" } )
    public String addRecipePage ( final Model model ) {
        return "recipe";
    }

    /**
     * On a GET request to /deleterecipe, the DeleteRecipeController will return
     * /src/main/resources/templates/deleterecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    public String deleteRecipeForm ( final Model model ) {
        return "deleterecipe";
    }

    /**
     * On a GET request to /editrecipe, the EditRecipeController will return
     * /src/main/resources/templates/editrecipe.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    public String editRecipeForm ( final Model model ) {
        return "editrecipe";
    }

    /**
     * Handles a GET request for inventory. The GET request provides a view to
     * the client that includes the list of the current ingredients in the
     * inventory and a form where the client can enter more ingredients to add
     * to the inventory.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/inventory", "/inventory.html" } )
    public String inventoryForm ( final Model model ) {
        return "inventory";
    }

    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/makecoffee", "/makecoffee.html" } )
    public String makeCoffeeForm ( final Model model ) {
        return "makecoffee";
    }

    /**
     * On a GET request to /addingredients, the AddIngredientsController will
     * return /src/main/resources/templates/addingredients.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/addingredients", "/addingredients.html" } )
    public String addIngredientsForm ( final Model model ) {
        return "addingredients";
    }

    /**
     * On a GET request to /signup, the APIUserController will return
     * /src/main/resources/templates/signups.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/signup", "/signup.html" } )
    public String signupPage ( final Model model ) {
        return "signup";
    }

    /**
     * On a GET request to /signup, the APIUserController will return
     * /src/main/resources/templates/signups.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/sendorder", "/sendorder.html" } )
    public String sendOrderPage ( final Model model ) {
        return "sendorder";
    }

    /**
     * On a GET request to staff, the StaffController will return
     * /src/main/resources/templates/staff.html.
     *
     * @param model
     *            underlying UI model
     * @param username
     *            Username
     * @param sessionid
     *            Session ID for user
     * @param type
     *            User type
     * @return contents of the page
     */
    @GetMapping ( { "/staff", "/staff.html" } )
    public String staffForm ( final Model model,
            @CookieValue ( name = "username", required = false ) final String username,
            @CookieValue ( name = "sessionid", required = false ) final String sessionid,
            @CookieValue ( name = "type", required = false ) final String type ) {
        if ( sessionid != null && type != null && username != null ) {
            final CoffeemakerUser user = service.findByName( username );
            if ( user != null && user.compareSessionId( sessionid )
                    && user.getUserType() == CoffeemakerUserType.Staff ) {
                return "staff";
            }
        }
        return "login";
    }

    /**
     * On a GET request to staff, the CustomerController will return
     * /src/main/resources/templates/customer.html.
     *
     * @param model
     *            underlying UI model
     *
     * @param username
     *            Username
     * @param sessionid
     *            Session ID for user
     * @param type
     *            User type
     * @return contents of the page
     */
    @GetMapping ( { "/customer", "/customer.html" } )
    public String customer ( final Model model,
            @CookieValue ( name = "username", required = false ) final String username,
            @CookieValue ( name = "sessionid", required = false ) final String sessionid,
            @CookieValue ( name = "type", required = false ) final String type ) {
        if ( sessionid != null && type != null && username != null ) {
            final CoffeemakerUser user = service.findByName( username );
            if ( user != null && user.compareSessionId( sessionid ) ) {
                return "customer";
            }
        }
        return "login";
    }

    /**
     * On a GET request to staff, the CustomerController will return
     * /src/main/resources/templates/customer.html.
     *
     * @param model
     *            underlying UI model
     *
     * @param username
     *            Username
     * @param sessionid
     *            Session ID for user
     * @param type
     *            User type
     * @return contents of the page
     */
    @GetMapping ( { "/fulfill", "/fulfill.html" } )
    public String fulfill ( final Model model,
            @CookieValue ( name = "username", required = false ) final String username,
            @CookieValue ( name = "sessionid", required = false ) final String sessionid,
            @CookieValue ( name = "type", required = false ) final String type ) {
        if ( sessionid != null && type != null && username != null ) {
            final CoffeemakerUser user = service.findByName( username );
            if ( user != null && user.compareSessionId( sessionid ) ) {
                return "fulfill";
            }
        }
        return "login";
    }
}
