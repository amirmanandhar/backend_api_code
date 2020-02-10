function respond(response,status,message){
    response.json( 
        {
        status : status,
        message : message
    });
}

module.exports = {
    respond : respond
}