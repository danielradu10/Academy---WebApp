# Module Imports
import time

import bcrypt
import mariadb
import sys

from persistance.data.Item import User


class Database:
    def __init__(self):
        print("Entered the init of database")
        # time.sleep(5)
        try:
            print("Trying to connect to database")
            conn = mariadb.connect(
                user="user1",
                password="password1",
                host="mariadb",
                #host="localhost",
                port=3306,
                database="academia",
                autocommit=True
            )
            print("good!")

            self.conn = conn
            self.cursor = self.conn.cursor()

            print("Connected to MariaDB")

            create_table_query = """CREATE TABLE IF NOT EXISTS utilizatori (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE KEY,
                    password VARCHAR(255) NOT NULL,
                    user_role VARCHAR(255) NOT NULL
            );"""
            self.cursor.execute(create_table_query)

            print("Table created successfully")

        except mariadb.Error as e:
            print("Eroare la intializarea database.")
            print(f"Error connecting to MariaDB Platform: {e}")
            sys.exit(1)

    def get_all_users(self):
        query = "SELECT * FROM utilizatori"
        self.cursor.execute(query)
        users = self.cursor.fetchall()
        return users

    def add_element(self, user: User):
        email = user.email
        password_hashed = self.hash_password(user.password)
        password_hashed_str = password_hashed.decode("utf-8")
        user_role = user.role.value

        self.cursor.execute(
            "INSERT INTO utilizatori (email, password, user_role) VALUES (?, ?, ?)",
            (email, password_hashed_str, user_role))

        self.conn.commit()

    def verify_user(self, user: User):
        email = user.email
        password = user.password

        print(email)
        query =  "SELECT * FROM utilizatori"
        self.cursor.execute(query, (email,))
        print("All the elements in table: ")
        print(self.cursor.fetchall())

        query = "SELECT * FROM utilizatori WHERE email = ?"
        self.cursor.execute(query, (email,))
        resulted_user = self.cursor.fetchone()
        if resulted_user is None:
            return False
        if self.verify_password(password, resulted_user[2]):
            return True
        return False

    def get_user_by_email(self, email):
        query = "SELECT * FROM utilizatori WHERE email = ?"
        self.cursor.execute(query, (email,))
        resulted_user = self.cursor.fetchone()
        return resulted_user


    def hash_password(self, user_password):
        password_bytes = user_password.encode('utf-8')
        salt = bcrypt.gensalt()
        hashed_password = bcrypt.hashpw(password_bytes, salt)

        return hashed_password

    def verify_password(self, user_password, hashed_password):
        password_bytes = user_password.encode('utf-8')
        hashed_password_bytes = hashed_password.encode('utf-8')

        return bcrypt.checkpw(password_bytes, hashed_password_bytes)


