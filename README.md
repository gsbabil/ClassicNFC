# ClassicNFC

Code to demonstrate cloning Mifare Classic based access control cards
and sniffing credit card data using NFC on Android!

### Ruxcon Presentation

  - https://github.com/gsbabil/ClassicNFC/blob/master/gsbabil-ruxconf2015.pdf

### Compiling

  ```
  ./gradlew assembleDebug
  ```

### Disclaimer

The code was compiled and tested using the following:

  - Phone: Google Nexus S (codename: `crespo`)
    - Nexus S Image: http://imgur.com/P9dleUj

  - Operating System: CyanogenMod version - `10.1-20130411-EXPERIMENTAL-crespo-M3`
    - CyanogenMod flashable image: http://bit.ly/20zzGvL

  - Emulation button is currently disabled
    - My original intention was to emulate Mifare Classic
      - Mifare Classic Emulation doesn't seem to be possible without
        access to the Secure Element (SE) that's ships with Nexus S
      - Google doesn't share the credentials to access the SE
    - Credit card emulation should be possible with Android's Host Card
      - Emulation (HCE) API
