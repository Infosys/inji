package io.mosip.residentapp;

import com.reactnativesecurekeystore.SecureKeystoreImpl;
import com.reactnativesecurekeystore.KeyGeneratorImpl;
import com.reactnativesecurekeystore.CipherBoxImpl;
import com.reactnativesecurekeystore.DeviceCapability;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.reactnativesecurekeystore.biometrics.Biometrics;
import com.reactnativesecurekeystore.common.Util;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class RNSecureKeystoreModule extends ReactContextBaseJavaModule {
  private final KeyGeneratorImpl keyGenerator = new KeyGeneratorImpl();
  private final CipherBoxImpl cipherBox = new CipherBoxImpl();
  private final Biometrics biometrics;
  private final SecureKeystoreImpl keystore;
  private final DeviceCapability deviceCapability;
  private final String logTag;

  public RNSecureKeystoreModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.biometrics = new Biometrics(reactContext);
    this.keystore = new SecureKeystoreImpl(keyGenerator, cipherBox, biometrics);
    this.deviceCapability = new DeviceCapability(keystore, keyGenerator, biometrics);
    this.logTag = Util.Companion.getLogTag(getClass().getSimpleName());
  }


  @Override
  public String getName() {
    return "RNSecureKeystoreModule";
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public boolean deviceSupportsHardware() {
    return deviceCapability.supportsHardwareKeyStore();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public boolean hasAlias(String alias) {
    return keystore.hasAlias(alias);
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public void updatePopup(String title, String description) {
    Biometrics.Companion.updatePopupDetails(title, description);
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public void generateKey(String alias, boolean isAuthRequired, Integer authTimeout) {
    keystore.generateKey(alias, isAuthRequired, authTimeout);
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public String generateKeyPair(String alias, boolean isAuthRequired, Integer authTimeout) {
    return keystore.generateKeyPair(alias, isAuthRequired, authTimeout);
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public void generateHmacshaKey(String alias) {
    keystore.generateHmacSha256Key(alias);
  }

  @ReactMethod
  public void encryptData(String alias, String data, Promise promise) {
    Function1<String, Unit> successLambda = new Function1<String, Unit>() {
        @Override
        public Unit invoke(String encryptedText) {
           promise.resolve(encryptedText);
            return Unit.INSTANCE;
        }
    };

    Function2<Integer, String, Unit> failureLambda = new Function2<Integer, String, Unit>() {
        @Override
        public Unit invoke(Integer code, String message) {
            promise.reject(code.toString(),message);
            return Unit.INSTANCE;
        }
    };
    keystore.encryptData(
      alias,
      data,
      successLambda,
      failureLambda
    );
  }

  @ReactMethod
    public void decryptData(String alias, String encryptedText,Promise promise) {
        Function1<String, Unit> successLambda = new Function1<String, Unit>() {
            @Override
            public Unit invoke(String data) {
               promise.resolve(data);
                return Unit.INSTANCE;
            }
        };

        Function2<Integer, String, Unit> failureLambda = new Function2<Integer, String, Unit>() {
            @Override
            public Unit invoke(Integer code, String message) {
                promise.reject(code.toString(),message);
                return Unit.INSTANCE;
            }
        };

        keystore.decryptData(alias, encryptedText, successLambda, failureLambda);
    }


  @ReactMethod
  public void generateHmacSha(String alias, String data, Promise promise) {

    Function1<String, Unit> successLambda = new Function1<String, Unit>() {
        @Override
        public Unit invoke(String sha) {
           promise.resolve(sha);
            return Unit.INSTANCE;
        }
    };

    Function2<Integer, String, Unit> failureLambda = new Function2<Integer, String, Unit>() {
        @Override
        public Unit invoke(Integer code, String message) {
            promise.reject(code.toString(),message);
            return Unit.INSTANCE;
        }
    };

    keystore.generateHmacSha(
      alias,
      data,
      successLambda,
      failureLambda
    );
  }

  @ReactMethod
  public void sign(String alias, String data, Promise promise) {

    Function1<String, Unit> successLambda = new Function1<String, Unit>() {
        @Override
        public Unit invoke(String signature) {
           promise.resolve(signature);
            return Unit.INSTANCE;
        }
    };

    Function2<Integer, String, Unit> failureLambda = new Function2<Integer, String, Unit>() {
        @Override
        public Unit invoke(Integer code, String message) {
            promise.reject(code.toString(),message);
            return Unit.INSTANCE;
        }
    };

    keystore.sign(
      alias,
      data,
      successLambda,
      failureLambda
      
    );
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public void clearKeys() {
    keystore.removeAllKeys();
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  public boolean hasBiometricsEnabled() {
    return deviceCapability.hasBiometricsEnabled();
  }
}
