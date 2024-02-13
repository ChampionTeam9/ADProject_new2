package com.ad.teamnine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.ShoppingListItem;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem,Integer>{

}
