package main;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 2865488185179662738L;
    
    protected String username;
    private String password;
    private AccessLevel accessLevel;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int salary;
    private LocalDate birthday;
    
    public User(String firstName, String lastName, String username, String password, String email, String phone, int salary, LocalDate birthday, AccessLevel accessLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.salary = salary;
        this.birthday = birthday;
        this.accessLevel = accessLevel;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public int getSalary() {
        return salary;
    }
    public LocalDate getBirthday() {
        return birthday;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setSalary(int salary) {
        this.salary = salary;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
