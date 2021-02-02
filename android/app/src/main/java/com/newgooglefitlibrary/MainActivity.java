package com.newgooglefitlibrary;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import com.facebook.react.ReactActivity;
import com.facebook.react.bridge.Promise;

public class MainActivity extends ReactActivity {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "newgooglefitlibrary";
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(resultCode == Activity.RESULT_OK){
      Log.d("TAG","OKAY");
    } else if(resultCode == Activity.RESULT_CANCELED){
      Log.d("TAG","CANCELED BY USER");
    }


  }


}
