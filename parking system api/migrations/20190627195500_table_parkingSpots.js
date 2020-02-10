const tableName = "parkingspots";
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
                    table.string('longitude')
                    table.string('latitude')
                    table.integer('ownerId').comment('Owner`s user id')
                    table.string('title',30)
                    table.string('description',100)
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