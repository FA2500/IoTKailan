/* eslint-disable */
const admin = require('firebase-admin');
var serviceAccount = require("./serviceAccountKey.json");

const {setGlobalOptions} = require("firebase-functions/v2");
setGlobalOptions({maxInstances: 10});

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://iot-hkkailan-default-rtdb.asia-southeast1.firebasedatabase.app"
});

const {onRequest} = require("firebase-functions/v2/https");
const {onCall} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const functions = require("firebase-functions");
const { Telegraf } = require("telegraf");
const { getDatabase } = require('firebase-admin/database');
const { onSchedule } = require('firebase-functions/v2/scheduler');
const bot = new Telegraf("6054458858:AAHRxUL49D2E4FkvDB0OEKMjdDJIhGT0ZJ4");
const db = getDatabase();
const ref = db.ref("control");

exports.lowWaterLevel = onRequest({region: "asia-southeast1"}, (request, response) => {
  bot.telegram.sendMessage("845590413","Water Level is low. Please refill it."); //Faris
  bot.telegram.sendMessage("527653178","Water Level is low. Please refill it."); //Aiman
  bot.telegram.sendMessage("827606573","Water Level is low. Please refill it."); //Wani
  bot.telegram.sendMessage("824609471","Water Level is low. Please refill it."); //Syafiq
  bot.telegram.sendMessage("758623939","Water Level is low. Please refill it."); //Anas
  return response.status(200).send("OK");
});



exports.highWaterLevel = onRequest({region: "asia-southeast1"}, (request, response) => {
  bot.telegram.sendMessage("845590413","Water Level is high. Thank you for refill it."); //Faris
  bot.telegram.sendMessage("527653178","Water Level is high. Thank you for refill it."); //Aiman
  bot.telegram.sendMessage("827606573","Water Level is high. Thank you for refill it."); //Wani
  bot.telegram.sendMessage("824609471","Water Level is high. Thank you for refill it."); //Syafiq
  bot.telegram.sendMessage("758623939","Water Level is high. Thank you for refill it."); //Anas
  return response.status(200).send("OK");
});


exports.water4hours = functions.region("asia-southeast1").pubsub.schedule('0 */4 * * *').onRun( async (context) => {
  ref.update({
    'waterpump2': true
  });
  bot.telegram.sendMessage("845590413","Plant 2 have been watered via time interval."); //Faris
  bot.telegram.sendMessage("527653178","Plant 2 have been watered via time interval."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 2 have been watered via time interval."); //Wani
  bot.telegram.sendMessage("824609471","Plant 2 have been watered via time interval."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 2 have been watered via time interval."); //Anas
  return "200";
});

exports.waterplant1 = onRequest({region: "asia-southeast1"}, (request, response) => {
  ref.update({
    'waterpump1': true
  });
  bot.telegram.sendMessage("845590413","Plant 1 have been watered."); //Faris
  bot.telegram.sendMessage("527653178","Plant 1 have been watered."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 1 have been watered."); //Wani
  bot.telegram.sendMessage("824609471","Plant 1 have been watered."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 1 have been watered."); //Anas
  return response.status(200).send("OK");
});

exports.waterplant1android = onCall({region: "asia-southeast1"}, (request) => {
  ref.update({
    'waterpump1': true
  });
  bot.telegram.sendMessage("845590413","Plant 1 have been watered via android."); //Faris
  bot.telegram.sendMessage("527653178","Plant 1 have been watered via android."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 1 have been watered via android."); //Wani
  bot.telegram.sendMessage("824609471","Plant 1 have been watered via android."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 1 have been watered via android."); //Anas
  return "200";
});

exports.waterplant2 = onRequest({region: "asia-southeast1"}, (request, response) => {
  ref.update({
    'waterpump2': true
  });
  bot.telegram.sendMessage("845590413","Plant 2 have been watered."); //Faris
  bot.telegram.sendMessage("527653178","Plant 2 have been watered."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 2 have been watered."); //Wani
  bot.telegram.sendMessage("824609471","Plant 2 have been watered."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 2 have been watered."); //Anas
  return response.status(200).send("OK");
});

exports.waterplant2android = onCall({region: "asia-southeast1"}, (request) => {
  ref.update({
    'waterpump2': true
  });
  bot.telegram.sendMessage("845590413","Plant 2 have been watered via android."); //Faris
  bot.telegram.sendMessage("527653178","Plant 2 have been watered via android."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 2 have been watered via android."); //Wani
  bot.telegram.sendMessage("824609471","Plant 2 have been watered via android."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 2 have been watered via android."); //Anas
  return "200";
});

exports.fanplant1 = onRequest({region: "asia-southeast1"}, (request, response) => {
  ref.update({
    'fan': true
  });
  bot.telegram.sendMessage("845590413","Plant 1 fan have been activated."); //Faris
  bot.telegram.sendMessage("527653178","Plant 1 fan have been activated."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 1 fan have been activated."); //Wani
  bot.telegram.sendMessage("824609471","Plant 1 fan have been activated."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 1 fan have been activated."); //Anas
  return response.status(200).send("OK");
});

exports.fanplant1android = onCall({region: "asia-southeast1"}, (request) => {
  ref.update({
    'fan': true
  });
  bot.telegram.sendMessage("845590413","Plant 1 fan have been activated via android."); //Faris
  bot.telegram.sendMessage("527653178","Plant 1 fan have been activated via android."); //Aiman
  bot.telegram.sendMessage("827606573","Plant 1 fan have been activated via android."); //Wani
  bot.telegram.sendMessage("824609471","Plant 1 fan have been activated via android."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant 1 fan have been activated via android."); //Anas
  return "200";
});

exports.servoOpen = onRequest({region: "asia-southeast1"}, (request, response) => {
  ref.update({
    'servo': true
  });
  bot.telegram.sendMessage("845590413","Plant container have been covered."); //Faris
  bot.telegram.sendMessage("527653178","Plant container have been covered."); //Aiman
  bot.telegram.sendMessage("827606573","Plant container have been covered."); //Wani
  bot.telegram.sendMessage("824609471","Plant container have been covered."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant container have been covered."); //Anas
  return response.status(200).send("OK");
});

exports.servoClose = onRequest({region: "asia-southeast1"}, (request, response) => {
  ref.update({
    'servo': false
  });
  bot.telegram.sendMessage("845590413","Plant container have been exposed."); //Faris
  bot.telegram.sendMessage("527653178","Plant container have been exposed."); //Aiman
  bot.telegram.sendMessage("827606573","Plant container have been exposed."); //Wani
  bot.telegram.sendMessage("824609471","Plant container have been exposed."); //Syafiq
  bot.telegram.sendMessage("758623939","Plant container have been exposed."); //Anas
  return response.status(200).send("OK");
});

exports.errorUrgent = onRequest({region: "asia-southeast1"}, (request, response) => {
  ref.update({
    'servo': false
  });
  bot.telegram.sendMessage("845590413","IoT encounter some errors. Please check the project."); //Faris
  bot.telegram.sendMessage("527653178","IoT encounter some errors. Please check the project."); //Aiman
  bot.telegram.sendMessage("827606573","IoT encounter some errors. Please check the project."); //Wani
  bot.telegram.sendMessage("824609471","IoT encounter some errors. Please check the project."); //Syafiq
  bot.telegram.sendMessage("758623939","IoT encounter some errors. Please check the project."); //Anas
  return response.status(200).send("OK");
});
