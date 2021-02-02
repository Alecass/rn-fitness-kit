package com.bitfrit.ReactPackage

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.SessionInsertRequest
import java.util.concurrent.TimeUnit


class GoogleFit(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), ActivityEventListener {

    var permissionPromise: Promise? = null

    init {
        reactContext.addActivityEventListener(this);
    }


    override fun getName(): String {
        return "GoogleFit"
    }


    override fun getConstants(): Map<String, Any>? {

        val constants = HashMap<String, Any>()

        constants.put(RUNNING, FitnessActivities.RUNNING)
        constants.put(AEROBICS, FitnessActivities.AEROBICS)
        constants.put(BIKING, FitnessActivities.BIKING)
        constants.put(GYMNASTICS, FitnessActivities.GYMNASTICS)
        constants.put(BOXING, FitnessActivities.BOXING)
        constants.put(KICKBOXING, FitnessActivities.KICKBOXING)

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
                    currentActivity!!, 1,
                    getGoogleAccount(),
                    getFitnessOptions())

        } catch (e: Exception) {
            promise.reject("Error:", e);
        }

    }


    @ReactMethod
    private fun isGoogleFitPermissionGranted(callback: Callback) {

            if(GoogleSignIn.hasPermissions(getGoogleAccount(), getFitnessOptions())){
                callback.invoke(true);
            } else{
                callback.invoke(false);
            };

    }


    @ReactMethod
    private fun insertSession(sessionName: String, id: String, activityType: String,
                              startTimeInMin: Int, endTimeInMin: Int,
                              calories: Float, promise: Promise){

            val session = Session.Builder()
                    .setName(sessionName)
                    .setIdentifier(id)
                    .setActivity(activityType)
                    .setStartTime(startTimeInMin.toLong(), TimeUnit.MINUTES)
                    .setEndTime(endTimeInMin.toLong(), TimeUnit.MINUTES)
                    .build()

            val caloriesDataSource = DataSource.Builder()
                    .setAppPackageName(reactApplicationContext)
                    .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                    .setType(DataSource.TYPE_RAW)
                    .build()

           val calories = DataPoint.builder(caloriesDataSource)
                    .setTimeInterval(startTimeInMin.toLong(), endTimeInMin.toLong(), TimeUnit.MINUTES)
                    .setField(Field.FIELD_CALORIES, calories)
                    .build()

            val caloriesDataSet = DataSet.builder(caloriesDataSource)
                    .add(calories)
                    .build()

            val insertRequest = SessionInsertRequest.Builder()
                    .setSession(session)
                    .addDataSet(caloriesDataSet)
                    .build()

            Fitness.getSessionsClient(reactApplicationContext, getGoogleAccount())
                    .insertSession(insertRequest)
                    .addOnSuccessListener {
                        promise.resolve("Successfully added new session!");
                    }
                    .addOnFailureListener { e ->
                        promise.reject("There was a problem inserting the session: ", e)
                    }
    }

    companion object {
        private const val RUNNING = "RUNNING"
        private const val AEROBICS = "AEROBICS"
        private const val BIKING = "BIKING"
        private const val GYMNASTICS = "GYMNASTICS"
        private const val BOXING = "BOXING"
        private const val KICKBOXING = "KICKBOXING"
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {
           permissionPromise?.resolve("Result ok")
        } else if (resultCode == Activity.RESULT_CANCELED) {
            permissionPromise?.resolve("Canceled")
        }

    }

    override fun onNewIntent(intent: Intent?) {
        TODO("Not yet implemented")
    }

}


