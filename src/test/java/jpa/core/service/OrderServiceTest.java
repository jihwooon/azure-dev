package jpa.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jpa.core.domain.Address;
import jpa.core.domain.Member;
import jpa.core.domain.Order;
import jpa.core.domain.OrderStatus;
import jpa.core.domain.items.Book;
import jpa.core.domain.items.Item;
import jpa.core.exception.NotEnoughStockException;
import jpa.core.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @Test
    public void 상품주문() throws Exception {

        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(getOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(getOrder.getTotalPrice()).isEqualTo(10000 * orderCount);
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }


    @Test
    public void 상품주문_재고수량초과() throws Exception {
        Member member = createMember();

        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));
    }

    @Test
    public void 주문취소() throws Exception {
        Member member = createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        assertThat(item.getStockQuantity()).isEqualTo(8); // 주문 시 재고 감소

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCEL); // 주문 취소 시 상태는 CANCEL 이다.
        assertThat(item.getStockQuantity()).isEqualTo(10); // 주문 취소된 상품은 그만큼 재고가 증가해야 한다.
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }
}
