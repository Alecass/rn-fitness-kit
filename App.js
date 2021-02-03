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


  async function connectGoogleFit() {

    try {

      const hasPermissions = await GoogleFit.isGoogleFitPermissionGranted();

      if (hasPermissions === false) {

        const response = await GoogleFit.requestGoogleFitPermission();

        if (response === 'ok') {
          console.log('ok');
        } else if (response === 'canceled') {
          console.log('canceled by user');
        }

      } else {
        console.log('Already asked for permissions');
      }

    } catch (e) {
      console.error(e);
    }

  }


  return (
    <>
      <StatusBar barStyle="dark-content" />

      <SafeAreaView>

        <Button title="Connect" onPress={connectGoogleFit} />

        <Button title="Insert " onPress={() => {

          GoogleFit.insertSession('Boxingonee', '100', GoogleFit.BOXING, 1612341015425, 1612342085425)
            .then((result) => { console.log(result); }).catch();

        }} />

      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({

});

export default App;
