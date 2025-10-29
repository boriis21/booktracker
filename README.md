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

## 🧰 Testing

To run the tests, execute:

```bash
mvn clean test
