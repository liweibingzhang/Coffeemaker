package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public abstract class CoffeemakerUser extends DomainObject {

    @Id
    @GeneratedValue
    private Long               id;

    /** Username, represented as a string. */
    private String             name;

    /** User password hashed, represented as a string. */
    private String             passwordHash;

    /** User password salt, represented as a string. */
    private String             salt;

    /** Tracks if this user is logged in */
    private boolean            loggedIn;

    /** Tracks session id for API permissions */
    private String             sessionId;

    /** Time of last api useage, for session timeouts. */
    private long               lastTime;

    /** Random for generating salt */
    private final SecureRandom random = new SecureRandom();

    public CoffeemakerUser () {

    }

    public CoffeemakerUser ( final String name, final String password ) {
        setName( name );
        setPasswordHash( password );
    }

    public boolean hasPermission ( final Permissions permission ) {
        return false;
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
        this.sessionId = bytes.toString();
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
        final boolean rightId = getSHA( this.sessionId ).equals( getSHA( sessionId ) ) && this.loggedIn;
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
