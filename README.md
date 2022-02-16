# Instnt Android SDK
This documentation covers the basics of Instnt Android SDK. For a detailed overview of Instnt's functionality, visit the [Instnt documentation library](https://support.instnt.org/hc/en-us/articles/360055345112-Integration-Overview)

# Table of Contents

- [Prerequisites](#prerequisites)
- [Getting started](#getting-started)
     * [Initialization of a transaction](#initialization-of-a-transaction)
- [Document verification](#document-verification)
    * [Document verification pre-requisites](#document-verification-pre-requisites)
    * [Document verifications steps](#document-verification-steps)
- [OTP verification](#otp-one-time-passcode)
    * [OTP workflow ](#otp-flow )
- [Submit form data](#submit-form-data)
- [Callback handler](#callback-handler)
- [Instnt object](#instnt-object)
- [Instnt functions](#instnt-functions)
- [Assertion Response Payload](#assertion-response-payload)
- [Resource links](#resource-links)

# Prerequisites

* Sign in to your account on the Instnt Accept's dashboard and create a customer signup workflow that works for your company. Get the workflow ID, this ID is important during the integration with Instnt SDK.
Refer [Quick start guide](https://support.instnt.org/hc/en-us/articles/4408781136909) and [Developer guide, ](https://support.instnt.org/hc/en-us/articles/360055345112-Integration-Overview) for more information.

* The integration of SDK depends on your workflow; read the [Instnt Accept integration process,](https://support.instnt.org/hc/en-us/articles/4418538578701-Instnt-Accept-Integration-Process) to understand the functionalities provided by Instnt and how to integrate SDK with your application.

**Note:** Your implementation with Instnt's SDK may diverge from the integration shown in sample app, please contact Instnt support team for additional questions related to Integration.

# Getting Started

Note that a Workflow ID is required in order to properly execute the android functions. For more information concerning Workflow IDs, please visit
[Instnt's documentation library.](https://support.instnt.org/hc/en-us/articles/360055345112-Integration-Overview)

**Install InstntSDK**

### Gradle
```
dependencies {
   implementation 'com.instant:instantsdk:2.0.0'
}
```

## Initialize transaction
Instnt treats each signup as a transction. To initialize the signup session and to begin the transaction use the static method `instantSDK = InstntSDK.init(this.formKey, serverUrl, this);`

**formKey** : workflowID
**ServerURl**: production URL or sandbox URL
**this** : CallbackHandler

The function returns an [InstntSDK interface](#instnt-object), that interface can be used for further invocation of other SDK functionalities. This interface and various callback handlers listed below are the SDK artifacts that you need to interact with.


See the following sample code implementaion of initializing the transaction.

``` java

public class MainActivity implements CallbackHandler  {

    private InstntSDK instantSDK;

    private void init() {
        String formKey = "REPLACE_YOUR_FORM_KEY";
        String serverURL = "REPLACE_YOUR_SERVER_URL";
        instantSDK = InstntSDK.init(formKey, serverURL, this);
    }
    }

```


# Document verification

Document verification feature comes into the picture if you have enabled it during the workflow creation.

When this feature is enabled, the physical capture and verification of Government-issued identification documents such as Driver's Licenses and Passportsare available.

**Note:** Document Verification feature usage in your SDK requires a **License** **key**. Please contact the support at the email support@instnt.org for further assistance.

## Document verification pre-requisites

* Android mobile devices reasonabily updated OS version, modern hardware spec and a good camera

## Document verification steps


1. Firstly you need to initialize the device camera for capturing the images like font/backside of a Drivers's License, then obtain a pre-signed URL to scan and upload the document and is taken care of by functioning the SDK; see the following sample code:

```java

private void scanDocument(String documentType) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, MY_CAMERA_REQUEST_CODE);
        } else {
            instantSDK.scanDocument(this.isFront, this.isAutoUpload, documentType, getBaseContext(), DOCUMENT_VERIFY_LICENSE_KEY);
        }
    }


```

2. Next, upload the attachment. The document verification has an auto-upload feature which is turned on by default. It uploads the image to Instnt cloud storage once the image gets captured successfully. If you donot want autoUpload to be on, upload attachment method on InstntSDK interface to initiate the upload.

Following sample code demonstrates the upload attachment process:

```java
binding.uploadDocBtn.setVisibility(View.VISIBLE);
            binding.uploadDocBtn.setOnClickListener(v -> {
                this.instantSDK.uploadAttachment(this.isFront);
```

3. Next, verify the documents that were uploaded.

Following sample code demonstrates the verify document process:

``` java
this.instantSDK.verifyDocuments("License");
```

# OTP (One-Time Passcode)

OTP functionality can be enabled by logging in Instnt dashboard and enabling OTP in your workflow. Refer to the [OTP](https://support.instnt.org/hc/en-us/articles/4408781136909#heading-5) section of the Quickstart guide for more information.

## OTP flow

* User enters mobile number as part of the signup screen.
* Your app calls send OTP() SDK function and pass the mobile number.
* Instnt SDK calls Instnt API and returns the response upon successful OTP delivery.
* Your app shows the user a screen to enter the OTP code.
* User enters the OTP code which they received.
* Your app calls verify the OTP() SDK function to verify the OTP and pass mobile number and OTP code.
* Instnt SDK calls Instnt API and returns the response upon successful OTP verification

InstntSDK provides two methods to conduct OTP verification. we have also provided the sample code for the implementation.

1. sendOTP (mobileNumber)

```java
private void sendOTP() {

        //Call send otp api
        View view = binding.containerStep3Contact.getChildAt(0);
        TextView mobile = view.findViewById(org.instnt.accept.instntsdk.R.id.value);
        String mobileNumber = mobile.getText() == null ? null : mobile.getText().toString();

        //Validate number
        if (!isValidE123(mobileNumber)) {
            showProgressDialog(false);
            CommonUtils.showToast(getBaseContext(), "Please enter a valid number with country code");
            return;
        }

        this.instantSDK.sendOTP(mobileNumber);
    }
```
2. verifyOTP(mobileNumber, otpCode)

```java
private void verifyOTP() {

        View view = binding.containerStep3Contact.getChildAt(0);
        TextView mobile = view.findViewById(org.instnt.accept.instntsdk.R.id.value);
        String mobileNumber = mobile.getText() == null ? null : mobile.getText().toString();

        View view1 = binding.containerStep4Otp.getChildAt(1);
        TextView otpText = view1.findViewById(org.instnt.accept.instntsdk.R.id.value);
        String otpCode = otpText.getText() == null ? null : otpText.getText().toString();

        this.instantSDK.verifyOTP(mobileNumber, otpCode);
    }
```

Please refer to the [InstntSDK interface](#instnt-object) methods listed below for more details.

# Submit form data

After gathering all the relevant end-user information and processing the documents, you can submit all the data to Instnt via a function.

See the sample code of the implementation:

```java
private void submit() {
        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i<binding.containerStep2Name.getChildCount(); i++) {
            BaseInputView inputView = (BaseInputView) binding.containerStep2Name.getChildAt(i);
            inputView.input(paramMap);
        }

        for (int i = 0; i<binding.containerStep3Contact.getChildCount(); i++) {
            BaseInputView inputView = (BaseInputView) binding.containerStep3Contact.getChildAt(i);
            inputView.input(paramMap);
        }

        for (int i = 0; i<binding.containerStep5Address.getChildCount(); i++) {
            BaseInputView inputView = (BaseInputView) binding.containerStep5Address.getChildAt(i);
            inputView.input(paramMap);
        }

        Map<String, String> deviceInfoMap = this.instantSDK.getDeviceInfo(getBaseContext(), this.getWindowManager());
        paramMap.put("mobileDeviceInfo", deviceInfoMap);
        this.instantSDK.submitForm(paramMap);
    }

```

# Callback handler

Instnt provides an `Interface` called `CallbackHandler` that should be used by your application to handle the callback functions.

<table data-layout="default" data-local-id="1461e79a-6df4-4f4b-b7df-a9a072096fd3" class="confluenceTable"><colgroup><col style="width: 200.0px;"><col style="width: 250.0px;"><col style="width: 200.0px;"></colgroup><tbody><tr><th class="confluenceTh"><p><strong>Method</strong></p></th><th class="confluenceTh"><p><strong>Description</strong></p></th><th class="confluenceTh"><p><strong>Input Parameters</strong></p></th></tr>


<tr><td class="confluenceTd"><p>

## <font size="2">uploadAttachmentSuccessCallback</font>
</p></td><td class="confluenceTd"><p> Callback fuction when uploading an attachmnet is a success.</p></td><td class="confluenceTd"><p>(byte[] imageData)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">scanDocumentSuccessCallback</font>
</p></td><td class="confluenceTd"><p> Callback function when scan document is a success.</p></td><td class="confluenceTd"><p>(byte[] imageData)</p></td></tr>
<tr><td class="confluenceTd"><p>

## <font size="2">submitDataSuccessCallback</font>
</p></td><td class="confluenceTd"><p> Callback function when submitting the data is successful.</p></td><td class="confluenceTd"><p>(FormSubmitData formSubmitData)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">getTransactionIDSuccessCallback</font>
</p></td><td class="confluenceTd"><p> Callback function when transaction ID is fetched successfully.</p></td><td class="confluenceTd"><p>(String instnttxnid)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">sendOTPSuccessCallback</font>
</p></td><td class="confluenceTd"><p> Callback function when sending OTP is a success.</p></td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">verifyOTPSuccessCallback</font>
</p></td><td class="confluenceTd"><p>Callback function when verifying the OTP is a success.</p></td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">scanDocumentCancelledErrorCallback</font>
</p></td><td class="confluenceTd"><p>Callback function when scan document functionality has error becasue the user cancels the scan.</p></td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">scanDocumentCaptureErrorCallback</font>
</p></td><td class="confluenceTd"><p>Callback function when scan document functionality has document capture error.</p></td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">submitDataErrorCallback</font>
</p></td><td class="confluenceTd"><p>Callback function when submitdata functionality has a error.</p></td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">getTransactionIDErrorCallback</font>
</p></td><td class="confluenceTd"><p>Callback function when there is a error while fetching the transaction id.</td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">sendOTPErrorCallback</font>
</p></td><td class="confluenceTd"><p>Callback function when send OTP functionality encounters error.</p></td><td class="confluenceTd"><p>(String message)</p></td></tr>

<tr><td class="confluenceTd"><p>

## <font size="2">verifyOTPErrorCallback</font>
</p></td><td class="confluenceTd"><p>Callback function verify OTP functionality encounters error.</td><td class="confluenceTd"><p>(String message)</p></td></tr>


</tbody></table>

# InstntSDK interface


<table data-layout="default" data-local-id="1461e79a-6df4-4f4b-b7df-a9a072096fd3" class="confluenceTable"><colgroup><col style="width: 173.0px;"><col style="width: 71.0px;"><col style="width: 65.0px;"></colgroup><tbody><tr><th class="confluenceTh"><p><strong>Method</strong></p></th><th class="confluenceTh"><p><strong>Input Parameters</strong></p></th><th class="confluenceTh"><p><strong>Return Parameters</strong></p></th><th class="confluenceTh"><p><strong>Description</strong></p></th></tr>


<tr><td class="confluenceTd"><p>
<a id="user-content-init" class="anchor" aria-hidden="true" href="#init">init
</p></td><td class="confluenceTd"><p>(String formKey, String serverUrl, CallbackHandler callbackHandler)</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>Initializes a user signup session. You need to implement the CallbackHandler class to handle the callbacks.</p></td></tr>

<tr><td class="confluenceTd"><p>
<a id="user-content-scanDocument" class="anchor" aria-hidden="true" href="#scandocument">
scanDocument
</p></td><td class="confluenceTd"><p>(boolean ifFront, boolean isAutoUpload, String documentType, Context context, String documentVerifyLicenseKey) </td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>This fuction enables the document scan. Here the input parameter "Context" that is passed must be getBaseContext() method.</p></td></tr>

<tr><td class="confluenceTd"><p>

<a id="user-content-uploadAttachment" class="anchor" aria-hidden="true" href="#uploadAttachment"> uploadAttachment
</p></td><td class="confluenceTd"><p>boolean ifFront </td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>Upload a document file to Instnt server. The input parameter is of the type boolean that represents if the front side of the document is being uploaded.</p></td></tr>

<tr><td class="confluenceTd"><p>

<a id="user-content-verifyDocuments" class="anchor" aria-hidden="true" href="#verifyDocuments">verifyDocuments
</p></td><td class="confluenceTd"><p>documentType</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>Initiate document verification on Instnt server.</p></td></tr>

<tr><td class="confluenceTd"><p>

<a id="user-content-submitData" class="anchor" aria-hidden="true" href="#submitData">submitData
</p></td><td class="confluenceTd"><p>Map < string, Object > body</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>Submit the user entered data to Instnt server and initiate customer approval process.</p></td></tr>


<tr><td class="confluenceTd"><p>

<a id="user-content-sendOTP" class="anchor" aria-hidden="true" href="#sendOTP">sendOTP

</p></td><td class="confluenceTd"><p>function,mobileNumber</p></td><td class="confluenceTd"><p></p></td><td class="confluenceTd"><p>Sends one-time password to the mobile number provided.</p></td></tr>
<tr><td class="confluenceTd"><p>

<a id="user-content-verifyOTP" class="anchor" aria-hidden="true" href="#verifyOTP">verifyOTP

</p></td><td class="confluenceTd"><p>mobileNumber, otpCode</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>Verifies one-time password that was sent to the provided mobile number.</p></td></tr>

<tr><td class="confluenceTd"><p>getInstnttxnid</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>UUID</p></td><td class="confluenceTd"><p>Instnt Transaction ID</p></td></tr>

<tr><td class="confluenceTd"><p>isOTPverificationEnabled</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>boolean</p></td><td class="confluenceTd"><p>Whether Instnt Form/Workflow has OTP verification enabled</p></td></tr>

<tr><td class="confluenceTd"><p>isDocumentVerificationEnabled</p></td><td class="confluenceTd"><p> </p></td><td class="confluenceTd"><p>boolean</p></td><td class="confluenceTd"><p>Whether Instnt Form/Workflow has document verification enabled</p></td></tr>

</tbody></table>

# Resource links
- [Quick start guide](https://support.instnt.org/hc/en-us/articles/4408781136909)
- [Developer guide](https://support.instnt.org/hc/en-us/articles/360055345112-Integration-Overview)
- [Instnt API endpoints](https://swagger.instnt.org/)
- [Instnt support](https://support.instnt.org/hc/en-us)

# License

The instnt-reactjs SDK is under MIT license.
