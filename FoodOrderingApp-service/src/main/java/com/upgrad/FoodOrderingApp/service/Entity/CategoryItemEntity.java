package com.upgrad.FoodOrderingApp.service.Entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "category_item")
@NamedQueries(
        {
                @NamedQuery(name = "getItemsByCategory", query = "select c from CategoryItemEntity c where c.category = :category order by lower(c.item.itemName) ASC")
        }
)
public class CategoryItemEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "item_id")
    private com.upgrad.FoodOrderingApp.service.Entity.ItemEntity item;

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

    public com.upgrad.FoodOrderingApp.service.Entity.ItemEntity getItem() {
        return item;
    }

    public void setItem(com.upgrad.FoodOrderingApp.service.Entity.ItemEntity item) {
        this.item = item;
    }

    public com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity category) {
        this.category = category;
    }
}
