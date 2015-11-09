package com.gsbabil.nfc;

import sasc.iso7816.AID;

public class MifareManagerCommands {

    public static final AID MIFARE_MANAGER_AID = new AID("A0000004763030");

    public static final byte[] SELECT_MIFARE_MANAGER = { 
    	(byte) 0x00, /* CLA */
        (byte) 0xA4, /* INS: select */
        (byte) 0x04, /* P1: select by name */
        (byte) 0x00, /* P2: first or only occurrence */
        (byte) 0x07, /* Lc: length of AID */
        (byte) 0xA0, (byte) 0x00, (byte) 0x00, /* Data: AID */
        (byte) 0x04, (byte) 0x76, (byte) 0x30, (byte) 0x30, 
//        (byte) 0x0B,
//        (byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x96,
//        (byte) 0x4d, (byte) 0x66, (byte) 0x34, (byte) 0x4d, (byte) 0x00, 
//        (byte) 0x02,
        (byte) 0x00 /* Le */
        };
    
    public static final byte[] PUT_MIFARE_KEYS_FIRST_K = {
    	(byte) 0x80, /* CLA: 0x80 or 0x84 */
    	(byte) 0x52, /* INS: put Mifare keys */
    	(byte) 0x00, /* P1: first 'K' (sectors 0x00 to 0x0F) */
    	(byte) 0x00, /* P2: 0x00 for init, 0x01 for update */
    	(byte) 0xC0, /* Lc: length of data */
    	
    	/* Data: 16 pair default key*/
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 0 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 0 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 1 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 1 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 2 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 2 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 3 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 3 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 4 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 4 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,

    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 5 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 5 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,

    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 6 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 6 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,

    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 7 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 7 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 8 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 8 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 9 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 9 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,

    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 10 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 10 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,

    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 11 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 11 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 12 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 12 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 13 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 13 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,

    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 14 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 14 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key A sector 15 */ 
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	(byte) 0xff, (byte) 0xff, (byte) 0xff, /* key B sector 15 */
    	(byte) 0xff, (byte) 0xff, (byte) 0xff,
    	
    	(byte) 0x00 /* Le: 0x00 */ 
    };

}
