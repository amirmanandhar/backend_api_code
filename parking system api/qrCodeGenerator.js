const QRCode = require('qrcode')

function generate(data,cb){
    QRCode.toDataURL(data)
    .then(url => {
        cb(url)
    })
    .catch(err => {
        console.error(err)
    })
}

module.exports = {
    generate:generate
}