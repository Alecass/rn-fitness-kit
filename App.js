import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  StatusBar,
  Button,
  NativeModules,
} from 'react-native';

const { GoogleFit } = NativeModules;

const App = () => {


  function connectGoogleFit() {

    GoogleFit.isGoogleFitPermissionGranted(async (response) => {

      try {

        if (response === false) {

          const hasPermissions = await GoogleFit.requestGoogleFitPermission();
          console.log(hasPermissions);

        } else {
          console.log('Already asked for permissions');
        }

      } catch (e) {
        console.error(e);
      }

    });

  }


  return (
    <>
      <StatusBar barStyle="dark-content" />

      <SafeAreaView>

        <Button title="Connect" onPress={connectGoogleFit} />

        <Button title="Insert " onPress={() => {

          GoogleFit.insertSession('Boxingonee', '100', GoogleFit.BOXING, 26871218, 26871291, 200).then((result) => { console.log(result); }).catch();

        }} />

      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({

});

export default App;
