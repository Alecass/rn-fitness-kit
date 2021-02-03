package com.bitfrit.ReactPackage

import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Session
import com.google.android.gms.fitness.request.SessionInsertRequest
import java.util.concurrent.TimeUnit


class GoogleFit(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {

    var permissionPromise: Promise? = null
    private val googleRequestCode = 111


    init {
        reactContext.addActivityEventListener(this);
    }


    override fun getName(): String {
        return "GoogleFit"
    }


    override fun getConstants(): Map<String, Any>? {

        val constants = HashMap<String, Any>()

        constants[AEROBICS] = FitnessActivities.AEROBICS
        constants[BOXING] = FitnessActivities.BOXING
        constants[KICKBOXING] = FitnessActivities.KICKBOXING

        return constants
    }


    private fun getGoogleAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getAccountForExtension(reactApplicationContext, getFitnessOptions())
    }


    private fun getFitnessOptions(): FitnessOptions {

        return FitnessOptions.builder()
                .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_WRITE)
                .build()
    }


    @ReactMethod
    private fun requestGoogleFitPermission(promise: Promise) {

        permissionPromise = promise

        try {

            GoogleSignIn.requestPermissions(
                    currentActivity!!,
                    googleRequestCode,
                    getGoogleAccount(),
                    getFitnessOptions())

        } catch (e: Exception) {
            promise.reject("Error:", e);
        }

    }


    @ReactMethod
    private fun isGoogleFitPermissionGranted(promise: Promise) {

        try {

            if(GoogleSignIn.hasPermissions(getGoogleAccount(), getFitnessOptions())){
                promise.resolve(true);
            } else{
                promise.resolve(false);
            };

        } catch (e: Exception) {
            promise.reject("Error:", e);
        }

    }


    @ReactMethod
    private fun insertSession(sessionName: String, id: String, activityType: String,
                              startTimeInMin: Double, endTimeInMin: Double, promise: Promise){

            val session = Session.Builder()
                    .setName(sessionName)
                    .setIdentifier(id)
                    .setActivity(activityType)
                    .setStartTime(startTimeInMin.toLong(), TimeUnit.MILLISECONDS)
                    .setEndTime(endTimeInMin.toLong(), TimeUnit.MILLISECONDS)
                    .build()

            val insertRequest = SessionInsertRequest.Builder()
                    .setSession(session)
                    .build()

            Fitness.getSessionsClient(reactApplicationContext, getGoogleAccount())
                    .insertSession(insertRequest)
                    .addOnSuccessListener {
                        promise.resolve("Successfully added a new session!");
                    }
                    .addOnFailureListener { e ->
                        promise.reject("There was a problem inserting the session: ", e)
                    }
    }


    companion object {
        private const val BOXING = "BOXING"
        private const val KICKBOXING = "KICKBOXING"
        private const val AEROBICS = "AEROBICS"
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == googleRequestCode) {
           permissionPromise?.resolve("ok")
        } else if (resultCode == Activity.RESULT_CANCELED && requestCode == googleRequestCode) {
            permissionPromise?.resolve("canceled")
        }

    }

    override fun onNewIntent(intent: Intent?) {
    }

}


