package com.upgrad.FoodOrderingApp.service.Entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "restaurant_category")
@NamedQueries(
        {
                @NamedQuery(name = "getRestaurantCategories", query = "SELECT rc from RestaurantCategoryEntity rc where rc.restaurant=:restaurant"),
                @NamedQuery(name = "getRestaurantsByCategoryId", query = "SELECT rc from RestaurantCategoryEntity rc where rc.category=:category"),
        }
)
public class RestaurantCategoryEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id")
    private com.upgrad.FoodOrderingApp.service.Entity.RestaurantEntity restaurant;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id")
    private com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public com.upgrad.FoodOrderingApp.service.Entity.RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(com.upgrad.FoodOrderingApp.service.Entity.RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    public com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity category) {
        this.category = category;
    }
}

