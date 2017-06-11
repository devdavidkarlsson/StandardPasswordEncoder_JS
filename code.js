/**
 * Created by daka on 09/08/16.
 */
var crypto = require('crypto');
console.log("###########🔰 JS Implementation started... ###########")



var constants = {
    role_user : {BACK_OFFICE : "OWNER", KODI_MANAGER : "KODI_MANAGER"},
    role_client_type : {CONTENT_CONTROLLERS_PANEL :"ROLE_CLIENT_CONTENT_CONTROLLERS_PANEL"},
    API_VERSION: 3,
    TRUSTED_SERVER_URL : "http://localhost:8888/",
    PW_SECRET: "***REMOVED***",
    PW_ITERATIONS: 1,
    PW_ALG: "sha256",
    PW_HASH_LEN: 40
};


String.prototype.getBytes = function(){
    var bytes = [];
    for (var i = 0; i < this.length; ++i) {
        bytes.push(this.charCodeAt(i));
    }
    return bytes;
};

String.prototype.hexEncode = function(){
    var hex, i;

    var result = "";
    for (i=0; i<this.length; i++) {
        hex = this.charCodeAt(i).toString(16);
        result += (""+hex).slice(-4);
    }
    return result
}

String.prototype.hexDecode = function(){
    var j;
    var hexes = this.match(/.{1,4}/g) || [];
    var back = "";
    for(j = 0; j<hexes.length; j++) {
        back += String.fromCharCode(parseInt(hexes[j], 16));
    }
    return back;
}

var _matches = function(rawPassword, encodedPassword){
    var digested = hexToBytes(encodedPassword);
    var salt = digested.slice(0, 8);
    var encodedRaw = _encode(rawPassword, bytesToHex(salt));
    var saltedEncodedRaw = bytesToHex(salt.concat(hexToBytes(encodedRaw)));
    return encodedPassword === saltedEncodedRaw;
}

var _digest = function(value){
    console.log("JS about to digest this: "+ value);

    for(var i = 0; i < constants.PW_ITERATIONS; i++){
        value = crypto.createHash(constants.PW_ALG).update(Buffer.from(value, "hex")).digest("hex");
        console.log("JS Digest: "+ value + " iteration:" + (i+1));
    }
    return value;
}



/*
* The bytes variables below consist of arrays of byte representation of the values they represent.
*
 */

var _encode = function(rawPassword, salt) {
    var passwordBytes = hexToBytes(rawPassword.hexEncode());
    var secretBytes = hexToBytes(constants.PW_SECRET.hexEncode());
    var saltBytes = hexToBytes(salt);

    console.log("Salt bytes: " + saltBytes + " regenerated hex:" + bytesToHex(saltBytes));
    console.log( "Password bytes: " + passwordBytes + " regenerated hex: "+ bytesToHex(passwordBytes));
    console.log( "Secret bytes: " + secretBytes + " regenerated secret: "+ bytesToHex(secretBytes));

    var concatenatedSaltSecretPassword = saltBytes.concat(secretBytes, passwordBytes);

    console.log("VALUE concatenated:" + concatenatedSaltSecretPassword);


    //Attempt to digest hex representation:
    var digest1 = _digest(bytesToHex(concatenatedSaltSecretPassword));

    //Attempt to digest string representation:
    //var digest2 = _digest(salt+constants.PW_SECRET+rawPassword);

    //Attempt to digest bytes string:
    //var digest3 = _digest(concatenatedSaltSecretPassword.join(""));

    return digest1
}

// Convert a hex string to a byte array
function hexToBytes(hex) {
    for (var bytes = [], c = 0; c < hex.length; c += 2)
        bytes.push(parseInt(hex.substr(c, 2), 16));
    return bytes;
}

// Convert a byte array to a hex string
function bytesToHex(bytes) {
    for (var hex = [], i = 0; i < bytes.length; i++) {
        hex.push((bytes[i] >>> 4).toString(16));
        hex.push((bytes[i] & 0xF).toString(16));
    }
    return hex.join("");
}

var args = process.argv.slice(2);
var password = args[0];
var salt = args[1];
constants.PW_ITERATIONS = args[2];
var digestedInJava = args[3];

console.log("\n JS: will encode password: "+ password + " with salt: "+ salt + " and secret: " + constants.PW_SECRET);
var digestedInJS = _encode(password, salt);
console.log("\n JS: digested the password to the following hash: " + digestedInJS);

if( digestedInJava ===  digestedInJS ){
    console.log("\n✅ SUCCESS! The JS implementation yielded the same result as the Java implementation did...\n\n");
}else{
    console.log(digestedInJava + "\nand\n"+ digestedInJS + "\n did not match sorry :(");
}
console.log("###########🔰 JS Implementation completed... ###########")

