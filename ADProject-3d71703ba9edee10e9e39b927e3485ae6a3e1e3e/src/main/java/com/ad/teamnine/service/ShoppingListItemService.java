package com.ad.teamnine.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.ShoppingListItem;
import com.ad.teamnine.repository.ShoppingListItemRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShoppingListItemService {
	@Autowired
	ShoppingListItemRepository shoppingListItemRepo;
	
	public ShoppingListItem saveShoppingListItem(ShoppingListItem item) {
		return shoppingListItemRepo.save(item);
	}
	
	// get specific shoppingListItem by id
	public ShoppingListItem getShoppingListItemById(Integer id) {
		Optional<ShoppingListItem> shoppingListItem = shoppingListItemRepo.findById(id);
		return shoppingListItem.orElse(null);
	};
	
	// delete specific recipe by shoppingListItem
	public void deleteShoppingListItem(ShoppingListItem shoppingListItem) {
		shoppingListItemRepo.delete(shoppingListItem);
	}
}
