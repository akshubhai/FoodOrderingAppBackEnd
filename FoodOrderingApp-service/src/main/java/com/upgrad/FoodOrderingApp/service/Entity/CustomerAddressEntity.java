package com.upgrad.FoodOrderingApp.service.Entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_address")
@NamedQueries({
        @NamedQuery(name = "getCustomerAddress", query = "SELECT c from CustomerAddressEntity c where c.customer=:customer"),
        @NamedQuery(name = "getAddress", query = "SELECT c from CustomerAddressEntity c where c.address = :address")
})
public class CustomerAddressEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private com.upgrad.FoodOrderingApp.service.Entity.CustomerEntity customer;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    private com.upgrad.FoodOrderingApp.service.Entity.AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public com.upgrad.FoodOrderingApp.service.Entity.CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(com.upgrad.FoodOrderingApp.service.Entity.CustomerEntity customer) {
        this.customer = customer;
    }

    public com.upgrad.FoodOrderingApp.service.Entity.AddressEntity getAddress() {
        return address;
    }

    public void setAddress(com.upgrad.FoodOrderingApp.service.Entity.AddressEntity address) {
        this.address = address;
    }
}
