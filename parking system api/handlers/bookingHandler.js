const Knex = require("knex");
const knexOptions = require(".././knexfile");
const knex = Knex(knexOptions);

const responder = require("./respond")
const auth = require("./auth")
const tableName = 'booking';

// for qr code
const qrGenerator = require(".././qrCodeGenerator")
// for unique random valuw to be set fo veryfing bookings
const uuidv1 = require('uuid/v1');
const base64Img = require('base64-img');


function bookParking(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            // required db values
            var values = {
                userId : req.body.userId,
                parkingSpotId : req.body.parkingSpotId,
                vehicleId : req.body.vehicleId,
                datetime : req.body.datetime
            };

            if( !auth.isValuesEmpty(values) ){
                //add to db
                knex(tableName)
                .insert(values)
                .then(
                    ()=>{

                        getLastBookingId(function(bookingId){
                            const uniqueId = uuidv1();
                            knex(tableName)
                            .where({ id: bookingId })
                            .update({"verificationId" : uniqueId})
                            .then(
                                ()=>{
                                    res.json(
                                        {
                                            status : "success",
                                            message : "Parking spot booked.",
                                            bookingId : bookingId
                                        }
                                    )
                                }
                            )
                            .catch(error => {
                                responder.respond(res,"fail","Error: "+error)
                            })
                        })
                        
                    }
                )
                .catch(error => {
                    responder.respond(res,"fail","Error: "+error)
                })
            }else{
                responder.respond(res,"fail","All booking details required.")
            }

            
        }else{
            responder.respond(res,"fail",payload.error)
        }
        
    })
    
}

// send authenticated user's bookings
function getMyBookings(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            knex(tableName)
            .join('parkingspots','parkingspots.id','booking.parkingSpotId')
            .join('vehicle','vehicle.id','booking.vehicleId')
            .select('booking.id','vehicle.name as vehicleName','parkingspots.title','parkingspots.description','booking.datetime','booking.verificationId')
            .where('booking.userId',req.params.userId)
            // .debug(true)
            .then((data)=>{
                res.json(data)
            })
            .catch(error => {
                res.json("Error occured!")
            })
        }
    })
}

// generate qr code for provided booking
function getQr(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            knex
            .select('verificationId')
            .table(tableName)
            .where({ id: req.params.bookingId })
            .then((data)=>{
                const verificationId = data[0].verificationId
                qrGenerator.generate(verificationId,function(qrData){
                    const qrImageTag = "<img src='"+qrData+"'/>"
                    base64Img.imgSync(qrData, './qr', 'qr');
                    res.json(qrImageTag)
                })
                
            })
            .catch(error => {
                res.json("Error occured! "+error)
            })
        }
    })
}

function deleteBooking(req,res){
    auth.checkAuthentication(req.headers.token,(payload)=>{
        if( !payload.error ){
            knex(tableName)
            .delete()
            .where('id',req.params.id)
            .then(()=>{
                responder.respond(res,"success","Booking deleted")
            })
            .catch(er=>{
                responder.respond(res,"fail","error")
            })
        }
    })
}

function getLastBookingId(cb){
    knex
        .select('id')
        .table(tableName)
        .orderBy('id','desc')
        .first()
        .then((data)=>{
            cb(data.id);
        })
        .catch(error => {
            cb({"error":error})
        })
}

module.exports = {
    getMyBookings : getMyBookings,
    bookParking : bookParking,
    getQr : getQr,
    deleteBooking:deleteBooking
}