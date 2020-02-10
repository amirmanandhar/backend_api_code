const tableName = "booking";
exports.up = function(knex, Promise) {
    return knex
    .schema
    .hasTable(tableName)
    .then(function (exists) {
        if (!exists) {
            return knex
                .schema
                .createTable(tableName, function (table) {
                    table.increments('id').primary()
                    table.integer('userId').comment('Determines which user`s booking is this')
                    table.integer('parkingSpotId').comment('Determines which parking spot is booked')
                    table.integer('vehicleId').comment('Determines which vehicle is booked')
                    table.dateTime('datetime').comment('Determines booking`s date time')
                    table.string('verificationId').defaultTo('unique-id').comment('This is used for identifying booking`s qr code')
                })
                .then(console.log("Table "+tableName+" created."));
        }else{
            console.log("Table "+tableName+" already created!");
        }
    })
};

exports.down = function(knex, Promise) {
    return knex.schema.dropTable(tableName);
};