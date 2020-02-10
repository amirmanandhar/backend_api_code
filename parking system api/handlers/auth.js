const jwt = require('jsonwebtoken');
function checkAuthentication(token,cb){
    if( !token ){
        cb({error:"Invalid authentication."});
    }else{
        try {
            const payload = jwt.verify(token, "qrparking");
            cb(payload);
        } catch(error) {
            cb({error:'Either signature is invalid or invalid secret key is passed.'});
        }
    }
}

function isValuesEmpty(obj) {
    for (var key in obj) {
        if (obj[key] == null || obj[key] == "") return true;
    }
    return false;
}

module.exports = {
    checkAuthentication : checkAuthentication,
    isValuesEmpty : isValuesEmpty
}