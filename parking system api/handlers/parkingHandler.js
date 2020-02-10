const Knex = require("knex");
const knexOptions = require(".././knexfile");
const knex = Knex(knexOptions);
const auth = require("./auth")
const responder = require("./respond")

const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const tableName = 'parkingspots';

//users handlers
function getAllParkingSpots(req,res){ 
    console.log(req)
    knex
    .select()
    .table(tableName)
    .then((data)=>{
        console.log(data)
        res.json(data)
    })
    .catch(error => {
        res.json("fail","Error occured!")
    })
}

function getParkingByOwner(req,res){
    knex
    .select()
    .table(tableName)
    .where({ ownerId: req.params.ownerId })
    .then((data)=>{
        responder.respond(res,"success",data)
    })
    .catch(error => {
        res.json("Error occured!")
    })
}

function addSpot(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            // required db values
            var values = {
                title: req.body.title,
                description: req.body.description,
                longitude: req.body.longitude,
                latitude: req.body.latitude,
                ownerId: req.body.ownerId
            };

            //add to db
            knex(tableName)
            .insert(values)
            .then(
                ()=>{
                    responder.respond(res,"success","Parking spot added.")
                }
            )
            .catch(error => {
                responder.respond(res,"fail","Error: "+error)
            })
        }
    })
}

function deleteSpot(req,res){
    var id = req.body.id;
    knex(tableName)
    .where({ id: id })
    .del()
    .then(
        ()=>{
            res.json("Spot deleted!")
        }
    )
    .catch(error => {
        res.json("Error occured!")
    })
}

function updateSpot(req,res){

    var updateValues = {},
    id = req.body.id;

    if(req.body.username) updateValues.username = req.body.username;
    if(req.body.password){
        //hash password
        const hashedPassword = bcrypt.hashSync(req.body.password, 10)
        updateValues.password = hashedPassword;
    } 
    if(req.body.email) updateValues.email = req.body.email;
    if(req.body.phone) updateValues.phone = req.body.phone;

    knex(tableName)
    .where({ id: id })
    .update(updateValues)
    .then(
        ()=>{
            res.json("Spot updated!")
        }
    )
    .catch(error => {
        res.json("Error occured!")
    })
}

module.exports = {
    getAllParkingSpots : getAllParkingSpots,
    getParkingByOwner : getParkingByOwner,
    addSpot : addSpot,
    deleteSpot : deleteSpot,
    updateSpot : updateSpot
};