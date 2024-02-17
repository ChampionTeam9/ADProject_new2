package com.ad.teamnine.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.ShoppingListItem;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.ShoppingListItemRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShoppingListItemService {
	@Autowired
	ShoppingListItemRepository shoppingListItemRepo;
	
	@Autowired
	MemberRepository memberRepo;
	
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
	
	public ShoppingListItem saveItemFromAndroid (String username, boolean isChecked, String itemName) {
		Member member = memberRepo.findByUsername(username);
		ShoppingListItem item = new ShoppingListItem(member, itemName);
		item.setChecked(isChecked);
		return shoppingListItemRepo.save(item);
	}
	
	public void updateItemFromAndroid(Integer itemId, boolean isChecked) {
		ShoppingListItem item = shoppingListItemRepo.findById(itemId).get();
		item.setChecked(isChecked);
		shoppingListItemRepo.save(item);
	}
	
	public void deleteItemFromAndroid(Integer itemId) {
		shoppingListItemRepo.deleteById(itemId);
	}
}
