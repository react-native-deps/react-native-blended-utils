package com.reactnativeblendedutils;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import android.util.Base64;

import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;


import static java.nio.charset.StandardCharsets.UTF_8;

public class MnemonicUtils extends ReactContextBaseJavaModule {

    MnemonicUtils(ReactApplicationContext context) {
       super(context);
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
        gen.init(mnemonic.getBytes(UTF_8), salt.getBytes(UTF_8), SEED_ITERATIONS);
        promise.resolve(Base64.encodeToString(((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey(), Base64.NO_WRAP));
    }

    @Override
    public String getName() {
       return "MnemonicUtils";
    }
}
