package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.Entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/* Service for Item Entity and OrderItem and Order and Restaurant and Category */
@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    RestaurantItemDao restaurantItemDao;

    /* Get Items By Popularity from Restaurant Entity,
     * Check for Restaurant exists in DB, Return Sorted List
     *  */
    public List<com.upgrad.FoodOrderingApp.service.Entity.ItemEntity> getItemsByPopularity(com.upgrad.FoodOrderingApp.service.Entity.RestaurantEntity restaurantEntity) throws RestaurantNotFoundException {

        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }
        List<com.upgrad.FoodOrderingApp.service.Entity.OrderEntity> ordersEntities = orderDao.getOrderByRestaurant(restaurantEntity);

        List<com.upgrad.FoodOrderingApp.service.Entity.ItemEntity> itemEntities = new LinkedList<>();

        ordersEntities.forEach(ordersEntity -> {
            List<com.upgrad.FoodOrderingApp.service.Entity.OrderItemEntity> orderItemEntities = orderItemDao.getItemsByOrders(ordersEntity);
            orderItemEntities.forEach(orderItemEntity -> {
                itemEntities.add(orderItemEntity.getItem());
            });
        });
        Map<String, Integer> itemCountMap = new HashMap<>();
        itemEntities.forEach(itemEntity -> {
            Integer count = itemCountMap.get(itemEntity.getUuid());
            itemCountMap.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
        });
        Map<String, Integer> sortedItemCountMap = itemDao.sortMapByValues(itemCountMap);

        List<com.upgrad.FoodOrderingApp.service.Entity.ItemEntity> sortedItemEntites = new LinkedList<>();
        Integer count = 0;
        for (Map.Entry<String, Integer> item : sortedItemCountMap.entrySet()) {
            if (count < 5) {
                sortedItemEntites.add(itemDao.getItemByUuid(item.getKey()));
                count = count + 1;
            } else {
                break;
            }
        }

        return sortedItemEntites;
    }

    /* Get Items By filtering Category UUID and Restaurant UUID - Returns list of items in a category in a restaurant  */
    public List<com.upgrad.FoodOrderingApp.service.Entity.ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        com.upgrad.FoodOrderingApp.service.Entity.RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        com.upgrad.FoodOrderingApp.service.Entity.CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryUuid);

        List<com.upgrad.FoodOrderingApp.service.Entity.RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemByRestaurant(restaurantEntity);

        List<com.upgrad.FoodOrderingApp.service.Entity.CategoryItemEntity> categoryItemEntities = categoryDao.getItemByCategory(categoryEntity);

        List<com.upgrad.FoodOrderingApp.service.Entity.ItemEntity> itemEntities = new LinkedList<>();
        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if (restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())) {
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });
        return itemEntities;
    }

    /* Get Item by Item UUID */
    @Transactional(propagation = Propagation.REQUIRED)
    public com.upgrad.FoodOrderingApp.service.Entity.ItemEntity getItemByUuid(final String itemUuid) throws ItemNotFoundException {
        com.upgrad.FoodOrderingApp.service.Entity.ItemEntity itemEntity = itemDao.getItemByUuid(itemUuid);
        if(itemEntity == null ) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return itemEntity;
    }
}
