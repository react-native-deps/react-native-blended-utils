package com.reactnativeblendedutils;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import android.util.Base64;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

@ReactModule(name = BlendedUtilsModule.NAME)
public class BlendedUtilsModule extends ReactContextBaseJavaModule {
    public static final String NAME = "BlendedUtils";

    public BlendedUtilsModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    private static final int SEED_ITERATIONS = 2048;
    private static final int SEED_KEY_SIZE = 512;

    private static boolean isMnemonicEmpty(String mnemonic) {
        return mnemonic == null || mnemonic.trim().isEmpty();
    }


    @ReactMethod
    public static void generateSeed(String mnemonic, String passphrase, Promise promise) {
        if (isMnemonicEmpty(mnemonic)) {
            throw new IllegalArgumentException("Mnemonic is required to generate a seed");
        }
        passphrase = passphrase == null ? "" : passphrase;
        String salt = String.format("mnemonic%s", passphrase);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(mnemonic.getBytes("UTF-8"), salt.getBytes("UTF-8"), SEED_ITERATIONS);
        promise.resolve(Base64.encodeToString(((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey(), Base64.NO_WRAP));
    }

    public static native int nativeMultiply(int a, int b);
}
