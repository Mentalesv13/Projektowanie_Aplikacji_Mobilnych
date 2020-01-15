package com.example.projekt.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Functions {

    //Main URL
    private static String MAIN_URL = "https://ldzmusictheatre.000webhostapp.com/";

    // Login URL
    public static String LOGIN_URL = MAIN_URL + "android_login/login.php";

    // Register URL
    public static String REGISTER_URL = MAIN_URL + "android_login/register.php";

    // OTP Verification
    public static String OTP_VERIFY_URL = MAIN_URL + "android_login/verification.php";

    // Forgot Password
    public static String RESET_PASS_URL = MAIN_URL + "android_login/reset-password.php";

    // Get Spectacle
    public static String GET_SPECTALES_URL = MAIN_URL + "db/spectacles.php";

    // Get Event
    public static String GET_EVENT_URL = MAIN_URL + "db/events.php";

    // Get RepertoireView
    public static String GET_REPERTOIRE_URL = MAIN_URL + "db/repertoire.php";

    // Get Seat
    public static String GET_SEATS_URL = MAIN_URL + "db/places.php";

    // POST Seat
    public static String POST_SEAT_URL = MAIN_URL + "db/sites.php";

    // POST Seat Cancel
    public static String POST_SEATCANCELED_URL = MAIN_URL + "db/sitesCanceled.php";

    // Get PerformanceOfSpectales
    public static String GET_POF_URL = MAIN_URL + "db/performancesOfSpectacle.php";

    // POST addTicketsAndOrder
    public static String POST_TICKET = MAIN_URL + "db/addOrderAndTickets.php";

    // POST updateTickets
    public static String POST_UTICKET = MAIN_URL + "db/updateTickets.php";

    // POST updateOrders
    public static String POST_UORDER = MAIN_URL + "db/updateOrder.php";

    // POST sendTicket
    public static String POST_SEND_TICKET = MAIN_URL + "db/sendTicket.php";

    // POST cancelOrder
    public static String POST_ORDERCANCELED = MAIN_URL + "db/cancelOrder.php";

    // POST cancelOrder
    public static String GET_ORDERS_URL = MAIN_URL + "db/orders.php";

    // POST cancelOrder
    public static String IMAGES_URL = MAIN_URL + "images/";




    /**
     * Function to logout user
     * Resets the temporary data stored in SQLite Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetLogin();
        return true;
    }

    /**
     *  Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        //String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@gmail.com";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }


}
