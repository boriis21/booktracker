# 📚 Book Tracker

**Java JPA/Hibernate project** showcases understanding of **JPA**, **EntityManager**, **DAO-Service abstractions** and **H2 in-memory database** for tesing.

---

## ⚙️ Overview

**BookTracker** is a small library managment system that manages:
- **Authors** and their **Books** (one-to-many)
- **Borrowers** and their borrowed **Books** (many-to-many)

It demonstrates:
- Core JPA usage
- Entity Lifecycle managment
- DAO + Service pattern
- In-memory H2 testing
- Transaction management
- Clean Maven structure

---

## ⚙️ Build, Run & Test

### 🧩 Build the Project
Make sure you have **Java 17+** and **Maven 3.9+** installed.

```bash
mvn clean compile
```

### 🚀 Run the Demo

```bash
mvn exec:java -Dexec.mainClass="com.jpa.booktracker.App"
```
Uses the configuration in `src/main/resources/META-INF/persistence.xml.`

### 🧪 Run the Tests

```bash
mvn test
```
Runs all unit and integration tests using an in-memory H2 database.

---

## 🧠 Notes

- Lombok is used throughout (`@Getter`, `@Setter`, etc.).
Make sure annotation processing is enabled in your IDE.