from collections.abc import Mapping
from typing import Any

from pymongo import MongoClient
from pymongo.synchronous.collection import Collection
from pymongo.synchronous.database import Database

username = "username"
password = "password"
host = "mongodb"
#host = "localhost"
port = 27017
auth_db = "admin"


def connectDatabase() -> Database[Mapping[str, Any]] | None:
    try:
        client = MongoClient(f"mongodb://{username}:{password}@{host}:{port}/{auth_db}")
        mydb = client["admin"]
        return mydb
    except:
        return None


def getCollection(mydb: Database[Mapping[str, Any]], collectionName: str) -> Collection[Mapping[str, Any]]:
    collection = None

    try:
        collection = mydb[collectionName]
        print("Collection returned!")
    except Exception as e:
        print("Error finding collection {}".format(collectionName), e)

    return collection