
exports.up = function(knex, Promise) {
    return knex
    .schema
    .hasTable('users')
    .then(function (exists) {
        if (!exists) {
            return knex
                .schema
                .createTable('users', function (table) {
                    table.increments('id').primary()
                    table.string('username')
                    table.string('password')
                    table.string('email')
                    table.string('phone')
                    table.string('type').defaultTo('user')
                    table.unique('username')
                })
                .then(console.log("Table users created."));
        }else{
            console.log("Table users already created!");
        }
    })
};

exports.down = function(knex, Promise) {
    return knex.schema.dropTable('users');
};
