package Main;

import java.sql.Date;

public class Customer {
    private int customerId;
    private String name;
    private String email;
    private long phoneNumber;
    private Date createdAt;

    public Customer(int customerId, String name, String email, long phoneNumber, Date createdAt) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

}
