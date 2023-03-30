#import <React/RCTBridgeModule.h>
#import <CommonCrypto/CommonCryptoError.h>
#import <CommonCrypto/CommonDigest.h>
#import <CommonCrypto/CommonKeyDerivation.h>

@interface BlendedUtils : NSObject <RCTBridgeModule>
@end

@implementation BlendedUtils

RCT_EXPORT_MODULE();

static const int SEED_ITERATIONS = 2048;
static const int SEED_KEY_SIZE = 512;

+ (BOOL)isMnemonicEmpty:(NSString *)mnemonic {
  return mnemonic == nil || [mnemonic stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]].length == 0;
}

RCT_EXPORT_METHOD(generateSeed:(NSString *)mnemonic passphrase:(NSString *)passphrase resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
  if ([BlendedUtils isMnemonicEmpty:mnemonic]) {
    NSString *errorMessage = @"Mnemonic is required to generate a seed";
    reject(@"INVALID_INPUT", errorMessage, nil);
    return;
  }

  passphrase = passphrase ?: @"";
  NSString *salt = [NSString stringWithFormat:@"mnemonic%@", passphrase];

  NSMutableData *keyData = [NSMutableData dataWithLength:SEED_KEY_SIZE / 8];
  int result = CCKeyDerivationPBKDF(kCCPBKDF2,
                                    mnemonic.UTF8String, mnemonic.length,
                                    salt.UTF8String, salt.length,
                                    kCCPRFHmacAlgSHA512,
                                    SEED_ITERATIONS,
                                    keyData.mutableBytes, keyData.length);

  if (result != kCCSuccess) {
    NSString *errorMessage = [NSString stringWithFormat:@"Failed to generate seed: %d", result];
    reject(@"GEN_SEED_FAILED", errorMessage, nil);
    return;
  }

  resolve([keyData base64EncodedStringWithOptions:0]);
}

@end
