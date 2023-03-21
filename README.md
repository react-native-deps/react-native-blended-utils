# react-native-blended-utils

You can generate wallet with quick seed generation.

You can use this to replace seed generation's default algo ``pbkdf2`` in ethers.js algo which runs slow in mobile to this ``generateSeed`` function.

```javascript
import { ethers } from 'ethers';
import { NativeModules } from 'react-native';
import {
  defaultPath,
  entropyToMnemonic,
  HDNode,
  randomBytes,
  base64,
} from 'ethers/lib/utils';
import { provider } from '../ethers';
// import { sensitiveStorage } from '../../redux/reducer';

import { generateSeed } from "react-native-blended-utils";

const createRandomWallet = async () => {
  const random = randomBytes(32);
  const mnemonic = entropyToMnemonic(random);
  const password = ''; // password to set if needed
  const encodedSeed = await generateSeed(mnemonic, password);
  const u = base64.decode(encodedSeed);
  // eslint-disable-next-line no-underscore-dangle
  const walletNode: HDNode = HDNode._fromSeed(u, {
    phrase: mnemonic,
    path: defaultPath,
    locale: 'en',
  });
  return new ethers.Wallet(
    walletNode.privateKey,
    walletNode.address,
    0,
    provider,
  );
};

export default createRandomWallet;
```
