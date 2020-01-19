from flask import Flask, render_template, request, jsonify
from bson.json_util import dumps
from flask_pymongo import PyMongo
import json



app = Flask(__name__, template_folder = "C:\\Users\\maple\\Desktop\\Website Demo\\template_folder")
app.config["MONGO_URI"] = "mongodb://localhost:27017/policeDatabase"

mongo = PyMongo(app)
# C:\Users\maple\Desktop\Website Demo\template_folder

@app.route("/post_database")
def post_database():
     name = request.args.get("name")
     location = request.args.get("location")
     safe = request.args.get("safe")
     date = request.args.get("date")
     mongo.db.policeDatabase.insert({"name":name, "location":location, "safe":safe, "date":date})
     return "Done"

@app.route("/JSON_insert", methods = ["POST"])
def JSON_insert():
    data_given = request.get_json(force = True)
    name = None
    location = None
    safe = None
    date = None
    if "name" in data_given:
        name = data_given["name"]
    if "location" in data_given:
        location = data_given["location"]
    if "safe" in data_given:
        others = data_given["safe"]
    if "date" in data_given:
        date = data_given["date"]
    mongo.db.policeDatabase.insert({"name":name, "location":location, "safe":safe, "date":date})
    return "Done"

@app.route("/get_database")
def get_database():
    return dumps(mongo.db.policeDatabase.find())

@app.route("/get_database/safe-injured")
def search_safe_injured():
    return dumps(mongo.db.policeDatabase.find({"safe": "safe", "injury":"injured"}))

@app.route("/get_database/safe-uninjured")
def search_safe_uninjured():
    return dumps(mongo.db.policeDatabase.find({"safe":"safe", "injury":"uninjured"}))

@app.route("/get_database/unsafe")
def search_unsafe():
    return dumps(mongo.db.policeDatabase.find({"safe":"unsafe"}))


@app.route("/get_database/name")
def search_name():
    name = request.form.get("name")
    return dumps(mongo.db.policeDatabase.find({"name": name}))



if __name__ == "__main__":
    app.run(debug=True, port = 5000)
