package org.instnt.accept.instntsdk.implementations;

import android.util.Log;

import org.instnt.accept.instntsdk.enums.CallbackType;
import org.instnt.accept.instntsdk.interfaces.CallbackData;
import org.instnt.accept.instntsdk.interfaces.CallbackHandler;
import org.instnt.accept.instntsdk.interfaces.OTPHandler;
import org.instnt.accept.instntsdk.network.NetworkUtil;
import org.instnt.accept.instntsdk.utils.CommonUtils;

public class OTPHandlerImpl implements OTPHandler {

    private static final String TAG = "OTPHandlerImpl";
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

        Log.i(TAG, "Calling Send OTP");
        networkModule.sendOTP(mobileNumber, this.instnttxnid).subscribe(otpResponse->{
            Log.i(TAG, "Send OTP called successfully");
            CallbackDataImpl callbackDataImpl = new CallbackDataImpl();
            if(otpResponse != null && !otpResponse.getResponse().isValid()) {
                Log.e(TAG, "Send OTP called successfully but returns with error : " + otpResponse.getResponse().getErrors()[0]);
                this.callbackHandler.errorCallBack(otpResponse.getResponse().getErrors()[0], CallbackType.ERROR_SEND_OTP);
                return;
            }
            this.callbackHandler.successCallBack(callbackDataImpl, "OTP sent successfully", CallbackType.SUCCESS_SEND_OTP);
        }, throwable -> {
            Log.e(TAG, "Send OTP returns with error", throwable);
            this.callbackHandler.errorCallBack("Failed to send OTP", CallbackType.ERROR_SEND_OTP);
        });
    }

    /**
     * Verify OTP
     * @param mobileNumber
     * @param otpCode
     */
    @Override
    public void verifyOTP(String mobileNumber, String otpCode) {

        Log.i(TAG, "Calling verify OTP");
        networkModule.verifyOTP(mobileNumber, otpCode, this.instnttxnid).subscribe(otpResponse->{
            Log.i(TAG, "Verify OTP called successfully");
            CallbackDataImpl callbackDataImpl = new CallbackDataImpl();
            if(otpResponse != null && !otpResponse.getResponse().isValid()) {
                Log.e(TAG, "Verify OTP called successfully but returns with error : " + otpResponse.getResponse().getErrors()[0]);
                this.callbackHandler.errorCallBack(otpResponse.getResponse().getErrors()[0], CallbackType.ERROR_VERIFY_OTP);
                return;
            }
            this.callbackHandler.successCallBack(callbackDataImpl, "OTP verified successfully", CallbackType.SUCCESS_VERIFY_OTP);
        }, throwable -> {
            Log.e(TAG, "Verify OTP returns with error", throwable);
            this.callbackHandler.errorCallBack(CommonUtils.getErrorMessage(throwable), CallbackType.ERROR_VERIFY_OTP);
        });
    }

    /**
     * Set instnt transaction id
     * @param instnttxnid
     */
    @Override
    public void setInstnttxnid(String instnttxnid) {
        Log.i(TAG, "Set instnttxnid");
        this.instnttxnid = instnttxnid;
    }

    /**
     * Set call back handler
     * @param callbackHandler
     */
    @Override
    public void setCallbackHandler(CallbackHandler callbackHandler) {
        Log.i(TAG, "Set callbackHandler");
        this.callbackHandler = callbackHandler;
    }
}
