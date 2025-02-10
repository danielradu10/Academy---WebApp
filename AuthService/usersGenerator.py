from persistance.data.Item import User, Role
from persistance.database import Database

if __name__ == "__main__":
    db = Database()
    user = User("admin@gmail.com", "a", role=Role.admin)
    with open("userPasswords", 'a') as f:
        f.write(user.email)
        f.write(user.password)
        f.write(str(user.role))
        f.write("\n")
    db.add_element(user)

    user = User("profesor.profesor@academic.tuiasi.ro", "p", role=Role.professor)
    with open("userPasswords", 'a') as f:
        f.write(user.email)
        f.write(user.password)
        f.write(str(user.role))
        f.write("\n")
    db.add_element(user)

    user = User("student.student@student.tuiasi.ro", "s", role=Role.student)
    with open("userPasswords", 'a') as f:
        f.write(user.email)
        f.write(user.password)
        f.write(str(user.role))
        f.write("\n")
    db.add_element(user)

    user = User("testplease.test@test", "t", role=Role.professor)
    with open("userPasswords", 'a') as f:
        f.write(user.email)
        f.write(user.password)
        f.write(str(user.role))
        f.write("\n")
    db.add_element(user)