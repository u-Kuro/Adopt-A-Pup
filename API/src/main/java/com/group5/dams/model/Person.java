package com.group5.dams.model;

import javax.persistence.*;

@Entity
@Table(name="person")
public class Person {
      @Id
      @GeneratedValue(strategy = GenerationType.AUTO)
      private long id;

      private String firstName;
      private String lastName;
      private String email;
      private String password;

      public Person() { }

      public Person(long id, String firstName, String lastName, String email, String password) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
      }

      public long getId() {
            return id;
      }

      public void setId(long id) {
            this.id = id;
      }

      public String getFirstName() {
            return firstName;
      }

      public void setFirstName(String firstName) {
            this.firstName = firstName;
      }

      public String getLastName() {
            return lastName;
      }

      public void setLastName(String lastName) {
            this.lastName = lastName;
      }

      public String getFullName() {
            return firstName+" "+lastName;
      }

      public String getEmail() {
            return email;
      }

      public void setEmail(String email) {
            this.email = email;
      }

      public String getPassword() {
            return password;
      }

      public void setPassword(String password) {
            this.password = password;
      }
}
