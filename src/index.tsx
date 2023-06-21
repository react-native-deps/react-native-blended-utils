import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-blended-utils' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const BlendedUtils = NativeModules.BlendedUtils
  ? NativeModules.BlendedUtils
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function generateSeed(
  mnemonic: string,
  passphrase?: string
): Promise<string> {
  return BlendedUtils.generateSeed(mnemonic, passphrase);
}

export function keccak256(utf8String: string): Promise<string> {
  return BlendedUtils.keccak256Native(utf8String);
}
