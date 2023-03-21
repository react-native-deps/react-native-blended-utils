import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-blended-utils' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const MnemonicUtils = NativeModules.MnemonicUtils
  ? NativeModules.MnemonicUtils
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function generateSeed(mnemonic: string, passphrase?: string): Promise<number> {
  return MnemonicUtils.generateSeed(mnemonic, passphrase);
}
