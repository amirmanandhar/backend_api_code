const Knex = require("knex");
const knexOptions = require(".././knexfile");
const knex = Knex(knexOptions);

const responder = require("./respond")
const auth = require("./auth")
const tableName = 'vehicle';

//users handlers
function getAllVehicles(req,res){
    knex
    .select()
    .table(tableName)
    .then((data)=>{
        res.json(data)
    })
    .catch(error => {
        res.json("fail","Error occured!")
    })
}

function getVehicleByUser(req,res){
    knex
    .select()
    .table(tableName)
    .where({ userId: req.params.userId })
    .then((data)=>{
        res.json(data)
    })
    .catch(error => {
        res.json("Error occured!")
    })
}

function addVehicle(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            // required db values
            var values = {
                name : req.body.name,
                brand : req.body.brand,
                type : req.body.type,
                vehicleNo : req.body.vehicleNo,
                userId : req.body.userId
            };

            if( !auth.isValuesEmpty(values) ){
                //add to db
                knex(tableName)
                .insert(values)
                .then(
                    ()=>{
                        responder.respond(res,"success","Vehicle added.")
                    }
                )
                .catch(error => {
                    responder.respond(res,"fail","Error: "+error)
                })
            }else{
                responder.respond(res,"fail","All vehicle details required.")
            }

            
        }else{
            responder.respond(res,"fail",payload.error)
        }
        
    })
    
}

function deleteVehicle(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            var id = req.params.id;
            knex(tableName)
            .delete()
            .where('id',id)
            .then(
                ()=>{
                    responder.respond(res,"success","Vehicle deleted!")
                }
            )
            .catch(error => {
                responder.respond(res,"fail","Error occured!")
            })
        }
    })
    
}

// send authenticated user's vehicle
function getMyVehicle(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            knex
            .select()
            .table(tableName)
            .where({ userId: req.params.userId })
            .then((data)=>{
                res.json(data)
            })
            .catch(error => {
                res.json("Error occured!")
            })
        }
    })
}

module.exports = {
    getAllVehicles : getAllVehicles,
    getVehicleByUser : getVehicleByUser,
    addVehicle : addVehicle,
    deleteVehicle : deleteVehicle,
    getMyVehicle : getMyVehicle
};