package jpa.core.service;

import java.util.List;
import jpa.core.domain.items.Item;
import jpa.core.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems () {
        return itemRepository.findAll();
    }

    public Item findOne (Long id) {
        return itemRepository.findOne(id);
    }
}
