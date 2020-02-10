const Express = require("express")
const app = Express()
const cors = require('cors')
// const multerInc = require("./multerFileUpload")

// users imports
const userHandler = require("./handlers/userHandlers")
const parkingHandler = require("./handlers/parkingHandler")
const vehicleHandler = require("./handlers/vehicleHandler")
const bookingHandler = require("./handlers/bookingHandler")


//body parser stuffs
const bodyParser = require('body-parser')
// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))
// parse application/json
app.use(bodyParser.json())
//use cors
app.use(cors());

//required for profile images
app.use('/static', Express.static('profiles'))
app.use('/static/qr', Express.static('qr'))

const apiRoute = "/api/v1/";

//api health check
app.get(apiRoute+"/health",(req,res)=>{
    res.json({
        "status" : "ok"
    })
})

//user api -->
// /api/v1/users/
app.get(apiRoute+"users/",userHandler.getAllUsers)

// /api/v1/users/
app.post(apiRoute+"users/",userHandler.addUser)

// /api/v1/users/auth/
app.post(apiRoute+"users/auth/",userHandler.authenticateUser)

// /api/v1/users/auth/
app.get(apiRoute+"users/auth/",userHandler.getLoggedInUser)

// /api/v1/users/auth/
app.put(apiRoute+"users/auth/",userHandler.updateUser)

// /api/v1/users/auth/
app.delete(apiRoute+"users/auth/del/:id",userHandler.deleteUser)

// <-- user api


// parking api -->
// /api/v1/parking/spots/
app.get(apiRoute+"parking/spots/",parkingHandler.getAllParkingSpots)

// /api/v1/parking/spots/{ownerId}
app.get(apiRoute+"parking/spots/:ownerId",parkingHandler.getParkingByOwner)

// /api/v1/parking/spots/
app.post(apiRoute+"parking/spots/",parkingHandler.addSpot)
// <-- parking api

// vehicle api -->
// /api/v1/vehicle/auth/{userId}
app.get(apiRoute+"vehicle/auth/:userId",vehicleHandler.getMyVehicle)

// /api/v1/vehicle/auth/
app.post(apiRoute+"vehicle/auth/",vehicleHandler.addVehicle)

// /api/v1/vehicle/auth/{id}
app.delete(apiRoute+"vehicle/auth/:id",vehicleHandler.deleteVehicle)

// <-- vehicle api

// booking api -->
// /api/v1/booking/auth/{userId}
app.get(apiRoute+"booking/auth/:userId",bookingHandler.getMyBookings)

// /api/v1/booking/auth/
app.post(apiRoute+"booking/auth/",bookingHandler.bookParking)

// /api/v1/booking/auth/viewQr/{bookingId}
app.get(apiRoute+"booking/auth/viewQr/:bookingId",bookingHandler.getQr)

// /api/v1/booking/auth/{id}
app.delete(apiRoute+"booking/auth/:id",bookingHandler.deleteBooking)

// <-- booking api



//start server
const port = 8040;
app.listen(port,()=>{
    console.log("Server listening on "+port);
})