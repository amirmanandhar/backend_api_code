# QR Parking API

### Database name (default) : dbqrparking
- Db name is set at [knexfile.js](https://github.com/amirmanandhar/parkingapi/blob/master/knexfile.js)

### Server and Migration 
- ```npm start``` > start server
- ``` npm run migrate:run``` > run migration

### Routes
- main head route is ```/api/version/```
- authorized routes contains ```/auth/```

#### User routes

- User route 
	- GET / POST > ```/api/v1/users/```
- User authenticated route
	- GET / POST / PUT / DELETE > ```/api/v1/users/auth/```

#### Parking routes

-	GET / POST > ```/api/v1/parking/spots/```

#### Booking routes
- GET / POST / PUT / DELETE > ```/api/v1/booking/auth/```

#### Vehicle routes
- GET / POST / DELETE >	```/api/v1/vehicle/auth/```