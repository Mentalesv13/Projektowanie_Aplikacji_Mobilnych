package com.example.projekt.payment;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

public class Config {
	// PayPal app configuration
	public static final String PAYPAL_CLIENT_ID = "AUalVtOptv8f8Z2ShaNHOndM-nuinopjOSg_TF8AzVG7E6stRN7ywydqiIA4-_gGAjWE7hpp23wtVRgJ";
	public static final String PAYPAL_CLIENT_SECRET = "EGAEyF_4X8xmvEZL5XfPUCFgE4OtcLouHWbL_CG-Aae9z_fGVb1yQ7MQ1832Iwt2eobHkh5R6Nm3tszG";

	public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
	public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
	public static final String DEFAULT_CURRENCY = "PLN";

	// Our php+mysql server urls
    private static String MAIN_URL = "https://ldzmusictheatre.000webhostapp.com/";
	public static final String URL_PRODUCTS = MAIN_URL + "PayPalServer/v1/products";
	public static final String URL_VERIFY_PAYMENT = MAIN_URL + "PayPalServer/v1/verifypayment.php";

}
