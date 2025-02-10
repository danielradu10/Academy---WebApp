# LOGIN PAGE

La deschiderea aplicatiei, utilizatorul se poate autentifica.
Exista mai apoi 3 variante: profil de student, de profesor sau de admin.
![img.png](images/img.png)


# ADMIN
Admin-ul are acces la majoritatea operatiilor de baza din cadrul aplicatiei.
![img_1.png](images/img_1.png)


## Get accounts
Aceasta metoda returneaza toate conturile cu parolele criptate din cadrul aplicatiei.
Metoda a fost implementata pentru a oferi ajutor admin-ului atunci cand doreste sa insereze useri noi.
![img_2.png](images/img_2.png)

**NOTE:**
In cazul in care nu exista deloc conturi, in componenta AuthService exista un script - usersGenerator.py care poate fi rulat pentru a incarca 3 accounturi de test.

## Insert account

Prespunem ca un admin incearca sa insereze un cont cu email gresit.
![img_3.png](images/img_3.png)

Un cont care  exista deja:
![img_4.png](images/img_4.png)

Operatie reusita:
![img_5.png](images/img_5.png)

## View professors
![img_6.png](images/img_6.png)
### HATEOAS pentru - next page previous page. 
![img_7.png](images/img_7.png)

### Daca se apasa pe Delete professor:
![img_9.png](images/img_9.png)

### Se apasa apoi pe view disciplines:
## HATEOAS pentru - next page, previous page
![img_8.png](images/img_8.png)

### Daca se incearca stergerea unei discipline la care sunt inrolati anumiti studenti.
![img_10.png](images/img_10.png)

## Insert professor
### Cazul in care se insereaza cu un mail care exista (exemplu)
![img_11.png](images/img_11.png)

### Cazul in care se insereaza cu prea multe caractere (exemplu)
![img_12.png](images/img_12.png)

### Cazul in care se insereaza cu succes (exemplu)
![img_13.png](images/img_13.png)

## View students
### HATEOAS - next page, previous page
![img_14.png](images/img_14.png)

## Daca se apasa pe view details:
### HATEOAS - view disciplines, patch disciplines
![img_15.png](images/img_15.png)

## Daca se apasa pe view disciplines:
### HATEOAS - view professor details, view discipline details, delete 
![img_16.png](images/img_16.png)

## Daca se apasa pe view professor details se deschid detaliile intr-un tab nou:
![img_17.png](images/img_17.png)

## Daca se apasa pe view discipline details se deschid detaliile intr-un tab nou:
![img_18.png](images/img_18.png)

## Daca nu se apasa pe view disciplines, dar se apasa pe patch disciplines:
![img_19.png](images/img_19.png)

## View disciplines
### HATEOAS pentru toate butoanele care corespund unei discipline si nextPage, previousPage pentru lista de discipline.
### Informatiile din linkuri se deschid in alerte.
![img_20.png](images/img_20.png)

## Insert discipline
### Aceasta metoda compune 2 call-uri - unul catre API-ul de materiale iar altul catre API-ul profi studenti.
**NOTE: In Materiale salvam codul disciplinei si titularul.**
![img_21.png](images/img_21.png)

### HATEOAS - toate butoanele care apar dupa inserarea disciplinei
![img_22.png](images/img_22.png)


# PROFESSOR PROFILE
### HATEOAS - My disciplines, My students, next/previous page in fiecare scenariu
![img_23.png](images/img_23.png)

### Insert Ponderi
Aici profesorul poate insera ponderile disciplinei
![img_24.png](images/img_24.png)

### Ponderi incorecte
![img_25.png](images/img_25.png)

### Ponderi corect
### HATEOAS - view ponderi
![img_26.png](images/img_26.png)

### View Ponderi
![img_27.png](images/img_27.png)

### Upload Laboratory materials
![img_28.png](images/img_28.png)

### Dupa ce se selecteaza disciplina si se apasa Fetch Materials:
### HATEOAS - Preview, next page, previous page, Preview
![img_29.png](images/img_29.png)

### Daca se apasa Preview, se deschide fisierul intr-un alt tab
![img_30.png](images/img_30.png)

# STUDENT PROFILE
### Minimal, am folosit acceeasi componenta pe care am mai folosit-o la liste de discipline.
![img_31.png](images/img_31.png)

### Daca studentul incearca sa vizualizeze profesorul sau sa stearga disciplina, se returneaza:
![img_32.png](images/img_32.png)






