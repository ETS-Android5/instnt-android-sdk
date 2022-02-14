package org.instnt.accept.instntsdk.implementations;

import android.util.Log;
import com.google.gson.Gson;

import org.instnt.accept.instntsdk.interfaces.CallbackHandler;
import org.instnt.accept.instntsdk.interfaces.OTPHandler;
import org.instnt.accept.instntsdk.model.OTPResponse;
import org.instnt.accept.instntsdk.network.NetworkUtil;
import org.instnt.accept.instntsdk.utils.CommonUtils;

public class OTPHandlerImpl implements OTPHandler {

    private NetworkUtil networkModule;
    private String instnttxnid;
    private CallbackHandler callbackHandler;

    public OTPHandlerImpl(NetworkUtil networkModule) {
        this.networkModule = networkModule;
    }

    /**
     * Send OTP
     * @param mobileNumber
     */
    @Override
    public void sendOTP(String mobileNumber) {

        Log.i(CommonUtils.LOG_TAG, "Calling Send OTP");
        networkModule.sendOTP(mobileNumber, this.instnttxnid).subscribe(otpResponse->{
            Log.i(CommonUtils.LOG_TAG, "Send OTP called successfully");        
            if(otpResponse != null && otpResponse.getResponse().getErrors() != null && otpResponse.getResponse().getErrors().length > 0) {
                Log.e(CommonUtils.LOG_TAG, "Send OTP called successfully but returns with error : " + otpResponse.getResponse().getErrors()[0]);
                this.callbackHandler.sendOTPErrorCallback(otpResponse.getResponse().getErrors()[0]);
                return;
            }
            this.callbackHandler.sendOTPSuccessCallback("OTP sent successfully");
        }, throwable -> {
            Log.e(CommonUtils.LOG_TAG, "Send OTP returns with error", throwable);
            this.callbackHandler.sendOTPErrorCallback("Failed to send OTP");
        });
    }

    /**
     * Verify OTP
     * @param mobileNumber
     * @param otpCode
     */
    @Override
    public void verifyOTP(String mobileNumber, String otpCode) {

        Log.i(CommonUtils.LOG_TAG, "Calling verify OTP");
        networkModule.verifyOTP(mobileNumber, otpCode, this.instnttxnid).subscribe(otpResponse->{
            Log.i(CommonUtils.LOG_TAG, "Verify OTP called successfully");
            if(otpResponse != null && otpResponse.getResponse().getErrors() != null && otpResponse.getResponse().getErrors().length > 0) {
                Log.e(CommonUtils.LOG_TAG, "Verify OTP called successfully but returns with error : " + otpResponse.getResponse().getErrors()[0]);
                this.callbackHandler.verifyOTPErrorCallback(otpResponse.getResponse().getErrors()[0]);
                return;
            }
            this.callbackHandler.verifyOTPSuccessCallback("OTP verified successfully");
        }, throwable -> {
            Log.e(CommonUtils.LOG_TAG, "Verify OTP returns with error", throwable);
            this.callbackHandler.verifyOTPErrorCallback(CommonUtils.getErrorMessage(throwable));
        });
    }

    /**
     * Set instnt transaction id
     * @param instnttxnid
     */
    @Override
    public void setInstnttxnid(String instnttxnid) {
        Log.i(CommonUtils.LOG_TAG, "Set instnttxnid");
        this.instnttxnid = instnttxnid;
    }

    /**
     * Set call back handler
     * @param callbackHandler
     */
    @Override
    public void setCallbackHandler(CallbackHandler callbackHandler) {
        Log.i(CommonUtils.LOG_TAG, "Set callbackHandler");
        this.callbackHandler = callbackHandler;
    }
}
