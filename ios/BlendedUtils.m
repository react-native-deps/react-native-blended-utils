#import <React/RCTBridgeModule.h>
#import <CommonCrypto/CommonCrypto.h>
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

- (NSString *)keccak256HashWithString:(NSString *)inputString {
    NSData *inputData = [inputString dataUsingEncoding:NSUTF8StringEncoding];

    unsigned char hash[CC_SHA3_256_DIGEST_LENGTH];

    CC_SHA3_CTX ctx;
    CC_SHA3_Init(&ctx, CC_SHA3_256_DIGEST_LENGTH);
    CC_SHA3_Update(&ctx, [inputData bytes], (CC_LONG)[inputData length]);
    CC_SHA3_Final(hash, &ctx);

    NSMutableString *hashHex = [NSMutableString string];
    for (int i = 0; i < CC_SHA3_256_DIGEST_LENGTH; i++) {
        [hashHex appendFormat:@"%02x", hash[i]];
    }

    return hashHex;
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

RCT_EXPORT_METHOD(keccak256Native:(NSString *)inputString resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    NSString *hash = [self keccak256HashWithString:inputString];
    if (hash) {
        resolve([@"0x" stringByAppendingString:hash]);
    } else {
        NSString *errorMessage = @"Invalid input type. Expecting a UTF-8 string.";
        reject(@"INVALID_INPUT", errorMessage, nil);
    }
}

@end
