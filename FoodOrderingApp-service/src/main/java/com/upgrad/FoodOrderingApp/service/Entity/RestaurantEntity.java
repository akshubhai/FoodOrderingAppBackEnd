package com.upgrad.FoodOrderingApp.service.Entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant")
@NamedQueries(
        {
                @NamedQuery(name = "getAllRestaurants", query = "select r from RestaurantEntity r order by r.customerRating desc"),
                @NamedQuery(name = "getRestaurantByUuid", query = "select r from RestaurantEntity r where r.uuid = :uuid"),
                @NamedQuery(name = "restaurantByName", query = "select r from RestaurantEntity r where lower(r.restaurantName) like :restaurant_name_lower")
        }
)
public class RestaurantEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "restaurant_name")
    @NotNull
    @Size(max = 50)
    private String restaurantName;

    @Column(name = "photo_url")
    @NotNull
    @Size(max = 255)
    private String photoUrl;

    @Column(name = "customer_rating")
    @NotNull
    private Double customerRating;

    @Column(name = "average_price_for_two")
    @NotNull
    private Integer avgPriceForTwo;

    @Column(name = "number_of_customers_rated")
    @NotNull
    private Integer numOfCustomersRated;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    private com.upgrad.FoodOrderingApp.service.Entity.AddressEntity address;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity> category = new ArrayList<com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity> getCategories() {
        return category;
    }

    public void setCategories(List<com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity> categories) {
        this.category = categories;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Double customerRating) {
        this.customerRating = customerRating;
    }

    public Integer getNumOfCustomersRated() {
        return numOfCustomersRated;
    }

    public void setNumberCustomersRated(Integer numOfCustomersRated) {
        this.numOfCustomersRated = numOfCustomersRated;
    }

    public Integer getAvgPriceForTwo() {
        return avgPriceForTwo;
    }

    public void setAvgPrice(Integer avgPriceForTwo) {
        this.avgPriceForTwo = avgPriceForTwo;
    }

    public com.upgrad.FoodOrderingApp.service.Entity.AddressEntity getAddress() {
        return address;
    }

    public void setAddress(com.upgrad.FoodOrderingApp.service.Entity.AddressEntity address) {
        this.address = address;
    }

}
