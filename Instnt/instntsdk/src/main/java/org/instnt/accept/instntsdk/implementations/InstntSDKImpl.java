package org.instnt.accept.instntsdk.implementations;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import org.instnt.accept.instntsdk.InstntCallbackHandler;
import org.instnt.accept.instntsdk.InstntSDK;
import org.instnt.accept.instntsdk.enums.DocumentType;
import org.instnt.accept.instntsdk.enums.ErrorCallbackType;
import org.instnt.accept.instntsdk.interfaces.DeviceHandler;
import org.instnt.accept.instntsdk.interfaces.DocumentHandler;
import org.instnt.accept.instntsdk.interfaces.FormHandler;
import org.instnt.accept.instntsdk.interfaces.OTPHandler;
import org.instnt.accept.instntsdk.model.FormCodes;
import org.instnt.accept.instntsdk.network.NetworkUtil;
import org.instnt.accept.instntsdk.utils.CommonUtils;
import org.instnt.accept.instntsdk.utils.InstntInputValidator;

import java.util.Map;

public class InstntSDKImpl implements InstntSDK {

    private DocumentHandler documentHandler;
    private OTPHandler otpHandler;
    private FormHandler formHandler;
    private DeviceHandler deviceHandler;
    private NetworkUtil networkModule;
    private String formKey;
    private String serverUrl;
    private InstntCallbackHandler instntCallbackHandler;
    private FormCodes formCodes;

    public InstntSDKImpl(String formKey, String serverUrl, InstntCallbackHandler instntCallbackHandler) {

        InstntInputValidator.validateFormKey(formKey);
        InstntInputValidator.validateServerUrl(serverUrl);
        InstntInputValidator.validateInstntCallbackHandler(instntCallbackHandler);

        networkModule = new NetworkUtil();
        documentHandler = new DocumentHandlerImpl(networkModule);
        otpHandler = new OTPHandlerImpl(networkModule);
        formHandler = new FormHandlerImpl(networkModule);
        deviceHandler = new DeviceHandlerImpl();

        this.setServerURL(serverUrl);
        this.setFormKey(formKey);
        this.setCallbackHandler(instntCallbackHandler);
    }

    public void setNetworkModule(NetworkUtil networkUtil) {
        this.networkModule = networkUtil;
    }

    public void setDocumentHandler(DocumentHandler documentHandler) { this.documentHandler = documentHandler; }

    public void setFormHandler(FormHandler formHandler) { this.formHandler = formHandler; }

    public void setDeviceHandler(DeviceHandler deviceHandler) { this.deviceHandler = deviceHandler; }

    public void setOTPHandler(OTPHandler otpHandler) { this.otpHandler = otpHandler; }

    private void setServerURL(String serverURL) {
        this.serverUrl = serverURL;
        networkModule.setServerUrl(serverURL);
        formHandler.setServerUrl(serverURL);
    }

    private void setFormKey(String formKey) {
        this.formKey = formKey;
        this.documentHandler.setFormKey(formKey);
    }

    private void setWorkFlowDetail(FormCodes formCodes) {
        this.formCodes = formCodes;
        this.formHandler.setWorkFlowDetail(formCodes);
    }

    private void setCallbackHandler(InstntCallbackHandler instntCallbackHandler) {
        this.instntCallbackHandler = instntCallbackHandler;
        this.documentHandler.setCallbackHandler(instntCallbackHandler);
        this.formHandler.setCallbackHandler(instntCallbackHandler);
        this.otpHandler.setCallbackHandler(instntCallbackHandler);
    }

    /**
     * Initialize transaction
     */
    @Override
    public void initTransaction() {

        Log.i(CommonUtils.LOG_TAG, "Calling getTransactionID");
        networkModule.getTransactionID(this.formKey).subscribe(response->{
            Log.i(CommonUtils.LOG_TAG, "Calling getTransactionID returns with success response");
            this.setWorkFlowDetail(response);
            this.instntCallbackHandler.initTransactionSuccessCallback(response.getInstnttxnid());
        }, throwable -> {
            Log.e(CommonUtils.LOG_TAG, "Calling getTransactionID returns with error response", throwable);
            this.instntCallbackHandler.instntErrorCallback("Transaction initialization failed", ErrorCallbackType.INIT_TRANSACTION_ERROR);
        });
    }

    /**
     * Scan document
     * @param isFront
     * @param isAutoUpload
     * @param documentType
     * @param context
     * @param documentVerifyLicenseKey
     */
    @Override
    public void scanDocument(boolean isFront, boolean isAutoUpload, String documentType, Context context, String documentVerifyLicenseKey, String instnttxnid) {

        InstntInputValidator.validateDocumentType(documentType);
        InstntInputValidator.validateContext(context);
        InstntInputValidator.validateDocumentVerifyLicenseKey(documentVerifyLicenseKey);
        InstntInputValidator.validateInstnttxnid(instnttxnid);

        documentHandler.scanDocument(isFront, isAutoUpload, DocumentType.valueOf(documentType), context, documentVerifyLicenseKey, instnttxnid);
    }

    /**
     * Upload attachment
     * @param isFront
     */
    @Override
    public void uploadAttachment(byte[] imageData, boolean isFront, boolean isSelfie, String instnttxnid) {

        InstntInputValidator.validateImageData(imageData);
        InstntInputValidator.validateInstnttxnid(instnttxnid);

        this.documentHandler.uploadAttachment(imageData, isFront, isSelfie, instnttxnid);
    }

    /**
     * Verify documents
     * @param documentType
     */
    @Override
    public void verifyDocuments(String documentType, String instnttxnid) {

        InstntInputValidator.validateDocumentType(documentType);
        InstntInputValidator.validateInstnttxnid(instnttxnid);

        this.documentHandler.verifyDocuments(DocumentType.valueOf(documentType), instnttxnid);
    }

    /**
     * Submit form
     * @param context
     * @param windowManager
     * @param body
     */
    @Override
    public void submitData(Context context, WindowManager windowManager, Map<String, Object> body, String instnttxnid) {

        InstntInputValidator.validateContext(context);
        InstntInputValidator.validateWindowManager(windowManager);
        InstntInputValidator.validateBody(body);
        InstntInputValidator.validateInstnttxnid(instnttxnid);

        body.put("mobileDeviceInfo", deviceHandler.getDeviceInfo(context, windowManager));
        this.formHandler.submitData(body, instnttxnid);
    }

    /**
     * Send OTP
     * @param mobileNumber
     */
    @Override
    public void sendOTP(String mobileNumber, String instnttxnid) {

        InstntInputValidator.validateMobileNumber(mobileNumber);
        InstntInputValidator.validateInstnttxnid(instnttxnid);

        otpHandler.sendOTP(mobileNumber, instnttxnid);
    }

    /**
     * Verify OTP
     * @param mobileNumber
     * @param otpCode
     */
    @Override
    public void verifyOTP(String mobileNumber, String otpCode, String instnttxnid) {

        InstntInputValidator.validateMobileNumber(mobileNumber);
        InstntInputValidator.validateOtpCode(otpCode);
        InstntInputValidator.validateInstnttxnid(instnttxnid);

        otpHandler.verifyOTP(mobileNumber, otpCode, instnttxnid);
    }

    /**
     * Get instnt transaction id
     * @return
     */
    @Override
    public String getInstnttxnid() {

        InstntInputValidator.validateFormCodes(this.formCodes);
        return this.formCodes.getInstnttxnid();
    }

    /**
     * Check is otp verification enabled
     * @return
     */
    @Override
    public boolean isOTPverificationEnabled() {

        InstntInputValidator.validateFormCodes(this.formCodes);
        return this.formCodes.isOtpVerification();
    }

    /**
     * Check is document verification enabled
     * @return
     */
    @Override
    public boolean isDocumentVerificationEnabled() {

        InstntInputValidator.validateFormCodes(this.formCodes);
        return this.formCodes.isDocumentVerification();
    }

    /*
    private void validateSDKSetup(String instnttxnid) {
        String defaultMessage = "Please call InstntSDK.init() method to properly initialize the SDK";
        if(networkModule == null) {
            throw new InstntSDKValidationException("Instnt SDK has not been initialized correctly. " + defaultMessage);
        } else if(networkModule.getServerUrl() == null || networkModule.getServerUrl().length() == 0) {
            throw new InstntSDKValidationException("serverURL is not setup. " + defaultMessage);
        }
        else if(this.instntCallbackHandler == null) {
            throw new InstntSDKValidationException("instntCallbackHandler is not setup. " + defaultMessage);
        } else if(formKey == null || formKey.length() == 0) {
            throw new InstntSDKValidationException("formKey is not setup. " + defaultMessage);
        } else if(instnttxnid == null || instnttxnid.length() == 0) {
            throw new InstntSDKValidationException("instntTxnId is not valid. The provided instntTxnId is " + instnttxnid);
        }
    }
    */
}
