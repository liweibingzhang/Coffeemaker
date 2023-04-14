package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import edu.ncsu.csc.CoffeeMaker.models.enums.CoffeemakerUserType;
import edu.ncsu.csc.CoffeeMaker.models.enums.Permissions;

/**
 * Coffeemaker user. Handles authentication and holds information like passwords
 * and user type.
 *
 * @author finnt
 *
 */
@Entity
public class CoffeemakerUser extends DomainObject {

    /** User id */
    @Id
    @GeneratedValue
    private Long                id;

    /** Username, represented as a string. */
    private String              name;

    /** User password hashed, represented as a string. */
    private String              passwordHash;

    /** User password salt, represented as a string. */
    private String              salt;

    /** Tracks if this user is logged in */
    private boolean             loggedIn;

    /** Tracks session id for API permissions */
    private String              sessionId;

    /** Time of last api useage, for session timeouts. */
    private long                lastTime;

    /** Type of user, staff or customer */
    private CoffeemakerUserType userType;

    /** Random for generating salt */
    @Transient
    private final SecureRandom  random = new SecureRandom();

    /**
     * Empty constructor for hibernate
     */
    public CoffeemakerUser () {
        sessionId = "Empty";
    }

    /**
     * Default constructor for making a new user.
     *
     * @param name
     *            Username of the new user
     * @param password
     *            Password of the new user
     * @param type
     *            Type of user, "Staff" or "Customer"
     */
    public CoffeemakerUser ( final String name, final String password, final String type ) {
        setName( name );
        setPasswordHash( password );
        if ( "Staff".equals( type ) ) {
            userType = CoffeemakerUserType.Staff;
        }
        else if ( "Customer".equals( type ) ) {
            userType = CoffeemakerUserType.Customer;
        }
        else {
            throw new IllegalArgumentException( "User type invalid" );
        }
        sessionId = "Empty";
    }

    /**
     * Check if a user has a certain permission.
     *
     * @param permission
     *            Permission to check
     * @return True if the user has that permssion
     */
    public boolean hasPermission ( final Permissions permission ) {
        if ( userType == CoffeemakerUserType.Customer ) {
            switch ( permission ) {
                case CanModifyInventory:
                    return false;
                case CanEditRecipe:
                    return false;
                case CanCreateRecipe:
                    return false;
                case CanOrderRecipe:
                    return true;
                case CanMakeRecipe:
                    return false;
                case CanFulfillOrder:
                    return false;
                case CanPickupOrder:
                    return true;
                default:
                    return false;
            }
        }
        else if ( userType == CoffeemakerUserType.Staff ) {
            switch ( permission ) {
                case CanModifyInventory:
                    return true;
                case CanEditRecipe:
                    return true;
                case CanCreateRecipe:
                    return true;
                case CanOrderRecipe:
                    return true;
                case CanMakeRecipe:
                    return true;
                case CanFulfillOrder:
                    return true;
                case CanPickupOrder:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    /**
     * Gets user type, staff or customer.
     *
     * @return user type, staff or customer.
     */
    public CoffeemakerUserType getUserType () {
        return userType;
    }

    /**
     * Sets the user type
     *
     * @param userType
     *            User type, Staff or Customer
     */
    public void setUserType ( final CoffeemakerUserType userType ) {
        this.userType = userType;
    }

    /**
     * Gets the name of the user.
     *
     * @return User's username.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the username.
     *
     * @param name
     *            Name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the hashed form of the password.
     *
     * @return Hashed password.
     */
    public String getPasswordHash () {
        return passwordHash;
    }

    /**
     * Sets the hash of the password, from a plaintext password.
     *
     * @param password
     *            Plaintext password
     */
    public void setPasswordHash ( final String password ) {
        final byte[] bytes = new byte[16];
        random.nextBytes( bytes );
        salt = toHexString( bytes );
        this.passwordHash = toHexString( getSHA( password + salt ) );
    }

    /**
     * Compares a plaintext password to the current set password
     *
     * @param password
     *            Password to compare
     * @return True if the passwords are equal
     */
    public boolean compareHash ( final String password ) {
        return passwordHash.equals( toHexString( getSHA( password + salt ) ) );
    }

    /**
     * Get SHA256 hash of string
     *
     * @param input
     *            Text to hash
     * @return Hash of the password
     */
    private static byte[] getSHA ( final String input ) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance( "SHA-256" );
        }
        catch ( final NoSuchAlgorithmException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return md.digest( input.getBytes( StandardCharsets.UTF_8 ) );
    }

    /**
     * Converts bytes to a hex string.
     *
     * @param hash
     *            Bytes.
     * @return Hex string.
     */
    private static String toHexString ( final byte[] hash ) {
        final BigInteger number = new BigInteger( 1, hash );

        final StringBuilder hexString = new StringBuilder( number.toString( 16 ) );

        while ( hexString.length() < 64 ) {
            hexString.insert( 0, '0' );
        }

        return hexString.toString();
    }

    /**
     * Logs this user in.
     *
     * @return Session ID to send to the client.
     */
    public final String login () {
        final byte[] bytes = new byte[64];
        random.nextBytes( bytes );
        this.sessionId = toHexString( bytes );
        this.loggedIn = true;
        this.lastTime = System.currentTimeMillis();
        return this.sessionId;
    }

    /**
     * Disables this users current session ID.
     */
    public void logout () {
        this.loggedIn = false;
    }

    /**
     * Compares a given session ID to this users.
     *
     * @param sessionId
     *            Session ID to compare.
     * @return True if the session IDs are equal
     */
    public boolean compareSessionId ( final String sessionId ) {
        // Session expiration
        if ( System.currentTimeMillis() - this.lastTime > 300000 ) {
            return false;
        }
        // Hash to prevent timing attacks
        final String thisSessionHashed = toHexString( getSHA( this.sessionId ) );
        final String proposedSessionHashed = toHexString( getSHA( sessionId ) );
        final boolean rightId = thisSessionHashed.equals( proposedSessionHashed ) && this.loggedIn;
        if ( rightId ) {
            this.lastTime = System.currentTimeMillis();
        }
        return rightId;
    }

    /**
     * Hibernate method for getting this classes unique id.
     */
    @Override
    public Serializable getId () {
        // TODO Auto-generated method stub
        return id;
    }

}
