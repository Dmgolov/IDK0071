package com.ttu.tarkvaratehnika.empires.gameofempires.messagekeys;

import com.ttu.tarkvaratehnika.empires.gameofempires.security.Constants;

public class MessageKeys {

    public static final String AUTH_FAIL = "Authentication failed";
    public static final String LOBBY_NOT_FOUND = "No lobby with this id found";
    public static final String DUPLICATE_NAME = "Duplicate name";

    public static final String DUPLICATE_EMAIL = "Duplicate email";
    public static final String DUPLICATE_MAP_NAME = "Duplicate map name";
    public static final String UPLOAD_SUCCESS = "Upload successful";

    public static final String NO_UPDATE = "No update available or update already received";
    public static final String NULL = "null";
    public static final String NO_PLAYER_FOUND = "No player with this username found";

    public static final String EMPTY_AUTH_DATA = "Empty email or password";
    public static final String NO_MATCHING_AUTH_DATA = "No matching email and password found";
    public static final String NO_USERNAME_FOUND = "Failed to find username";

    public static final String NO_GAME_FOUND = "No game with this id found";
    public static final String NO_MAP_FOUND = "No map found";
    public static final String NEW_DIR_CREATION_FAIL = "Failed to create directory";

    public static final String RESTRICTED_NAME = "Restricted username";
    public static final String EMPTY_NAME = "Username must not be empty";
    public static final String EMPTY_EMAIL = "Email must not be empty";

    public static final String EMPTY_PASS = "Password must not be empty";
    public static final String SHORT_PASS = String.format("Password length must be at least %d symbols",
            Constants.PASSWORD_MIN_LENGTH);
    public static final String INVALID_EMAIL = "Email must contain one @ symbol";
}
