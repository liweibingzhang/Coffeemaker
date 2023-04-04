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

@Entity
public class CoffeemakerUser extends DomainObject {

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

    public CoffeemakerUser () {
        sessionId = "Empty";
    }

    public CoffeemakerUser ( final String name, final String password, final String type ) {
        setName( name );
        setPasswordHash( password );
        if ( type.equals( "Staff" ) ) {
            userType = CoffeemakerUserType.Staff;
        }
        else if ( type.equals( "Customer" ) ) {
            userType = CoffeemakerUserType.Customer;
        }
        else {
            throw new IllegalArgumentException( "User type invalid" );
        }
        sessionId = "Empty";
    }

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

    public CoffeemakerUserType getUserType () {
        return userType;
    }

    public void setUserType ( final CoffeemakerUserType userType ) {
        this.userType = userType;
    }

    public String getName () {
        return name;
    }

    public void setName ( final String name ) {
        this.name = name;
    }

    public String getPasswordHash () {
        return passwordHash;
    }

    public void setPasswordHash ( final String password ) {
        final byte[] bytes = new byte[16];
        random.nextBytes( bytes );
        salt = bytes.toString();
        this.passwordHash = toHexString( getSHA( password + salt ) );
    }

    public boolean compareHash ( final String password ) {
        return passwordHash.equals( toHexString( getSHA( password + salt ) ) );
    }

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

    private static String toHexString ( final byte[] hash ) {
        final BigInteger number = new BigInteger( 1, hash );

        final StringBuilder hexString = new StringBuilder( number.toString( 16 ) );

        while ( hexString.length() < 64 ) {
            hexString.insert( 0, '0' );
        }

        return hexString.toString();
    }

    public final String login () {
        final byte[] bytes = new byte[64];
        random.nextBytes( bytes );
        this.sessionId = toHexString( bytes );
        this.loggedIn = true;
        this.lastTime = System.currentTimeMillis();
        return this.sessionId;
    }

    public void logout () {
        this.loggedIn = false;
    }

    public boolean compareSessionId ( final String sessionId ) {
        // Session expiration
        if ( System.currentTimeMillis() - this.lastTime > 300000 ) {
            return false;
        }
        // Hash to prevent timing attacks
        final String A = toHexString( getSHA( this.sessionId ) );
        final String B = toHexString( getSHA( sessionId ) );
        final boolean rightId = A.equals( B ) && this.loggedIn;
        if ( rightId ) {
            this.lastTime = System.currentTimeMillis();
        }
        return rightId;
    }

    @Override
    public Serializable getId () {
        // TODO Auto-generated method stub
        return id;
    }

}
