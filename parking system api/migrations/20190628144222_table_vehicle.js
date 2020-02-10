const tableName = "vehicle";
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
                    table.string('name')
                    table.string('brand')
                    table.string('type')
                    table.string('vehicleNo')
                    table.integer('userId')
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