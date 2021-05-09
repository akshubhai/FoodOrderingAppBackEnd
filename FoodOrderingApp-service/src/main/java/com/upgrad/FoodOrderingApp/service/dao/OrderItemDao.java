package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/* Database Repository connection */
@Repository
public class OrderItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    /* Get Items in order */
    public List<OrderItemEntity> getItemsByOrders(OrderEntity orderEntity) {
        try {
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getItemsByOrders", OrderItemEntity.class).setParameter("order", orderEntity).getResultList();
            return orderItemEntities;
        } catch (NoResultException nre) {
            return null;
        }
    }

    /* get order items by order */
    public List<OrderItemEntity> getOrderItemsByOrder(OrderEntity orderEntity) {
        try {
            List<OrderItemEntity> orderItemEntities = entityManager.createNamedQuery("getOrderItemByOrder", OrderItemEntity.class).setParameter("order", orderEntity).getResultList();
            return orderItemEntities;

        } catch (NoResultException nre) {
            return null;
        }
    }
}

