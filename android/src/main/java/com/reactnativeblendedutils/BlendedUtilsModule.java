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
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;

@ReactModule(name = BlendedUtilsModule.NAME)
public class BlendedUtilsModule extends ReactContextBaseJavaModule {
    public static final String NAME = "BlendedUtils";
    private static final String UTF_8 = "UTF-8";

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
    public static void generateSeed(String mnemonic, String passphrase, Promise promise) throws UnsupportedEncodingException {
        if (isMnemonicEmpty(mnemonic)) {
            throw new IllegalArgumentException("Mnemonic is required to generate a seed");
        }
        passphrase = passphrase == null ? "" : passphrase;
        String salt = String.format("mnemonic%s", passphrase);
        PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA512Digest());
        gen.init(mnemonic.getBytes(UTF_8), salt.getBytes(UTF_8), SEED_ITERATIONS);
        promise.resolve(Base64.encodeToString(((KeyParameter) gen.generateDerivedParameters(SEED_KEY_SIZE)).getKey(), Base64.NO_WRAP));
    }

    @ReactMethod
    public static String keccak256Native(String utf8String) {
        byte[] inputData = utf8String.getBytes();
        KeccakDigest digest = new KeccakDigest(256);
        digest.update(inputData, 0, inputData.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return "0x" + Hex.toHexString(hash);
    }

    @ReactMethod
    public static String keccak256Native(byte[] data) {
        KeccakDigest digest = new KeccakDigest(256);
        digest.update(data, 0, data.length);
        byte[] hash = new byte[digest.getDigestSize()];
        digest.doFinal(hash, 0);
        return "0x" + Hex.toHexString(hash);
    }

    public static native int nativeMultiply(int a, int b);
}
