const Knex = require("knex");
const knexOptions = require(".././knexfile");
const knex = Knex(knexOptions);

const responder = require("./respond")
const auth = require("./auth")

const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

//users handlers
function getAllUsers(req,res){
    knex
    .select()
    .table('users')
    .then((data)=>{
        res.json(data)
    })
    .catch(error => {
        res.json("fail","Error occured!")
    })
}

function getLoggedInUser(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            knex
            .select()
            .table('users')
            .where({ username: payload.username })
            .then((data)=>{
                res.json(data[0])
            })
            .catch(error => {
                res.json(error)
            })
        }else{
            res.json(payload.error)
        }
    })
}

function addUser(req,res){

    //hash password
    const hashedPassword = bcrypt.hashSync(req.body.password, 10)
    // required db values
    var values = {
        username: req.body.username,
        password: hashedPassword,
        email: req.body.email,
        phone : req.body.phone
    };

    knex
    .select("username")
    .from("users")
    .where("username",values.username)
    .then(usernameList => {
        if( usernameList.length === 0 ){
            //add to db
            knex('users')
            .insert(values)
            .then(
                ()=>{
                    responder.respond(res,"success","added")
                }
            )
            .catch(error => {
                responder.respond(res,"fail","Error: "+error)
            })
        }else{
            responder.respond(res,"taken","Username already taken")
        }
    })

    
}

function deleteUser(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            var id = req.params.id;
            knex('users')
            .where({ id: id })
            .delete()
            .then(
                ()=>{
                    res.json("User deleted!")
                }
            )
            .catch(error => {
                res.json("Error occured!")
            })
        }
    })
    
}

function updateUser(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            if( !req.body.id ){
                res.json({
                    status: 'fail',
                    message: "No user id provided"
                })
                return;
            }
            var updateValues = {},
            userId = req.body.id,
            toUpdateValuesName = "";

            if(req.body.username && req.body.username != ""){
                updateValues.username = req.body.username;
                toUpdateValuesName += "Username ";
            } 
            if(req.body.password && req.body.password != ""){
                //hash password
                const hashedPassword = bcrypt.hashSync(req.body.password, 10)
                updateValues.password = hashedPassword;
                toUpdateValuesName += "Password ";
            } 
            if(req.body.email && req.body.email != ""){
                updateValues.email = req.body.email;
                toUpdateValuesName += "Email ";
            }
            if(req.body.phone && req.body.phone != ""){
                updateValues.phone = req.body.phone;
                toUpdateValuesName += "Phone ";
            }
            if(req.body.type && req.body.type != ""){
                updateValues.type = req.body.type;
                toUpdateValuesName += "UserType ";
            }

            if( toUpdateValuesName != "" ){
                // remove space and add comma
                toUpdateValuesName = toUpdateValuesName.split(/[ ,]+/).join(',');

                // remove last comma
                toUpdateValuesName = toUpdateValuesName.replace(/,\s*$/, "");
            } 

            knex('users')
            .where({ id: userId })
            .update(updateValues)
            .then(
                ()=>{
                    res.json({
                        status: 'success',
                        message: toUpdateValuesName
                    })
                }
            )
            .catch(error => {
                res.json({
                    status: 'fail',
                    message: error
                })
            })

        }
    })
    
}

function authenticateUser(req,res){

    const username = req.body.username;
    const passwordFromJSON = req.body.password;

    if( username.length < 1 || passwordFromJSON < 1 ){
        res.json({
            status: 'fail',
            message: 'Please enter username and password to login'
        })
        return;
    }

    knex
    .table('users')
    .first('password')
    .where('username', username)
    .then(data => {
        if (!data) {
            res.json({
            status: 'fail',
            message: 'No user found with that username.'
        })
        } else {
            const password = data.password;
            const isMatch = bcrypt.compareSync(passwordFromJSON, password);
            if (isMatch) {
                // password matched
                res.json(
                    {
                        status : "success",
                        message : jwt.sign({username: username}, 'qrparking')
                    }
                )
            } else {
                res.json({status : "fail",message:"Username or password is incorrect."})
            }
        }
        
    })
    .catch(error => {
        res.json({status : "fail",message:"Error occured! This might occur if mysql server is not running. "})
    })
}

module.exports = {
    getAllUsers : getAllUsers,
    getLoggedInUser : getLoggedInUser,
    addUser : addUser,
    deleteUser : deleteUser,
    updateUser : updateUser,
    authenticateUser : authenticateUser
};